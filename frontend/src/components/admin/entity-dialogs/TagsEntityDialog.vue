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
      <div class="space-y-2"><label class="text-sm font-medium">Tag name</label><Input v-model="panel.tagForm.name" required maxlength="255" :aria-invalid="panel.hasFieldError('tag.name')" /><p v-if="panel.hasFieldError('tag.name')" class="text-sm text-red-600">{{ panel.fieldError('tag.name') }}</p></div>
      <div class="space-y-2"><label class="text-sm font-medium">Color</label><Input v-model="panel.tagForm.color" placeholder="#0f766e" pattern="^#[0-9a-fA-F]{6}$" maxlength="7" required :aria-invalid="panel.hasFieldError('tag.color')" /><p v-if="panel.hasFieldError('tag.color')" class="text-sm text-red-600">{{ panel.fieldError('tag.color') }}</p></div>
      <div class="space-y-2 md:col-span-2"><label class="text-sm font-medium">Description</label><Textarea v-model="panel.tagForm.description" maxlength="500" :aria-invalid="panel.hasFieldError('tag.description')" /><p v-if="panel.hasFieldError('tag.description')" class="text-sm text-red-600">{{ panel.fieldError('tag.description') }}</p></div>
      <div class="md:col-span-2"><label class="flex items-center gap-2 text-sm font-medium"><input v-model="panel.tagForm.featured" type="checkbox" class="size-4 rounded border-slate-300" />Mark as featured label</label></div>
    </div>
  </AdminDialogFrame>
</template>
