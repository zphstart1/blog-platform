// ============================================
// 全局应用状态
// ============================================
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCategories, getTags } from '@/api/category'
import type { Category, Tag } from '@/types'

export const useAppStore = defineStore('app', () => {
  const categories = ref<Category[]>([])
  const tags = ref<Tag[]>([])
  const siteLoading = ref(false)

  /** 加载分类和标签（全局缓存） */
  async function fetchSiteMeta() {
    siteLoading.value = true
    try {
      const [catResult, tagResult] = await Promise.all([
        getCategories(),
        getTags()
      ])
      categories.value = catResult
      tags.value = tagResult
    } catch {
      // 静默失败，页面自行处理错误状态
    } finally {
      siteLoading.value = false
    }
  }

  return {
    categories,
    tags,
    siteLoading,
    fetchSiteMeta
  }
})
