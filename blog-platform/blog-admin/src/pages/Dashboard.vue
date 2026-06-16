<template>
  <div class="dashboard">
    <h3>欢迎回来，{{ adminStore.user?.nickname || '管理员' }}</h3>
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="已发布文章" :value="stats.published" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="草稿" :value="stats.drafts" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待审核评论" :value="stats.pendingComments">
            <template #suffix>
              <el-tag v-if="stats.pendingComments > 0" size="small" type="danger">待处理</el-tag>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="总访问量" :value="stats.totalViews" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { useAdminStore } from '@/stores/user'
import { getAdminArticleList } from '@/api/article'
import { getPendingComments } from '@/api/comment'

const adminStore = useAdminStore()

const stats = reactive({
  published: 0,
  drafts: 0,
  pendingComments: 0,
  totalViews: 0
})

onMounted(async () => {
  try {
    const [pubRes, draftRes, pendingRes] = await Promise.all([
      getAdminArticleList({ status: 'PUBLISHED', size: 1 }),
      getAdminArticleList({ status: 'DRAFT', size: 1 }),
      getPendingComments({ size: 1 })
    ])
    stats.published = pubRes.total
    stats.drafts = draftRes.total
    stats.pendingComments = pendingRes.total
  } catch { /* 静默 */ }
})
</script>

<style scoped>
.dashboard h3 {
  margin-bottom: 24px;
  font-size: 20px;
}

.stats-row {
  margin-bottom: 24px;
}
</style>
