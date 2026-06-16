// ============================================
// 分类 & 标签 API
// ============================================
import request from './request'
import type { Category, Tag, CategoryForm, TagForm } from '@/types'

/** 公开分类列表（树形） */
export function getCategories() {
  return request.get<unknown, Category[]>('/categories')
}

/** 管理端创建分类 */
export function createCategory(data: CategoryForm) {
  return request.post<unknown, Category>('/admin/categories', data)
}

/** 管理端更新分类 */
export function updateCategory(id: number, data: Partial<CategoryForm>) {
  return request.put<unknown, Category>(`/admin/categories/${id}`, data)
}

/** 管理端删除分类 */
export function deleteCategory(id: number) {
  return request.delete<unknown, null>(`/admin/categories/${id}`)
}

/** 公开标签列表 */
export function getTags() {
  return request.get<unknown, Tag[]>('/tags')
}

/** 标签云 */
export function getTagCloud() {
  return request.get<unknown, Tag[]>('/tags/cloud')
}

/** 管理端创建标签 */
export function createTag(data: TagForm) {
  return request.post<unknown, Tag>('/admin/tags', data)
}

/** 管理端更新标签 */
export function updateTag(id: number, data: Partial<TagForm>) {
  return request.put<unknown, Tag>(`/admin/tags/${id}`, data)
}

/** 管理端删除标签 */
export function deleteTag(id: number) {
  return request.delete<unknown, null>(`/admin/tags/${id}`)
}
