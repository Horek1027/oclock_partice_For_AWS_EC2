import http from './httpClient'

export function createMember(payload) {
  return http.post('/api/members', payload)
}
