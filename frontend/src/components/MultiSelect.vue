<script setup lang="ts">
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { AcceptableValue, useForwardProps } from "reka-ui";

const props = defineProps<{
  elements: { id: number; label: string }[];
  placeholder: string;
}>();
const { elements, placeholder } = props;
const forwarded = useForwardProps(props);
const modelValue = defineModel<AcceptableValue[]>();
</script>

<template>
  <Select
    v-bind="forwarded"
    multiple
    v-model:model-value="modelValue"
  >
    <SelectTrigger class="w-full">
      <SelectValue :placeholder="placeholder" />
    </SelectTrigger>
    <SelectContent>
      <SelectGroup>
        <SelectLabel>{{ placeholder }}</SelectLabel>
        <SelectItem v-for="el in elements" :value="el.id">
          {{ el.label }}
        </SelectItem>
      </SelectGroup>
    </SelectContent>
  </Select>
</template>
