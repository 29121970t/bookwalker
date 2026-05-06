<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { Search } from "lucide-vue-next";
import { useRoute, useRouter } from "vue-router";
import { toast } from "vue-sonner";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import BookCard from "@/components/BookCard.vue";
import SectionHeading from "@/components/SectionHeading.vue";
import { useBookstore } from "@/stores/bookstore";
import {
  InputGroup,
  InputGroupInput,
  InputGroupAddon,
} from "@/components/ui/input-group";
import TagSelect from "@/components/TagSelect.vue";
import AuthorSelect from "@/components/AuthorSelect.vue";
import PriceRangeInput from "@/components/PriceRangeInput.vue";
import PublisherSelect from "@/components/PublisherSelect.vue";
import Label from "@/components/ui/label/Label.vue";
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";

const { books, genres, addToCart } = useBookstore();
const route = useRoute();
const router = useRouter();

const ITEMS_PER_PAGE = 9;

const query = ref(typeof route.query.q === "string" ? route.query.q : "");
const selectedGenre = ref("All");
const selectedTagIds = ref<number[]>([]);
const selectedAuthor = ref("");
const selectedPublisher = ref("");
const minPrice = ref<number | null>(null);
const maxPrice = ref<number | null>(null);
const currentPage = ref(1);

const tagOptions = computed(() => {
  const unique = new Map<number, string>();
  books.value.forEach((book) => {
    (book.tagIds ?? []).forEach((tagId, index) => {
      unique.set(tagId, book.tags[index] ?? `Tag ${tagId}`);
    });
  });

  return [...unique.entries()]
    .map(([id, label]) => ({ id, label }))
    .sort((left, right) => left.label.localeCompare(right.label));
});

const authorOptions = computed(() => {
  const unique = new Map<string, string>();
  books.value.forEach((book) => {
    (book.authors ?? []).forEach((author) => {
      unique.set(author.name, author.name);
    });
  });

  return [...unique.entries()]
    .map(([value, label]) => ({ value, label }))
    .sort((left, right) => left.label.localeCompare(right.label));
});

const publisherOptions = computed(() => {
  const unique = new Map<string, string>();
  books.value.forEach((book) => {
    (book.publishers ?? []).forEach((publisher) => {
      unique.set(publisher.name, publisher.name);
    });
  });

  return [...unique.entries()]
    .map(([value, label]) => ({ value, label }))
    .sort((left, right) => left.label.localeCompare(right.label));
});

const filteredBooks = computed(() =>
  books.value.filter((book) => {
    const normalized = query.value.trim().toLowerCase();
    const matchesSearch =
      normalized.length === 0 ||
      book.title.toLowerCase().includes(normalized) ||
      book.author.toLowerCase().includes(normalized) ||
      book.publisher.toLowerCase().includes(normalized) ||
      book.tags.some((tag) => tag.toLowerCase().includes(normalized));

    const matchesGenre =
      selectedGenre.value === "All" || book.genre === selectedGenre.value;

    const matchesTags =
      selectedTagIds.value.length === 0 ||
      selectedTagIds.value.every((tagId) =>
        (book.tagIds ?? []).includes(tagId),
      );

    const matchesAuthor =
      selectedAuthor.value.length === 0 ||
      (book.authors ?? []).some(
        (author) => author.name === selectedAuthor.value,
      );

    const matchesPublisher =
      selectedPublisher.value.length === 0 ||
      (book.publishers ?? []).some(
        (publisher) => publisher.name === selectedPublisher.value,
      );

    const priceToCheck = book.price;
    const matchesMinPrice =
      minPrice.value == null || priceToCheck >= minPrice.value;
    const matchesMaxPrice =
      maxPrice.value == null || priceToCheck <= maxPrice.value;

    return (
      matchesSearch &&
      matchesGenre &&
      matchesTags &&
      matchesAuthor &&
      matchesPublisher &&
      matchesMinPrice &&
      matchesMaxPrice
    );
  }),
);

const pageCount = computed(() =>
  Math.max(1, Math.ceil(filteredBooks.value.length / ITEMS_PER_PAGE)),
);

const paginatedBooks = computed(() => {
  const start = (currentPage.value - 1) * ITEMS_PER_PAGE;
  return filteredBooks.value.slice(start, start + ITEMS_PER_PAGE);
});

const pageStartItem = computed(() => {
  if (filteredBooks.value.length === 0) {
    return 0;
  }

  return (currentPage.value - 1) * ITEMS_PER_PAGE + 1;
});

const pageEndItem = computed(() =>
  Math.min(currentPage.value * ITEMS_PER_PAGE, filteredBooks.value.length),
);

watch(
  [
    query,
    selectedGenre,
    selectedTagIds,
    selectedAuthor,
    selectedPublisher,
    minPrice,
    maxPrice,
  ],
  () => {
    currentPage.value = 1;
  },
  { deep: true },
);

