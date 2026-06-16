// ============================================
// 文章 API（公开 + 管理）
// ============================================
import request from './request'
import type { PageData } from './request'
import type { ArticleListItem, ArticleDetail, ArticleForm, ArticleSortField, SortOrder } from '@/types'

interface ArticleListParams {
  page?: number
  size?: number
  categoryId?: number
  tagId?: number
  keyword?: string
  sort?: ArticleSortField
  order?: SortOrder
}

interface AdminArticleListParams {
  page?: number
  size?: number
  status?: string
  categoryId?: number
}

/** 公开文章列表 */
export function getArticleList(params: ArticleListParams = {}) {
  return request.get<unknown, PageData<ArticleListItem>>('/articles', { params })
}

/** 文章详情（通过 slug） */
export function getArticleDetail(slug: string) {
  return request.get<unknown, ArticleDetail>(`/articles/${slug}`)
}

/** 管理端文章列表 */
export function getAdminArticleList(params: AdminArticleListParams = {}) {
  return request.get<unknown, PageData<ArticleListItem>>('/admin/articles', { params })
}

/** 创建文章 */
export function createArticle(data: ArticleForm) {
  return request.post<unknown, ArticleListItem>('/admin/articles', data)
}

/** 更新文章 */
export function updateArticle(id: number, data: Partial<ArticleForm>) {
  return request.put<unknown, ArticleListItem>(`/admin/articles/${id}`, data)
}

/** 删除文章 */
export function deleteArticle(id: number) {
  return request.delete<unknown, null>(`/admin/articles/${id}`)
}

/** 获取草稿列表 */
export function getDrafts() {
  return request.get<unknown, PageData<ArticleListItem>>('/admin/articles/drafts')
}
