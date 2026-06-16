<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <p>&copy; {{ year }} 个人博客. Powered by Vue 3 + Element Plus.</p>
    </div>
  </footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
.app-footer {
  background: #fff;
  border-top: 1px solid #eee;
  margin-top: 60px;
  padding: 24px 0;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  color: #999;
  font-size: 13px;
}
</style>
