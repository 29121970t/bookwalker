<script setup lang="ts">
import { Plus, Search } from "lucide-vue-next";
import AdminSectionTable from "@/components/admin/AdminSectionTable.vue";
import { useAdminPanel, type AdminSection } from "@/composables/useAdminPanel";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import InputGroup from "@/components/ui/input-group/InputGroup.vue";
import InputGroupInput from "@/components/ui/input-group/InputGroupInput.vue";
import InputGroupAddon from "@/components/ui/input-group/InputGroupAddon.vue";

defineProps<{
  section: AdminSection;
}>();

const panel = useAdminPanel();
</script>

<template>
  <div class="grid gap-6 w-full min-h-screen">
    <Card
      class="rounded-[2rem] border-white/70 bg-white/90 shadow-lg shadow-slate-200/60"
    >
      <CardHeader class="gap-4 grid grid-rows-2 grid-cols-[1fr] md:items-center md:justify-between w-full">
        <div class="w-full">
          <CardTitle>{{ panel.sectionMeta[section].title }} table</CardTitle>
          <CardDescription>
            Search, update, create, and remove entries directly from one place.
          </CardDescription>
        </div>

        <div class="flex justify-between w-full">
          <InputGroup class="w-1/2">
            <InputGroupInput
              class="rounded-md pl-9"
              v-model="panel.search[section]"
              :placeholder="`Search ${panel.sectionMeta[section].title.toLowerCase()}...`"
            />
            <InputGroupAddon>
              <Search class="size-4" />
            </InputGroupAddon>
          </InputGroup>
          <Button class="rounded-full" :disabled="!panel.canCreateSection[section]" @click="panel.openCreateDialog(section)">
            <Plus class="size-4" />
            Add {{ panel.dialogTitles[section] }}
          </Button>
        </div>
      </CardHeader>

      <CardContent class="h-full">
        <AdminSectionTable :section="section" />
      </CardContent>
    </Card>
  </div>
</template>
