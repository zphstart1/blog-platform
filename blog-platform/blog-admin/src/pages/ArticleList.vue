<template>
  <div class="article-list-page">
    <div class="page-toolbar">
      <el-button type="primary" @click="$router.push('/articles/new')">
        <el-icon><Edit /></el-icon> 写文章
      </el-button>
      <div class="toolbar-right">
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable size="default" style="width: 140px" @change="fetchList">
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="草稿" value="DRAFT" />
        </el-select>
      </div>
    </div>

    <el-table :data="articles" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="{ row }">
          <router-link :to="`/articles/${row.id}/edit`" class="article-link">
            {{ row.title }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
            {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category.name" label="分类" width="120" />
      <el-table-column label="标签" width="200">
        <template #default="{ row }">
          <el-tag v-for="tag in row.tags" :key="tag.id" size="small" type="warning" effect="plain" style="margin-right:4px">
            {{ tag.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="阅读" width="80" />
      <el-table-column prop="publishedAt" label="发布时间" width="170">
        <template #default="{ row }">
          {{ row.publishedAt ? dayjs(row.publishedAt).format('YYYY-MM-DD HH:mm') : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="$router.push(`/articles/${row.id}/edit`)">
            编辑
          </el-button>
          <el-popconfirm title="确定删除此文章？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button text type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 30]"
        layout="total, prev, pager, next, sizes"
        @change="fetchList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminArticleList, deleteArticle } from '@/api/article'
import type { ArticleListItem } from '@/types'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const articles = ref<ArticleListItem[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const filterStatus = ref<string | null>(null)

async function fetchList() {
  loading.value = true
  try {
    const result = await getAdminArticleList({
      page: page.value,
      size: size.value,
      status: filterStatus.value || undefined
    })
    articles.value = result.records
    total.value = result.total
  } catch { /* handled */ }
  finally { loading.value = false }
}

async function handleDelete(id: number) {
  try {
    await deleteArticle(id)
    ElMessage.success({ message: '删除成功', duration: 1000 })
    fetchList()
  } catch { /* handled */ }
}

onMounted(() => fetchList())
</script>

<style scoped>
.page-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.article-link {
  color: #333;
  text-decoration: none;
}
.article-link:hover {
  color: #409eff;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
