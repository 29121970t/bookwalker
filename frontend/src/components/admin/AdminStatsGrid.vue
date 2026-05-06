<script setup lang="ts">
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"

defineProps<{
  stats: Array<{
    title: string
    value: string
    note: string
    icon: unknown
  }>
}>()
</script>

<template>
  <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
    <Card
      v-for="stat in stats"
      :key="stat.title"
      class="rounded-[2rem] border-white/70 bg-white/85 shadow-lg shadow-slate-200/60"
    >
      <CardHeader class="flex flex-row items-start justify-between space-y-0">
        <div>
          <CardDescription>{{ stat.title }}</CardDescription>
          <CardTitle class="mt-2 text-3xl">{{ stat.value }}</CardTitle>
        </div>
        <div class="grid size-10 place-items-center rounded-2xl bg-slate-950 text-white">
          <component :is="stat.icon" class="size-4" />
        </div>
      </CardHeader>
      <CardContent class="pt-0 text-sm text-slate-500">
        {{ stat.note }}
      </CardContent>
    </Card>
  </div>
</template>
