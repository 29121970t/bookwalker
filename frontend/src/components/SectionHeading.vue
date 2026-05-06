<script setup lang="ts">
defineProps<{
  eyebrow?: string
  title: string
  description?: string
}>()
</script>

<template>
  <div class="space-y-2">
    <p v-if="eyebrow" class="text-sm font-semibold uppercase tracking-[0.28em] text-sky-700">
      {{ eyebrow }}
    </p>
    <h2 class="text-3xl font-semibold tracking-tight text-slate-950 sm:text-4xl">
      {{ title }}
    </h2>
    <p v-if="description" class="max-w-2xl text-base text-slate-600">
      {{ description }}
    </p>
  </div>
</template>
