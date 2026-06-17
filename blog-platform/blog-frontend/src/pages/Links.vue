<template>
  <div class="links-page">
    <h1 class="page-title">友情链接</h1>

    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="3" animated />
    </div>

    <el-empty v-else-if="!links.length" description="暂无友链" />

    <div v-else class="links-grid">
      <a
        v-for="link in links"
        :key="link.id"
        :href="link.url"
        target="_blank"
        rel="noopener noreferrer"
        class="link-card"
      >
        <el-avatar :size="48" :src="link.logo || undefined">
          {{ link.name.charAt(0) }}
        </el-avatar>
        <div class="link-info">
          <span class="link-name">{{ link.name }}</span>
          <span v-if="link.description" class="link-desc">{{ link.description }}</span>
        </div>
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import type { Link } from '@/types'

const loading = ref(false)
const links = ref<Link[]>([])

onMounted(async () => {
  loading.value = true
  try {
    // 友链接口，拦截器已自动解包 {code, data} → data
    const result = await request.get<unknown, Link[]>('/links')
    links.value = Array.isArray(result) ? result : []
  } catch {
    // 友链接口可能暂未实现，静默
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.links-page {
  max-width: 860px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  margin-bottom: 24px;
}

.loading-wrapper {
  padding: 20px 0;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.link-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  text-decoration: none;
  color: inherit;
  transition: box-shadow 0.2s, transform 0.2s;
}

.link-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.link-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.link-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.link-desc {
  font-size: 13px;
  color: #999;
}
</style>
