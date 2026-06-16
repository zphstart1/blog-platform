import request from './request'
import type { LoginForm, User } from '@/types'

export interface LoginResult {
  token: string
  user: User
}

export function loginApi(data: LoginForm) {
  return request.post<unknown, LoginResult>('/auth/login', data)
}
