<script setup lang="ts">
import {
  BookOpenText,
  Building2,
  LayoutDashboard,
  PenSquare,
  ShoppingCart,
  Users,
} from "lucide-vue-next"
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuBadge,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"
import { RouterLink } from "vue-router"

type AdminSection = "books" | "orders" | "tags" | "users" | "authors" | "publishers"

defineProps<{
  activeSection: AdminSection
  counts: Record<AdminSection, number>
}>()

const items = [
  { id: "books", title: "Books", icon: BookOpenText, to: { name: "admin-books" } },
  { id: "orders", title: "Orders", icon: ShoppingCart, to: { name: "admin-orders" } },
  // { id: "tags", title: "Tags", icon: Tags, to: { name: "admin-tags" } },
  { id: "users", title: "Users", icon: Users, to: { name: "admin-users" } },
  { id: "authors", title: "Authors", icon: PenSquare, to: { name: "admin-authors" } },
  { id: "publishers", title: "Publishers", icon: Building2, to: { name: "admin-publishers" } },
] as const
</script>

<template>
  <Sidebar variant="inset" collapsible="icon">
    <SidebarHeader class="border-b border-sidebar-border/70 px-3 py-4">
      <div class="flex items-center gap-3 px-2">
        <div class="grid size-10 place-items-center rounded-2xl bg-slate-950 text-white">
          <LayoutDashboard class="size-5" />
        </div>
        <div class="min-w-0">
          <p class="truncate text-sm font-semibold">Bookwalker Admin</p>
          <p class="truncate text-xs text-sidebar-foreground/70">Catalog control panel</p>
        </div>
      </div>
    </SidebarHeader>

    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupLabel>Management</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in items" :key="item.id">
              <SidebarMenuButton :data-active="activeSection === item.id" :is-active="activeSection === item.id" tooltip="Open section" as-child>
                <RouterLink :to="item.to">
                  <component :is="item.icon" />
                  <span>{{ item.title }}</span>
                </RouterLink>
              </SidebarMenuButton>
              <SidebarMenuBadge>{{ counts[item.id] }}</SidebarMenuBadge>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>
  </Sidebar>
</template>
