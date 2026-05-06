import { computed, ref } from "vue"
import { apiRequest, resolveApiUrl } from "@/lib/api"
import type {
  AdminAuthor,
  AdminOrder,
  AdminPublisher,
  AdminTag,
  AdminUser,
} from "@/types/admin"
import type { Book } from "@/types/book"

interface ApiBook {
  id: number
  title: string
  authors: Array<{ id: number, name: string, bio?: string, country?: string, website?: string }>
  genre: { id: number, name: string, description?: string }
  price: number
  discountPrice?: number | null
  format: string
  pages: number
  year: number
  publishDate?: string
  publishers: Array<{ id: number, name: string, description?: string, country?: string, website?: string }>
  blurb: string
  description: string
  longDescription: string
  tags: Array<{ id: number, name: string, description?: string, color?: string, featured?: boolean, usageCount?: number }>
  coverUrl?: string | null
  featured?: boolean
  popular?: boolean
  newArrival?: boolean
}

interface ApiOrder {
  id: number
  orderCode: string
  date: string
  status: string
  total: number
  paymentMethod?: string
  deliveryCity?: string
  client?: { name?: string, email?: string }
  items: Array<{ book: { id: number }, quantity: number }>
}

const books = ref<Book[]>([])
const orders = ref<AdminOrder[]>([])
const tags = ref<AdminTag[]>([])
const users = ref<AdminUser[]>([])
const authors = ref<AdminAuthor[]>([])
const publishers = ref<AdminPublisher[]>([])
const genresCatalog = ref<Array<{ id: number, name: string, description?: string }>>([])
let loadPromise: Promise<void> | null = null

function paletteFor(seed: string) {
  const palettes = [
    { from: "#13293d", via: "#1b4965", to: "#5fa8d3" },
    { from: "#402039", via: "#6f2dbd", to: "#c04cf0" },
    { from: "#2d1e2f", via: "#6b0f1a", to: "#f17105" },
    { from: "#0b3954", via: "#087e8b", to: "#bfd7ea" },
    { from: "#151515", via: "#d62828", to: "#fcbf49" },
  ]
  return palettes[Math.abs(seed.length) % palettes.length]
}

function mapBook(book: ApiBook): Book {
  return {
    id: book.id,
    title: book.title,
    authors: book.authors,
    authorIds: book.authors.map((author) => author.id),
    author: book.authors.map((author) => author.name).join(", "),
    price: Number(book.price),
    originalPrice: book.discountPrice == null ? undefined : Number(book.discountPrice),
    rating: 4.7,
    reviews: 0,
    genreId: book.genre.id,
    genre: book.genre.name,
    format: book.format === "HARDCOVER" ? "Hardcover" : "Paperback",
    pages: book.pages,
    year: book.year,
    publishDate: book.publishDate,
    publishers: book.publishers,
    publisherIds: book.publishers.map((publisher) => publisher.id),
    publisher: book.publishers.map((publisher) => publisher.name).join(", "),
    blurb: book.blurb,
    description: book.description,
    longDescription: book.longDescription,
    tags: book.tags.map((tag) => tag.name),
    tagIds: book.tags.map((tag) => tag.id),
    coverUrl: resolveApiUrl(book.coverUrl),
    palette: paletteFor(book.title),
    featured: Boolean(book.featured),
    popular: Boolean(book.popular),
    newArrival: Boolean(book.newArrival),
  }
}

async function loadAll(force = false) {
  if (loadPromise && !force) {
    return loadPromise
  }

  loadPromise = (async () => {
    const [booksData, authorsData, publishersData, tagsData, genresData] = await Promise.all([
      apiRequest<ApiBook[]>("/books", { auth: false }),
      apiRequest<AdminAuthor[]>("/authors", { auth: false }),
      apiRequest<AdminPublisher[]>("/publishers", { auth: false }),
      apiRequest<AdminTag[]>("/tags", { auth: false }),
      apiRequest<Array<{ id: number, name: string, description?: string }>>("/genres", { auth: false }),
    ])

    books.value = booksData.map(mapBook)
    authors.value = authorsData.map((author) => ({
      ...author,
      featured: author.featured ?? false,
      booksCount: author.booksCount ?? books.value.filter((book) => (book.authorIds ?? []).includes(author.id)).length,
    }))
    publishers.value = publishersData.map((publisher) => ({
      ...publisher,
      featured: publisher.featured ?? false,
      booksCount: publisher.booksCount ?? books.value.filter((book) => (book.publisherIds ?? []).includes(publisher.id)).length,
    }))
    tags.value = tagsData.map((tag) => ({
      ...tag,
      featured: tag.featured ?? false,
      usageCount: tag.usageCount ?? books.value.filter((book) => (book.tagIds ?? []).includes(tag.id)).length,
    }))
    genresCatalog.value = genresData

    try {
      const [ordersData, usersData] = await Promise.all([
        apiRequest<ApiOrder[]>("/orders"),
        apiRequest<Array<{ id: number, name: string, email: string, role: string, status: string, joinedAt: string, orders: Array<{ total: number }> }>>("/clients"),
      ])

      orders.value = ordersData.map((order) => ({
        entityId: order.id,
        id: order.orderCode,
        date: order.date,
        status: order.status,
        total: Number(order.total),
        customerName: order.client?.name ?? "",
        customerEmail: order.client?.email ?? "",
        paymentMethod: order.paymentMethod ?? "",
        deliveryCity: order.deliveryCity ?? "",
        items: order.items.map((item) => ({ bookId: item.book.id, quantity: item.quantity })),
      }))

      users.value = usersData.map((user) => ({
        id: user.id,
        name: user.name,
        email: user.email,
        role: user.role === "ADMIN" ? "Admin" : "Client",
        status: user.status === "ACTIVE" ? "Active" : "Blocked",
        joinedAt: user.joinedAt,
        ordersCount: user.orders.length,
        totalSpent: user.orders.reduce((sum, order) => sum + Number(order.total), 0),
      }))
    } catch {
      orders.value = []
      users.value = []
    }
  })()

  try {
    await loadPromise
  } finally {
    loadPromise = null
  }
}

