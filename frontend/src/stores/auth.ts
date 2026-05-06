import { computed, ref } from "vue"
import { apiRequest, getStoredToken, setStoredToken } from "@/lib/api"

interface AuthResponse {
  token: string
  name: string
  email: string
  role: string
}

interface MeResponse {
  id: number
  name: string
  email: string
  role: string
}

const token = ref<string | null>(getStoredToken())
const user = ref<MeResponse | null>(null)
let restorePromise: Promise<void> | null = null

async function loadMe() {
  if (!token.value) {
    user.value = null
    return
  }
  user.value = await apiRequest<MeResponse>("/auth/me")
}

async function restoreSession() {
  if (restorePromise) {
    return restorePromise
  }

  restorePromise = (async () => {
    if (!token.value) {
      user.value = null
      return
    }
    try {
      await loadMe()
    } catch {
      logout()
    }
  })()

  try {
    await restorePromise
  } finally {
    restorePromise = null
  }
}

async function login(email: string, password: string) {
  const response = await apiRequest<AuthResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
    auth: false,
  })
  token.value = response.token
  setStoredToken(response.token)
  await loadMe()
}

async function register(name: string, email: string, password: string) {
  const response = await apiRequest<AuthResponse>("/auth/register", {
    method: "POST",
    body: JSON.stringify({ name, email, password }),
    auth: false,
  })
  token.value = response.token
  setStoredToken(response.token)
  await loadMe()
}

function logout() {
  token.value = null
  user.value = null
  setStoredToken(null)
}

export function useAuthStore() {
  return {
    token,
    user,
    isAuthenticated: computed(() => Boolean(token.value)),
    isAdmin: computed(() => user.value?.role === "ADMIN"),
    restoreSession,
    loadMe,
    login,
    register,
    logout,
  }
}
