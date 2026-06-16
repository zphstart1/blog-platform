import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

/** 后端统一响应格式 */
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页响应 */
export interface PageData<T> {
  records: T[]
  total: number
  page: number
  size: number
  pages: number
}

/** 创建 Axios 实例，基础配置 */
const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 用于在 Pinia 未初始化时暂存 token 获取函数
let tokenGetter: (() => string | null) | null = null

/** 注册 token 获取函数（在 Pinia setup 后调用） */
export function registerTokenGetter(getter: () => string | null) {
  tokenGetter = getter
}

/** 请求拦截器：注入 JWT Token */
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = tokenGetter?.()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

/** 响应拦截器：统一处理错误 */
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, message } = response.data
    // code 为 0 表示成功，直接返回 data 字段
    if (code === 0) {
      return response.data.data as any
    }
    // 业务错误
    ElMessage.error({ message: message || '请求失败', duration: 1000 })
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    // 网络错误或 HTTP 错误
    if (error.response) {
      const { status } = error.response
      switch (status) {
        case 401: {
          // Token 过期或未登录，清除登录态并跳转
          const userStore = (window as any).__userStore
          if (userStore?.logout) {
            userStore.logout()
          }
          ElMessage.error({ message: '登录已过期，请重新登录', duration: 1000 })
          break
        }
        case 403:
          ElMessage.error({ message: '权限不足', duration: 1000 })
          break
        case 404:
          ElMessage.error({ message: '请求的资源不存在', duration: 1000 })
          break
        case 409:
          ElMessage.error({ message: error.response.data?.message || '资源冲突', duration: 1000 })
          break
        case 429:
          ElMessage.warning({ message: '操作过于频繁，请稍后重试', duration: 1000 })
          break
        case 500:
          ElMessage.error({ message: '服务器内部错误', duration: 1000 })
          break
        default:
          ElMessage.error({ message: `请求失败 (${status})`, duration: 1000 })
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error({ message: '请求超时，请检查网络', duration: 1000 })
    } else {
      ElMessage.error({ message: '网络异常，请检查连接', duration: 1000 })
    }
    return Promise.reject(error)
  }
)

export default request
