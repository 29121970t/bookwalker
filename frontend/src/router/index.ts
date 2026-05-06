import { createRouter, createWebHistory } from "vue-router";
import HomeView from "@/views/user/HomeView.vue";
import BrowseView from "@/views/user/BrowseView.vue";
import CartView from "@/views/user/CartView.vue";
import AccountView from "@/views/user/AccountView.vue";
import BookView from "@/views/user/BookView.vue";
import MainView from "@/views/user/MainView.vue";
import LoginView from "@/views/user/LoginView.vue";
import AdminHomeView from "@/views/admin/AdminHomeView.vue";
import AdminBooksView from "@/views/admin/sections/AdminBooksView.vue";
import AdminOrdersView from "@/views/admin/sections/AdminOrdersView.vue";
import AdminTagsView from "@/views/admin/sections/AdminTagsView.vue";
import AdminUsersView from "@/views/admin/sections/AdminUsersView.vue";
import AdminAuthorsView from "@/views/admin/sections/AdminAuthorsView.vue";
import AdminPublishersView from "@/views/admin/sections/AdminPublishersView.vue";
import { useAuthStore } from "@/stores/auth";
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      component: MainView,
      children: [
        {
          path: "",
          name: "home",
          component: HomeView,
          meta: { title: "Bookwalker | Home" },
        },
        {
          path: "/browse",
          name: "browse",
          component: BrowseView,
          meta: { title: "Bookwalker | Browse" },
        },
        {
          path: "/cart",
          name: "cart",
          component: CartView,
          meta: { title: "Bookwalker | Cart" },
        },
        {
          path: "/account",
          name: "account",
          component: AccountView,
          meta: { title: "Bookwalker | Account", requiresAuth: true },
        },
        {
          path: "/books/:id",
          name: "book",
          component: BookView,
          meta: { title: "Bookwalker | Book" },
        },
      ],
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
      meta: { title: "Bookwalker | Login", guestOnly: true },
    },
    {
      path: "/admin",
      component: AdminHomeView,
      meta: { title: "Bookwalker | Admin", requiresAdmin: true },
      redirect: { name: "admin-books" },
      children: [
        {
          path: "books",
          name: "admin-books",
          component: AdminBooksView,
          meta: { title: "Bookwalker | Admin Books", adminSection: "books", requiresAdmin: true },
        },
        {
          path: "orders",
          name: "admin-orders",
          component: AdminOrdersView,
          meta: { title: "Bookwalker | Admin Orders", adminSection: "orders", requiresAdmin: true },
        },
        {
          path: "tags",
          name: "admin-tags",
          component: AdminTagsView,
          meta: { title: "Bookwalker | Admin Tags", adminSection: "tags", requiresAdmin: true },
        },
        {
          path: "users",
          name: "admin-users",
          component: AdminUsersView,
          meta: { title: "Bookwalker | Admin Users", adminSection: "users", requiresAdmin: true },
        },
        {
          path: "authors",
          name: "admin-authors",
          component: AdminAuthorsView,
          meta: { title: "Bookwalker | Admin Authors", adminSection: "authors", requiresAdmin: true },
        },
        {
          path: "publishers",
          name: "admin-publishers",
          component: AdminPublishersView,
          meta: { title: "Bookwalker | Admin Publishers", adminSection: "publishers", requiresAdmin: true },
        },
      ],
    },
  ],
  scrollBehavior() {
    return { top: 0, behavior: "smooth" };
  },
});

router.beforeEach(async (to) => {
  const auth = useAuthStore();
  await auth.restoreSession();

  if (to.meta.requiresAdmin && !auth.isAdmin.value) {
    return { name: auth.isAuthenticated.value ? "home" : "login" };
  }

  if (to.meta.requiresAuth && !auth.isAuthenticated.value) {
    return { name: "login", query: { redirect: to.fullPath } };
  }

  if (to.meta.guestOnly && auth.isAuthenticated.value) {
    return { name: "account" };
  }
});

router.afterEach((to) => {
  document.title =
    typeof to.meta.title === "string" ? to.meta.title : "Bookwalker";
});

export default router;
