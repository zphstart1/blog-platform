<template>
  <div class="home-page">
    <h1 class="page-title">最新文章</h1>
    <div class="article-list">
      <article
        v-for="article in articles"
        :key="article.id"
        class="article-card"
      >
        <NuxtLink :to="`/article/${article.slug}`">
          <h2>{{ article.title }}</h2>
        </NuxtLink>
        <div class="article-meta">
          <span>{{ article.author.nickname }}</span>
          <span>{{ formatDate(article.publishedAt) }}</span>
          <span>{{ article.viewCount }} 阅读</span>
          <span v-if="article.category">{{ article.category.name }}</span>
        </div>
        <p v-if="article.summary" class="article-summary">{{ article.summary }}</p>
        <div v-if="article.tags.length" class="article-tags">
          <span v-for="tag in article.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
        </div>
      </article>
    </div>

    <div class="pagination">
      <NuxtLink
        v-if="page > 1"
        :to="`/?page=${page - 1}`"
        class="page-btn"
      >
        上一页
      </NuxtLink>
      <span class="page-info">第 {{ page }} / {{ totalPages }} 页</span>
      <NuxtLink
        v-if="page < totalPages"
        :to="`/?page=${page + 1}`"
        class="page-btn"
      >
        下一页
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { fetchArticles } from '~/utils/api'
import type { ArticleItem } from '~/utils/api'
import dayjs from 'dayjs'

const route = useRoute()
const page = computed(() => Number(route.query.page) || 1)

// SSG 预渲染时在服务端获取数据
const { data } = await useAsyncData(
  `articles-page-${page.value}`,
  () => fetchArticles(page.value, 10)
)

const articles = computed<ArticleItem[]>(() => data.value?.records ?? [])
const totalPages = computed(() => data.value?.pages ?? 1)

function formatDate(dateStr: string | null) {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD')
}
</script>

<style scoped>
.page-title {
  font-size: 24px;
  margin-bottom: 24px;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.article-card a {
  text-decoration: none;
}

.article-card h2 {
  font-size: 20px;
  color: #333;
  margin-bottom: 8px;
}

.article-card h2:hover {
  color: #409eff;
}

.article-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
  margin-bottom: 10px;
}

.article-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.article-tags {
  display: flex;
  gap: 8px;
}

.tag {
  font-size: 12px;
  background: #f0f0f0;
  color: #666;
  padding: 2px 8px;
  border-radius: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
}

.page-btn {
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: #333;
  text-decoration: none;
}

.page-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.page-info {
  font-size: 14px;
  color: #999;
}
</style>
