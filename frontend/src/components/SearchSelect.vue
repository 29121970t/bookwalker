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
import { useForwardPropsEmits } from "reka-ui";

const props = defineProps<{
  elements: { id: number; label: string }[];
  placeholder: string;
}>();
const { elements, placeholder } = props;
const emit = defineEmits(["select"]);
const forwarded = useForwardPropsEmits(props, emit);

const open = ref(false);
const value = defineModel({ default: -1 });

const selectedElement = computed(() =>
  elements.find((element) => element.id === value.value),
);

function selectElement(selectedValue: number) {
  value.value = selectedValue === value.value ? -1 : selectedValue;
  open.value = false;
  emit("select", selectedValue);
}
</script>

<template>
  <Popover v-model:open="open" class="w-full">
    <PopoverTrigger as-child v-bind="forwarded">
      <Button
        variant="outline"
        role="combobox"
        :aria-expanded="open"
        :class="[
          { 'text-muted-foreground': !selectedElement?.label },
          'w-full justify-between',
        ]"
      >
        {{ selectedElement?.label || placeholder }}
        <ChevronsUpDownIcon class="opacity-50" />
      </Button>
    </PopoverTrigger>
    <PopoverContent class="p-0 w-(--reka-popover-trigger-width)">
      <Command class="bg-background">
        <CommandInput class="h-9" placeholder="Search author..." />
        <CommandList>
          <CommandEmpty>{{ placeholder }}</CommandEmpty>
          <CommandGroup>
            <CommandItem
              v-for="element in elements"
              :key="element.id"
              :value="element.id"
              @select="
                (ev) => {
                  selectElement(ev.detail.value as number);
                }
              "
              class="hover:bg-accent"
            >
              {{ element.label }}
              <CheckIcon
                :class="
                  cn(
                    'ml-auto',
                    value === element.id ? 'opacity-100' : 'opacity-0',
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
