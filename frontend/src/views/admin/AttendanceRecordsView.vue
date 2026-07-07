<script setup>
import { ref, onMounted } from 'vue'
import { listRecords } from '@/services/attendanceService'

const STATUS_LABELS = {
  NORMAL: '正常',
  LATE: '遲到',
  EARLY_LEAVE: '早退',
  ABSENT: '缺席',
}

const startDate = ref('')
const endDate = ref('')
const records = ref([])
const errorMessage = ref('')
const isLoading = ref(false)

function formatTime(isoString) {
  if (!isoString) return '-'
  return new Date(isoString).toLocaleTimeString('zh-TW', { hour12: false })
}

async function loadRecords() {
  errorMessage.value = ''
  isLoading.value = true
  try {
    records.value = await listRecords({
      startDate: startDate.value || undefined,
      endDate: endDate.value || undefined,
    })
  } catch (err) {
    errorMessage.value = err.message
  } finally {
    isLoading.value = false
  }
}

onMounted(loadRecords)
</script>

<template>
  <div class="page">
    <div class="card">
      <h1 class="page-title">打卡紀錄</h1>

      <div class="filters">
        <label class="filter-field">
          <span class="field-label">起始日期</span>
          <input v-model="startDate" type="date" />
        </label>
        <label class="filter-field">
          <span class="field-label">結束日期</span>
          <input v-model="endDate" type="date" />
        </label>
        <button class="filter-btn" :disabled="isLoading" @click="loadRecords">查詢</button>
      </div>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <table v-if="records.length" class="records-table">
        <thead>
          <tr>
            <th>日期</th>
            <th>上班時間</th>
            <th>下班時間</th>
            <th>狀態</th>
            <th>備註</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="record in records" :key="record.id">
            <td>{{ record.workDate }}</td>
            <td>{{ formatTime(record.clockInTime) }}</td>
            <td>{{ formatTime(record.clockOutTime) }}</td>
            <td>{{ STATUS_LABELS[record.status] || record.status }}</td>
            <td>{{ record.remark || '-' }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else-if="!isLoading" class="empty-text">目前沒有出勤紀錄</p>
    </div>
  </div>
</template>

<style scoped>
.page {
  max-width: 720px;
  margin: 0 auto;
  padding: 40px 24px;
}

.card {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft);
  padding: 32px;
}

.page-title {
  margin: 0 0 24px;
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text);
}

.filters {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
}

.filter-field input {
  padding: 10px 12px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  font-size: 14px;
  outline: none;
}

.filter-btn {
  padding: 10px 20px;
  border: none;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.filter-btn:hover:not(:disabled) {
  background: var(--color-primary-dark);
}

.filter-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  font-size: 13px;
  color: #c0392b;
}

.empty-text {
  font-size: 14px;
  color: var(--color-text-muted);
}

.records-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.records-table th,
.records-table td {
  text-align: left;
  padding: 10px 12px;
  border-bottom: 1px solid var(--color-border);
}

.records-table th {
  color: var(--color-text-muted);
  font-weight: 600;
}
</style>
