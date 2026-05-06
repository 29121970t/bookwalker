import { computed, reactive, ref, watch } from "vue"
import { toast } from "vue-sonner"
import { BookOpenText, Building2, CircleDollarSign, PenSquare, Tag, Users } from "lucide-vue-next"
import { useAdminStore } from "@/stores/admin"
import type {
  AdminAuthor,
  AdminOrder,
  AdminPublisher,
  AdminTag,
  AdminUser,
} from "@/types/admin"
import type { Book } from "@/types/book"

export type AdminSection = "books" | "orders" | "tags" | "users" | "authors" | "publishers"
type DialogMode = "create" | "edit"

type BookForm = {
  id: number
  title: string
  authorIds: number[]
  genreId: number
  price: number
  originalPrice: number
  format: "HARDCOVER" | "PAPERBACK"
  pages: number
  year: number
  publisherId: number | null
  blurb: string
  description: string
  longDescription: string
  tagIds: number[]
  featured: boolean
  popular: boolean
  newArrival: boolean
}

type OrderForm = {
  entityId: number | null
  id: string
  date: string
  status: string
  total: number
  customerName: string
  customerEmail: string
  paymentMethod: string
  deliveryCity: string
  items: Array<{ bookId: number, quantity: number }>
}

type TagForm = {
  id: number
  name: string
  description: string
  color: string
  featured: boolean
}

type UserForm = {
  id: number
  name: string
  email: string
  role: AdminUser["role"]
  status: AdminUser["status"]
  joinedAt: string
  ordersCount: number
  totalSpent: number
}

type AuthorForm = {
  id: number
  name: string
  bio: string
  country: string
  website: string
  featured: boolean
}

type PublisherForm = {
  id: number
  name: string
  description: string
  country: string
  website: string
  featured: boolean
}

const adminSections: AdminSection[] = ["books", "orders", "tags", "users", "authors", "publishers"]
const ITEMS_PER_PAGE = 10
const ORDER_STATUSES = ["Processing", "Paid", "Shipped", "Delivered", "Cancelled"] as const
const PAYMENT_METHODS = ["CARD", "CASH", "PAYPAL"] as const

const {
  books,
  orders,
  tags,
  users,
  authors,
  publishers,
  genresCatalog,
  booksById,
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
} = useAdminStore()

const dialogOpen = ref(false)
const dialogSection = ref<AdminSection>("books")
const dialogMode = ref<DialogMode>("create")
const dialogSubmitting = ref(false)
const selectedBookCover = ref<File | null>(null)
const validationErrors = reactive<Record<string, string>>({})

const search = reactive<Record<AdminSection, string>>({
  books: "",
  orders: "",
  tags: "",
  users: "",
  authors: "",
  publishers: "",
})

const currentPage = reactive<Record<AdminSection, number>>({
  books: 1,
  orders: 1,
  tags: 1,
  users: 1,
  authors: 1,
  publishers: 1,
})

const sectionMeta: Record<AdminSection, { title: string, description: string }> = {
  books: {
    title: "Books",
    description: "Manage catalog positions, prices, tags, and shelf visibility.",
  },
  orders: {
    title: "Orders",
    description: "Track current purchases, customer details, and delivery progress.",
  },
  tags: {
    title: "Tags",
    description: "Curate catalog labels used across cards, browse filters, and detail pages.",
  },
  users: {
    title: "Users",
    description: "Keep admin and client records under control.",
  },
  authors: {
    title: "Authors",
    description: "Maintain author cards and propagate name changes across linked books.",
  },
  publishers: {
    title: "Publishers",
    description: "Manage publishing houses used in the storefront catalog.",
  },
}

const dialogTitles: Record<AdminSection, string> = {
  books: "book",
  orders: "order",
  tags: "tag",
  users: "user",
  authors: "author",
  publishers: "publisher",
}

const canCreateSection: Record<AdminSection, boolean> = {
  books: true,
  orders: false,
  tags: true,
  users: false,
  authors: true,
  publishers: true,
}

