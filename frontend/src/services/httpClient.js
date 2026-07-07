import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response.data.data,
  async (error) => {
    const message = error.response?.data?.message || '網路連線異常，請稍後再試'

    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      await authStore.logout()
    }

    return Promise.reject(new Error(message))
  },
)

export default http
