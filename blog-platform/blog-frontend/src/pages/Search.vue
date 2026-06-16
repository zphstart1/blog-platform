<template>
  <div class="search-page">
    <div class="search-header">
      <h1>文章搜索</h1>
      <div class="search-input-wrapper">
        <el-input
          v-model="keyword"
          size="large"
          placeholder="输入关键词搜索文章..."
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><SearchIcon /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" size="large" @click="handleSearch" :loading="loading">
          搜索
        </el-button>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton v-for="i in 5" :key="i" :rows="3" animated style="margin-bottom: 16px" />
    </div>

    <!-- 空搜索词 -->
    <el-empty v-else-if="!searched" description="请输入关键词开始搜索" />

    <!-- 无结果 -->
    <el-empty v-else-if="!results.length" description="未找到相关文章">
      <p class="no-result-tip">试试其他关键词？</p>
    </el-empty>

    <!-- 搜索结果 -->
    <template v-else>
      <div class="search-info">
        找到 <strong>{{ total }}</strong> 篇相关文章，关键词："<em>{{ searchedKeyword }}</em>"
      </div>
      <div class="result-list">
        <div
          v-for="item in results"
          :key="item.id"
          class="result-item"
          @click="$router.push(`/article/${item.slug}`)"
        >
          <h3 class="result-title">
            <span v-html="highlightTitle(item.title)" />
          </h3>
          <div class="result-meta">
            <span>{{ item.author.nickname }}</span>
            <span>{{ formatDate(item.publishedAt) }}</span>
            <span>{{ item.viewCount }} 阅读</span>
            <el-tag v-if="item.category" size="small">{{ item.category.name }}</el-tag>
          </div>
          <p class="result-summary" v-html="item.summary" />
          <div class="result-score">
            相关度：{{ Math.round(item.relevanceScore * 100) / 100 }}
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          :current-page="page"
          :page-size="size"
          :total="total"
          :page-sizes="PAGINATION.pageSizes"
          layout="total, prev, pager, next"
          @current-change="handleCurrentChange"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search as SearchIcon } from '@element-plus/icons-vue'
import { searchArticles } from '@/api/search'
import { PAGINATION } from '@/constants'
import type { SearchResultItem } from '@/types'
import dayjs from 'dayjs'

const keyword = ref('')
const searched = ref(false)
const searchedKeyword = ref('')
const loading = ref(false)
const results = ref<SearchResultItem[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(PAGINATION.defaultSize)

function handleSearch() {
  page.value = 1
  doSearch()
}

function handleCurrentChange(val: number) {
  page.value = val
  doSearch()
}

async function doSearch() {
  const kw = keyword.value.trim()
  if (!kw) return

  loading.value = true
  searched.value = true
  searchedKeyword.value = kw
  try {
    const result = await searchArticles({ keyword: kw, page: page.value, size: size.value })
    results.value = result.records
    total.value = result.total
  } catch {
    results.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

/** 标题也高亮关键词 */
function highlightTitle(title: string): string {
  if (!searchedKeyword.value) return title
  const escaped = searchedKeyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  return title.replace(
    new RegExp(`(${escaped})`, 'gi'),
    '<mark>$1</mark>'
  )
}

function formatDate(dateStr: string) {
  return dayjs(dateStr).format('YYYY-MM-DD')
}
</script>

<style scoped>
.search-page {
  max-width: 860px;
  margin: 0 auto;
}

.search-header {
  text-align: center;
  margin-bottom: 32px;
}

.search-header h1 {
  font-size: 24px;
  margin-bottom: 20px;
}

.search-input-wrapper {
  display: flex;
  gap: 12px;
  max-width: 600px;
  margin: 0 auto;
}

.loading-wrapper {
  padding: 20px 0;
}

.search-info {
  color: #666;
  font-size: 14px;
  margin-bottom: 16px;
}

.search-info em {
  color: #409eff;
  font-style: normal;
  font-weight: 600;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-item {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.result-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.result-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.result-title :deep(mark) {
  background: #fff3cd;
  padding: 0 2px;
  border-radius: 2px;
}

.result-meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #999;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.result-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 8px;
}

.result-summary :deep(mark) {
  background: #fff3cd;
  padding: 0 2px;
  border-radius: 2px;
}

.result-score {
  font-size: 12px;
  color: #bbb;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.no-result-tip {
  color: #999;
  font-size: 14px;
  margin-top: 8px;
}
</style>
