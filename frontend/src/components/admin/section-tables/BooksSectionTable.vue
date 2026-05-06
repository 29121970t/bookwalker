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
  <AdminSectionTableFrame section="books" :total="panel.filteredBooks.value.length">
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Book</TableHead>
          <TableHead>Genre</TableHead>
          <TableHead>Price</TableHead>
          <TableHead>Tags</TableHead>
          <TableHead>Status</TableHead>
          <TableHead class="text-right">Actions</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        <TableRow v-for="book in panel.paginatedBooks.value" :key="book.id">
          <TableCell>
            <div class="space-y-1">
              <p class="font-medium text-slate-950">{{ book.title }}</p>
              <p class="text-sm text-slate-500">{{ book.author }} · {{ book.publisher }}</p>
            </div>
          </TableCell>
          <TableCell>{{ book.genre }}</TableCell>
          <TableCell>{{ panel.formatCurrency(book.price) }}</TableCell>
          <TableCell>
            <div class="flex flex-wrap gap-2">
              <Badge v-for="bookTag in book.tags.slice(0, 3)" :key="bookTag" variant="outline">
                {{ bookTag }}
              </Badge>
              <Badge v-if="book.tags.length > 3" variant="secondary">
                +{{ book.tags.length - 3 }}
              </Badge>
            </div>
          </TableCell>
          <TableCell>
            <div class="flex flex-wrap gap-2">
              <Badge v-if="book.featured" variant="secondary">Featured</Badge>
              <Badge v-if="book.popular" variant="outline">Popular</Badge>
              <Badge v-if="book.newArrival" variant="outline">New</Badge>
            </div>
          </TableCell>
          <TableCell class="text-right">
            <div class="flex justify-end">
              <ButtonGroup>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.openEditDialog('books', book)">
                  <Pencil class="size-4" /> Edit
                </Button>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.deleteBook(book)">
                  <Trash2 class="size-4" /> Delete
                </Button>
              </ButtonGroup>
            </div>
          </TableCell>
        </TableRow>

        <TableRow v-if="panel.filteredBooks.value.length === 0">
          <TableCell colspan="6" class="py-10 text-center text-slate-500">
            No books matched the current search.
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </AdminSectionTableFrame>
</template>
