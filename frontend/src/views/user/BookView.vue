<script setup lang="ts">
import { computed } from "vue";
import { useRoute, RouterLink } from "vue-router";
import { Tags } from "lucide-vue-next";
import { toast } from "vue-sonner";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import BookCard from "@/components/BookCard.vue";
import BookCover from "@/components/BookCover.vue";
import SectionHeading from "@/components/SectionHeading.vue";
import { useBookstore } from "@/stores/bookstore";

const route = useRoute();
const { books, addToCart, getBookById, quantityFor } = useBookstore();

const book = computed(() => getBookById(Number(route.params.id)));

const relatedBooks = computed(() => {
  const currentBook = book.value;

  if (!currentBook) {
    return [];
  }

  return books
    .value
    .filter(
      (item) => item.id !== currentBook.id && item.genre === currentBook.genre,
    )
    .slice(0, 3);
});

function handleAdd(bookId: number) {
  addToCart(bookId);
  toast.success("Book added to cart");
}
</script>

<template>
  <div v-if="book" class="space-y-10 pb-10">
    <section
      class="grid gap-8 lg:grid-cols-[0.9fr_1.1fr] lg:grid-rows-2 lg:items-start md:items-center"
    >
      <BookCover
        :book="book"
        class="md:max-h-[600px] rounded-[2rem] lg:row-span-2 md:min-w-1/2 md:justify-self-center lg:justify-self-auto"
      />

      <div class="space-y-4">
        <div class="flex flex-wrap gap-2">
          <Badge variant="secondary">{{ book.genre }}</Badge>
          <Badge variant="outline">{{ book.format }}</Badge>
          <Badge v-if="book.popular" variant="outline">Popular</Badge>
        </div>
        <div>
          <h1
            class="text-4xl font-semibold tracking-tight text-balance text-slate-950 sm:text-5xl"
          >
            {{ book.title }}
          </h1>
          <p class="mt-3 text-xl text-slate-600">{{ book.author }}</p>
          <p class="mt-2 text-sm text-slate-600">
            {{ book.pages }} pages • published by {{ book.publisher }}
          </p>
        </div>
        <p class="max-w-3xl text-base leading-7 text-slate-600">
          {{ book.longDescription }}
        </p>
      </div>

      <Card
        class="rounded-[2rem] border-white/70 bg-white/90 shadow-lg shadow-slate-200/60 self-end"
      >
        <CardContent class="space-y-5 p-6">
          <div class="flex flex-wrap items-center gap-3">
            <p class="text-3xl font-semibold text-slate-950">
              ${{ book.price.toFixed(2) }}
            </p>
            <p
              v-if="book.originalPrice"
              class="text-lg text-slate-400 line-through"
            >
              ${{ book.originalPrice.toFixed(2) }}
            </p>
            <Badge class="rounded-full px-3 py-1"
              >In cart: {{ quantityFor(book.id) }}</Badge
            >
          </div>
          <p class="text-slate-600">{{ book.blurb }}</p>
          <div class="flex flex-wrap gap-2">
            <Badge v-for="tag in book.tags" :key="tag" variant="outline">
              <Tags class="mr-1 size-3" />
              {{ tag }}
            </Badge>
          </div>
          <div class="flex flex-col gap-3 sm:flex-row">
            <Button class="rounded-full" size="lg" @click="handleAdd(book.id)"
              >Add to cart</Button
            >
            <RouterLink :to="{ name: 'browse' }">
              <Button
                variant="outline"
                size="lg"
                class="w-full rounded-full sm:w-auto"
                >Back to browse</Button
              >
            </RouterLink>
          </div>
        </CardContent>
      </Card>
    </section>

    <section class="space-y-6">
      <SectionHeading
        eyebrow="Related"
        title="More from the same shelf"
        description="Recommendations based on genre."
      />
      <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
        <BookCard
          v-for="item in relatedBooks"
          :key="item.id"
          :book="item"
          @add="handleAdd"
        />
      </div>
    </section>
  </div>

  <Card
    v-else
    class="rounded-[2rem] border-dashed border-slate-300 bg-white/75"
  >
    <CardContent class="space-y-4 p-12 text-center">
      <p class="text-3xl font-semibold text-slate-950">Book not found.</p>
      <p class="mx-auto max-w-lg text-slate-600">
        This route exists, but the requested book ID is missing from the API
        catalog.
      </p>
      <RouterLink :to="{ name: 'browse' }">
        <Button class="rounded-full">Return to browse</Button>
      </RouterLink>
    </CardContent>
  </Card>
</template>
