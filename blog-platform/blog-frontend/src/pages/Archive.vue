<template>
  <div class="archive-page">
    <h1 class="page-title">文章归档</h1>

    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="8" animated />
    </div>

    <el-empty v-else-if="!archives.length" description="暂无文章" />

    <div v-else class="archive-list">
      <div v-for="group in archives" :key="`${group.year}-${group.month}`" class="archive-group">
        <h2 class="archive-month">
          {{ group.year }} 年 {{ String(group.month).padStart(2, '0') }} 月
          <span class="archive-count">{{ group.articles.length }} 篇</span>
        </h2>
        <div class="archive-articles">
          <div
            v-for="article in group.articles"
            :key="article.id"
            class="archive-item"
            @click="$router.push(`/article/${article.slug}`)"
          >
            <span class="archive-date">{{ formatDay(article.publishedAt) }}</span>
            <span class="archive-title">{{ article.title }}</span>
            <span class="archive-meta">
              <el-tag v-if="article.category" size="small" type="info">
                {{ article.category.name }}
              </el-tag>
              <span class="archive-views">
                <el-icon><View /></el-icon> {{ article.viewCount }}
              </span>
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getArticleList } from '@/api/article'
import type { ArticleListItem } from '@/types'
import dayjs from 'dayjs'

interface ArchiveGroup {
  year: number
  month: number
  articles: ArticleListItem[]
}

const loading = ref(false)
const archives = ref<ArchiveGroup[]>([])

function formatDay(dateStr: string | null) {
  if (!dateStr) return ''
  return dayjs(dateStr).format('MM-DD')
}

onMounted(async () => {
  loading.value = true
  try {
    // 获取所有已发布文章（最大 200 篇用于归档）
    const result = await getArticleList({ page: 1, size: 200, sort: 'publishedAt', order: 'desc' })
    // 按年月分组
    const grouped = new Map<string, ArchiveGroup>()
    for (const article of result.records) {
      if (!article.publishedAt) continue
      const d = dayjs(article.publishedAt)
      const key = `${d.year()}-${d.month() + 1}`
      if (!grouped.has(key)) {
        grouped.set(key, {
          year: d.year(),
          month: d.month() + 1,
          articles: []
        })
      }
      grouped.get(key)!.articles.push(article)
    }
    archives.value = Array.from(grouped.values())
  } catch {
    // 静默
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.archive-page {
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

.archive-group {
  margin-bottom: 24px;
}

.archive-month {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.archive-count {
  font-size: 13px;
  color: #999;
  font-weight: 400;
  margin-left: 8px;
}

.archive-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 0;
  border-bottom: 1px dashed #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.archive-item:hover {
  background: #fafafa;
}

.archive-date {
  font-size: 13px;
  color: #999;
  min-width: 50px;
}

.archive-title {
  flex: 1;
  font-size: 15px;
  color: #333;
}

.archive-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.archive-views {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #bbb;
}
</style>
