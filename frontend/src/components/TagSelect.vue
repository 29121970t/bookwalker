<script setup lang="ts">
import { CheckIcon, ChevronDown } from "lucide-vue-next";
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
import { Badge } from "@/components/ui/badge";

const props = defineProps<{
  tags: { id: number; label: string }[];
  id?: string;
}>();

const selectedIds = defineModel<number[]>({ default: [] });
const open = ref(false);
const search = ref("");

const filteredTags = computed(() => {
  const normalized = search.value.trim().toLowerCase();
  if (!normalized) {
    return props.tags;
  }

  return props.tags.filter((tag) => tag.label.toLowerCase().includes(normalized));
});

const selectedTags = computed(() =>
  props.tags.filter((tag) => selectedIds.value.includes(tag.id)),
);

function toggleTag(tagId: number) {
  if (selectedIds.value.includes(tagId)) {
    selectedIds.value = selectedIds.value.filter((id) => id !== tagId);
    return;
  }

  selectedIds.value = [...selectedIds.value, tagId];
}

</script>

<template>
  <Popover v-model:open="open">
    <PopoverTrigger as-child>
      <Button
        variant="outline"
        role="combobox"
        :aria-expanded="open"
        class="flex min-h-10 w-full items-center justify-between gap-2 px-3 py-2"
      >
        <div class="flex min-w-0 flex-1 flex-wrap gap-1 text-left">
          <template v-if="selectedTags.length > 0">
            <Badge
              v-for="tag in selectedTags.slice(0, 2)"
              :key="tag.id"
              variant="secondary"
              class="max-w-full truncate"
            >
              {{ tag.label }}
            </Badge>
            <Badge v-if="selectedTags.length > 2" variant="outline">
              +{{ selectedTags.length - 2 }}
            </Badge>
          </template>
          <span v-else class="text-muted-foreground">Select tags...</span>
        </div>
        <ChevronDown class="size-4 shrink-0 opacity-60" />
      </Button>
    </PopoverTrigger>

    <PopoverContent class="w-(--reka-popover-trigger-width) p-0">
      <Command>
        <CommandInput
          :id="id"
          v-model="search"
          placeholder="Search tags..."
          class="h-9"
        />
        <CommandList>
          <CommandEmpty>No tags found.</CommandEmpty>
          <CommandGroup>
            <CommandItem
              v-for="tag in filteredTags"
              :key="tag.id"
              :value="tag.label"
              @select="toggleTag(tag.id)"
            >
              {{ tag.label }}
              <CheckIcon
                :class="
                  cn(
                    'ml-auto size-4',
                    selectedIds.includes(tag.id) ? 'opacity-100' : 'opacity-0',
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
