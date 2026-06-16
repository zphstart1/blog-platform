<template>
  <footer class="ssg-footer">
    <div class="footer-inner">
      <p>&copy; {{ new Date().getFullYear() }} 个人博客. Powered by Nuxt 3 SSG.</p>
    </div>
  </footer>
</template>

<style scoped>
.ssg-footer {
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
