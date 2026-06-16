<template>
  <div class="sidebar-widget">
    <h3 class="widget-title">{{ title }}</h3>
    <slot />
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string }>()
</script>

<style scoped>
.sidebar-widget {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.widget-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}
</style>
