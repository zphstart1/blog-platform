// ============================================
// 评论 API
// ============================================
import request from './request'
import type { PageData } from './request'
import type { Comment, CommentForm, ReviewAction } from '@/types'

interface CommentListParams {
  page?: number
  size?: number
  sort?: 'createdAt' | 'createdAtAsc'
}

/** 文章评论列表（已审核） */
export function getComments(articleId: number, params: CommentListParams = {}) {
  return request.get<unknown, PageData<Comment>>(`/articles/${articleId}/comments`, { params })
}

/** 提交评论 */
export function submitComment(articleId: number, data: CommentForm) {
  return request.post<unknown, { id: number; status: string }>(`/articles/${articleId}/comments`, data)
}

/** 管理端待审核评论列表 */
export function getPendingComments(params: { page?: number; size?: number } = {}) {
  return request.get<unknown, PageData<Comment>>('/admin/comments/pending', { params })
}

/** 审核评论 */
export function reviewComment(id: number, action: ReviewAction) {
  return request.put<unknown, null>(`/admin/comments/${id}/review`, { action })
}

/** 删除评论 */
export function deleteComment(id: number) {
  return request.delete<unknown, null>(`/admin/comments/${id}`)
}
