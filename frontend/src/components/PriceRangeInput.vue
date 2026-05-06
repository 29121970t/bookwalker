<script setup lang="ts">
import {
  InputGroup,
  InputGroupInput,
} from "@/components/ui/input-group";
import Separator from "./ui/separator/Separator.vue";

const { id } = defineProps<{ id? :string }>()
const minModel = defineModel<number | null>("min", { default: null })
const maxModel = defineModel<number | null>("max", { default: null })

function formatValue(value: number | null) {
  return value == null ? "" : String(value)
}

function sanitizeNumericInput(value: string) {
  const normalized = value
    .replace(",", ".")
    .replace(/[^0-9.]/g, "")
    .replace(/^(\d*\.\d*).*$/, "$1")

  return normalized
}

function parseValue(value: string) {
  const sanitized = sanitizeNumericInput(value)

  if (!sanitized) {
    return null
  }

  const parsed = Number(sanitized)
  return Number.isFinite(parsed) ? parsed : null
}

function handleInput(flag : boolean, event: Event) {
  const input = event.target as HTMLInputElement
  const sanitized = sanitizeNumericInput(input.value)
  input.value = input.value.replace(/[^0-9]/g, '')
  // if (input.value !== sanitized) {
  //   input.value = sanitized
  // }

  if(!flag){
    minModel.value = parseValue(sanitized)
  }
  else{
    maxModel.value = parseValue(sanitized)
  }
  
}

function blockInvalidKeys(event: KeyboardEvent) {
  if (["e", "E", "+", "-"].includes(event.key)) {
    event.preventDefault()
  }
}
</script>

<template>
  <InputGroup class="w-28 justify-between">
    <InputGroupInput
      :id="id"
      :model-value="formatValue(minModel)"
      type="number"
      min="0.1"
      step="0.01"
      inputmode="decimal"
      placeholder="from"
      class="text-center"
      @input="handleInput(false, $event)"
      @keydown="blockInvalidKeys"
    />
    <Separator orientation="vertical" />
    <InputGroupInput
      :model-value="formatValue(maxModel)"
      type="number"
      min="0.1"
      step="0.01"
      inputmode="decimal"
      placeholder="to"
      class="text-center"
      @input="handleInput(true, $event)"
      @keydown="blockInvalidKeys"
    />
  </InputGroup>
</template>
