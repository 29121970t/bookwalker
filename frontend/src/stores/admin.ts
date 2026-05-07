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

interface ApiClient {
  id: number
  name: string
  email: string
  role: string
  status: string
  joinedAt: string
  orders: Array<{ total: number }>
}

interface ApiAuthor {
  id: number
  name: string
  bio?: string | null
  country?: string | null
  website?: string | null
  booksCount?: number | null
}

interface ApiPublisher {
  id: number
  name: string
  description?: string | null
  country?: string | null
  website?: string | null
  booksCount?: number | null
}

interface ApiTag {
  id: number
  name: string
  description?: string | null
  color?: string | null
  featured?: boolean | null
  usageCount?: number | null
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

function mapAuthor(author: ApiAuthor): AdminAuthor {
  return {
    id: author.id,
    name: author.name,
    bio: author.bio ?? "",
    country: author.country ?? "",
    website: author.website ?? "",
    featured: false,
    booksCount: author.booksCount ?? books.value.filter((book) => (book.authorIds ?? []).includes(author.id)).length,
  }
}

function mapPublisher(publisher: ApiPublisher): AdminPublisher {
  return {
    id: publisher.id,
    name: publisher.name,
    description: publisher.description ?? "",
    country: publisher.country ?? "",
    website: publisher.website ?? "",
    featured: false,
    booksCount: publisher.booksCount ?? books.value.filter((book) => (book.publisherIds ?? []).includes(publisher.id)).length,
  }
}

function mapTag(tag: ApiTag): AdminTag {
  return {
    id: tag.id,
    name: tag.name,
    description: tag.description ?? "",
    color: tag.color ?? "#0f766e",
    featured: tag.featured ?? false,
    usageCount: tag.usageCount ?? books.value.filter((book) => (book.tagIds ?? []).includes(tag.id)).length,
  }
}

function mapOrder(order: ApiOrder): AdminOrder {
  return {
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
  }
}

function mapUser(user: ApiClient): AdminUser {
  return {
    id: user.id,
    name: user.name,
    email: user.email,
    role: user.role === "ADMIN" ? "Admin" : "Client",
    status: user.status === "ACTIVE" ? "Active" : "Blocked",
    joinedAt: user.joinedAt,
    ordersCount: user.orders.length,
    totalSpent: user.orders.reduce((sum, order) => sum + Number(order.total), 0),
  }
}

async function loadAll(force = false, includeRestricted = false) {
  if (loadPromise && !force) {
    return loadPromise
  }

  loadPromise = (async () => {
    const [booksData, authorsData, publishersData, tagsData, genresData] = await Promise.all([
      apiRequest<ApiBook[]>("/books", { auth: false }),
      apiRequest<ApiAuthor[]>("/authors", { auth: false }),
      apiRequest<ApiPublisher[]>("/publishers", { auth: false }),
      apiRequest<ApiTag[]>("/tags", { auth: false }),
      apiRequest<Array<{ id: number, name: string, description?: string }>>("/genres", { auth: false }),
    ])

    books.value = booksData.map(mapBook)
    authors.value = authorsData.map(mapAuthor)
    publishers.value = publishersData.map(mapPublisher)
    tags.value = tagsData.map(mapTag)
    genresCatalog.value = genresData

    if (includeRestricted) {
      const [ordersData, usersData] = await Promise.all([
        apiRequest<ApiOrder[]>("/orders"),
        apiRequest<ApiClient[]>("/clients"),
      ])

      orders.value = ordersData.map(mapOrder)
      users.value = usersData.map(mapUser)
    }
  })()

  try {
    await loadPromise
  } finally {
    loadPromise = null
  }
}

async function createOrder(payload: Record<string, unknown>) {
  const created = await apiRequest<ApiOrder>("/orders", {
    method: "POST",
    body: JSON.stringify(payload),
  })
  const mapped = mapOrder(created)
  orders.value = [mapped, ...orders.value]
  return mapped
}

async function updateOrder(id: number, payload: Record<string, unknown>) {
  const updated = await apiRequest<ApiOrder>(`/orders/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  })
  const mapped = mapOrder(updated)
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
  await loadAll(true, true)
}

async function createUser(payload: Record<string, unknown>) {
  const created = await apiRequest<ApiClient>("/clients", {
    method: "POST",
    body: JSON.stringify(payload),
  })
  const mapped = mapUser(created)
  users.value = [mapped, ...users.value]
  return mapped
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

async function createTag(payload: Record<string, unknown>) {
  const created = await apiRequest<ApiTag>("/tags", {
    method: "POST",
    body: JSON.stringify(payload),
  })
  const mapped = mapTag(created)
  tags.value = [mapped, ...tags.value]
  return mapped
}

async function updateTag(id: number, payload: Record<string, unknown>) {
  const updated = await apiRequest<ApiTag>(`/tags/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  })
  const mapped = mapTag(updated)
  tags.value = tags.value.map((tag) => (tag.id === id ? mapped : tag))
  return mapped
}

async function deleteTagRemote(id: number) {
  await apiRequest<void>(`/tags/${id}`, { method: "DELETE" })
  tags.value = tags.value.filter((tag) => tag.id !== id)
}

async function createAuthor(payload: Record<string, unknown>) {
  const created = await apiRequest<ApiAuthor>("/authors", {
    method: "POST",
    body: JSON.stringify(payload),
  })
  const mapped = mapAuthor(created)
  authors.value = [mapped, ...authors.value]
  return mapped
}

async function updateAuthor(id: number, payload: Record<string, unknown>) {
  const updated = await apiRequest<ApiAuthor>(`/authors/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  })
  const mapped = mapAuthor(updated)
  authors.value = authors.value.map((author) => (author.id === id ? mapped : author))
  return mapped
}

async function deleteAuthorRemote(id: number) {
  await apiRequest<void>(`/authors/${id}`, { method: "DELETE" })
  authors.value = authors.value.filter((author) => author.id !== id)
}

async function createPublisher(payload: Record<string, unknown>) {
  const created = await apiRequest<ApiPublisher>("/publishers", {
    method: "POST",
    body: JSON.stringify(payload),
  })
  const mapped = mapPublisher(created)
  publishers.value = [mapped, ...publishers.value]
  return mapped
}

async function updatePublisher(id: number, payload: Record<string, unknown>) {
  const updated = await apiRequest<ApiPublisher>(`/publishers/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  })
  const mapped = mapPublisher(updated)
  publishers.value = publishers.value.map((publisher) => (publisher.id === id ? mapped : publisher))
  return mapped
}

async function deletePublisherRemote(id: number) {
  await apiRequest<void>(`/publishers/${id}`, { method: "DELETE" })
  publishers.value = publishers.value.filter((publisher) => publisher.id !== id)
}

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
    createOrder,
    createTag,
    updateTag,
    deleteTagRemote,
    createAuthor,
    updateAuthor,
    deleteAuthorRemote,
    createPublisher,
    updatePublisher,
    deletePublisherRemote,
    updateOrder,
    deleteOrderRemote,
    createUser,
    updateUser,
    deleteUserRemote,
  }
}