const bookForm = reactive<BookForm>(createEmptyBookForm())
const orderForm = reactive<OrderForm>(createEmptyOrderForm())
const tagForm = reactive<TagForm>(createEmptyTagForm())
const userForm = reactive<UserForm>(createEmptyUserForm())
const authorForm = reactive<AuthorForm>(createEmptyAuthorForm())
const publisherForm = reactive<PublisherForm>(createEmptyPublisherForm())

const counts = computed(() => ({
  books: books.value.length,
  orders: orders.value.length,
  tags: tags.value.length,
  users: users.value.length,
  authors: authors.value.length,
  publishers: publishers.value.length,
}))

const dashboardStats = computed(() => [
  {
    title: "Catalog books",
    value: books.value.length.toString(),
    note: `${books.value.filter((book) => book.featured).length} featured`,
    icon: BookOpenText,
  },
  {
    title: "Order revenue",
    value: formatCurrency(orders.value.reduce((sum, order) => sum + order.total, 0)),
    note: `${orders.value.filter((order) => order.status !== "Delivered").length} active orders`,
    icon: CircleDollarSign,
  },
  {
    title: "Authors",
    value: authors.value.length.toString(),
    note: `${authors.value.filter((author) => author.featured).length} featured`,
    icon: PenSquare,
  },
  {
    title: "Publishers",
    value: publishers.value.length.toString(),
    note: `${publishers.value.filter((publisher) => publisher.featured).length} featured`,
    icon: Building2,
  },
  {
    title: "Tags in use",
    value: tags.value.filter((tag) => tag.usageCount > 0).length.toString(),
    note: `${tags.value.length} total labels`,
    icon: Tag,
  },
  {
    title: "Users",
    value: users.value.length.toString(),
    note: `${users.value.filter((user) => user.status === "Active").length} active`,
    icon: Users,
  },
])

const currentDialogTitle = computed(
  () => `${dialogMode.value === "create" ? "Create" : "Edit"} ${dialogTitles[dialogSection.value]}`,
)

const authorOptions = computed(() => authors.value.map((author) => ({ id: author.id, label: author.name })))
const publisherOptions = computed(() => publishers.value.map((publisher) => ({ id: publisher.id, label: publisher.name })))
const tagOptions = computed(() => tags.value.map((tag) => ({ id: tag.id, label: tag.name })))
const genreOptions = computed(() => genresCatalog.value.map((genre) => ({ id: genre.id, label: genre.name })))

const filteredBooks = computed(() => {
  const query = search.books.trim().toLowerCase()
  return books.value.filter((book) => {
    if (!query) return true
    return [book.title, book.author, book.genre, book.publisher, book.tags.join(" ")]
      .some((value) => value.toLowerCase().includes(query))
  })
})

const filteredOrders = computed(() => {
  const query = search.orders.trim().toLowerCase()
  return orders.value.filter((order) => {
    if (!query) return true
    const items = order.items
      .map((item) => booksById.value.get(item.bookId)?.title ?? `Book #${item.bookId}`)
      .join(" ")
    return [
      order.id,
      order.status,
      order.customerName,
      order.customerEmail,
      order.deliveryCity,
      items,
    ].some((value) => value.toLowerCase().includes(query))
  })
})

const filteredTags = computed(() => {
  const query = search.tags.trim().toLowerCase()
  return tags.value.filter((tag) => {
    if (!query) return true
    return [tag.name, tag.description].some((value) => value.toLowerCase().includes(query))
  })
})

const filteredUsers = computed(() => {
  const query = search.users.trim().toLowerCase()
  return users.value.filter((user) => {
    if (!query) return true
    return [user.name, user.email, user.role, user.status]
      .some((value) => value.toLowerCase().includes(query))
  })
})

