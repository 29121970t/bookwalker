<script setup lang="ts">
import AuthorSelect from "@/components/AuthorSelect.vue"
import PublisherSelect from "@/components/PublisherSelect.vue"
import TagSelect from "@/components/TagSelect.vue"
import { useAdminPanel } from "@/composables/useAdminPanel"
import { Checkbox } from "@/components/ui/checkbox"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import AdminDialogFrame from "./AdminDialogFrame.vue"

const panel = useAdminPanel()
</script>

<template>
  <AdminDialogFrame>
    <div class="grid gap-4 md:grid-cols-2">
      <div class="space-y-2 md:col-span-2">
        <Label for="book-title">Title</Label>
        <Input id="book-title" v-model="panel.bookForm.title" required maxlength="255" :aria-invalid="panel.hasFieldError('book.title')" />
        <p v-if="panel.hasFieldError('book.title')" class="text-sm text-red-600">{{ panel.fieldError('book.title') }}</p>
      </div>

      <div class="space-y-2">
        <Label>Authors</Label>
        <AuthorSelect
          v-model="panel.bookForm.authorIds"
          :authors="panel.authorOptions.value"
          :invalid="panel.hasFieldError('book.authors')"
          multiple
        />
        <p v-if="panel.hasFieldError('book.authors')" class="text-sm text-red-600">{{ panel.fieldError('book.authors') }}</p>
      </div>

      <div class="space-y-2">
        <Label>Publisher</Label>
        <PublisherSelect
          v-model="panel.bookForm.publisherId"
          :publishers="panel.publisherOptions.value"
          :invalid="panel.hasFieldError('book.publisher')"
        />
        <p v-if="panel.hasFieldError('book.publisher')" class="text-sm text-red-600">{{ panel.fieldError('book.publisher') }}</p>
      </div>

      <div class="space-y-2">
        <Label>Genre</Label>
        <Select v-model="panel.bookForm.genreId">
          <SelectTrigger class="w-full" :class="panel.hasFieldError('book.genre') ? 'border-red-500 ring-red-500/20' : ''">
            <SelectValue placeholder="Select genre" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="genre in panel.genreOptions.value" :key="genre.id" :value="genre.id">
              {{ genre.label }}
            </SelectItem>
          </SelectContent>
        </Select>
        <p v-if="panel.hasFieldError('book.genre')" class="text-sm text-red-600">{{ panel.fieldError('book.genre') }}</p>
      </div>

      <div class="space-y-2">
        <Label>Format</Label>
        <Select v-model="panel.bookForm.format">
          <SelectTrigger class="w-full">
            <SelectValue placeholder="Select format" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="HARDCOVER">Hardcover</SelectItem>
            <SelectItem value="PAPERBACK">Paperback</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div class="space-y-2">
        <Label for="book-price">Price</Label>
        <Input id="book-price" v-model.number="panel.bookForm.price" type="number" min="0.01" step="0.01" required :aria-invalid="panel.hasFieldError('book.price')" />
        <p v-if="panel.hasFieldError('book.price')" class="text-sm text-red-600">{{ panel.fieldError('book.price') }}</p>
      </div>

      <div class="space-y-2">
        <Label for="book-original-price">Original price</Label>
        <Input id="book-original-price" v-model.number="panel.bookForm.originalPrice" type="number" min="0" step="0.01" :aria-invalid="panel.hasFieldError('book.originalPrice')" />
        <p v-if="panel.hasFieldError('book.originalPrice')" class="text-sm text-red-600">{{ panel.fieldError('book.originalPrice') }}</p>
      </div>

      <div class="space-y-2">
        <Label for="book-pages">Pages</Label>
        <Input id="book-pages" v-model.number="panel.bookForm.pages" type="number" min="1" max="10000" required :aria-invalid="panel.hasFieldError('book.pages')" />
        <p v-if="panel.hasFieldError('book.pages')" class="text-sm text-red-600">{{ panel.fieldError('book.pages') }}</p>
      </div>

      <div class="space-y-2">
        <Label for="book-year">Year</Label>
        <Input id="book-year" v-model.number="panel.bookForm.year" type="number" min="1000" max="9999" required :aria-invalid="panel.hasFieldError('book.year')" />
        <p v-if="panel.hasFieldError('book.year')" class="text-sm text-red-600">{{ panel.fieldError('book.year') }}</p>
      </div>

      <div class="space-y-2 md:col-span-2">
        <Label>Tags</Label>
        <TagSelect v-model="panel.bookForm.tagIds" :tags="panel.tagOptions.value" />
      </div>

      <div class="space-y-2 md:col-span-2">
        <Label for="book-cover">Cover</Label>
        <Input
          id="book-cover"
          type="file"
          accept="image/*"
          @change="panel.setSelectedBookCover((($event.target as HTMLInputElement).files?.[0] ?? null))"
        />
      </div>

      <div class="space-y-2 md:col-span-2">
        <Label for="book-blurb">Blurb</Label>
        <Textarea id="book-blurb" v-model="panel.bookForm.blurb" maxlength="500" :aria-invalid="panel.hasFieldError('book.blurb')" />
        <p v-if="panel.hasFieldError('book.blurb')" class="text-sm text-red-600">{{ panel.fieldError('book.blurb') }}</p>
      </div>

      <div class="space-y-2 md:col-span-2">
        <Label for="book-description">Description</Label>
        <Textarea id="book-description" v-model="panel.bookForm.description" maxlength="2000" :aria-invalid="panel.hasFieldError('book.description')" />
        <p v-if="panel.hasFieldError('book.description')" class="text-sm text-red-600">{{ panel.fieldError('book.description') }}</p>
      </div>

      <div class="space-y-2 md:col-span-2">
        <Label for="book-long-description">Long description</Label>
        <Textarea id="book-long-description" v-model="panel.bookForm.longDescription" class="min-h-32" maxlength="6000" :aria-invalid="panel.hasFieldError('book.longDescription')" />
        <p v-if="panel.hasFieldError('book.longDescription')" class="text-sm text-red-600">{{ panel.fieldError('book.longDescription') }}</p>
      </div>

      <label class="flex items-center gap-3 text-sm">
        <Checkbox v-model:model-value="panel.bookForm.featured" />
        Featured
      </label>
      <label class="flex items-center gap-3 text-sm">
        <Checkbox v-model:model-value="panel.bookForm.popular" />
        Popular
      </label>
      <label class="flex items-center gap-3 text-sm">
        <Checkbox v-model:model-value="panel.bookForm.newArrival" />
        New arrival
      </label>
    </div>
  </AdminDialogFrame>
</template>
