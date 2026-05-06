<script setup lang="ts">
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { useAdminPanel } from "@/composables/useAdminPanel"
import AdminDialogFrame from "./AdminDialogFrame.vue"

const panel = useAdminPanel()
</script>

<template>
  <AdminDialogFrame>
    <div class="grid gap-4 md:grid-cols-2">
      <div class="space-y-2"><label class="text-sm font-medium">Publisher name</label><Input v-model="panel.publisherForm.name" required maxlength="255" :aria-invalid="panel.hasFieldError('publisher.name')" /><p v-if="panel.hasFieldError('publisher.name')" class="text-sm text-red-600">{{ panel.fieldError('publisher.name') }}</p></div>
      <div class="space-y-2"><label class="text-sm font-medium">Country</label><Input v-model="panel.publisherForm.country" maxlength="120" /></div>
      <div class="space-y-2 md:col-span-2"><label class="text-sm font-medium">Website</label><Input v-model="panel.publisherForm.website" type="url" placeholder="https://publisher.example" maxlength="255" :aria-invalid="panel.hasFieldError('publisher.website')" /><p v-if="panel.hasFieldError('publisher.website')" class="text-sm text-red-600">{{ panel.fieldError('publisher.website') }}</p></div>
      <div class="space-y-2 md:col-span-2"><label class="text-sm font-medium">Description</label><Textarea v-model="panel.publisherForm.description" maxlength="2000" /></div>
      <div class="md:col-span-2"><label class="flex items-center gap-2 text-sm font-medium"><input v-model="panel.publisherForm.featured" type="checkbox" class="size-4 rounded border-slate-300" />Mark as featured publisher</label></div>
    </div>
  </AdminDialogFrame>
</template>
