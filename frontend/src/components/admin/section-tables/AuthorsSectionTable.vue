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
  <AdminSectionTableFrame section="authors" :total="panel.filteredAuthors.value.length">
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Author</TableHead>
          <TableHead>Country</TableHead>
          <TableHead>Books</TableHead>
          <TableHead>Featured</TableHead>
          <TableHead class="text-right">Actions</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        <TableRow v-for="author in panel.paginatedAuthors.value" :key="author.id">
          <TableCell>
            <div class="space-y-1">
              <p class="font-medium text-slate-950">{{ author.name }}</p>
              <p class="text-sm text-slate-500 line-clamp-2">{{ author.bio || "No bio yet" }}</p>
            </div>
          </TableCell>
          <TableCell>{{ author.country || "Unknown" }}</TableCell>
          <TableCell>{{ author.booksCount }}</TableCell>
          <TableCell>
            <Badge :variant="author.featured ? 'secondary' : 'outline'">
              {{ author.featured ? "Featured" : "Standard" }}
            </Badge>
          </TableCell>
          <TableCell class="text-right">
            <div class="flex justify-end">
              <ButtonGroup>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.openEditDialog('authors', author)">
                  <Pencil class="size-4" /> Edit
                </Button>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.deleteAuthor(author)">
                  <Trash2 class="size-4" /> Delete
                </Button>
              </ButtonGroup>
            </div>
          </TableCell>
        </TableRow>

        <TableRow v-if="panel.filteredAuthors.value.length === 0">
          <TableCell colspan="5" class="py-10 text-center text-slate-500">
            No authors matched the current search.
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </AdminSectionTableFrame>
</template>
