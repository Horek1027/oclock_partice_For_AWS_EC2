<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import logo from '@/assets/logo.jpg'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

async function handleSubmit() {
  errorMessage.value = ''
  isLoading.value = true
  try {
    await authStore.login({ email: email.value, password: password.value })
    router.push({ name: 'clock' })
  } catch (err) {
    errorMessage.value = err.message
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <img :src="logo" alt="邦布" class="logo" />
      <h1 class="title">邦布打卡</h1>
      <p class="subtitle">歡迎回來，請登入你的帳號</p>

      <form class="login-form" @submit.prevent="handleSubmit">
        <label class="field">
          <span class="field-label">Email</span>
          <input v-model="email" type="email" required placeholder="you@example.com" />
        </label>

        <label class="field">
          <span class="field-label">密碼</span>
          <input v-model="password" type="password" required placeholder="請輸入密碼" />
        </label>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

        <button type="submit" class="submit-btn" :disabled="isLoading">
          {{ isLoading ? '登入中...' : '登入' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at 18% 18%, var(--color-primary-light), transparent 45%),
    radial-gradient(circle at 82% 82%, var(--color-primary-light), transparent 42%),
    var(--color-bg);
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft);
  padding: 40px 32px;
  text-align: center;
}

.logo {
  width: 96px;
  height: 96px;
  object-fit: contain;
  margin-bottom: 12px;
}

.title {
  margin: 0 0 4px;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
}

.subtitle {
  margin: 0 0 28px;
  font-size: 14px;
  color: var(--color-text-muted);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
  text-align: left;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
}

.field input {
  padding: 12px 14px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  font-size: 15px;
  outline: none;
  transition: border-color 0.15s ease;
}

.field input:focus {
  border-color: var(--color-primary);
}

.error-message {
  margin: -6px 0 0;
  font-size: 13px;
  color: #c0392b;
}

.submit-btn {
  margin-top: 8px;
  padding: 12px;
  border: none;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s ease;
}

.submit-btn:hover:not(:disabled) {
  background: var(--color-primary-dark);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