const filteredAuthors = computed(() => {
  const query = search.authors.trim().toLowerCase()
  return authors.value.filter((author) => {
    if (!query) return true
    return [author.name, author.bio, author.country, author.website]
      .some((value) => value.toLowerCase().includes(query))
  })
})

const filteredPublishers = computed(() => {
  const query = search.publishers.trim().toLowerCase()
  return publishers.value.filter((publisher) => {
    if (!query) return true
    return [publisher.name, publisher.description, publisher.country, publisher.website]
      .some((value) => value.toLowerCase().includes(query))
  })
})

function pageCount(total: number) {
  return Math.max(1, Math.ceil(total / ITEMS_PER_PAGE))
}

function clampCurrentPage(section: AdminSection, total: number) {
  currentPage[section] = Math.min(currentPage[section], pageCount(total))
}

function paginateItems<T>(items: T[], section: AdminSection) {
  const start = (currentPage[section] - 1) * ITEMS_PER_PAGE
  return items.slice(start, start + ITEMS_PER_PAGE)
}

const paginatedBooks = computed(() => paginateItems(filteredBooks.value, "books"))
const paginatedOrders = computed(() => paginateItems(filteredOrders.value, "orders"))
const paginatedTags = computed(() => paginateItems(filteredTags.value, "tags"))
const paginatedUsers = computed(() => paginateItems(filteredUsers.value, "users"))
const paginatedAuthors = computed(() => paginateItems(filteredAuthors.value, "authors"))
const paginatedPublishers = computed(() => paginateItems(filteredPublishers.value, "publishers"))

const pageCountBySection = computed<Record<AdminSection, number>>(() => ({
  books: pageCount(filteredBooks.value.length),
  orders: pageCount(filteredOrders.value.length),
  tags: pageCount(filteredTags.value.length),
  users: pageCount(filteredUsers.value.length),
  authors: pageCount(filteredAuthors.value.length),
  publishers: pageCount(filteredPublishers.value.length),
}))

function setPage(section: AdminSection, page: number) {
  currentPage[section] = Math.min(Math.max(1, page), pageCountBySection.value[section])
}

watch(
  () => ({ ...search }),
  () => {
    adminSections.forEach((section) => {
      currentPage[section] = 1
    })
  },
  { deep: true },
)

watch(() => filteredBooks.value.length, (total) => clampCurrentPage("books", total), { immediate: true })
watch(() => filteredOrders.value.length, (total) => clampCurrentPage("orders", total), { immediate: true })
watch(() => filteredTags.value.length, (total) => clampCurrentPage("tags", total), { immediate: true })
watch(() => filteredUsers.value.length, (total) => clampCurrentPage("users", total), { immediate: true })
watch(() => filteredAuthors.value.length, (total) => clampCurrentPage("authors", total), { immediate: true })
watch(() => filteredPublishers.value.length, (total) => clampCurrentPage("publishers", total), { immediate: true })
watch(() => orderForm.items, recalculateOrderFormTotal, { deep: true })

function createEmptyBookForm(): BookForm {
  return {
    id: 0,
    title: "",
    authorIds: [],
    genreId: genresCatalog.value[0]?.id ?? 0,
    price: 0.01,
    originalPrice: 0.01,
    format: "HARDCOVER",
    pages: 1,
    year: new Date().getFullYear(),
    publisherId: null,
    blurb: "",
    description: "",
    longDescription: "",
    tagIds: [],
    featured: false,
    popular: false,
    newArrival: false,
  }
}

function createEmptyOrderForm(): OrderForm {
  return {
    entityId: null,
    id: "",
    date: new Date().toISOString().slice(0, 16),
    status: "Processing",
    total: 0,
    customerName: "",
    customerEmail: "",
    paymentMethod: "CARD",
    deliveryCity: "",
    items: [],
  }
}

function createEmptyTagForm(): TagForm {
  return { id: 0, name: "", description: "", color: "#0f766e", featured: false }
}

