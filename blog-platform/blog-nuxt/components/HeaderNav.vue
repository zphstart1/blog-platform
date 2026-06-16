<template>
  <header class="ssg-header">
    <div class="header-inner">
      <NuxtLink to="/" class="logo">📝 个人博客</NuxtLink>
      <nav>
        <NuxtLink to="/">首页</NuxtLink>
        <NuxtLink to="/archive">归档</NuxtLink>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.ssg-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  text-decoration: none;
}

nav {
  display: flex;
  gap: 24px;
}

nav a {
  color: #666;
  text-decoration: none;
  font-size: 15px;
}

nav a:hover,
nav a.router-link-active {
  color: #409eff;
}
</style>