watch(
  () => route.query.q,
  (value) => {
    query.value = typeof value === "string" ? value : "";
  },
);

watch(query, async (value) => {
  const normalized = value.trim();
  const current = typeof route.query.q === "string" ? route.query.q : "";

  if (normalized === current) {
    return;
  }

  await router.replace({
    name: "browse",
    query: {
      ...route.query,
      ...(normalized ? { q: normalized } : {}),
    },
  });
});

watch(pageCount, (value) => {
  currentPage.value = Math.min(currentPage.value, value);
});

function resetFilters() {
  query.value = "";
  selectedGenre.value = "All";
  selectedTagIds.value = [];
  selectedAuthor.value = "";
  selectedPublisher.value = "";
  minPrice.value = null;
  maxPrice.value = null;
  currentPage.value = 1;
}

function handleAdd(bookId: number) {
  addToCart(bookId);
  toast.success("Book added to cart");
}
</script>

<template>
  <div class="space-y-8 pb-10 m-h-[120vh]">
    <SectionHeading eyebrow="Browse" title="Search the catalog" />
    <div class="grid gap-5 lg:grid-cols-[0.3fr_1fr]">
      <div>
        <Card
          class="rounded-[2rem] border-white/70 bg-white/85 shadow-lg shadow-slate-200/60 w-[inherit] lg:sticky lg:top-1/6 py-0"
        >
          <CardContent class="space-y-5 p-5 sm:p-6">
            <Label for="search">Search</Label>
            <div>
              <InputGroup>
                <InputGroupInput
                  v-model="query"
                  placeholder="Search..."
                  id="search"
                />
                <InputGroupAddon>
                  <Search />
                </InputGroupAddon>
              </InputGroup>
            </div>
            <Label for="genre">Genre</Label>
            <select
              v-model="selectedGenre"
              id="genre"
              class="border-input bg-background w-full rounded-md border px-3 py-2 text-sm"
            >
              <option v-for="genre in genres" :key="genre" :value="genre">
                {{ genre }}
              </option>
            </select>
            <Label for="tags">Tags</Label>
            <TagSelect id="tags" v-model="selectedTagIds" :tags="tagOptions" />
            <Label for="author">Author</Label>
            <AuthorSelect
              id="author"
              v-model="selectedAuthor"
              :authors="authorOptions"
            />
            <Label for="price">Price</Label>
            <PriceRangeInput
              id="price"
              v-model:min="minPrice"
              v-model:max="maxPrice"
            />
            <Label for="publisher">Publisher</Label>
            <PublisherSelect
              id="publisher"
              v-model="selectedPublisher"
              :publishers="publisherOptions"
            />
            <div>
              <Button variant="outline" class="w-full" @click="resetFilters"
                >Reset filters</Button
              >
            </div>
          </CardContent>
        </Card>
      </div>
      <div class="space-y-5">
        <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
          <BookCard
            v-for="book in paginatedBooks"
            :key="book.id"
            :book="book"
            @add="handleAdd"
          />
          <Card
            v-if="filteredBooks.length === 0"
            class="rounded-[2rem] border-dashed border-slate-300 bg-white/70 col-span-full"
          >
            <CardContent class="space-y-3 p-10 text-center">
              <p class="text-2xl font-semibold text-slate-950">
                Nothing matched this shelf.
              </p>
              <p class="mx-auto max-w-lg text-slate-600">
                Try another genre or a broader search term like “fantasy”,
                “Ishiguro”, or “cozy”.
              </p>
              <Button
                variant="outline"
                class="mx-auto rounded-full"
                @click="resetFilters"
              >
                Reset filters
              </Button>
            </CardContent>
          </Card>
        </div>

        <div
          v-if="filteredBooks.length > 0"
          class="flex flex-col gap-3 px-1 py-2 sm:flex-row sm:items-center sm:justify-between"
        >
          <p class="text-sm text-slate-500">
            Showing {{ pageStartItem }}-{{ pageEndItem }} of
            {{ filteredBooks.length }}
          </p>

          <Pagination
            :page="currentPage"
            :items-per-page="ITEMS_PER_PAGE"
            :total="filteredBooks.length"
            :sibling-count="1"
            show-edges
            @update:page="currentPage = $event"
          >
            <PaginationContent v-slot="{ items }">
              <PaginationPrevious />

              <template
                v-for="(item, index) in items"
                :key="`${item.type}-${index}`"
              >
                <PaginationItem
                  v-if="item.type === 'page'"
                  :value="item.value"
                  :is-active="item.value === currentPage"
                >
                  {{ item.value }}
                </PaginationItem>
                <PaginationEllipsis v-else :index="index" />
              </template>

              <PaginationNext />
            </PaginationContent>
          </Pagination>
        </div>
      </div>
    </div>
  </div>
</template>
