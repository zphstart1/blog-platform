<template>
  <article class="article-page">
    <!-- 结构化数据 (Schema.org) -->
    <Head>
      <title>{{ article.title }} - 个人博客</title>
      <Meta name="description" :content="article.summary || article.title" />
      <Meta name="keywords" :content="article.tags?.map(t => t.name).join(',') || ''" />
      <Meta property="og:title" :content="article.title" />
      <Meta property="og:description" :content="article.summary || article.title" />
      <Meta property="og:type" content="article" />
      <Meta property="article:published_time" :content="article.publishedAt || ''" />
      <Meta property="article:author" :content="article.author?.nickname || ''" />
      <Script type="application/ld+json">
        {{ JSON.stringify(structuredData) }}
      </Script>
    </Head>

    <div class="article-body">
      <header class="article-header">
        <h1>{{ article.title }}</h1>
        <div class="article-meta">
          <span>{{ article.author?.nickname }}</span>
          <span>{{ formatDate(article.publishedAt) }}</span>
          <span>{{ article.viewCount }} 阅读</span>
          <span v-if="article.category">{{ article.category.name }}</span>
        </div>
        <div v-if="article.tags?.length" class="article-tags">
          <span v-for="tag in article.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
        </div>
      </header>

      <!-- 文章内容（优先服务端预渲染 HTML，fallback 到原文） -->
      <div class="article-content" v-html="article.contentHtml || article.content" />

      <!-- 文章导航 -->
      <nav class="article-nav">
        <div v-if="article.prevArticle" class="nav-prev">
          <NuxtLink :to="`/article/${article.prevArticle.slug}`">
            <span class="nav-label">上一篇</span>
            {{ article.prevArticle.title }}
          </NuxtLink>
        </div>
        <div v-if="article.nextArticle" class="nav-next">
          <NuxtLink :to="`/article/${article.nextArticle.slug}`">
            <span class="nav-label">下一篇</span>
            {{ article.nextArticle.title }}
          </NuxtLink>
        </div>
      </nav>
    </div>
  </article>
</template>

<script setup lang="ts">
import { fetchArticleBySlug, fetchAllSlugs } from '~/utils/api'
import type { ArticleDetail } from '~/utils/api'
import dayjs from 'dayjs'

const route = useRoute()
const slug = route.params.slug as string

// SSG 预渲染时获取数据
const { data: article } = await useAsyncData(
  `article-${slug}`,
  () => fetchArticleBySlug(slug)
) as { data: Ref<ArticleDetail> }

if (!article.value) {
  throw createError({ statusCode: 404, message: '文章不存在' })
}

// 结构化数据
const structuredData = computed(() => ({
  '@context': 'https://schema.org',
  '@type': 'Article',
  headline: article.value.title,
  description: article.value.summary || '',
  author: {
    '@type': 'Person',
    name: article.value.author?.nickname || '博主'
  },
  datePublished: article.value.publishedAt,
  dateModified: article.value.updatedAt
}))

function formatDate(dateStr: string | null) {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD')
}
</script>

<style scoped>
.article-page {
  max-width: 860px;
  margin: 0 auto;
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

.article-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.4;
  margin-bottom: 16px;
}

.article-meta {
  display: flex;
  gap: 16px;
  color: #999;
  font-size: 14px;
  margin-bottom: 12px;
}

.article-tags {
  display: flex;
  gap: 8px;
}

.tag {
  font-size: 12px;
  background: #f0f0f0;
  color: #666;
  padding: 2px 8px;
  border-radius: 4px;
}

/* Markdown 内容样式 */
.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin: 1.5em 0 0.8em;
  font-weight: 600;
}

.article-content :deep(h1) { font-size: 24px; }
.article-content :deep(h2) { font-size: 20px; border-bottom: 1px solid #eee; padding-bottom: 8px; }
.article-content :deep(h3) { font-size: 18px; }

.article-content :deep(p) {
  margin: 0.8em 0;
  line-height: 1.8;
  font-size: 16px;
}

.article-content :deep(pre) {
  background: #2d2d2d;
  color: #ccc;
  border-radius: 8px;
  overflow-x: auto;
  padding: 16px;
  margin: 1em 0;
}

.article-content :deep(code) {
  font-family: 'Fira Code', Consolas, monospace;
  font-size: 14px;
}

.article-content :deep(p code) {
  background: #f0f0f0;
  color: #e74c3c;
  padding: 2px 6px;
  border-radius: 4px;
}

.article-content :deep(blockquote) {
  border-left: 4px solid #409eff;
  padding: 8px 16px;
  margin: 1em 0;
  background: #f5f7fa;
  color: #666;
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
}

.nav-label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.nav-prev a,
.nav-next a {
  text-decoration: none;
  color: #333;
  font-size: 14px;
}

.nav-prev a:hover,
.nav-next a:hover {
  color: #409eff;
}

.nav-next {
  text-align: right;
  margin-left: auto;
}
</style>
