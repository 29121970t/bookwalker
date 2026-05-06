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
  <AdminSectionTableFrame section="tags" :total="panel.filteredTags.value.length">
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Tag</TableHead>
          <TableHead>Description</TableHead>
          <TableHead>Usage</TableHead>
          <TableHead>Featured</TableHead>
          <TableHead class="text-right">Actions</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        <TableRow v-for="catalogTag in panel.paginatedTags.value" :key="catalogTag.id">
          <TableCell>
            <div class="flex items-center gap-3">
              <span class="size-3 rounded-full" :style="{ backgroundColor: catalogTag.color }" />
              <span class="font-medium text-slate-950">{{ catalogTag.name }}</span>
            </div>
          </TableCell>
          <TableCell class="max-w-md text-slate-600">{{ catalogTag.description }}</TableCell>
          <TableCell>{{ catalogTag.usageCount }}</TableCell>
          <TableCell>
            <Badge :variant="catalogTag.featured ? 'secondary' : 'outline'">
              {{ catalogTag.featured ? "Featured" : "Standard" }}
            </Badge>
          </TableCell>
          <TableCell class="text-right">
            <div class="flex justify-end">
              <ButtonGroup>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.openEditDialog('tags', catalogTag)">
                  <Pencil class="size-4" /> Edit
                </Button>
                <Button variant="outline" size="sm" class="rounded-full" @click="panel.deleteTag(catalogTag)">
                  <Trash2 class="size-4" /> Delete
                </Button>
              </ButtonGroup>
            </div>
          </TableCell>
        </TableRow>

        <TableRow v-if="panel.filteredTags.value.length === 0">
          <TableCell colspan="5" class="py-10 text-center text-slate-500">
            No tags matched the current search.
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </AdminSectionTableFrame>
</template>
