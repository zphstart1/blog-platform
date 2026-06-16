<template>
  <div class="home-page">
    <div class="home-layout">
      <!-- 左侧主内容 -->
      <div class="home-main">
        <!-- 分类/标签筛选 -->
        <div class="filter-bar">
          <el-radio-group v-model="filterType" size="small" @change="handleFilterTypeChange">
            <el-radio-button value="all">全部</el-radio-button>
            <el-radio-button value="category">按分类</el-radio-button>
            <el-radio-button value="tag">按标签</el-radio-button>
          </el-radio-group>
          <el-select
            v-if="filterType !== 'all'"
            v-model="filterId"
            :placeholder="filterType === 'category' ? '选择分类' : '选择标签'"
            size="small"
            clearable
            class="filter-select"
            @change="handleFilterChange"
          >
            <el-option
              v-for="item in filterOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <div class="sort-actions">
            <el-button
              size="small"
              :type="sortField === 'publishedAt' ? 'primary' : 'default'"
              @click="setSort('publishedAt')"
            >
              最新
            </el-button>
            <el-button
              size="small"
              :type="sortField === 'viewCount' ? 'primary' : 'default'"
              @click="setSort('viewCount')"
            >
              热门
            </el-button>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-wrapper">
          <el-skeleton v-for="i in 5" :key="i" :rows="3" animated style="margin-bottom: 20px" />
        </div>

        <!-- 错误状态 -->
        <el-empty v-else-if="error" description="加载失败，请稍后重试">
          <el-button type="primary" @click="fetchArticles">重新加载</el-button>
        </el-empty>

        <!-- 空状态 -->
        <el-empty v-else-if="!articles.length" description="暂无文章" />

        <!-- 文章列表 -->
        <template v-else>
          <ArticleCard v-for="article in articles" :key="article.id" :article="article" />

          <!-- 分页 -->
          <div v-if="total > 0" class="pagination-wrapper">
            <el-pagination
              v-model:current-page="page"
              v-model:page-size="size"
              :total="total"
              :page-sizes="PAGINATION.pageSizes"
              layout="total, prev, pager, next, sizes"
              @change="fetchArticles"
            />
          </div>
        </template>
      </div>

      <!-- 右侧边栏 -->
      <aside class="home-sidebar">
        <SidebarWidget title="分类">
          <div class="category-list">
            <el-tag
              v-for="cat in appStore.categories"
              :key="cat.id"
              class="category-item"
              @click="selectCategory(cat.id)"
            >
              {{ cat.name }} ({{ cat.articleCount || 0 }})
            </el-tag>
          </div>
        </SidebarWidget>

        <SidebarWidget title="标签云">
          <TagCloud :tags="tagCloudTags" @tag-click="selectTag" />
        </SidebarWidget>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import ArticleCard from '@/components/ArticleCard.vue'
import TagCloud from '@/components/TagCloud.vue'
import SidebarWidget from '@/components/SidebarWidget.vue'
import { getArticleList } from '@/api/article'
import { getTagCloud } from '@/api/category'
import { useAppStore } from '@/stores/app'
import { PAGINATION } from '@/constants'
import type { ArticleListItem, Tag, ArticleSortField } from '@/types'

const route = useRoute()
const appStore = useAppStore()

const loading = ref(false)
const error = ref(false)
const articles = ref<ArticleListItem[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(PAGINATION.defaultSize)
const sortField = ref<ArticleSortField>('publishedAt')

const filterType = ref<'all' | 'category' | 'tag'>('all')
const filterId = ref<number | null>(null)
const tagCloudTags = ref<Tag[]>([])

const filterOptions = computed(() => {
  if (filterType.value === 'category') return appStore.categories
  return appStore.tags
})

// 从 URL 初始化筛选
function initFromRoute() {
  if (route.params.slug) {
    if (route.name === 'CategoryArticles') {
      const cat = appStore.categories.find(c => c.slug === route.params.slug)
      if (cat) {
        filterType.value = 'category'
        filterId.value = cat.id
      }
    } else if (route.name === 'TagArticles') {
      const tag = appStore.tags.find(t => t.slug === route.params.slug)
      if (tag) {
        filterType.value = 'tag'
        filterId.value = tag.id
      }
    }
  }
}

async function fetchArticles() {
  loading.value = true
  error.value = false
  try {
    const params: Record<string, unknown> = {
      page: page.value,
      size: size.value,
      sort: sortField.value,
      order: 'desc'
    }
    if (filterType.value === 'category' && filterId.value) {
      params.categoryId = filterId.value
    } else if (filterType.value === 'tag' && filterId.value) {
      params.tagId = filterId.value
    }
    const result = await getArticleList(params as any)
    articles.value = result.records
    total.value = result.total
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

function handleFilterTypeChange() {
  filterId.value = null
  page.value = 1
  fetchArticles()
}

function handleFilterChange() {
  page.value = 1
  fetchArticles()
}

function selectCategory(id: number) {
  filterType.value = 'category'
  filterId.value = id
  page.value = 1
  fetchArticles()
}

function selectTag(tag: Tag) {
  filterType.value = 'tag'
  filterId.value = tag.id
  page.value = 1
  fetchArticles()
}

function setSort(field: ArticleSortField) {
  if (sortField.value === field) return
  sortField.value = field
  page.value = 1
  fetchArticles()
}

// 监听路由参数变化
watch(() => route.params.slug, () => {
  initFromRoute()
  fetchArticles()
})

onMounted(async () => {
  // 加载标签云
  try {
    tagCloudTags.value = await getTagCloud()
  } catch { /* 静默 */ }
  initFromRoute()
  fetchArticles()
})
</script>

<style scoped>
.home-layout {
  display: flex;
  gap: 24px;
}

.home-main {
  flex: 1;
  min-width: 0;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filter-select {
  width: 160px;
}

.sort-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.loading-wrapper {
  padding: 20px 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.home-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.category-item {
  cursor: pointer;
}

@media (max-width: 768px) {
  .home-layout {
    flex-direction: column;
  }
  .home-sidebar {
    width: 100%;
  }
}
</style>
