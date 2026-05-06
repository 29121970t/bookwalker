<script setup lang="ts">
import { Input } from "@/components/ui/input"
import { useAdminPanel } from "@/composables/useAdminPanel"
import AdminDialogFrame from "./AdminDialogFrame.vue"

const panel = useAdminPanel()
</script>

<template>
  <AdminDialogFrame>
    <div class="grid gap-4 md:grid-cols-2">
      <div class="space-y-2"><label class="text-sm font-medium">Name</label><Input v-model="panel.userForm.name" required maxlength="255" :aria-invalid="panel.hasFieldError('user.name')" /><p v-if="panel.hasFieldError('user.name')" class="text-sm text-red-600">{{ panel.fieldError('user.name') }}</p></div>
      <div class="space-y-2"><label class="text-sm font-medium">Email</label><Input v-model="panel.userForm.email" type="email" required maxlength="255" :aria-invalid="panel.hasFieldError('user.email')" /><p v-if="panel.hasFieldError('user.email')" class="text-sm text-red-600">{{ panel.fieldError('user.email') }}</p></div>
      <div class="space-y-2">
        <label class="text-sm font-medium">Role</label>
        <select v-model="panel.userForm.role" class="border-input bg-background w-full rounded-md border px-3 py-2 text-sm">
          <option value="Admin">Admin</option><option value="Client">Client</option>
        </select>
      </div>
      <div class="space-y-2">
        <label class="text-sm font-medium">Status</label>
        <select v-model="panel.userForm.status" class="border-input bg-background w-full rounded-md border px-3 py-2 text-sm">
          <option value="Active">Active</option><option value="Blocked">Blocked</option>
        </select>
      </div>
      <div class="space-y-2"><label class="text-sm font-medium">Joined at</label><Input v-model="panel.userForm.joinedAt" type="date" disabled /></div>
      <div class="space-y-2 rounded-md border px-3 py-2">
        <p class="text-sm font-medium">Orders count</p>
        <p class="text-sm text-slate-600">{{ panel.userForm.ordersCount }}</p>
      </div>
      <div class="space-y-2 rounded-md border px-3 py-2">
        <p class="text-sm font-medium">Total spent</p>
        <p class="text-sm text-slate-600">${{ panel.userForm.totalSpent.toFixed(2) }}</p>
      </div>
    </div>
  </AdminDialogFrame>
</template>
