import request from './request'
import type { PageData } from './request'
import type { Comment, ReviewAction } from '@/types'

export function getPendingComments(params: { page?: number; size?: number } = {}) {
  return request.get<unknown, PageData<Comment>>('/admin/comments/pending', { params })
}

export function reviewComment(id: number, action: ReviewAction) {
  return request.put<unknown, null>(`/admin/comments/${id}/review`, { action })
}

export function deleteComment(id: number) {
  return request.delete<unknown, null>(`/admin/comments/${id}`)
}
