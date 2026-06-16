import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types'
import { loginApi } from '@/api/auth'
import type { LoginForm } from '@/types'
import { ElMessage } from 'element-plus'

export const useAdminStore = defineStore('admin', () => {
  const token = ref<string | null>(localStorage.getItem('admin_token'))
  const user = ref<User | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value)
  const canManage = computed(() => {
    const role = user.value?.role
    return role === 'OWNER' || role === 'ADMIN' || role === 'AUTHOR'
  })
  const isOwnerOrAdmin = computed(() => {
    const role = user.value?.role
    return role === 'OWNER' || role === 'ADMIN'
  })

  async function login(data: LoginForm) {
    loading.value = true
    try {
      const result = await loginApi(data)
      // 检查是否有管理权限
      const role = result.user.role
      if (role !== 'OWNER' && role !== 'ADMIN' && role !== 'AUTHOR') {
        ElMessage.error({ message: '无管理后台访问权限', duration: 1000 })
        throw new Error('无管理后台访问权限')
      }
      token.value = result.token
      user.value = result.user
      localStorage.setItem('admin_token', result.token)
      return result
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('admin_token')
  }

  return {
    token,
    user,
    loading,
    isLoggedIn,
    canManage,
    isOwnerOrAdmin,
    login,
    logout
  }
})
