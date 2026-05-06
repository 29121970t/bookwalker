<script setup lang="ts">
import { computed } from "vue"
import type { Component } from "vue"
import AuthorsEntityDialog from "@/components/admin/entity-dialogs/AuthorsEntityDialog.vue"
import BooksEntityDialog from "@/components/admin/entity-dialogs/BooksEntityDialog.vue"
import OrdersEntityDialog from "@/components/admin/entity-dialogs/OrdersEntityDialog.vue"
import PublishersEntityDialog from "@/components/admin/entity-dialogs/PublishersEntityDialog.vue"
import TagsEntityDialog from "@/components/admin/entity-dialogs/TagsEntityDialog.vue"
import UsersEntityDialog from "@/components/admin/entity-dialogs/UsersEntityDialog.vue"
import { useAdminPanel, type AdminSection } from "@/composables/useAdminPanel"

const panel = useAdminPanel()

const dialogComponents: Record<AdminSection, Component> = {
  books: BooksEntityDialog,
  orders: OrdersEntityDialog,
  tags: TagsEntityDialog,
  users: UsersEntityDialog,
  authors: AuthorsEntityDialog,
  publishers: PublishersEntityDialog,
}

const currentDialog = computed(() => dialogComponents[panel.dialogSection.value])
</script>

<template>
  <component :is="currentDialog" />
</template>
