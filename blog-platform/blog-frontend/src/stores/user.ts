// ============================================
// 用户状态管理
// ============================================
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, UserRole } from '@/types'
import { loginApi, registerApi } from '@/api/auth'
import type { LoginForm, RegisterForm } from '@/types'

export const useUserStore = defineStore('user', () => {
  // ========== 状态 ==========
  const token = ref<string | null>(localStorage.getItem('blog_token'))
  const user = ref<User | null>(null)
  const loading = ref(false)

  // ========== 计算属性 ==========
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed<UserRole | null>(() => user.value?.role ?? null)
  const isAdmin = computed(() => {
    const role = user.value?.role
    return role === 'OWNER' || role === 'ADMIN'
  })
  const canManage = computed(() => {
    const role = user.value?.role
    return role === 'OWNER' || role === 'ADMIN' || role === 'AUTHOR'
  })

  // ========== 方法 ==========
  /** 登录 */
  async function login(data: LoginForm) {
    loading.value = true
    try {
      const result = await loginApi(data)
      token.value = result.token
      user.value = result.user
      localStorage.setItem('blog_token', result.token)
      return result
    } finally {
      loading.value = false
    }
  }

  /** 注册 */
  async function register(data: RegisterForm) {
    loading.value = true
    try {
      return await registerApi(data)
    } finally {
      loading.value = false
    }
  }

  /** 退出登录 */
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('blog_token')
  }

  /** 设置用户信息（页面刷新后恢复） */
  function setUser(u: User) {
    user.value = u
  }

  return {
    token,
    user,
    loading,
    isLoggedIn,
    userRole,
    isAdmin,
    canManage,
    login,
    register,
    logout,
    setUser
  }
})
