<script setup lang="ts">
import { computed } from "vue"
import { RouterView, useRoute } from "vue-router"
import AdminSidebar from "@/components/AdminSidebar.vue"
import AdminEntityDialog from "@/components/admin/AdminEntityDialog.vue"
import { useAdminPanel, type AdminSection } from "@/composables/useAdminPanel"
import { SidebarProvider } from "@/components/ui/sidebar"

const route = useRoute()
const panel = useAdminPanel()

const activeSection = computed(
  () => ((route.meta.adminSection as AdminSection | undefined) ?? "books"),
)
</script>

<template>
  <SidebarProvider :open="true">
    <AdminSidebar :active-section="activeSection" :counts="panel.counts.value" />

      <div class="min-h-screen  p-4 md:p-6 w-full">
        <div class="mx-auto flex max-w-7xl flex-col gap-6 ">
          <RouterView />
        </div>
      </div>

    <AdminEntityDialog />
  </SidebarProvider>
</template>
