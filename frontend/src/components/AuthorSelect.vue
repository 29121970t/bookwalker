<script setup lang="ts">
import { CheckIcon, ChevronsUpDownIcon } from "lucide-vue-next"
import { computed, ref } from "vue"
import { cn } from "@/lib/utils"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"

type SelectValue = string | number | null
type ModelValue = SelectValue | SelectValue[]
type SelectOption = {
  id?: number
  value?: string | number
  label: string
}

const props = defineProps<{
  authors: SelectOption[]
  id?: string
  invalid?: boolean
  multiple?: boolean
}>()
const modelValue = defineModel<ModelValue>({ default: "" })

const open = ref(false)
const selectedAuthors = computed(() =>
  props.authors.filter((author) => isSelected(optionValue(author))),
)

function optionValue(author: SelectOption) {
  return author.value ?? author.id ?? ""
}

function emptyValue() {
  if (props.multiple) return []
  return typeof modelValue.value === "number" || modelValue.value === null ? null : ""
}

function values() {
  return Array.isArray(modelValue.value) ? modelValue.value : []
}

function isSelected(selectedValue: SelectValue) {
  if (props.multiple) {
    return values().some((value) => Object.is(value, selectedValue))
  }
  return Object.is(selectedValue, modelValue.value)
}

function selectAuthor(selectedValue: SelectValue) {
  if (props.multiple) {
    modelValue.value = isSelected(selectedValue)
      ? values().filter((value) => !Object.is(value, selectedValue))
      : [...values(), selectedValue]
    return
  }

  modelValue.value = isSelected(selectedValue) ? emptyValue() : selectedValue
  open.value = false
}
</script>

<template>
  <Popover v-model:open="open" class="w-full">
    <PopoverTrigger as-child>
      <Button
        variant="outline"
        role="combobox"
        :aria-expanded="open"
        :aria-invalid="invalid"
        :class="[
          { 'text-muted-foreground': selectedAuthors.length === 0 },
          invalid ? 'border-red-500 ring-red-500/20' : '',
          'min-h-9 w-full justify-between gap-2',
        ]"
      >
        <span v-if="!multiple" class="truncate">
          {{ selectedAuthors[0]?.label || "Select author..." }}
        </span>
        <span v-else class="flex min-w-0 flex-1 flex-wrap gap-1 text-left">
          <template v-if="selectedAuthors.length > 0">
            <Badge
              v-for="author in selectedAuthors.slice(0, 2)"
              :key="optionValue(author)"
              variant="secondary"
              class="max-w-full truncate"
            >
              {{ author.label }}
            </Badge>
            <Badge v-if="selectedAuthors.length > 2" variant="outline">
              +{{ selectedAuthors.length - 2 }}
            </Badge>
          </template>
          <span v-else>Select authors...</span>
        </span>
        <ChevronsUpDownIcon class="shrink-0 opacity-50" />
      </Button>
    </PopoverTrigger>
    <PopoverContent class="p-0 w-(--reka-popover-trigger-width)">
      <Command class=" bg-background">
        <CommandInput :id="id" class="h-9" placeholder="Search author..." />
        <CommandList>
          <CommandEmpty>No author found.</CommandEmpty>
          <CommandGroup>
            <CommandItem
              v-for="author in authors"
              :key="optionValue(author)"
              :value="author.label"
              @select="() => {
                selectAuthor(optionValue(author))
              }"
              class="hover:bg-accent"
            >
              {{ author.label }}
              <CheckIcon
                :class="cn(
                  'ml-auto',
                  isSelected(optionValue(author)) ? 'opacity-100' : 'opacity-0',
                )"
              />
            </CommandItem>
          </CommandGroup>
        </CommandList>
      </Command>
    </PopoverContent>
  </Popover>
</template>
