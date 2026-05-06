<script setup lang="ts">
import { Pencil, Trash2 } from "lucide-vue-next"
import AdminSectionTableFrame from "@/components/admin/AdminSectionTableFrame.vue"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { ButtonGroup } from "@/components/ui/button-group"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { useAdminPanel } from "@/composables/useAdminPanel"

const panel = useAdminPanel()
</script>

<template>
  <AdminSectionTableFrame section="orders" :total="panel.filteredOrders.value.length">
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Order</TableHead>
          <TableHead>Customer</TableHead>
          <TableHead>Items</TableHead>
          <TableHead>Total</TableHead>
          <TableHead>Status</TableHead>
          <TableHead class="text-right">Actions</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        <TableRow v-for="order in panel.paginatedOrders.value" :key="order.id">
          <TableCell>
            <div class="space-y-1">
              <p class="font-medium text-slate-950">{{ order.id }}</p>
              <p class="text-sm text-slate-500">{{ order.date }}</p>
            </div>
          </TableCell>
          <TableCell>
            <div class="space-y-1">
              <p class="font-medium text-slate-950">{{ order.customerName }}</p>
              <p class="text-sm text-slate-500">{{ order.customerEmail }}</p>
            </div>
          </TableCell>
          <TableCell class="max-w-xs text-sm text-slate-600 whitespace-normal">
            {{ panel.orderItemsPreview(order) || "No items" }}
          </TableCell>
          <TableCell>{{ panel.formatCurrency(order.total) }}</TableCell>
          <TableCell>
            <Badge :variant="panel.badgeVariantForStatus(order.status)">{{ order.status }}</Badge>
          </TableCell>
          <TableCell class="text-right">
            <div class="flex justify-end">
              <ButtonGroup>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.openEditDialog('orders', order)">
                  <Pencil class="size-4" /> Edit
                </Button>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.deleteOrder(order)">
                  <Trash2 class="size-4" /> Delete
                </Button>
              </ButtonGroup>
            </div>
          </TableCell>
        </TableRow>

        <TableRow v-if="panel.filteredOrders.value.length === 0">
          <TableCell colspan="6" class="py-10 text-center text-slate-500">
            No orders matched the current search.
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </AdminSectionTableFrame>
</template>
