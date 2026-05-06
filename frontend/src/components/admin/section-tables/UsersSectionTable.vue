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
  <AdminSectionTableFrame section="users" :total="panel.filteredUsers.value.length">
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>User</TableHead>
          <TableHead>Role</TableHead>
          <TableHead>Status</TableHead>
          <TableHead>Orders</TableHead>
          <TableHead>Spent</TableHead>
          <TableHead class="text-right">Actions</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        <TableRow v-for="user in panel.paginatedUsers.value" :key="user.id">
          <TableCell>
            <div class="space-y-1">
              <p class="font-medium text-slate-950">{{ user.name }}</p>
              <p class="text-sm text-slate-500">{{ user.email }}</p>
            </div>
          </TableCell>
          <TableCell>{{ user.role }}</TableCell>
          <TableCell>
            <Badge :variant="panel.badgeVariantForStatus(user.status)">{{ user.status }}</Badge>
          </TableCell>
          <TableCell>{{ user.ordersCount }}</TableCell>
          <TableCell>{{ panel.formatCurrency(user.totalSpent) }}</TableCell>
          <TableCell class="text-right">
            <div class="flex justify-end">
              <ButtonGroup>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.openEditDialog('users', user)">
                  <Pencil class="size-4" /> Edit
                </Button>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.deleteUser(user)">
                  <Trash2 class="size-4" /> Delete
                </Button>
              </ButtonGroup>
            </div>
          </TableCell>
        </TableRow>

        <TableRow v-if="panel.filteredUsers.value.length === 0">
          <TableCell colspan="6" class="py-10 text-center text-slate-500">
            No users matched the current search.
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </AdminSectionTableFrame>
</template>
