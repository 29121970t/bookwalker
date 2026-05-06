<script setup lang="ts">
import { computed } from "vue"
import type { AdminSection } from "@/composables/useAdminPanel"
import { useAdminPanel } from "@/composables/useAdminPanel"
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"

const props = defineProps<{
  section: AdminSection
  total: number
}>()

const panel = useAdminPanel()

const currentPage = computed(() => panel.currentPage[props.section])
const startItem = computed(() => {
  if (props.total === 0) return 0
  return (currentPage.value - 1) * panel.itemsPerPage + 1
})
const endItem = computed(() => Math.min(currentPage.value * panel.itemsPerPage, props.total))
</script>

<template>
  <div
    v-if="total > 0"
    class="flex flex-col gap-3 px-4 py-4 sm:flex-row sm:items-center sm:justify-between align-baseline"
  >
    <p class="text-sm text-slate-500 self-start">
      Showing {{ startItem }}-{{ endItem }} of {{ total }}
    </p>

    <Pagination
      :page="currentPage"
      :items-per-page="panel.itemsPerPage"
      :total="total"
      :sibling-count="1"
      show-edges
      @update:page="panel.setPage(section, $event)"
      class="w-fit"
    >
      <PaginationContent v-slot="{ items }">
        <PaginationPrevious />

        <template v-for="(item, index) in items" :key="`${item.type}-${index}`">
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
</template>
