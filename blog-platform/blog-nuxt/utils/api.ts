// API 请求工具（Nuxt 3 服务端/客户端通用）
// 注意：Nuxt 3 构建时使用 NUXT_PUBLIC_API_BASE 环境变量
// 构建命令：NUXT_PUBLIC_API_BASE=https://your-api.com/api npx nuxt generate
import axios from 'axios'

let apiBase = 'http://127.0.0.1:8080/api'
// Nuxt 3 构建时通过 process.env 注入（仅在服务端可用）
if (process.env.NUXT_PUBLIC_API_BASE) {
  apiBase = process.env.NUXT_PUBLIC_API_BASE
}

export const api = axios.create({
  baseURL: apiBase,
  timeout: 10000
})

/** 通用响应格式 */
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageData<T> {
  records: T[]
  total: number
  page: number
  size: number
  pages: number
}

/** 文章列表项 */
export interface ArticleItem {
  id: number
  title: string
  slug: string
  summary: string | null
  coverImage: string | null
  category: { id: number; name: string; slug: string } | null
  tags: { id: number; name: string; slug: string }[]
  author: { id: number; nickname: string }
  viewCount: number
  isTop: boolean
  publishedAt: string | null
}

/** 文章详情 */
export interface ArticleDetail extends ArticleItem {
  content: string
  contentHtml: string
  createdAt: string
  updatedAt: string
  prevArticle: { id: number; title: string; slug: string } | null
  nextArticle: { id: number; title: string; slug: string } | null
}

/** 获取文章列表 */
export async function fetchArticles(page = 1, size = 10): Promise<PageData<ArticleItem>> {
  const { data } = await api.get<ApiResponse<PageData<ArticleItem>>>('/articles', {
    params: { page, size, sort: 'publishedAt', order: 'desc' }
  })
  return data.data
}

/** 获取文章详情 */
export async function fetchArticleBySlug(slug: string): Promise<ArticleDetail> {
  const { data } = await api.get<ApiResponse<ArticleDetail>>(`/articles/${slug}`)
  return data.data
}

/** 获取所有文章 slug（用于生成静态路径） */
export async function fetchAllSlugs(): Promise<string[]> {
  const slugs: string[] = []
  let page = 1
  let hasMore = true
  while (hasMore) {
    const result = await fetchArticles(page, 50)
    slugs.push(...result.records.map(r => r.slug))
    hasMore = result.page < result.pages
    page++
  }
  return slugs
}

/** 获取分类列表 */
export async function fetchCategories(): Promise<{ id: number; name: string; slug: string }[]> {
  const { data } = await api.get<ApiResponse<any[]>>('/categories')
  return data.data
}