function createEmptyUserForm(): UserForm {
  return {
    id: 0,
    name: "",
    email: "",
    role: "Client",
    status: "Active",
    joinedAt: "2026-04-29",
    ordersCount: 0,
    totalSpent: 0,
  }
}

function createEmptyAuthorForm(): AuthorForm {
  return { id: 0, name: "", bio: "", country: "", website: "", featured: false }
}

function createEmptyPublisherForm(): PublisherForm {
  return { id: 0, name: "", description: "", country: "", website: "", featured: false }
}

function clearValidationErrors() {
  Object.keys(validationErrors).forEach((key) => delete validationErrors[key])
}

function resetForm(section: AdminSection) {
  clearValidationErrors()
  if (section === "books") {
    Object.assign(bookForm, createEmptyBookForm())
    selectedBookCover.value = null
  }
  if (section === "orders") Object.assign(orderForm, createEmptyOrderForm())
  if (section === "tags") Object.assign(tagForm, createEmptyTagForm())
  if (section === "users") Object.assign(userForm, createEmptyUserForm())
  if (section === "authors") Object.assign(authorForm, createEmptyAuthorForm())
  if (section === "publishers") Object.assign(publisherForm, createEmptyPublisherForm())
}

function hasFieldError(field: string) {
  return Boolean(validationErrors[field])
}

function fieldError(field: string) {
  return validationErrors[field] ?? ""
}

function addValidationError(field: string, message: string) {
  validationErrors[field] = message
}

function hasValidationErrors() {
  return Object.keys(validationErrors).length > 0
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(value)
}

function recalculateOrderFormTotal() {
  orderForm.total = Number(orderForm.items.reduce((sum, item) => {
    const book = booksById.value.get(item.bookId)
    if (!book) return sum
    return sum + (book.price * item.quantity)
  }, 0).toFixed(2))
}

