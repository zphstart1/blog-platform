<template>
  <div class="archive-page">
    <Head>
      <title>文章归档 - 个人博客</title>
    </Head>

    <h1>文章归档</h1>
    <div v-for="group in archives" :key="`${group.year}-${group.month}`" class="archive-group">
      <h2>{{ group.year }} 年 {{ String(group.month).padStart(2, '0') }} 月</h2>
      <div v-for="article in group.articles" :key="article.id" class="archive-item">
        <NuxtLink :to="`/article/${article.slug}`">
          <span class="archive-date">{{ formatDay(article.publishedAt) }}</span>
          {{ article.title }}
        </NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { fetchArticles } from '~/utils/api'
import type { ArticleItem } from '~/utils/api'
import dayjs from 'dayjs'

interface ArchiveGroup {
  year: number
  month: number
  articles: ArticleItem[]
}

const { data } = await useAsyncData('archive', async () => {
  const result = await fetchArticles(1, 200)
  const grouped = new Map<string, ArchiveGroup>()
  for (const article of result.records) {
    if (!article.publishedAt) continue
    const d = dayjs(article.publishedAt)
    const key = `${d.year()}-${d.month() + 1}`
    if (!grouped.has(key)) {
      grouped.set(key, { year: d.year(), month: d.month() + 1, articles: [] })
    }
    grouped.get(key)!.articles.push(article)
  }
  return Array.from(grouped.values())
})

const archives = computed(() => data.value ?? [])

function formatDay(dateStr: string | null) {
  if (!dateStr) return ''
  return dayjs(dateStr).format('MM-DD')
}
</script>

<style scoped>
.archive-page {
  max-width: 860px;
  margin: 0 auto;
}

.archive-page h1 {
  font-size: 24px;
  margin-bottom: 24px;
}

.archive-group {
  margin-bottom: 24px;
}

.archive-group h2 {
  font-size: 18px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.archive-item {
  padding: 8px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.archive-item a {
  text-decoration: none;
  color: #333;
  font-size: 15px;
}

.archive-item a:hover {
  color: #409eff;
}

.archive-date {
  display: inline-block;
  width: 50px;
  font-size: 13px;
  color: #999;
  margin-right: 12px;
}
</style>
