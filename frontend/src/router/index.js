import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import LoginView from '@/views/LoginView.vue'
import ClockView from '@/views/ClockView.vue'
import MemberCreateView from '@/views/admin/MemberCreateView.vue'
import AttendanceRecordsView from '@/views/admin/AttendanceRecordsView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'login', component: LoginView },
    {
      path: '/',
      component: AuthenticatedLayout,
      meta: { requiresAuth: true },
      children: [
        { path: 'clock', name: 'clock', component: ClockView },
        {
          path: 'admin/members/new',
          name: 'admin-member-create',
          component: MemberCreateView,
          meta: { requiresAdmin: true },
        },
        {
          path: 'attendance/records',
          name: 'attendance-records',
          component: AttendanceRecordsView,
          meta: { requiresAdmin: true },
        },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.token) {
    return { name: 'login' }
  }

  if (to.meta.requiresAuth && authStore.token && !authStore.user) {
    try {
      await authStore.hydrateUser()
    } catch {
      authStore.logout()
      return { name: 'login' }
    }
  }

  if (to.meta.requiresAdmin && authStore.user?.role !== 'ADMIN') {
    return { name: 'clock' }
  }
})

export default router
