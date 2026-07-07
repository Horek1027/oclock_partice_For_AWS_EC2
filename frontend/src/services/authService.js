import http from './httpClient'

export function login(credentials) {
  return http.post('/api/auth/login', credentials)
}

export function getCurrentUser() {
  return http.get('/api/auth/me')
}

export function refreshToken() {
  return http.post('/api/auth/refresh')
}
