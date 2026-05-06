<script setup lang="ts">
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { useAdminPanel } from "@/composables/useAdminPanel"

const panel = useAdminPanel()
</script>

<template>
  <Dialog v-model:open="panel.dialogOpen.value">
    <DialogContent class="max-h-[90vh] overflow-y-auto sm:max-w-3xl">
      <DialogHeader>
        <DialogTitle>{{ panel.currentDialogTitle.value }}</DialogTitle>
        <DialogDescription>
          Update the admin data shown across the dashboard and storefront.
        </DialogDescription>
      </DialogHeader>

      <slot />

      <DialogFooter>
        <Button variant="outline" :disabled="panel.dialogSubmitting.value" @click="panel.dialogOpen.value = false">Cancel</Button>
        <Button :disabled="panel.dialogSubmitting.value" @click="panel.saveDialog()">
          {{ panel.dialogSubmitting.value ? "Saving..." : panel.dialogMode.value === "create" ? "Create" : "Save changes" }}
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
