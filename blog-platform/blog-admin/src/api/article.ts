import request from './request'
import type { PageData } from './request'
import type { ArticleListItem, ArticleDetail, ArticleForm } from '@/types'

interface ArticleListParams {
  page?: number
  size?: number
  status?: string
  categoryId?: number
}

export function getAdminArticleList(params: ArticleListParams = {}) {
  return request.get<unknown, PageData<ArticleListItem>>('/admin/articles', { params })
}

export function getArticleById(id: number) {
  return request.get<unknown, ArticleDetail>(`/admin/articles/${id}`)
}

export function createArticle(data: ArticleForm) {
  return request.post<unknown, ArticleListItem>('/admin/articles', data)
}

export function updateArticle(id: number, data: Partial<ArticleForm>) {
  return request.put<unknown, ArticleListItem>(`/admin/articles/${id}`, data)
}

export function deleteArticle(id: number) {
  return request.delete<unknown, null>(`/admin/articles/${id}`)
}

export function getDrafts() {
  return request.get<unknown, PageData<ArticleListItem>>('/admin/articles/drafts')
}
