<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import logo from '@/assets/logo.jpg'

const authStore = useAuthStore()

const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

function handleLogout() {
  authStore.logout()
}
</script>

<template>
  <header class="topbar">
    <router-link to="/clock" class="brand">
      <img :src="logo" alt="邦布" class="topbar-logo" />
      <span class="topbar-title">邦布打卡</span>
    </router-link>

    <nav v-if="isAdmin" class="admin-nav">
      <div class="nav-item">
        <span class="nav-link">員工管理</span>
        <div class="dropdown">
          <router-link to="/admin/members/new" class="dropdown-item">新增員工</router-link>
        </div>
      </div>
      <router-link to="/attendance/records" class="nav-link">打卡紀錄</router-link>
    </nav>

    <button class="logout-btn" @click="handleLogout">登出</button>
  </header>
</template>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.topbar-logo {
  width: 36px;
  height: 36px;
  object-fit: contain;
}

.topbar-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
}

.admin-nav {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-left: 32px;
  flex: 1;
}

.nav-item {
  position: relative;
}

.nav-item::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  height: 8px;
}

.nav-link {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-muted);
  text-decoration: none;
  cursor: pointer;
  transition: color 0.15s ease;
}

.nav-link:hover,
.nav-item:hover .nav-link {
  color: var(--color-primary-dark);
}

.dropdown {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 8px;
  min-width: 140px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-soft);
  overflow: hidden;
  z-index: 10;
}

.nav-item:hover .dropdown {
  display: block;
}

.dropdown-item {
  display: block;
  padding: 10px 16px;
  font-size: 14px;
  color: var(--color-text);
  text-decoration: none;
  white-space: nowrap;
}

.dropdown-item:hover {
  background: var(--color-primary-light);
}

.logout-btn {
  margin-left: auto;
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--color-text-muted);
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.15s ease, color 0.15s ease;
}

.logout-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary-dark);
}
</style>
