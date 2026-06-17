<template>
  <div class="comment-item">
    <div class="comment-header">
      <span class="comment-author">{{ comment.authorName }}</span>
      <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
    </div>
    <p class="comment-content">{{ commentContent }}</p>
    <div class="comment-actions">
      <span class="reply-btn" @click="showReply = !showReply">回复</span>
    </div>

    <!-- 回复表单 -->
    <div v-if="showReply" class="reply-form">
      <CommentForm
        :article-id="articleId"
        :parent-id="comment.id"
        :reply-to-name="comment.authorName"
        @submitted="handleReplied"
      />
    </div>

    <!-- 子评论 -->
    <div v-if="comment.children?.length" class="comment-children">
      <CommentItem
        v-for="child in comment.children"
        :key="child.id"
        :comment="child"
        :article-id="articleId"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { Comment } from '@/types'
import CommentForm from './CommentForm.vue'
import dayjs from 'dayjs'

const props = defineProps<{
  comment: Comment
  articleId: number
}>()

/** 防御性处理：content 可能为值对象 { value: "..." } 或纯字符串 */
const commentContent = computed(() => {
  const c = props.comment.content
  return typeof c === 'string' ? c : (c as any)?.value ?? String(c ?? '')
})

const emit = defineEmits<{
  replied: []
}>()

const showReply = ref(false)

function handleReplied() {
  showReply.value = false
  emit('replied')
}

function formatDate(dateStr: string) {
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}
</script>

<style scoped>
.comment-item {
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.comment-author {
  font-size: 13px;
  font-weight: 600;
  color: #555;
}

.comment-time {
  font-size: 11px;
  color: #bbb;
}

.comment-content {
  font-size: 13px;
  line-height: 1.6;
  color: #555;
  margin-bottom: 4px;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.reply-btn {
  font-size: 12px;
  color: #aaa;
  cursor: pointer;
  transition: color 0.2s;
}

.reply-btn:hover {
  color: #409eff;
}

.comment-children {
  margin-left: 24px;
  margin-top: 4px;
  padding-left: 12px;
  border-left: 2px solid #f0f0f0;
}

.reply-form {
  margin-top: 8px;
  padding: 8px;
  background: #fafafa;
  border-radius: 4px;
}
</style>
