<template>
  <div class="article-detail">
    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="12" animated />
    </div>

    <!-- 错误 -->
    <el-empty v-else-if="error" description="文章加载失败">
      <el-button type="primary" @click="fetchArticle">重新加载</el-button>
    </el-empty>

    <!-- 404 -->
    <el-empty v-else-if="!article" description="文章不存在" />

    <!-- 文章内容 -->
    <template v-else>
      <article class="article-body">
        <header class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span class="meta-item">
              <el-icon><User /></el-icon>
              {{ article.author?.nickname ?? '未知作者' }}
            </span>
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              {{ formatDate(article.publishedAt) }}
            </span>
            <span class="meta-item">
              <el-icon><View /></el-icon>
              {{ article.viewCount }} 阅读
            </span>
            <el-tag v-if="article.category" size="small" type="info">
              {{ article.category.name }}
            </el-tag>
          </div>
          <div v-if="article.tags?.length" class="article-tags">
            <el-tag
              v-for="tag in article.tags"
              :key="tag.id"
              size="small"
              type="warning"
              effect="plain"
            >
              {{ tag.name }}
            </el-tag>
          </div>
        </header>

        <!-- Markdown 渲染区域 -->
        <div
          class="article-content markdown-body"
          v-html="renderedContent"
          ref="contentRef"
        />

        <!-- 文章导航 -->
        <div class="article-nav">
          <div v-if="article.prevArticle?.slug" class="nav-item nav-prev">
            <router-link :to="`/article/${article.prevArticle.slug}`">
              <span class="nav-label">上一篇</span>
              <span class="nav-title">{{ article.prevArticle.title }}</span>
            </router-link>
          </div>
          <div v-if="article.nextArticle?.slug" class="nav-item nav-next">
            <router-link :to="`/article/${article.nextArticle.slug}`">
              <span class="nav-label">下一篇</span>
              <span class="nav-title">{{ article.nextArticle.title }}</span>
            </router-link>
          </div>
        </div>
      </article>

      <!-- 评论区域 -->
      <section class="comment-section">
        <div class="section-title">评论 ({{ commentTotal }})</div>

        <!-- 评论表单 -->
        <CommentForm :article-id="article.id" @submitted="fetchComments" />

        <!-- 评论列表 -->
        <div v-if="commentLoading" class="loading-wrapper">
          <el-skeleton :rows="3" animated />
        </div>
        <template v-else-if="comments.length">
          <CommentItem
            v-for="comment in comments"
            :key="comment.id"
            :comment="comment"
            :article-id="article.id"
            @replied="fetchComments"
          />
          <div v-if="commentTotal > comments.length" class="load-more">
            <el-button text @click="loadMoreComments">加载更多评论</el-button>
          </div>
        </template>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '@/api/article'
import { getComments } from '@/api/comment'
import CommentForm from '@/components/CommentForm.vue'
import CommentItem from '@/components/CommentItem.vue'
import Prism from 'prismjs'
import 'prismjs/components/prism-java'
import 'prismjs/components/prism-python'
import 'prismjs/components/prism-javascript'
import 'prismjs/components/prism-typescript'
import 'prismjs/components/prism-json'
import 'prismjs/components/prism-sql'
import 'prismjs/components/prism-bash'
import 'prismjs/components/prism-yaml'
import dayjs from 'dayjs'
import { marked } from 'marked'
import type { ArticleDetail as ArticleDetailType, Comment } from '@/types'

const route = useRoute()
const contentRef = ref<HTMLElement>()

const loading = ref(false)
const error = ref(false)
const article = ref<ArticleDetailType | null>(null)

/**
 * Markdown 渲染：统一用 marked 解析 content（Markdown 原文）
 */
const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  try {
    const normalized = article.value.content
      .replace(/\\\\/g, '\x00')
      .replace(/\\n/g, '\n')
      .replace(/\x00/g, '\\')
    return marked.parse(normalized, { breaks: true, gfm: true }) as string
  } catch {
    return article.value.content
  }
})

const commentLoading = ref(false)
const comments = ref<Comment[]>([])
const commentTotal = ref(0)
const commentPage = ref(1)

function formatDate(dateStr: string | null) {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

async function fetchArticle() {
  const slug = route.params.slug as string
  if (!slug) return

  loading.value = true
  error.value = false
  try {
    article.value = await getArticleDetail(slug)
    document.title = `${article.value.title} - 个人博客`
    await nextTick()
    highlightCode()
    await fetchComments()
  } catch (e: any) {
    if (e?.code === 404 || e?.response?.status === 404) {
      article.value = null
    } else {
      error.value = true
    }
  } finally {
    loading.value = false
  }
}

async function fetchComments() {
  if (!article.value) return
  commentLoading.value = true
  try {
    const result = await getComments(article.value.id, {
      page: commentPage.value,
      size: 20,
      sort: 'createdAt'
    })
    comments.value = result.records
    commentTotal.value = result.total
  } catch {
    // 静默
  } finally {
    commentLoading.value = false
  }
}

function loadMoreComments() {
  commentPage.value++
  fetchComments()
}

function highlightCode() {
  if (contentRef.value) {
    const codeBlocks = contentRef.value.querySelectorAll('pre code')
    codeBlocks.forEach((block) => {
      Prism.highlightElement(block)
    })
  }
}

watch(() => route.params.slug, () => {
  if (route.name === 'ArticleDetail') {
    commentPage.value = 1
    fetchArticle()
  }
})

onMounted(() => {
  fetchArticle()
})
</script>

<style>
@import 'github-markdown-css/github-markdown-light.css';

.markdown-body {
  box-sizing: border-box;
  min-width: 200px;
  max-width: 100%;
  padding: 0;
}
</style>

<style scoped>
.article-detail {
  max-width: 860px;
  margin: 0 auto;
}

.loading-wrapper {
  padding: 40px 0;
}

.article-body {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
}

.article-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.article-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.4;
  margin-bottom: 16px;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #999;
  font-size: 14px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.article-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.article-content :deep(pre) {
  background: #2d2d2d;
  border-radius: 8px;
  overflow-x: auto;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}

.article-nav {
  display: flex;
  justify-content: space-between;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  gap: 16px;
}

.nav-item a {
  text-decoration: none;
  color: inherit;
  display: block;
}

.nav-item:hover .nav-title {
  color: #409eff;
}

.nav-label {
  font-size: 12px;
  color: #999;
  display: block;
  margin-bottom: 4px;
}

.nav-title {
  font-size: 14px;
  color: #333;
  transition: color 0.2s;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.nav-next {
  text-align: right;
  margin-left: auto;
}

.comment-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px 24px;
  margin-top: 32px;
  opacity: 0.85;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #999;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.load-more {
  text-align: center;
  margin-top: 12px;
}
</style>
