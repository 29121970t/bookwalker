<script setup lang="ts">
import { computed } from "vue"
import { Minus, Plus, Trash2 } from "lucide-vue-next"
import { RouterLink, useRouter } from "vue-router"
import { toast } from "vue-sonner"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import BookCover from "@/components/BookCover.vue"
import SectionHeading from "@/components/SectionHeading.vue"
import { useBookstore } from "@/stores/bookstore"

const {
  cartItems,
  subtotal,
  deliveryFee,
  discount,
  total,
  incrementQuantity,
  decrementQuantity,
  removeFromCart,
  checkout,
} = useBookstore()

const hasItems = computed(() => cartItems.value.length > 0)
const router = useRouter()

async function handleCheckout() {
  try {
    await checkout()
    toast.success("Order created successfully")
    await router.push({ name: "account" })
  } catch (error) {
    toast.error(error instanceof Error ? error.message : "Failed to create order")
  }
}
</script>

<template>
  <div class="space-y-8 pb-10">
    <SectionHeading
      eyebrow="Cart"
      title="Ready for purchase"
    />

    <div v-if="hasItems" class="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
      <div class="space-y-4">
        <Card
          v-for="item in cartItems"
          :key="item.book.id"
          class="overflow-hidden rounded-[2rem] border-white/70 bg-white/85 shadow-lg shadow-slate-200/60"
        >
          <CardContent class="grid gap-5 p-5 sm:grid-cols-[180px_1fr]">
            <BookCover :book="item.book" compact class="h-full" />
            <div class="flex flex-col justify-between gap-4">
              <div class="space-y-3">
                <div class="flex flex-wrap items-center gap-2">
                  <Badge variant="secondary">{{ item.book.genre }}</Badge>
                  <Badge variant="outline">{{ item.book.format }}</Badge>
                </div>
                <div>
                  <RouterLink :to="{ name: 'book', params: { id: item.book.id } }" class="text-2xl font-semibold tracking-tight hover:text-primary">
                    {{ item.book.title }}
                  </RouterLink>
                  <p class="text-sm text-slate-600">{{ item.book.author }}</p>
                </div>
                <p class="max-w-xl text-sm leading-6 text-slate-600">
                  {{ item.book.description }}
                </p>
              </div>

              <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div class="flex items-center gap-3">
                  <Button variant="outline" size="icon" class="rounded-full" @click="decrementQuantity(item.book.id)">
                    <Minus class="size-4" />
                  </Button>
                  <span class="min-w-8 text-center text-lg font-semibold">{{ item.quantity }}</span>
                  <Button variant="outline" size="icon" class="rounded-full" @click="incrementQuantity(item.book.id)">
                    <Plus class="size-4" />
                  </Button>
                </div>

                <div class="flex items-center gap-4">
                  <div class="text-right">
                    <p class="text-xl font-semibold">${{ item.lineTotal.toFixed(2) }}</p>
                    <p class="text-sm text-slate-500">${{ item.book.price.toFixed(2) }} each</p>
                  </div>
                  <Button variant="ghost" size="icon" class="rounded-full text-slate-500 hover:text-red-600" @click="removeFromCart(item.book.id)">
                    <Trash2 class="size-4" />
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <Card class="sticky top-1/5 h-fit rounded-[2rem] border-white/70 bg-white/90 shadow-lg shadow-slate-200/60">
        <CardHeader>
          <CardTitle class="text-2xl">Summary</CardTitle>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="flex items-center justify-between text-sm text-slate-600">
            <span>Subtotal</span>
            <span>${{ subtotal.toFixed(2) }}</span>
          </div>
          <div class="flex items-center justify-between text-sm text-slate-600">
            <span>Delivery</span>
            <span>{{ deliveryFee === 0 ? "Free" : `$${deliveryFee.toFixed(2)}` }}</span>
          </div>
          <div class="flex items-center justify-between text-sm text-slate-600">
            <span>Member discount</span>
            <span>- ${{ discount.toFixed(2) }}</span>
          </div>
          <Separator />
          <div class="flex items-center justify-between text-lg font-semibold">
            <span>Total</span>
            <span>${{ total.toFixed(2) }}</span>
          </div>
        </CardContent>
        <CardFooter class="flex flex-col gap-3">
          <Button class="w-full rounded-full" @click="handleCheckout">Proceed to checkout</Button>
          <RouterLink :to="{ name: 'browse' }" class="w-full">
            <Button variant="outline" class="w-full rounded-full">Continue browsing</Button>
          </RouterLink>
        </CardFooter>
      </Card>
    </div>

    <Card v-else class="rounded-[2rem] border-dashed border-slate-300 bg-white/75">
      <CardContent class="space-y-4 p-12 text-center">
        <p class="text-3xl font-semibold text-slate-950">Your cart is empty.</p>
        <p class="mx-auto max-w-lg text-slate-600">Start with the browse page or head back to the landing shelf to pick a few popular titles.</p>
        <RouterLink :to="{ name: 'browse' }">
          <Button class="rounded-full">Browse books</Button>
        </RouterLink>
      </CardContent>
    </Card>
  </div>
</template>
