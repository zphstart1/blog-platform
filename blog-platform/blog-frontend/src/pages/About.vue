<template>
  <div class="about-page">
    <div class="about-card">
      <el-avatar :size="80" class="about-avatar">
        {{ '博主' }}
      </el-avatar>
      <h1>关于本站</h1>
      <p class="about-desc">
        这是一个基于 Vue 3 + Spring Boot 构建的个人博客系统。
        在这里，我会分享技术文章、学习笔记和生活感悟。
      </p>
      <el-divider />
      <div class="about-stack">
        <h3>技术栈</h3>
        <div class="stack-tags">
          <el-tag>Vue 3</el-tag>
          <el-tag type="success">TypeScript</el-tag>
          <el-tag type="warning">Element Plus</el-tag>
          <el-tag type="danger">Pinia</el-tag>
          <el-tag>Vue Router 4</el-tag>
          <el-tag type="info">Vite</el-tag>
          <el-tag type="success">Spring Boot</el-tag>
          <el-tag>MySQL 8.0</el-tag>
          <el-tag type="danger">Redis</el-tag>
          <el-tag>Nuxt 3 SSG</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about-page {
  max-width: 600px;
  margin: 0 auto;
}

.about-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
}

.about-avatar {
  margin-bottom: 20px;
}

.about-card h1 {
  font-size: 24px;
  margin-bottom: 16px;
}

.about-desc {
  color: #666;
  line-height: 1.8;
  font-size: 15px;
}

.about-stack {
  text-align: left;
}

.about-stack h3 {
  font-size: 16px;
  margin-bottom: 12px;
}

.stack-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
