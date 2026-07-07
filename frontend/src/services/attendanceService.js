import http from './httpClient'

export function clockIn() {
  return http.post('/api/attendance/clock-in')
}

export function clockOut(remark) {
  return http.post('/api/attendance/clock-out', remark ? { remark } : {})
}

export function getTodayRecord() {
  return http.get('/api/attendance/records/today')
}

export function listRecords(params) {
  return http.get('/api/attendance/records', { params })
}
