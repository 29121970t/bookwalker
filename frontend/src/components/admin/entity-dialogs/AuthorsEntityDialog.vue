<script setup lang="ts">
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useAdminPanel } from "@/composables/useAdminPanel";
import AdminDialogFrame from "./AdminDialogFrame.vue";
import Label from "@/components/ui/label/Label.vue";

const panel = useAdminPanel();
</script>

<template>
  <AdminDialogFrame>
    <div class="grid gap-4 md:grid-cols-2">
      <div class="space-y-2">
        <Label class="text-sm font-medium">Author name</Label>
        <Input v-model="panel.authorForm.name" required maxlength="255" :aria-invalid="panel.hasFieldError('author.name')" />
        <p v-if="panel.hasFieldError('author.name')" class="text-sm text-red-600">{{ panel.fieldError('author.name') }}</p>
      </div>
      <div class="space-y-2 md:col-span-2">
        <Label class="text-sm font-medium">Bio</Label
        ><Textarea v-model="panel.authorForm.bio" maxlength="4000" :aria-invalid="panel.hasFieldError('author.bio')" />
        <p v-if="panel.hasFieldError('author.bio')" class="text-sm text-red-600">{{ panel.fieldError('author.bio') }}</p>
      </div>
    </div>
  </AdminDialogFrame>
</template>
