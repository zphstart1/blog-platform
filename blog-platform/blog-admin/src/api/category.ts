import request from './request'
import type { Category, Tag, CategoryForm, TagForm } from '@/types'

export function getCategories() {
  return request.get<unknown, Category[]>('/categories')
}

export function createCategory(data: CategoryForm) {
  return request.post<unknown, Category>('/admin/categories', data)
}

export function updateCategory(id: number, data: Partial<CategoryForm>) {
  return request.put<unknown, Category>(`/admin/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return request.delete<unknown, null>(`/admin/categories/${id}`)
}

export function getTags() {
  return request.get<unknown, Tag[]>('/tags')
}

export function createTag(data: TagForm) {
  return request.post<unknown, Tag>('/admin/tags', data)
}

export function updateTag(id: number, data: Partial<TagForm>) {
  return request.put<unknown, Tag>(`/admin/tags/${id}`, data)
}

export function deleteTag(id: number) {
  return request.delete<unknown, null>(`/admin/tags/${id}`)
}
