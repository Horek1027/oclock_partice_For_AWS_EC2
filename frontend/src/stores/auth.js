import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getCurrentUser as getCurrentUserApi, refreshToken as refreshTokenApi } from '@/services/authService'
import { getJwtExpiryMs } from '@/utils/jwt'

const REFRESH_MARGIN_MS = 60 * 1000

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  let logoutTimer = null
  let refreshTimer = null

  function clearTokenTimers() {
    clearTimeout(logoutTimer)
    clearTimeout(refreshTimer)
    logoutTimer = null
    refreshTimer = null
  }

  function scheduleTokenLifecycle(jwt) {
    clearTokenTimers()
    const expiresAt = getJwtExpiryMs(jwt)
    if (!expiresAt) return

    const msUntilExpiry = expiresAt - Date.now()
    if (msUntilExpiry <= 0) {
      logout()
      return
    }

    logoutTimer = setTimeout(() => logout(), msUntilExpiry)
    refreshTimer = setTimeout(refreshSilently, Math.max(msUntilExpiry - REFRESH_MARGIN_MS, 0))
  }

  async function refreshSilently() {
    try {
      const data = await refreshTokenApi()
      token.value = data.token
      localStorage.setItem('token', data.token)
      scheduleTokenLifecycle(data.token)
    } catch {
      logout()
    }
  }

  async function login(credentials) {
    const data = await loginApi(credentials)
    token.value = data.token
    user.value = data.member
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(data.member))
    scheduleTokenLifecycle(data.token)
  }

  async function hydrateUser() {
    if (!token.value || user.value) return
    const member = await getCurrentUserApi()
    user.value = member
    localStorage.setItem('user', JSON.stringify(member))
  }

  async function logout() {
    clearTokenTimers()
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')

    const router = (await import('@/router')).default
    if (router.currentRoute.value.name !== 'login') {
      router.push({ name: 'login' })
    }
  }

  if (token.value) {
    scheduleTokenLifecycle(token.value)
  }

  return { token, user, login, logout, hydrateUser }
})
