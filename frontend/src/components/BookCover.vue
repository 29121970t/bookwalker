<script setup lang="ts">
import { computed } from "vue"
import { cn } from "@/lib/utils"
import type { Book } from "@/types/book"

interface Props {
  book: Book
  compact?: boolean
  class?: string
}

const props = withDefaults(defineProps<Props>(), {
  compact: false,
})

const coverStyle = computed(() => ({
  backgroundImage: `linear-gradient(140deg, ${props.book.palette.from}, ${props.book.palette.via}, ${props.book.palette.to})`,
}))
</script>

<template>
  <div
    :class="cn(
      'relative overflow-hidden rounded-2xl text-white shadow-lg shadow-black/10',
      props.compact ? 'aspect-[3/4]' : 'aspect-[4/5]',
      props.class,
    )"
    :style="coverStyle"
  >
    <img
      v-if="props.book.coverUrl"
      :src="props.book.coverUrl"
      :alt="props.book.title"
      class="absolute inset-0 h-full w-full object-cover"
    >
    <div v-if="!props.book.coverUrl" class="absolute inset-0 bg-[radial-gradient(circle_at_top_right,rgba(255,255,255,0.35),transparent_28%),radial-gradient(circle_at_bottom_left,rgba(255,255,255,0.15),transparent_30%)]" />
    <div :class="props.book.coverUrl ? 'absolute inset-0 bg-gradient-to-t from-black/60 via-black/15 to-black/10' : 'absolute inset-0 bg-black/12'" />
    <div v-if="!props.book.coverUrl" class="relative flex h-full flex-col justify-between p-5">
      <div class="flex items-center justify-between text-[10px] font-semibold uppercase tracking-[0.35em] text-white/75">
        <span>Bookwalker</span>
        <span>{{ book.year }}</span>
      </div>
      <div class="space-y-2">
        <p class="max-w-[10ch] text-2xl leading-none font-semibold text-balance">
          {{ book.title }}
        </p>
        <p class="text-sm text-white/80">
          {{ book.author }}
        </p>
      </div>
    </div>
  </div>
</template>
