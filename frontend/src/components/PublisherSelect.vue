<script setup lang="ts">
import { CheckIcon, ChevronsUpDownIcon } from "lucide-vue-next";
import { computed, ref } from "vue";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";

type SelectValue = string | number | null
type SelectOption = {
  id?: number
  value?: string | number
  label: string
}

const props = defineProps<{
  publishers: SelectOption[];
  id?: string;
  invalid?: boolean;
}>();
const modelValue = defineModel<SelectValue>({ default: "" })

const open = ref(false);
const selectedPublisher = computed(() =>
  props.publishers.find((publisher) => Object.is(optionValue(publisher), modelValue.value)),
);

function optionValue(publisher: SelectOption) {
  return publisher.value ?? publisher.id ?? "";
}

function emptyValue() {
  return typeof modelValue.value === "number" || modelValue.value === null ? null : "";
}

function selectPublisher(selectedValue: SelectValue) {
  modelValue.value = Object.is(selectedValue, modelValue.value) ? emptyValue() : selectedValue;
  open.value = false;
}
</script>

<template>
  <Popover v-model:open="open">
    <PopoverTrigger as-child>
      <Button
        variant="outline"
        role="combobox"
        :aria-expanded="open"
        :aria-invalid="invalid"
        :class="[
          { 'text-muted-foreground': !selectedPublisher?.label },
          invalid ? 'border-red-500 ring-red-500/20' : '',
          'w-full justify-between',
        ]"
      >
        {{ selectedPublisher?.label || "Select publisher..." }}
        <ChevronsUpDownIcon class="opacity-50" />
      </Button>
    </PopoverTrigger>
    <PopoverContent class="w-(--reka-popover-trigger-width) p-0">
      <Command>
        <CommandInput :id="id" class="h-9" placeholder="Search publisher..." />
        <CommandList>
          <CommandEmpty>No publisher found.</CommandEmpty>
          <CommandGroup>
            <CommandItem
              v-for="publisher in publishers"
              :key="optionValue(publisher)"
              :value="publisher.label"
              @select="
                () => {
                  selectPublisher(optionValue(publisher));
                }
              "
              class="hover:bg-accent"
            >
              {{ publisher.label }}
              <CheckIcon
                :class="
                  cn(
                    'ml-auto',
                    Object.is(modelValue, optionValue(publisher)) ? 'opacity-100' : 'opacity-0',
                  )
                "
              />
            </CommandItem>
          </CommandGroup>
        </CommandList>
      </Command>
    </PopoverContent>
  </Popover>
</template>
