<template>
  <div class="comment-review-page">
    <h3 class="page-title">评论审核队列</h3>

    <el-table :data="comments" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="authorName" label="评论者" width="120" />
      <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
      <el-table-column prop="articleTitle" label="所属文章" width="200" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="140" />
      <el-table-column prop="createdAt" label="提交时间" width="170">
        <template #default="{ row }">
          {{ dayjs(row.createdAt).format('YYYY-MM-DD HH:mm') }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="success" size="small" @click="handleReview(row.id, 'approve')">
            <el-icon><Select /></el-icon> 通过
          </el-button>
          <el-button type="warning" size="small" @click="handleReview(row.id, 'reject')">
            拒绝
          </el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" size="small" :icon="Delete" />
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && !comments.length" description="暂无待审核评论" />

    <div class="pagination-wrapper" v-if="total > 0">
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
import { Delete } from '@element-plus/icons-vue'
import { getPendingComments, reviewComment, deleteComment } from '@/api/comment'
import type { Comment } from '@/types'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const comments = ref<Comment[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)

async function fetchList() {
  loading.value = true
  try {
    const result = await getPendingComments({ page: page.value, size: size.value })
    comments.value = result.records
    total.value = result.total
  } catch { /* handled */ }
  finally { loading.value = false }
}

async function handleReview(id: number, action: 'approve' | 'reject') {
  try {
    await reviewComment(id, action)
    ElMessage.success({ message: action === 'approve' ? '审核通过' : '已拒绝', duration: 1000 })
    fetchList()
  } catch { /* handled */ }
}

async function handleDelete(id: number) {
  try {
    await deleteComment(id)
    ElMessage.success({ message: '删除成功', duration: 1000 })
    fetchList()
  } catch { /* handled */ }
}

onMounted(() => fetchList())
</script>

<style scoped>
.page-title {
  font-size: 18px;
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
