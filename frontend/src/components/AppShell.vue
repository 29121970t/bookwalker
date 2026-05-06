<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { Menu, Search, ShoppingBag, UserRound } from "lucide-vue-next";
import { RouterLink, useRoute, useRouter } from "vue-router";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { Separator } from "@/components/ui/separator";
import { useBookstore } from "@/stores/bookstore";
import { useAuthStore } from "@/stores/auth";
import { InputGroup, InputGroupAddon, InputGroupInput } from "./ui/input-group";

const route = useRoute();
const router = useRouter();
const { cartCount } = useBookstore();
const auth = useAuthStore();
const searchQuery = ref("");

const navigation = [
  { name: "Home", to: { name: "home" } },
  { name: "Browse", to: { name: "browse" } },
  // { name: "Cart", to: { name: "cart" } },
  { name: "Account", to: { name: "account" } },
];

const routeName = computed(() => route.name);

watch(
  () => route.query.q,
  (value) => {
    searchQuery.value = typeof value === "string" ? value : "";
  },
  { immediate: true },
);

async function submitSearch() {
  const normalized = searchQuery.value.trim();
  await router.push({
    name: "browse",
    query: normalized ? { q: normalized } : {},
  });
}
</script>

<template>
  <div
    class="min-h-screen w-full bg-[linear-gradient(180deg,#fffdf8_0%,#f7f8fc_35%,#eef4ff_100%)] text-slate-900"
  >
    <header
      class="sticky top-0 z-40 border-b border-white/60 bg-white/75 backdrop-blur-xl"
    >
      <div
        class="mx-auto flex max-w-7xl items-center gap-4 px-4 py-4 sm:px-6 lg:px-8"
      >
        <RouterLink :to="{ name: 'home' }" class="shrink-0">
          <div class="flex items-center gap-3">
            <div
              class="grid size-11 place-items-center rounded-2xl bg-slate-950 text-sm font-semibold text-white shadow-lg shadow-slate-300"
            >
              BW
            </div>
            <div>
              <p class="text-lg font-semibold tracking-tight">Bookwalker</p>
            </div>
          </div>
        </RouterLink>

        <nav class="hidden flex-1 items-center justify-center gap-2 md:flex">
          <RouterLink
            v-for="item in navigation"
            :key="item.name"
            :to="item.to"
            class="rounded-full px-4 py-2 text-sm font-medium transition-colors hidden lg:flex"
            :class="
              routeName === item.to.name
                ? 'bg-slate-900 text-white'
                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-950'
            "
          >
            {{ item.name }}
          </RouterLink>
        </nav>

        <div class="hidden items-center gap-3 md:flex">
          <form class="w-60" @submit.prevent="submitSearch">
            <InputGroup class="hidden lg:flex">
              <InputGroupInput v-model="searchQuery" placeholder="Search..." />
              <InputGroupAddon>
                <button type="submit" class="flex items-center">
                  <Search />
                </button>
              </InputGroupAddon>
            </InputGroup>
          </form>
          <RouterLink :to="{ name: 'cart' }">
            <Button variant="outline" class="relative">
              <ShoppingBag class="size-4" />
              Cart
              <Badge class="rounded-full px-1.5">{{ cartCount }}</Badge>
            </Button>
          </RouterLink>
          <RouterLink :to="{ name: 'account' }" class="hidden lg:flex">
            <Button variant="ghost" size="icon" class="rounded-full">
              <UserRound class="size-5" />
            </Button>
          </RouterLink>
          <RouterLink v-if="auth.isAdmin.value" :to="{ name: 'admin-books' }">
            <Button variant="outline">Admin</Button>
          </RouterLink>
          <RouterLink v-if="!auth.isAuthenticated.value" :to="{ name: 'login' }">
            <Button>Login</Button>
          </RouterLink>
          <Button v-else variant="outline" @click="auth.logout()">Logout</Button>
        </div>

        <div class="ml-auto lg:hidden">
          <Sheet>
            <SheetTrigger as-child>
              <Button variant="outline" size="icon" class="rounded-full">
                <Menu class="size-5" />
              </Button>
            </SheetTrigger>
            <SheetContent
              side="right"
              class="w-[320px] border-l-white/60 bg-white/95"
            >
              <div class="p-6">
                <form class="pb-6 w-5/6" @submit.prevent="submitSearch">
                  <InputGroup>
                    <InputGroupInput v-model="searchQuery" placeholder="Search..." />
                    <InputGroupAddon>
                      <button type="submit" class="flex items-center">
                        <Search />
                      </button>
                    </InputGroupAddon>
                  </InputGroup>
                </form>

                <Separator />
                <div class="grid gap-2">
                  <RouterLink
                    v-for="item in navigation"
                    :key="item.name"
                    :to="item.to"
                    class="rounded-2xl border border-transparent px-4 py-3 text-sm font-medium text-slate-700 transition-colors hover:border-slate-200 hover:bg-slate-50"
                  >
                    {{ item.name }}
                  </RouterLink>
                </div>
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </header>

    <main class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <slot />
    </main>

    <footer>
      <div
        class="mx-auto max-w-7xl px-4 py-8 text-center text-sm text-slate-500 sm:px-6 lg:px-8"
      >
        &copy; 2026 Bookwalker. All rights reserved.
      </div>
    </footer>
  </div>
</template>
