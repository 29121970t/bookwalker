import type { Order } from "@/types/book"

export interface AdminTag {
  id: number
  name: string
  description: string
  color: string
  featured: boolean
  usageCount: number
}

export interface AdminUser {
  id: number
  name: string
  email: string
  role: "Admin" | "Client"
  status: "Active" | "Blocked"
  joinedAt: string
  ordersCount: number
  totalSpent: number
}

export interface AdminAuthor {
  id: number
  name: string
  bio: string
  country: string
  website: string
  featured: boolean
  booksCount: number
}

export interface AdminPublisher {
  id: number
  name: string
  description: string
  country: string
  website: string
  featured: boolean
  booksCount: number
}

export interface AdminOrder extends Order {
  entityId: number
  customerName: string
  customerEmail: string
  paymentMethod: string
  deliveryCity: string
}
