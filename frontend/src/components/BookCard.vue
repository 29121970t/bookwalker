<script setup lang="ts">
import { ShoppingBag } from "lucide-vue-next"
import { RouterLink } from "vue-router"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Card,
  CardContent,
  CardFooter,
} from "@/components/ui/card"
import BookCover from "@/components/BookCover.vue"
import type { Book } from "@/types/book"

defineProps<{
  book: Book
}>()

const emit = defineEmits<{
  add: [bookId: number]
}>()
</script>

<template>
  <Card class="py-0 group overflow-hidden rounded-3xl border-white/60 bg-white/90 shadow-lg shadow-slate-200/60 backdrop-blur-sm transition-transform duration-300 hover:-translate-y-1">
    <CardContent class="space-y-4 p-4">
      <RouterLink :to="{ name: 'book', params: { id: book.id } }" class="block">
        <BookCover :book="book" compact class="transition-transform duration-300 group-hover:scale-[1.01]" />
      </RouterLink>

      <div class="space-y-3">
        <div class="flex flex-wrap gap-2">
          <Badge variant="secondary">{{ book.genre }}</Badge>
          <Badge v-if="book.featured" variant="outline">Featured</Badge>
        </div>

        <div class="space-y-1">
          <RouterLink
            :to="{ name: 'book', params: { id: book.id } }"
            class="line-clamp-1 text-lg font-semibold tracking-tight hover:text-primary"
          >
            {{ book.title }}
          </RouterLink>
          <p class="text-sm text-muted-foreground">
            {{ book.author }}
          </p>
          <p class="line-clamp-2 text-sm text-muted-foreground">
            {{ book.description }}
          </p>
        </div>

        <div class="flex items-center justify-between text-sm">
          <!-- <div class="flex items-center gap-1 text-amber-500">
            <Star class="size-4 fill-current" />
            <span class="font-medium text-foreground">{{ book.rating }}</span>
            <span class="text-muted-foreground">({{ book.reviews }})</span>
          </div> -->
          <span class="rounded-full bg-muted px-2.5 py-1 text-xs text-muted-foreground">
            {{ book.format }}
          </span>
        </div>
      </div>
    </CardContent>

    <CardFooter class="flex items-center justify-between gap-3 border-t border-border/60 bg-slate-50/70 p-4">
      <div class="space-y-0.5">
        <p class="text-lg font-semibold">
          ${{ book.price.toFixed(2) }}
        </p>
        <p :class="[{'opacity-0': !book.originalPrice}, 'text-sm text-muted-foreground line-through']">
          ${{book.originalPrice ? book.originalPrice.toFixed(2) : 0}}
        </p>
      </div>

      <Button class="rounded-full" @click="emit('add', book.id)">
        <ShoppingBag class="size-4" />
        Add
      </Button>
    </CardFooter>
  </Card>
</template>
