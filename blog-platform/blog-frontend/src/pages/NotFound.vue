<template>
  <div class="not-found-page">
    <div class="not-found-card">
      <h1 class="code-404">404</h1>
      <p class="desc">页面不存在或已被删除</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
}

.not-found-card {
  text-align: center;
}

.code-404 {
  font-size: 80px;
  font-weight: 700;
  color: #e0e0e0;
  margin-bottom: 8px;
}

.desc {
  font-size: 16px;
  color: #999;
  margin-bottom: 24px;
}
</style>
