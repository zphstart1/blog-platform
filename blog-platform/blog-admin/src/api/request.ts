import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageData<T> {
  records: T[]
  total: number
  page: number
  size: number
  pages: number
}

const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

let tokenGetter: (() => string | null) | null = null

export function registerTokenGetter(getter: () => string | null) {
  tokenGetter = getter
}

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

request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, message } = response.data
    if (code === 0) {
      return response.data.data as any
    }
    ElMessage.error({ message: message || '请求失败', duration: 1000 })
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    if (error.response) {
      const { status } = error.response
      switch (status) {
        case 401: {
          const adminStore = (window as any).__adminStore
          adminStore?.logout?.()
          ElMessage.error({ message: '登录已过期，请重新登录', duration: 1000 })
          break
        }
        case 403:
          ElMessage.error({ message: '权限不足', duration: 1000 })
          break
        case 404:
          ElMessage.error({ message: '资源不存在', duration: 1000 })
          break
        case 409:
          ElMessage.error({ message: error.response.data?.message || '资源冲突', duration: 1000 })
          break
        case 429:
          ElMessage.warning({ message: '操作过于频繁，请稍后重试', duration: 1000 })
          break
        default:
          ElMessage.error({ message: `请求失败 (${status})`, duration: 1000 })
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error({ message: '请求超时', duration: 1000 })
    } else {
      ElMessage.error({ message: '网络异常', duration: 1000 })
    }
    return Promise.reject(error)
  }
)

export default request
