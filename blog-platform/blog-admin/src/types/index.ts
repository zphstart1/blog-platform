// 管理后台类型定义（与博客前端共享核心类型）

export type UserRole = 'OWNER' | 'ADMIN' | 'AUTHOR' | 'VISITOR'
export type ArticleStatus = 'DRAFT' | 'PUBLISHED'
export type CommentStatus = 'PENDING' | 'APPROVED' | 'REJECTED'
export type ReviewAction = 'APPROVE' | 'REJECT'

export interface User {
  id: number
  username: string
  nickname: string
  avatar: string | null
  role: UserRole
  email: string | null
  status?: number
}

export interface Category {
  id: number
  name: string
  slug: string
  description?: string
  parentId?: number | null
  sortOrder?: number
  articleCount?: number
  children?: Category[]
}

export interface Tag {
  id: number
  name: string
  slug: string
  articleCount?: number
  weight?: number
}

export interface ArticleListItem {
  id: number
  title: string
  slug: string
  summary: string | null
  coverImage: string | null
  category: { id: number; name: string; slug: string; description?: string | null; articleCount?: number; children?: Category[] | null } | null
  tags: { id: number; name: string; slug: string; articleCount?: number; weight?: number }[]
  author: { id: number; nickname: string; avatar?: string | null }
  viewCount: number
  isTop: boolean
  status: ArticleStatus
  publishedAt: string | null
  updatedAt?: string
}

export interface ArticleDetail extends ArticleListItem {
  content: string
  categoryId?: number | null
  tagIds?: number[]
  createdAt: string
  updatedAt?: string
}

export interface Comment {
  id: number
  articleId: number
  articleTitle?: string
  parentId: number | null
  replyToId: number | null
  authorName: string
  authorEmail?: string | null
  authorWebsite?: string | null
  content: string
  status: CommentStatus
  ip?: string
  userAgent?: string
  createdAt: string
}

export interface ArticleForm {
  title: string
  slug?: string
  content: string
  summary?: string
  coverImage?: string
  categoryId?: number | null
  tagIds?: number[]
  status?: ArticleStatus
  isTop?: number
}

export interface CategoryForm {
  name: string
  slug?: string
  description?: string
  parentId?: number | null
  sortOrder?: number
}

export interface TagForm {
  name: string
  slug?: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface UploadResult {
  url: string
  originalName: string
  size: number
}
