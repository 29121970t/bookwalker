<script setup lang="ts">
import { computed } from "vue"
import type { Component } from "vue"
import BooksSectionTable from "@/components/admin/section-tables/BooksSectionTable.vue"
import OrdersSectionTable from "@/components/admin/section-tables/OrdersSectionTable.vue"
import TagsSectionTable from "@/components/admin/section-tables/TagsSectionTable.vue"
import UsersSectionTable from "@/components/admin/section-tables/UsersSectionTable.vue"
import AuthorsSectionTable from "@/components/admin/section-tables/AuthorsSectionTable.vue"
import PublishersSectionTable from "@/components/admin/section-tables/PublishersSectionTable.vue"
import type { AdminSection } from "@/composables/useAdminPanel"

const props = defineProps<{
  section: AdminSection
}>()

const sectionComponents: Record<AdminSection, Component> = {
  books: BooksSectionTable,
  orders: OrdersSectionTable,
  tags: TagsSectionTable,
  users: UsersSectionTable,
  authors: AuthorsSectionTable,
  publishers: PublishersSectionTable,
}

const currentTable = computed(() => sectionComponents[props.section])
</script>

<template>
  <component :is="currentTable" />
</template>
