<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { clockIn, clockOut, getTodayRecord } from '@/services/attendanceService'

const STATUS_LABELS = {
  NORMAL: '正常',
  LATE: '遲到',
  EARLY_LEAVE: '早退',
  ABSENT: '缺席',
}

const MIN_WORK_HOURS = 9

const authStore = useAuthStore()

const now = ref(new Date())
let timer = null

onMounted(async () => {
  timer = setInterval(() => {
    now.value = new Date()
  }, 1000)
  await loadTodayRecord()
})

onUnmounted(() => {
  clearInterval(timer)
})

const dateText = computed(() =>
  now.value.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
  }),
)
const timeText = computed(() => now.value.toLocaleTimeString('zh-TW', { hour12: false }))

const clockInTime = ref('')
const clockOutTime = ref('')
const status = ref('')
const isSubmitting = ref(false)
const errorMessage = ref('')
const earlyLeaveReason = ref('')
const reasonError = ref('')

const hasClockedIn = computed(() => !!clockInTime.value)
const hasClockedOut = computed(() => !!clockOutTime.value)
const statusLabel = computed(() => STATUS_LABELS[status.value] || '')
const greetingName = computed(() => authStore.user?.name || '訪客')

const elapsedHours = computed(() => {
  if (!hasClockedIn.value || hasClockedOut.value) return 0
  return (now.value.getTime() - new Date(clockInTime.value).getTime()) / (1000 * 60 * 60)
})
const requiresEarlyLeaveReason = computed(
  () => hasClockedIn.value && !hasClockedOut.value && elapsedHours.value < MIN_WORK_HOURS,
)

function formatTime(isoString) {
  if (!isoString) return '尚未打卡'
  return new Date(isoString).toLocaleTimeString('zh-TW', { hour12: false })
}

async function loadTodayRecord() {
  try {
    const record = await getTodayRecord()
    if (record) {
      clockInTime.value = record.clockInTime
      clockOutTime.value = record.clockOutTime
      status.value = record.status
    }
  } catch (err) {
    errorMessage.value = err.message
  }
}

async function handleClockIn() {
  errorMessage.value = ''
  isSubmitting.value = true
  try {
    const data = await clockIn()
    clockInTime.value = data.clockInTime
    status.value = data.status
  } catch (err) {
    errorMessage.value = err.message
  } finally {
    isSubmitting.value = false
  }
}

async function handleClockOut() {
  errorMessage.value = ''
  reasonError.value = ''

  if (requiresEarlyLeaveReason.value && !earlyLeaveReason.value.trim()) {
    reasonError.value = '需要輸入提早下班理由，不能空白'
    return
  }

  isSubmitting.value = true
  try {
    const data = await clockOut(requiresEarlyLeaveReason.value ? earlyLeaveReason.value.trim() : undefined)
    clockOutTime.value = data.clockOutTime
    status.value = data.status
  } catch (err) {
    errorMessage.value = err.message
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <main class="clock-main">
    <p class="greeting">哈囉，{{ greetingName }}</p>
    <p class="date-text">{{ dateText }}</p>
    <p class="time-text">{{ timeText }}</p>

    <button
      class="clock-btn"
      :class="{ 'clock-btn--out': hasClockedIn && !hasClockedOut }"
      :disabled="isSubmitting || hasClockedOut"
      @click="hasClockedIn ? handleClockOut() : handleClockIn()"
    >
      <span v-if="hasClockedOut">今日已完成打卡</span>
      <span v-else-if="hasClockedIn">下班打卡</span>
      <span v-else>上班打卡</span>
    </button>

    <div v-if="requiresEarlyLeaveReason" class="reason-field">
      <input
        v-model="earlyLeaveReason"
        type="text"
        placeholder="需要輸入提早下班理由，不能空白"
      />
      <p v-if="reasonError" class="error-message">{{ reasonError }}</p>
    </div>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

    <div class="record-card">
      <div class="record-row">
        <span class="record-label">上班時間</span>
        <span class="record-value">{{ formatTime(clockInTime) }}</span>
      </div>
      <div class="record-row">
        <span class="record-label">下班時間</span>
        <span class="record-value">{{ formatTime(clockOutTime) }}</span>
      </div>
      <div v-if="statusLabel" class="record-row">
        <span class="record-label">狀態</span>
        <span class="record-value">{{ statusLabel }}</span>
      </div>
    </div>
  </main>
</template>

<style scoped>
.clock-main {
  max-width: 420px;
  margin: 0 auto;
  padding: 48px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.greeting {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
}

.date-text {
  margin: 0 0 4px;
  font-size: 15px;
  color: var(--color-text-muted);
}

.time-text {
  margin: 0 0 32px;
  font-size: 40px;
  font-weight: 700;
  color: var(--color-text);
  font-variant-numeric: tabular-nums;
}

.clock-btn {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  border: none;
  background: var(--color-primary);
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: var(--shadow-soft);
  transition: background 0.15s ease, transform 0.15s ease;
}

.clock-btn:hover:not(:disabled) {
  background: var(--color-primary-dark);
  transform: scale(1.03);
}

.clock-btn:disabled {
  background: var(--color-text-muted);
  cursor: not-allowed;
}

.clock-btn--out {
  background: var(--color-text);
}

.clock-btn--out:hover:not(:disabled) {
  background: #1c1a17;
}

.reason-field {
  width: 100%;
  margin-top: 20px;
}

.reason-field input {
  width: 100%;
  padding: 12px 14px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  font-size: 14px;
  outline: none;
  transition: border-color 0.15s ease;
}

.reason-field input:focus {
  border-color: var(--color-primary);
}

.error-message {
  margin: 8px 0 0;
  font-size: 13px;
  color: #c0392b;
}

.record-card {
  width: 100%;
  margin-top: 32px;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft);
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-row {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
}

.record-label {
  color: var(--color-text-muted);
}

.record-value {
  font-weight: 600;
  color: var(--color-text);
}
</style>
