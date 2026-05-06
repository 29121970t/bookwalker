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
  <AdminSectionTableFrame section="publishers" :total="panel.filteredPublishers.value.length">
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Publisher</TableHead>
          <TableHead>Country</TableHead>
          <TableHead>Books</TableHead>
          <TableHead>Featured</TableHead>
          <TableHead class="text-right">Actions</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        <TableRow v-for="publisher in panel.paginatedPublishers.value" :key="publisher.id">
          <TableCell>
            <div class="space-y-1">
              <p class="font-medium text-slate-950">{{ publisher.name }}</p>
              <p class="text-sm text-slate-500 line-clamp-2">{{ publisher.description || "No description yet" }}</p>
            </div>
          </TableCell>
          <TableCell>{{ publisher.country || "Unknown" }}</TableCell>
          <TableCell>{{ publisher.booksCount }}</TableCell>
          <TableCell>
            <Badge :variant="publisher.featured ? 'secondary' : 'outline'">
              {{ publisher.featured ? "Featured" : "Standard" }}
            </Badge>
          </TableCell>
          <TableCell class="text-right">
            <div class="flex justify-end">
              <ButtonGroup>
                <Button
                  variant="outline"
                  size="sm"
                  class="rounded-full"
                  @click="panel.openEditDialog('publishers', publisher)"
                >
                  <Pencil class="size-4" /> Edit
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  class="rounded-full"
                  @click="panel.deletePublisher(publisher)"
                >
                  <Trash2 class="size-4" /> Delete
                </Button>
              </ButtonGroup>
            </div>
          </TableCell>
        </TableRow>

        <TableRow v-if="panel.filteredPublishers.value.length === 0">
          <TableCell colspan="5" class="py-10 text-center text-slate-500">
            No publishers matched the current search.
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </AdminSectionTableFrame>
</template>
