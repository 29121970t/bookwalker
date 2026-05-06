export interface AuthorRef {
  id: number
  name: string
  bio?: string
  country?: string
  website?: string
}

export interface PublisherRef {
  id: number
  name: string
  description?: string
  country?: string
  website?: string
}

export interface TagRef {
  id: number
  name: string
  description?: string
  color?: string
  featured?: boolean
  usageCount?: number
}

export interface GenreRef {
  id: number
  name: string
  description?: string
}

export interface Book {
  id: number
  title: string
  authors?: AuthorRef[]
  authorIds?: number[]
  author: string
  price: number
  originalPrice?: number
  rating?: number
  reviews?: number
  genreId?: number
  genre: string
  format: string
  pages: number
  year: number
  publishDate?: string
  publishers?: PublisherRef[]
  publisherIds?: number[]
  publisher: string
  blurb: string
  description: string
  longDescription: string
  tags: string[]
  tagIds?: number[]
  coverUrl?: string | null
  palette: {
    from: string
    via: string
    to: string
  }
  popular?: boolean
  featured?: boolean
  newArrival?: boolean
}

export interface Order {
  id: string
  date: string
  status: string
  total: number
  customerName?: string
  customerEmail?: string
  paymentMethod?: string
  deliveryCity?: string
  items: Array<{
    bookId: number
    quantity: number
  }>
}
