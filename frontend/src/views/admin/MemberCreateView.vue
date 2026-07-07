<script setup>
import { ref } from "vue";
import { createMember } from "@/services/memberService";

const employeeNo = ref("");
const name = ref("");
const email = ref("");
const password = ref("");
const phone = ref("");
const role = ref("EMPLOYEE");
const isSubmitting = ref(false);
const errorMessage = ref("");
const successMessage = ref("");

async function handleSubmit() {
  errorMessage.value = "";
  successMessage.value = "";
  isSubmitting.value = true;
  try {
    const member = await createMember({
      employeeNo: employeeNo.value,
      name: name.value,
      email: email.value,
      password: password.value,
      phone: phone.value || undefined,
      role: role.value || "EMPLOYEE",
    });
    successMessage.value = `已建立員工「${member.name}」（${member.employeeNo}）`;
    employeeNo.value = "";
    name.value = "";
    email.value = "";
    password.value = "";
    phone.value = "";
    role.value = "EMPLOYEE";
  } catch (err) {
    errorMessage.value = err.message;
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<template>
  <div class="page">
    <div class="card">
      <h1 class="page-title">新增員工</h1>

      <form class="form" @submit.prevent="handleSubmit">
        <label class="field">
          <span class="field-label">員工編號</span>
          <input v-model="employeeNo" required />
        </label>

        <label class="field">
          <span class="field-label">姓名</span>
          <input v-model="name" required />
        </label>

        <label class="field">
          <span class="field-label">Email</span>
          <input v-model="email" type="email" required />
        </label>

        <label class="field">
          <span class="field-label">密碼</span>
          <input
            v-model="password"
            type="password"
            required
            minlength="8"
            placeholder="至少 8 碼"
          />
        </label>

        <label class="field">
          <span class="field-label">電話（選填）</span>
          <input v-model="phone" />
        </label>

        <label class="field">
          <span class="field-label">管理權位</span>
          <select v-model="role">
            <option value="EMPLOYEE">員工</option>
            <option value="ADMIN">管理者</option>
          </select>
        </label>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success-message">
          {{ successMessage }}
        </p>

        <button type="submit" class="submit-btn" :disabled="isSubmitting">
          {{ isSubmitting ? "建立中..." : "建立員工" }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.page {
  max-width: 480px;
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

.form {
  display: flex;
  flex-direction: column;
  gap: 18px;
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

.field input,
.field select {
  padding: 12px 14px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  color: var(--color-text);
  font-size: 15px;
  font-family: inherit;
  outline: none;
  transition: border-color 0.15s ease;
}

.field select {
  cursor: pointer;
}

.field input:focus,
.field select:focus {
  border-color: var(--color-primary);
}

.error-message {
  margin: 0;
  font-size: 13px;
  color: #c0392b;
}

.success-message {
  margin: 0;
  font-size: 13px;
  color: #2e7d32;
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
