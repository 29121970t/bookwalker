<script setup lang="ts">
import { computed } from "vue";
import { RouterLink } from "vue-router";
import { toast } from "vue-sonner";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";
import BookCard from "@/components/BookCard.vue";
import BookCover from "@/components/BookCover.vue";
import SectionHeading from "@/components/SectionHeading.vue";
import { useBookstore } from "@/stores/bookstore";

const { books, addToCart } = useBookstore();

const featured = computed(() => books.value.filter((book) => book.featured));
const popular = computed(() => books.value.filter((book) => book.popular));

function handleAdd(bookId: number) {
  addToCart(bookId);
  toast.success("Book added to cart");
}
</script>

<template>
  <div class="space-y-14 pb-10">
    <section class="space-y-6">
      <SectionHeading
        eyebrow="Featured shelf"
        title="We recommend"
      />

      <Carousel
        class="rounded-[2rem]"
        :opts="{
          align: 'center',
          loop: true,
        }"
      >
        <CarouselContent>
          <CarouselItem
            v-for="book in featured"
            :key="book.id"
            class="md:basis-2/3"
          >
            <div
              class="h-full grid gap-6 rounded-[2rem] border border-white/70 bg-white/80 p-5 shadow-lg shadow-slate-200/60 backdrop-blur-sm lg:grid-cols-[0.8fr_1.2fr]"
            >
              <RouterLink :to="{ name: 'book', params: { id: book.id } }">
                <BookCover :book="book" class="min-h-[240px]" />
              </RouterLink>
              <div class="flex flex-col justify-between gap-6 p-1">
                <div class="space-y-4">
                  <div class="flex flex-wrap gap-2">
                    <Badge variant="secondary">{{ book.genre }}</Badge>
                    <Badge variant="outline">{{ book.format }}</Badge>
                  </div>
                  <div>
                    <h3
                      class="text-3xl font-semibold tracking-tight text-slate-950"
                    >
                      {{ book.title }}
                    </h3>
                    <p class="mt-2 text-lg text-slate-600">{{ book.author }}</p>
                  </div>
                  <p class="max-w-xl text-base leading-7 text-slate-600">
                    {{ book.longDescription }}
                  </p>
                </div>

                <div
                  class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
                >
                  <div>
                    <p class="text-3xl font-semibold text-slate-950">
                      ${{ book.price.toFixed(2) }}
                    </p>
                    <p class="text-sm text-slate-500">
                      {{ book.pages }} pages · {{ book.publisher }}
                    </p>
                  </div>
                  <div class="flex gap-3">
                    <RouterLink
                      :to="{ name: 'book', params: { id: book.id } }"
                    >
                      <Button variant="outline" class="rounded-full"
                        >Open details</Button
                      >
                    </RouterLink>
                    <Button class="rounded-full" @click="handleAdd(book.id)"
                      >Add to cart</Button
                    >
                  </div>
                </div>
              </div>
            </div>
          </CarouselItem>
        </CarouselContent>
        <CarouselPrevious class="left-4" />
        <CarouselNext class="right-4" />
      </Carousel>
    </section>

    <section class="space-y-6">
      <SectionHeading
        eyebrow="Popular picks"
        title="Readers keep coming back to these"
      />
      <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
        <BookCard
          v-for="book in popular"
          :key="book.id"
          :book="book"
          @add="handleAdd"
        />
      </div>
    </section>

    
  </div>
</template>
