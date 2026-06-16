// ============================================
// 认证 API
// ============================================
import request from './request'
import type { LoginForm, RegisterForm, User } from '@/types'

export interface LoginResult {
  token: string
  user: User
}

export interface RegisterResult {
  id: number
  username: string
  nickname: string
  role: string
  createdAt: string
}

/** 用户登录 */
export function loginApi(data: LoginForm) {
  return request.post<unknown, LoginResult>('/auth/login', data)
}

/** 用户注册 */
export function registerApi(data: RegisterForm) {
  return request.post<unknown, RegisterResult>('/auth/register', data)
}