async function updateOrder(id: number, payload: Record<string, unknown>) {
  const updated = await apiRequest<ApiOrder>(`/orders/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  })
  const mapped: AdminOrder = {
    entityId: updated.id,
    id: updated.orderCode,
    date: updated.date,
    status: updated.status,
    total: Number(updated.total),
    customerName: updated.client?.name ?? "",
    customerEmail: updated.client?.email ?? "",
    paymentMethod: updated.paymentMethod ?? "",
    deliveryCity: updated.deliveryCity ?? "",
    items: updated.items.map((item) => ({ bookId: item.book.id, quantity: item.quantity })),
  }
  orders.value = orders.value.map((order) => (order.entityId === id ? mapped : order))
  return mapped
}

async function deleteOrderRemote(id: number) {
  await apiRequest<void>(`/orders/${id}`, { method: "DELETE" })
  orders.value = orders.value.filter((order) => order.entityId !== id)
}

async function updateUser(id: number, payload: Record<string, unknown>) {
  await apiRequest(`/clients/${id}`, {
    method: "PATCH",
    body: JSON.stringify(payload),
  })
  await loadAll(true)
}

async function deleteUserRemote(id: number) {
  await apiRequest<void>(`/clients/${id}`, { method: "DELETE" })
  users.value = users.value.filter((user) => user.id !== id)
}

async function createBook(payload: Record<string, unknown>) {
  const created = await apiRequest<ApiBook>("/books", {
    method: "POST",
    body: JSON.stringify(payload),
  })
  books.value = [mapBook(created), ...books.value]
  return mapBook(created)
}

async function updateBook(id: number, payload: Record<string, unknown>) {
  const updated = await apiRequest<ApiBook>(`/books/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  })
  const mapped = mapBook(updated)
  books.value = books.value.map((book) => (book.id === id ? mapped : book))
  return mapped
}

async function uploadBookCover(id: number, file: File) {
  const formData = new FormData()
  formData.append("file", file)
  const updated = await apiRequest<ApiBook>(`/books/${id}/cover`, {
    method: "POST",
    body: formData,
  })
  const mapped = mapBook(updated)
  books.value = books.value.map((book) => (book.id === id ? mapped : book))
  return mapped
}

async function deleteBookRemote(id: number) {
  await apiRequest<void>(`/books/${id}`, { method: "DELETE" })
  books.value = books.value.filter((book) => book.id !== id)
}

function refreshTagCatalog() {
  tags.value = tags.value.map((tag) => ({
    ...tag,
    usageCount: books.value.filter((book) => (book.tagIds ?? []).includes(tag.id)).length,
  }))
}

function refreshAuthorCatalog() {
  authors.value = authors.value.map((author) => ({
    ...author,
    booksCount: books.value.filter((book) => (book.authorIds ?? []).includes(author.id)).length,
  }))
}

function refreshPublisherCatalog() {
  publishers.value = publishers.value.map((publisher) => ({
    ...publisher,
    booksCount: books.value.filter((book) => (book.publisherIds ?? []).includes(publisher.id)).length,
  }))
}

function renameTagAcrossBooks(..._args: unknown[]) {}
function renameAuthorAcrossBooks(..._args: unknown[]) {}
function renamePublisherAcrossBooks(..._args: unknown[]) {}
function removeTagFromBooks(..._args: unknown[]) {}
function removeAuthorFromBooks(..._args: unknown[]) {}
function removePublisherFromBooks(..._args: unknown[]) {}

export function useAdminStore() {
  void loadAll()

  return {
    books,
    orders,
    tags,
    users,
    authors,
    publishers,
    genresCatalog,
    genres: computed(() => ["All", ...new Set(books.value.map((book) => book.genre))]),
    booksById: computed(() => new Map(books.value.map((book) => [book.id, book]))),
    loadAll,
    createBook,
    updateBook,
    uploadBookCover,
    deleteBookRemote,
    updateOrder,
    deleteOrderRemote,
    updateUser,
    deleteUserRemote,
    refreshTagCatalog,
    refreshAuthorCatalog,
    refreshPublisherCatalog,
    renameTagAcrossBooks,
    renameAuthorAcrossBooks,
    renamePublisherAcrossBooks,
    removeTagFromBooks,
    removeAuthorFromBooks,
    removePublisherFromBooks,
  }
}
