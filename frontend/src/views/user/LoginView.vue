<script setup lang="ts">
import { reactive, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { toast } from "vue-sonner"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { ApiError } from "@/lib/api"
import { useAuthStore } from "@/stores/auth"

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const loading = ref(false)
const mode = ref<"login" | "register">("login")
const form = reactive({
  name: "",
  email: "",
  password: "",
})
const errors = reactive<Record<"name" | "email" | "password", string>>({
  name: "",
  email: "",
  password: "",
})

function clearErrors() {
  errors.name = ""
  errors.email = ""
  errors.password = ""
}

function validateForm() {
  clearErrors()

  const name = form.name.trim()
  const email = form.email.trim()

  if (mode.value === "register") {
    if (!name) errors.name = "Name is required."
    else if (name.length > 255) errors.name = "Name is too long."
  }

  if (!email) errors.email = "Email is required."
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) errors.email = "Enter a valid email address."
  else if (email.length > 255) errors.email = "Email is too long."

  if (!form.password) errors.password = "Password is required."
  else if (form.password.length < 6) errors.password = "Password must be at least 6 characters."
  else if (form.password.length > 255) errors.password = "Password is too long."

  return !errors.name && !errors.email && !errors.password
}

function toggleMode() {
  mode.value = mode.value === "login" ? "register" : "login"
  clearErrors()
}

async function submit() {
  if (!validateForm()) return

  loading.value = true
  try {
    if (mode.value === "login") {
      await auth.login(form.email.trim(), form.password)
      toast.success("Logged in successfully")
    } else {
      await auth.register(form.name.trim(), form.email.trim(), form.password)
      toast.success("Account created")
    }
    const redirect = typeof route.query.redirect === "string" ? route.query.redirect : null
    await router.push(redirect ?? (auth.isAdmin.value ? { name: "admin-books" } : { name: "account" }))
  } catch (error) {
    const message = error instanceof Error ? error.message : "Authentication failed"

    if (mode.value === "login" && error instanceof ApiError && error.status === 401) {
      errors.email = "Invalid email or password."
      errors.password = "Invalid email or password."
      toast.error("Invalid email or password")
      return
    }

    if (mode.value === "register" && error instanceof ApiError && error.status === 409) {
      const normalized = message.toLowerCase()
      if (normalized.includes("email")) {
        errors.email = "Email is already taken."
      } else if (normalized.includes("name")) {
        errors.name = "Name is already taken."
      }
      toast.error(message)
      return
    }

    toast.error(message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mx-auto flex min-h-[100vh] max-w-5xl items-center justify-center">
    <div class="grid w-1/2 gap-8 lg:grid-cols-[1fr]">


      <Card class="rounded-[2rem] border-white/70 bg-white/90 shadow-xl shadow-slate-200/60">
        <CardHeader>
          <CardTitle>{{ mode === "login" ? "Login" : "Create account" }}</CardTitle>
          <CardDescription>
            {{ mode === "login" ? "Enter your credentials to continue." : "Create a customer account linked to the API." }}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form class="space-y-4" novalidate @submit.prevent="submit">
            <div v-if="mode === 'register'" class="space-y-2">
              <Label for="name">Name</Label>
              <Input id="name" v-model="form.name" autocomplete="name" :aria-invalid="Boolean(errors.name)" />
              <p v-if="errors.name" class="text-sm text-red-600">{{ errors.name }}</p>
            </div>
            <div class="space-y-2">
              <Label for="email">Email</Label>
              <Input id="email" v-model="form.email" type="email" autocomplete="email" :aria-invalid="Boolean(errors.email)" />
              <p v-if="errors.email" class="text-sm text-red-600">{{ errors.email }}</p>
            </div>
            <div class="space-y-2">
              <Label for="password">Password</Label>
              <Input
                id="password"
                v-model="form.password"
                type="password"
                :autocomplete="mode === 'login' ? 'current-password' : 'new-password'"
                :aria-invalid="Boolean(errors.password)"
              />
              <p v-if="errors.password" class="text-sm text-red-600">{{ errors.password }}</p>
            </div>

            <Button type="submit" class="w-full rounded-full" :disabled="loading">
              {{ loading ? "Please wait..." : mode === "login" ? "Login" : "Create account" }}
            </Button>

            <Button type="button" variant="ghost" class="w-full" @click="toggleMode">
              {{ mode === "login" ? "Need an account? Register" : "Already have an account? Login" }}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  </div>
</template>
