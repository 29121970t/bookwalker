<script setup lang="ts">
import { RouterView } from "vue-router";
import { Toaster } from "@/components/ui/sonner";
</script>

<template>
  
  <RouterView></RouterView>
  <Toaster :toast-options="{
    class: 'bg-background flex items-center p-2 shadow m-2 animate-slidedown rounded-md',
    duration: 1000,
  }" class="z-50 fixed right-4 bottom-4" />
</template>
