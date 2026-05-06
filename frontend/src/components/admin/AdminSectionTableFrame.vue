<script setup lang="ts">
import type { AdminSection } from "@/composables/useAdminPanel";
import AdminTablePagination from "@/components/admin/AdminTablePagination.vue";

defineProps<{
  section: AdminSection;
  total: number;
}>();
</script>

<template>
  <div class="flex flex-col gap-6 h-full justify-between">
    <div class="overflow-hidden rounded-md border border-slate-200/80">
      <slot />
    </div>
    <AdminTablePagination :section="section" :total="total" />
  </div>
</template>
