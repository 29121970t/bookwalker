<script setup lang="ts">
import OrderItemsSelect from "@/components/OrderItemsSelect.vue"
import { useAdminPanel } from "@/composables/useAdminPanel"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import AdminDialogFrame from "./AdminDialogFrame.vue"

const panel = useAdminPanel()
</script>

<template>
  <AdminDialogFrame>
    <div class="grid gap-4 md:grid-cols-2">
      <div class="space-y-2">
        <Label>Order ID</Label>
        <Input v-model="panel.orderForm.id" placeholder="BW-2099" disabled />
      </div>
      <div class="space-y-2">
        <Label>Date</Label>
        <Input v-model="panel.orderForm.date" type="datetime-local" required :aria-invalid="panel.hasFieldError('order.date')" />
        <p v-if="panel.hasFieldError('order.date')" class="text-sm text-red-600">{{ panel.fieldError('order.date') }}</p>
      </div>
      <div class="space-y-2">
        <Label>Status</Label>
        <Select v-model="panel.orderForm.status">
          <SelectTrigger class="w-full" :class="panel.hasFieldError('order.status') ? 'border-red-500 ring-red-500/20' : ''">
            <SelectValue placeholder="Select status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="status in panel.orderStatuses" :key="status" :value="status">
              {{ status }}
            </SelectItem>
          </SelectContent>
        </Select>
        <p v-if="panel.hasFieldError('order.status')" class="text-sm text-red-600">{{ panel.fieldError('order.status') }}</p>
      </div>
      <div class="space-y-2">
        <Label>Total</Label>
        <Input :model-value="panel.orderForm.total.toFixed(2)" disabled />
      </div>
      <div class="space-y-2">
        <Label>Customer name</Label>
        <Input v-model="panel.orderForm.customerName" maxlength="255" disabled />
      </div>
      <div class="space-y-2">
        <Label>Customer email</Label>
        <Input v-model="panel.orderForm.customerEmail" type="email" disabled />
      </div>
      <div class="space-y-2">
        <Label>Payment method</Label>
        <Select v-model="panel.orderForm.paymentMethod">
          <SelectTrigger class="w-full" :class="panel.hasFieldError('order.paymentMethod') ? 'border-red-500 ring-red-500/20' : ''">
            <SelectValue placeholder="Select payment method" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="method in panel.paymentMethods" :key="method" :value="method">
              {{ method }}
            </SelectItem>
          </SelectContent>
        </Select>
        <p v-if="panel.hasFieldError('order.paymentMethod')" class="text-sm text-red-600">{{ panel.fieldError('order.paymentMethod') }}</p>
      </div>
      <div class="space-y-2">
        <Label>Delivery city</Label>
        <Input v-model="panel.orderForm.deliveryCity" maxlength="255" :aria-invalid="panel.hasFieldError('order.deliveryCity')" />
        <p v-if="panel.hasFieldError('order.deliveryCity')" class="text-sm text-red-600">{{ panel.fieldError('order.deliveryCity') }}</p>
      </div>
      <div class="space-y-2 md:col-span-2">
        <Label>Items</Label>
        <OrderItemsSelect
          v-model="panel.orderForm.items"
          :books="panel.books.value.map((book) => ({ id: book.id, label: book.title, subtitle: book.author }))"
        />
        <p v-if="panel.hasFieldError('order.items')" class="text-sm text-red-600">{{ panel.fieldError('order.items') }}</p>
        <p class="text-xs text-slate-500">Choose books and set the quantity for each selected entry.</p>
      </div>
    </div>
  </AdminDialogFrame>
</template>
