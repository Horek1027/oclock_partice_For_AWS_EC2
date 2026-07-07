<script setup>
import AppNavBar from '@/components/AppNavBar.vue'
</script>

<template>
  <div class="authenticated-layout">
    <AppNavBar />
    <router-view />
  </div>
</template>

<style scoped>
.authenticated-layout {
  min-height: 100vh;
  background: var(--color-bg);
}
</style>
