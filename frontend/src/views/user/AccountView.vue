<script setup lang="ts">
import { computed } from "vue";
import { Mail } from "lucide-vue-next";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import BookCard from "@/components/BookCard.vue";
import SectionHeading from "@/components/SectionHeading.vue";
import { useBookstore } from "@/stores/bookstore";
import HoverCard from "@/components/ui/hover-card/HoverCard.vue";
import { HoverCardTrigger } from "@/components/ui/hover-card";
import HoverCardContent from "@/components/ui/hover-card/HoverCardContent.vue";
import BookCover from "@/components/BookCover.vue";
import type { Book } from "@/types/book";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/auth";

const { account, orders, wishlist, addToCart, getBookById } = useBookstore();
const auth = useAuthStore();

const initials = computed(() =>
  account.name
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2),
);

function getPreviewBook(bookId: number): Book | null {
  return getBookById(bookId) ?? null;
}
</script>

<template>
  <div class="space-y-8 pb-10">
    <SectionHeading eyebrow="Account" title="Hi There" />

    <Card
      class="overflow-hidden rounded-[2rem] border-white/70 bg-white/90 shadow-lg shadow-slate-200/60"
    >
      <CardContent
        class="grid gap-6 p-6 lg:grid-cols-[auto_1fr] lg:items-center"
      >
        <Avatar class="size-24 rounded-3xl bg-slate-950 text-white">
          <AvatarFallback
            class="rounded-3xl bg-slate-950 text-2xl font-semibold text-white"
          >
            {{ initials }}
          </AvatarFallback>
        </Avatar>

        <div class="space-y-3">
          <div class="flex flex-wrap items-center gap-3">
            <h2 class="text-3xl font-semibold tracking-tight text-slate-950">
              {{ account.name }}
            </h2>
          </div>
          <div class="flex flex-wrap gap-4 text-sm text-slate-500">
            <span class="inline-flex items-center gap-2"
              ><Mail class="size-4" /> {{ account.email }}</span
            >
          </div>
          <Button variant="outline" class="rounded-full" @click="auth.logout()">Logout</Button>
        </div>
      </CardContent>
    </Card>

    <Tabs default-value="orders" class="space-y-6">
      <TabsList class="h-auto flex-wrap rounded-3xl bg-white/80 p-1 shadow-sm">
        <TabsTrigger value="orders" class="rounded-full px-5 py-2"
          >Orders</TabsTrigger
        >
        <TabsTrigger value="wishlist" class="rounded-full px-5 py-2"
          >Wishlist</TabsTrigger
        >
      </TabsList>

      <TabsContent value="orders" class="space-y-4">
        <Card
          v-for="order in orders"
          :key="order.id"
          class="rounded-[2rem] border-white/70 bg-white/85 shadow-lg shadow-slate-200/60"
        >
          <CardHeader class="flex flex-row items-center justify-between gap-4">
            <div>
              <CardTitle class="text-xl">{{ order.id }}</CardTitle>
              <p class="text-sm text-slate-500">{{ order.date }}</p>
            </div>
            <div class="text-right">
              <Badge variant="secondary">{{ order.status }}</Badge>
              <p class="mt-2 text-lg font-semibold">
                ${{ order.total.toFixed(2) }}
              </p>
            </div>
          </CardHeader>
          <CardContent class="space-y-3 pt-0 grid gap-4">
            <HoverCard
              v-for="item in order.items"
              :key="`${order.id}-${item.bookId}`"
              class=""
            >
              <HoverCardTrigger>
                <div
                  class="flex items-center justify-between rounded-2xl bg-slate-50 px-4 py-3 text-sm"
                >
                  <span>{{ getBookById(item.bookId)?.title }}</span>
                  <span>x{{ item.quantity }}</span>
                </div>
              </HoverCardTrigger>
              <HoverCardContent  class="h-400px bg-transparent shadow-none border-0">
                <BookCover v-if="getPreviewBook(item.bookId)" :book="getPreviewBook(item.bookId)!" class="top-full relative"/>
              </HoverCardContent>
            </HoverCard>
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="wishlist" class="space-y-4">
        <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
          <BookCard
            v-for="book in wishlist"
            :key="book.id"
            :book="book"
            @add="addToCart"
          />
        </div>
      </TabsContent>
    </Tabs>
  </div>
</template>
