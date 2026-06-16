<template>
  <div class="comment-form">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      @submit.prevent="handleSubmit"
    >
      <!-- 未登录才显示昵称输入 -->
      <div v-if="!userStore.isLoggedIn" class="guest-row">
        <el-form-item prop="authorName" class="nickname-item">
          <el-input v-model="form.authorName" placeholder="昵称（必填）" maxlength="20" size="small" />
        </el-form-item>
      </div>

      <!-- 已登录展示身份标识 -->
      <div v-else class="user-badge">
        <span class="badge-text">以 <strong>{{ userStore.user?.nickname }}</strong> 身份评论</span>
      </div>

      <div class="content-row">
        <el-form-item
          :label="parentId ? `回复 @${replyToName}` : undefined"
          prop="content"
          class="content-item"
        >
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="2"
            placeholder="写下你的想法..."
            maxlength="1000"
            show-word-limit
            size="small"
          />
        </el-form-item>
        <div class="actions-row">
          <el-button type="primary" size="small" @click="handleSubmit" :loading="submitting">
            {{ parentId ? '回复' : '发表评论' }}
          </el-button>
          <el-button v-if="parentId" size="small" @click="$emit('cancel')">取消</el-button>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { submitComment } from '@/api/comment'
import { VALIDATION_RULES } from '@/constants'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  articleId: number
  parentId?: number | null
  replyToName?: string
}>()

const emit = defineEmits<{
  submitted: []
  cancel: []
}>()

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  authorName: userStore.user?.nickname || '',
  authorEmail: userStore.user?.email || '',
  content: ''
})

const rules: FormRules = {
  authorName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 20, message: VALIDATION_RULES.commentAuthorName.message, trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { min: 1, max: 1000, message: VALIDATION_RULES.commentContent.message, trigger: 'blur' }
  ]
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const authorName = userStore.isLoggedIn ? userStore.user!.nickname : form.authorName
    const result = await submitComment(props.articleId, {
      authorName,
      authorEmail: userStore.user?.email || undefined,
      content: form.content,
      parentId: props.parentId ?? null
    })

    if (result.status === 'PENDING') {
      ElMessage.success({ message: '评论已提交，审核通过后展示', duration: 1000 })
    } else {
      ElMessage.success({ message: '评论成功', duration: 1000 })
    }

    form.content = ''
    if (!props.parentId && !userStore.isLoggedIn) {
      form.authorName = ''
    }
    emit('submitted')
  } catch {
    // 错误已在拦截器中统一处理
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.comment-form {
  padding: 0;
}

.guest-row {
  margin-bottom: 8px;
}

.nickname-item {
  margin-bottom: 0;
}

.nickname-item :deep(.el-form-item__content) {
  max-width: 220px;
}

.user-badge {
  font-size: 13px;
  color: #888;
  margin-bottom: 8px;
}

.badge-text strong {
  color: #555;
}

.content-row {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.content-item {
  flex: 1;
  margin-bottom: 0;
}

.actions-row {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-shrink: 0;
  padding-top: 4px;
}
</style>
