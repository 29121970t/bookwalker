import { useLocalStorage } from "@vueuse/core"
import { computed, reactive, ref } from "vue"
import { apiRequest } from "@/lib/api"
import { useAdminStore } from "@/stores/admin"
import { useAuthStore } from "@/stores/auth"
import type { Book, Order } from "@/types/book"

const adminStore = useAdminStore()
const authStore = useAuthStore()

const cart = useLocalStorage<Record<number, number>>("bookwalker-cart", {})
const orders = ref<Order[]>([])
const wishlistIds = [1]
let initPromise: Promise<void> | null = null

const booksById = computed(() => new Map(adminStore.books.value.map((book) => [book.id, book])))

const wishlist = computed(() =>
  wishlistIds
    .map((id) => booksById.value.get(id))
    .filter((book): book is Book => Boolean(book)),
)

const cartItems = computed(() =>
  Object.entries(cart.value)
    .map(([id, quantity]) => {
      const book = booksById.value.get(Number(id))
      if (!book || quantity <= 0) {
        return null
      }

      return {
        book,
        quantity,
        lineTotal: book.price * quantity,
      }
    })
    .filter((item): item is { book: Book, quantity: number, lineTotal: number } => Boolean(item)),
)

const cartCount = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))
const subtotal = computed(() => Number(cartItems.value.reduce((sum, item) => sum + item.lineTotal, 0).toFixed(2)))
const deliveryFee = computed(() => (subtotal.value >= 45 || subtotal.value === 0 ? 0 : 4.99))
const discount = computed(() => (subtotal.value >= 60 ? 8 : 0))
const total = computed(() => Number((subtotal.value + deliveryFee.value - discount.value).toFixed(2)))
const account = reactive({
  name: "",
  email: "",
  role: "",
})

function syncAccount() {
  account.name = authStore.user.value?.name ?? ""
  account.email = authStore.user.value?.email ?? ""
  account.role = authStore.user.value?.role ?? ""
}

async function loadOrders() {
  if (!authStore.isAuthenticated.value) {
    orders.value = []
    syncAccount()
    return
  }

  const data = await apiRequest<Array<{
    orderCode: string
    date: string
    status: string
    total: number
    paymentMethod?: string
    deliveryCity?: string
    items: Array<{ book: { id: number }, quantity: number }>
  }>>("/orders/me")

  orders.value = data.map((order) => ({
    id: order.orderCode,
    date: order.date,
    status: order.status,
    total: Number(order.total),
    paymentMethod: order.paymentMethod,
    deliveryCity: order.deliveryCity,
    items: order.items.map((item) => ({
      bookId: item.book.id,
      quantity: item.quantity,
    })),
  }))
  syncAccount()
}

async function initialize() {
  if (initPromise) {
    return initPromise
  }
  initPromise = (async () => {
    await adminStore.loadAll()
    await authStore.restoreSession()
    await loadOrders()
  })()
  try {
    await initPromise
  } finally {
    initPromise = null
  }
}

function addToCart(bookId: number) {
  cart.value = {
    ...cart.value,
    [bookId]: (cart.value[bookId] ?? 0) + 1,
  }
}

function setQuantity(bookId: number, quantity: number) {
  const next = { ...cart.value }
  if (quantity <= 0) {
    delete next[bookId]
  } else {
    next[bookId] = quantity
  }
  cart.value = next
}

function incrementQuantity(bookId: number) {
  setQuantity(bookId, (cart.value[bookId] ?? 0) + 1)
}

function decrementQuantity(bookId: number) {
  setQuantity(bookId, (cart.value[bookId] ?? 0) - 1)
}

function removeFromCart(bookId: number) {
  setQuantity(bookId, 0)
}

function clearCart() {
  cart.value = {}
}

function getBookById(id: number) {
  return adminStore.books.value.find((book) => book.id === id)
}

function quantityFor(bookId: number) {
  return cart.value[bookId] ?? 0
}

async function checkout() {
  if (!authStore.isAuthenticated.value || !authStore.user.value) {
    throw new Error("Please log in before checking out.")
  }
  if (cartItems.value.length === 0) {
    throw new Error("Your cart is empty.")
  }

  await apiRequest("/orders", {
    method: "POST",
    body: JSON.stringify({
      clientId: authStore.user.value.id,
      status: "Processing",
      paymentMethod: "CARD",
      deliveryCity: "Not specified",
      items: cartItems.value.map((item) => ({
        bookId: item.book.id,
        quantity: item.quantity,
      })),
    }),
  })

  clearCart()
  await loadOrders()
}

export function useBookstore() {
  void initialize()

  return {
    account,
    books: adminStore.books,
    genres: adminStore.genres,
    orders,
    wishlist,
    cartItems,
    cartCount,
    subtotal,
    deliveryFee,
    discount,
    total,
    addToCart,
    setQuantity,
    incrementQuantity,
    decrementQuantity,
    removeFromCart,
    clearCart,
    checkout,
    quantityFor,
    getBookById,
    refreshAccountData: loadOrders,
    initialize,
  }
}
