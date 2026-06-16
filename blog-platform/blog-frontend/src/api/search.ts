// ============================================
// 搜索 API
// ============================================
import request from './request'
import type { PageData } from './request'
import type { SearchResultItem } from '@/types'

interface SearchParams {
  keyword: string
  page?: number
  size?: number
}

/** 全文搜索 */
export function searchArticles(params: SearchParams) {
  return request.get<unknown, PageData<SearchResultItem> & { keyword: string }>('/search', { params })
}