function orderItemsPreview(order: AdminOrder) {
  return order.items
    .map((item) => `${booksById.value.get(item.bookId)?.title ?? `Book #${item.bookId}`} x${item.quantity}`)
    .join(", ")
}

function formatDateTimeLocal(value?: string) {
  if (!value) return ""
  return value.slice(0, 16)
}

function toIsoDateTime(value: string) {
  if (!value) return null
  return value.length === 16 ? `${value}:00` : value
}

function nextNumericId(items: Array<{ id: number }>) {
  return items.length > 0 ? Math.max(...items.map((item) => item.id)) + 1 : 1
}

function openCreateDialog(section: AdminSection) {
  dialogMode.value = "create"
  dialogSection.value = section
  resetForm(section)
  dialogOpen.value = true
}

function openEditDialog(section: "books", entity: Book): void
function openEditDialog(section: "orders", entity: AdminOrder): void
function openEditDialog(section: "tags", entity: AdminTag): void
function openEditDialog(section: "users", entity: AdminUser): void
function openEditDialog(section: "authors", entity: AdminAuthor): void
function openEditDialog(section: "publishers", entity: AdminPublisher): void
function openEditDialog(
  section: AdminSection,
  entity: Book | AdminOrder | AdminTag | AdminUser | AdminAuthor | AdminPublisher,
) {
  dialogMode.value = "edit"
  dialogSection.value = section
  dialogOpen.value = true
  clearValidationErrors()

  if (section === "books") {
    const book = entity as Book
    Object.assign(bookForm, {
      id: book.id,
      title: book.title,
      authorIds: [...(book.authorIds ?? [])],
      genreId: book.genreId ?? genresCatalog.value[0]?.id ?? 0,
      price: book.price,
      originalPrice: book.originalPrice ?? book.price,
      format: book.format.toUpperCase() === "HARDCOVER" ? "HARDCOVER" : "PAPERBACK",
      pages: book.pages,
      year: book.year,
      publisherId: book.publisherIds?.[0] ?? null,
      blurb: book.blurb,
      description: book.description,
      longDescription: book.longDescription,
      tagIds: [...(book.tagIds ?? [])],
      featured: Boolean(book.featured),
      popular: Boolean(book.popular),
      newArrival: Boolean(book.newArrival),
    })
    selectedBookCover.value = null
  }

  if (section === "orders") {
    const order = entity as AdminOrder
    Object.assign(orderForm, {
      entityId: order.entityId,
      id: order.id,
      date: formatDateTimeLocal(order.date),
      status: order.status,
      total: order.total,
      customerName: order.customerName,
      customerEmail: order.customerEmail,
      paymentMethod: order.paymentMethod,
      deliveryCity: order.deliveryCity,
      items: order.items.map((item) => ({ ...item })),
    })
  }

  if (section === "tags") Object.assign(tagForm, entity as AdminTag)
  if (section === "users") Object.assign(userForm, entity as AdminUser)

  if (section === "authors") {
    const author = entity as AdminAuthor
    Object.assign(authorForm, {
      id: author.id,
      name: author.name,
      bio: author.bio,
      country: author.country,
      website: author.website,
      featured: author.featured,
    })
  }

  if (section === "publishers") {
    const publisher = entity as AdminPublisher
    Object.assign(publisherForm, {
      id: publisher.id,
      name: publisher.name,
      description: publisher.description,
      country: publisher.country,
      website: publisher.website,
      featured: publisher.featured,
    })
  }
}

function validateBookForm() {
  clearValidationErrors()
  const price = Number(bookForm.price)
  const originalPrice = Number(bookForm.originalPrice)
  const pages = Number(bookForm.pages)
  const year = Number(bookForm.year)

  if (!bookForm.title.trim()) addValidationError("book.title", "Book title is required.")
  else if (bookForm.title.trim().length > 255) addValidationError("book.title", "Book title is too long.")
  if (bookForm.authorIds.length === 0) addValidationError("book.authors", "Select an author.")
  if (!bookForm.genreId) addValidationError("book.genre", "Select a genre.")
  if (bookForm.publisherId == null) addValidationError("book.publisher", "Select a publisher.")
  if (!Number.isFinite(price) || price <= 0) addValidationError("book.price", "Price must be greater than 0.")
  if (!Number.isFinite(originalPrice) || originalPrice < 0) addValidationError("book.originalPrice", "Original price cannot be negative.")
  if (!Number.isInteger(pages) || pages < 1) addValidationError("book.pages", "Pages must be at least 1.")
  if (!Number.isInteger(year) || year < 1000 || year > 9999) addValidationError("book.year", "Enter a valid publication year.")
  if (bookForm.blurb.length > 500) addValidationError("book.blurb", "Blurb must be 500 characters or fewer.")
  if (bookForm.description.length > 2000) addValidationError("book.description", "Description must be 2000 characters or fewer.")
  if (bookForm.longDescription.length > 6000) addValidationError("book.longDescription", "Long description must be 6000 characters or fewer.")

  return !hasValidationErrors()
}

function validateOrderForm() {
  clearValidationErrors()
  if (!ORDER_STATUSES.includes(orderForm.status as typeof ORDER_STATUSES[number])) addValidationError("order.status", "Select a valid order status.")
  if (!PAYMENT_METHODS.includes(orderForm.paymentMethod as typeof PAYMENT_METHODS[number])) addValidationError("order.paymentMethod", "Select a valid payment method.")
  if (!orderForm.date) addValidationError("order.date", "Order date is required.")
  if (orderForm.items.length === 0) addValidationError("order.items", "Select at least one order item.")
  if (orderForm.items.some((item) => !booksById.value.has(item.bookId) || item.quantity < 1)) {
    addValidationError("order.items", "Each order item must have a valid book and quantity.")
  }
  if (orderForm.deliveryCity.trim().length > 255) addValidationError("order.deliveryCity", "Delivery city is too long.")

  return !hasValidationErrors()
}

function validateUserForm() {
  clearValidationErrors()
  if (!userForm.name.trim()) addValidationError("user.name", "User name is required.")
  else if (userForm.name.trim().length > 255) addValidationError("user.name", "User name is too long.")
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(userForm.email.trim())) addValidationError("user.email", "Enter a valid email address.")
  else if (userForm.email.trim().length > 255) addValidationError("user.email", "Email is too long.")

  return !hasValidationErrors()
}

