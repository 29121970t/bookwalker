<script setup lang="ts">
import { CheckIcon, ChevronDown, Minus, Plus } from "lucide-vue-next"
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
import { Input } from "@/components/ui/input"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { ScrollArea } from "@/components/ui/scroll-area"

const props = defineProps<{
  books: Array<{ id: number, label: string, subtitle?: string }>
}>()

const selectedItems = defineModel<Array<{ bookId: number, quantity: number }>>({ default: [] })
const open = ref(false)

const selectedMap = computed(() => new Map(selectedItems.value.map((item) => [item.bookId, item.quantity])))

const selectedBooks = computed(() =>
  props.books.filter((book) => selectedMap.value.has(book.id)),
)

function setQuantity(bookId: number, quantity: number) {
  const safeQuantity = Math.max(0, Math.floor(quantity))
  if (safeQuantity === 0) {
    selectedItems.value = selectedItems.value.filter((item) => item.bookId !== bookId)
    return
  }

  const existing = selectedItems.value.find((item) => item.bookId === bookId)
  if (existing) {
    selectedItems.value = selectedItems.value.map((item) => item.bookId === bookId ? { ...item, quantity: safeQuantity } : item)
    return
  }

  selectedItems.value = [...selectedItems.value, { bookId, quantity: safeQuantity }]
}

function toggleBook(bookId: number) {
  const existing = selectedMap.value.get(bookId)
  setQuantity(bookId, existing ? 0 : 1)
}

function clearAll() {
  selectedItems.value = []
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
          <template v-if="selectedBooks.length > 0">
            <Badge
              v-for="book in selectedBooks.slice(0, 2)"
              :key="book.id"
              variant="secondary"
              class="max-w-full truncate"
            >
              {{ book.label }} x{{ selectedMap.get(book.id) }}
            </Badge>
            <Badge v-if="selectedBooks.length > 2" variant="outline">
              +{{ selectedBooks.length - 2 }}
            </Badge>
          </template>
          <span v-else class="text-muted-foreground">Select items...</span>
        </div>
        <ChevronDown class="size-4 shrink-0 opacity-60" />
      </Button>
    </PopoverTrigger>

    <PopoverContent class="w-(--reka-popover-trigger-width) p-0">
      <Command>
        <CommandInput placeholder="Search books..." class="h-9" />
        <CommandList>
          <CommandEmpty>No books found.</CommandEmpty>
          <ScrollArea class="max-h-80">
            <CommandGroup>
              <CommandItem
                v-for="book in books"
                :key="book.id"
                :value="`${book.label} ${book.subtitle ?? ''}`"
                class="flex items-start gap-3 px-3 py-3"
                @select.prevent="toggleBook(book.id)"
              >
                <div class="min-w-0 flex-1">
                  <p class="truncate text-sm font-medium">{{ book.label }}</p>
                  <p v-if="book.subtitle" class="truncate text-xs text-muted-foreground">
                    {{ book.subtitle }}
                  </p>
                </div>

                <div class="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size="icon"
                    class="size-7 rounded-full"
                    @click.stop="setQuantity(book.id, (selectedMap.get(book.id) ?? 0) - 1)"
                  >
                    <Minus class="size-3" />
                  </Button>
                  <Input
                    :model-value="selectedMap.get(book.id) ?? 0"
                    type="number"
                    min="0"
                    max="99"
                    class="h-8 w-16 text-center"
                    @click.stop
                    @update:model-value="setQuantity(book.id, Number($event))"
                  />
                  <Button
                    variant="outline"
                    size="icon"
                    class="size-7 rounded-full"
                    @click.stop="setQuantity(book.id, (selectedMap.get(book.id) ?? 0) + 1)"
                  >
                    <Plus class="size-3" />
                  </Button>
                  <CheckIcon
                    :class="cn('size-4', selectedMap.has(book.id) ? 'opacity-100' : 'opacity-0')"
                  />
                </div>
              </CommandItem>
            </CommandGroup>
          </ScrollArea>
        </CommandList>
        <div v-if="selectedItems.length > 0" class="border-t p-2">
          <Button variant="ghost" size="sm" class="w-full" @click="clearAll">
            Clear items
          </Button>
        </div>
      </Command>
    </PopoverContent>
  </Popover>
</template>
