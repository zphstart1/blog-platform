<template>
  <div class="article-card" @click="$router.push(`/article/${article.slug}`)">
    <el-image
      v-if="article.coverImage"
      :src="article.coverImage"
      fit="cover"
      class="card-cover"
      lazy
    />
    <div class="card-body">
      <div class="card-meta">
        <el-tag v-if="article.isTop" size="small" type="danger" class="top-tag">置顶</el-tag>
        <el-tag
          v-if="article.category"
          size="small"
          type="info"
          class="category-tag"
        >
          {{ article.category.name }}
        </el-tag>
        <span class="card-date">{{ formatDate(article.publishedAt) }}</span>
        <span class="card-views">
          <el-icon><View /></el-icon> {{ article.viewCount }}
        </span>
      </div>
      <h2 class="card-title">{{ article.title }}</h2>
      <p v-if="article.summary" class="card-summary">{{ article.summary }}</p>
      <div v-if="article.tags.length" class="card-tags">
        <el-tag
          v-for="tag in article.tags"
          :key="tag.id"
          size="small"
          type="warning"
          effect="plain"
          class="tag-item"
        >
          {{ tag.name }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ArticleListItem } from '@/types'
import dayjs from 'dayjs'

defineProps<{
  article: ArticleListItem
}>()

function formatDate(dateStr: string | null) {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD')
}
</script>

<style scoped>
.article-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.3s, transform 0.3s;
  margin-bottom: 16px;
}

.article-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.card-cover {
  width: 100%;
  height: 200px;
}

.card-body {
  padding: 20px;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 13px;
  color: #999;
  flex-wrap: wrap;
}

.top-tag {
  margin-right: 4px;
}

.card-date {
  margin-left: auto;
}

.card-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.card-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tag-item {
  cursor: default;
}
</style>
