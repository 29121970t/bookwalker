export const API_BASE_URL = (import.meta as ImportMeta).env?.VITE_API_BASE_URL
  ?? "http://localhost:8080/api"
const TOKEN_KEY = "bookwalker-auth-token"
type RequestOptions = RequestInit & {
  auth?: boolean
}

export class ApiError extends Error {
  constructor(
    message: string,
    public readonly status: number,
    public readonly body: unknown,
  ) {
    super(message)
    this.name = "ApiError"
  }
}

export function getStoredToken() {
  return window.localStorage.getItem(TOKEN_KEY)
}

export function setStoredToken(token: string | null) {
  if (token) {
    window.localStorage.setItem(TOKEN_KEY, token)
    return
  }
  window.localStorage.removeItem(TOKEN_KEY)
}

export function resolveApiUrl(path?: string | null) {
  if (!path) {
    return null
  }
  if (/^https?:\/\//i.test(path)) {
    return path
  }
  return `${API_BASE_URL}${path.startsWith("/") ? path : `/${path}`}`
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers = new Headers(options.headers ?? {})
  const token = getStoredToken()
  const isFormData = typeof FormData !== "undefined" && options.body instanceof FormData

  if (!isFormData && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json")
  }

  if (options.auth !== false && token && !headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    const text = await response.text()
    let body: unknown = text
    let message = text || `Request failed: ${response.status}`

    if (text) {
      try {
        body = JSON.parse(text) as unknown
        if (body && typeof body === "object" && "error" in body) {
          const error = (body as { error?: unknown }).error
          if (typeof error === "string" && error.trim()) {
            message = error
          }
        }
      } catch {
        body = text
      }
    }

    throw new ApiError(message, response.status, body)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}