function validateAuthorForm() {
  clearValidationErrors()
  if (!authorForm.name.trim()) addValidationError("author.name", "Author name is required.")
  else if (authorForm.name.trim().length > 255) addValidationError("author.name", "Author name is too long.")
  if (authorForm.bio.length > 4000) addValidationError("author.bio", "Author bio is too long.")
  if (authorForm.country.trim().length > 120) addValidationError("author.country", "Author country is too long.")
  if (authorForm.website.trim().length > 255) addValidationError("author.website", "Author website is too long.")
  if (authorForm.website && !/^https?:\/\//.test(authorForm.website.trim())) addValidationError("author.website", "Author website must start with http:// or https://.")

  return !hasValidationErrors()
}

function validatePublisherForm() {
  clearValidationErrors()
  if (!publisherForm.name.trim()) addValidationError("publisher.name", "Publisher name is required.")
  else if (publisherForm.name.trim().length > 255) addValidationError("publisher.name", "Publisher name is too long.")
  if (publisherForm.country.trim().length > 120) addValidationError("publisher.country", "Publisher country is too long.")
  if (publisherForm.website.trim().length > 255) addValidationError("publisher.website", "Publisher website is too long.")
  if (publisherForm.website && !/^https?:\/\//.test(publisherForm.website.trim())) addValidationError("publisher.website", "Publisher website must start with http:// or https://.")
  if (publisherForm.description.length > 2000) addValidationError("publisher.description", "Publisher description is too long.")

  return !hasValidationErrors()
}

function validateTagForm() {
  clearValidationErrors()
  if (!tagForm.name.trim()) addValidationError("tag.name", "Tag name is required.")
  else if (tagForm.name.trim().length > 255) addValidationError("tag.name", "Tag name is too long.")
  if (!/^#[0-9a-fA-F]{6}$/.test(tagForm.color.trim())) addValidationError("tag.color", "Tag color must be a 6-digit hex value.")
  if (tagForm.description.length > 500) addValidationError("tag.description", "Tag description is too long.")

  return !hasValidationErrors()
}

async function saveBookDialog() {
  if (!validateBookForm()) return
  const payload = {
    title: bookForm.title.trim(),
    authors: bookForm.authorIds,
    genreId: bookForm.genreId,
    price: Number(bookForm.price),
    discountPrice: bookForm.originalPrice > 0 ? Number(bookForm.originalPrice) : null,
    format: bookForm.format,
    pages: Number(bookForm.pages),
    year: Number(bookForm.year),
    publisherIds: bookForm.publisherId == null ? [] : [bookForm.publisherId],
    blurb: bookForm.blurb.trim(),
    description: bookForm.description.trim(),
    longDescription: bookForm.longDescription.trim(),
    tagIds: bookForm.tagIds,
    featured: bookForm.featured,
    popular: bookForm.popular,
    newArrival: bookForm.newArrival,
  }

  const savedBook = dialogMode.value === "edit"
    ? await updateBook(bookForm.id, payload)
    : await createBook(payload)

  if (selectedBookCover.value) {
    await uploadBookCover(savedBook.id, selectedBookCover.value)
  }

  await loadAll(true)
  dialogOpen.value = false
  resetForm("books")
  toast.success(dialogMode.value === "edit" ? "Book updated" : "Book created")
}

async function saveOrderDialog() {
  if (!validateOrderForm()) return
  if (orderForm.entityId == null) {
    throw new Error("Creating orders from the admin dialog is not supported.")
  }

  await updateOrder(orderForm.entityId, {
    status: orderForm.status,
    paymentMethod: orderForm.paymentMethod,
    deliveryCity: orderForm.deliveryCity.trim(),
    date: toIsoDateTime(orderForm.date),
    items: orderForm.items.map((item) => ({ bookId: item.bookId, quantity: item.quantity })),
  })

  await loadAll(true)
  dialogOpen.value = false
  clearValidationErrors()
  toast.success("Order updated")
}

async function saveUserDialog() {
  if (!validateUserForm()) return
  if (!userForm.id) {
    throw new Error("Creating users from the admin dialog is not supported.")
  }

  await updateUser(userForm.id, {
    name: userForm.name.trim(),
    email: userForm.email.trim(),
    role: userForm.role === "Admin" ? "ADMIN" : "CUSTOMER",
    status: userForm.status === "Active" ? "ACTIVE" : "BLOCKED",
  })

  await loadAll(true)
  dialogOpen.value = false
  clearValidationErrors()
  toast.success("User updated")
}

async function saveDialog() {
  dialogSubmitting.value = true

  try {
    if (dialogSection.value === "books") {
      await saveBookDialog()
      return
    }

    if (dialogSection.value === "orders") {
      await saveOrderDialog()
      return
    }

    if (dialogSection.value === "tags") {
      if (!validateTagForm()) return
      const previous = tags.value.find((tag) => tag.id === tagForm.id)
      const payload: AdminTag = {
        id: dialogMode.value === "edit" ? tagForm.id : nextNumericId(tags.value),
        name: tagForm.name.trim(),
        description: tagForm.description.trim(),
        color: tagForm.color.trim(),
        featured: tagForm.featured,
        usageCount: previous?.usageCount ?? 0,
      }
      tags.value = dialogMode.value === "edit"
        ? tags.value.map((tag) => (tag.id === payload.id ? payload : tag))
        : [payload, ...tags.value]
      if (dialogMode.value === "edit" && previous && previous.name !== payload.name) {
        renameTagAcrossBooks(previous.name, payload.name)
      } else {
        refreshTagCatalog()
      }
    }

    if (dialogSection.value === "users") {
      await saveUserDialog()
      return
    }

    if (dialogSection.value === "authors") {
      if (!validateAuthorForm()) return
      const previous = authors.value.find((author) => author.id === authorForm.id)
      const payload: AdminAuthor = {
        id: dialogMode.value === "edit" ? authorForm.id : nextNumericId(authors.value),
        name: authorForm.name.trim(),
        bio: authorForm.bio.trim(),
        country: authorForm.country.trim(),
        website: authorForm.website.trim(),
        featured: authorForm.featured,
        booksCount: previous?.booksCount ?? 0,
      }
      authors.value = dialogMode.value === "edit"
        ? authors.value.map((author) => (author.id === payload.id ? payload : author))
        : [payload, ...authors.value]
      if (dialogMode.value === "edit" && previous && previous.name !== payload.name) {
        renameAuthorAcrossBooks(previous.name, payload.name)
      } else {
        refreshAuthorCatalog()
      }
    }

    if (dialogSection.value === "publishers") {
      if (!validatePublisherForm()) return
      const previous = publishers.value.find((publisher) => publisher.id === publisherForm.id)
      const payload: AdminPublisher = {
        id: dialogMode.value === "edit" ? publisherForm.id : nextNumericId(publishers.value),
        name: publisherForm.name.trim(),
        description: publisherForm.description.trim(),
        country: publisherForm.country.trim(),
        website: publisherForm.website.trim(),
        featured: publisherForm.featured,
        booksCount: previous?.booksCount ?? 0,
      }
      publishers.value = dialogMode.value === "edit"
        ? publishers.value.map((publisher) => (publisher.id === payload.id ? payload : publisher))
        : [payload, ...publishers.value]
      if (dialogMode.value === "edit" && previous && previous.name !== payload.name) {
        renamePublisherAcrossBooks(previous.name, payload.name)
      } else {
        refreshPublisherCatalog()
      }
    }

    dialogOpen.value = false
    clearValidationErrors()
  } catch (error) {
    toast.error(error instanceof Error ? error.message : "Failed to save changes")
  } finally {
    dialogSubmitting.value = false
  }
}

async function deleteBook(book: Book) {
  if (!window.confirm(`Delete "${book.title}" from catalog?`)) return
  try {
    await deleteBookRemote(book.id)
    await loadAll(true)
    toast.success("Book deleted")
  } catch (error) {
    toast.error(error instanceof Error ? error.message : "Failed to delete book")
  }
}

async function deleteOrder(order: AdminOrder) {
  if (!window.confirm(`Delete order ${order.id}?`)) return
  try {
    await deleteOrderRemote(order.entityId)
    await loadAll(true)
    toast.success("Order deleted")
  } catch (error) {
    toast.error(error instanceof Error ? error.message : "Failed to delete order")
  }
}

function deleteTag(tag: AdminTag) {
  if (!window.confirm(`Delete tag "${tag.name}" and remove it from books?`)) return
  tags.value = tags.value.filter((item) => item.id !== tag.id)
  removeTagFromBooks(tag.name)
}

async function deleteUser(user: AdminUser) {
  if (!window.confirm(`Delete user ${user.name}?`)) return
  try {
    await deleteUserRemote(user.id)
    await loadAll(true)
    toast.success("User deleted")
  } catch (error) {
    toast.error(error instanceof Error ? error.message : "Failed to delete user")
  }
}

function deleteAuthor(author: AdminAuthor) {
  if (!window.confirm(`Delete author "${author.name}" and replace linked books with "Unknown Author"?`)) return
  authors.value = authors.value.filter((item) => item.id !== author.id)
  removeAuthorFromBooks(author.name)
}

function deletePublisher(publisher: AdminPublisher) {
  if (!window.confirm(`Delete publisher "${publisher.name}" and replace linked books with "Independent Press"?`)) return
  publishers.value = publishers.value.filter((item) => item.id !== publisher.id)
  removePublisherFromBooks(publisher.name)
}

function badgeVariantForStatus(status: string) {
  const normalized = status.toLowerCase()
  if (normalized === "delivered" || normalized === "active") return "secondary" as const
  if (normalized === "blocked" || normalized === "cancelled") return "destructive" as const
  return "outline" as const
}

function setSelectedBookCover(file: File | null) {
  selectedBookCover.value = file
}

export function useAdminPanel() {
  return {
    books,
    orders,
    tags,
    users,
    authors,
    publishers,
    genresCatalog,
    search,
    sectionMeta,
    dialogTitles,
    canCreateSection,
    dialogOpen,
    dialogSection,
    dialogMode,
    dialogSubmitting,
    currentDialogTitle,
    counts,
    dashboardStats,
    filteredBooks,
    filteredOrders,
    filteredTags,
    filteredUsers,
    filteredAuthors,
    filteredPublishers,
    currentPage,
    itemsPerPage: ITEMS_PER_PAGE,
    pageCountBySection,
    paginatedBooks,
    paginatedOrders,
    paginatedTags,
    paginatedUsers,
    paginatedAuthors,
    paginatedPublishers,
    authorOptions,
    publisherOptions,
    tagOptions,
    genreOptions,
    orderStatuses: ORDER_STATUSES,
    paymentMethods: PAYMENT_METHODS,
    booksById,
    bookForm,
    orderForm,
    tagForm,
    userForm,
    authorForm,
    publisherForm,
    selectedBookCover,
    validationErrors,
    hasFieldError,
    fieldError,
    clearValidationErrors,
    openCreateDialog,
    openEditDialog,
    saveDialog,
    deleteBook,
    deleteOrder,
    deleteTag,
    deleteUser,
    deleteAuthor,
    deletePublisher,
    formatCurrency,
    orderItemsPreview,
    badgeVariantForStatus,
    setPage,
    setSelectedBookCover,
  }
}
