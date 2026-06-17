// ============================================
// 全局 TypeScript 类型定义
// ============================================

/** 用户信息 */
export interface User {
  id: number
  username: string
  nickname: string
  avatar: string | null
  role: UserRole
  email: string | null
}

/** 用户角色 */
export type UserRole = 'OWNER' | 'ADMIN' | 'AUTHOR' | 'VISITOR'

/** 文章状态 */
export type ArticleStatus = 'DRAFT' | 'PUBLISHED'

/** 评论状态 */
export type CommentStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

/** 分类 */
export interface Category {
  id: number
  name: string
  slug: string
  description?: string
  articleCount?: number
  parentId?: number | null
  sortOrder?: number
  children?: Category[]
}

/** 标签 */
export interface Tag {
  id: number
  name: string
  slug: string
  articleCount?: number
  weight?: number
}

/** 文章列表项 */
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
  status?: ArticleStatus
  publishedAt: string | null
}

/** 文章详情 */
export interface ArticleDetail extends ArticleListItem {
  content: string
  /** 服务端预渲染的 HTML（优先使用，不需要前端 marked 渲染） */
  contentHtml?: string
  createdAt: string
  updatedAt: string
  prevArticle: { id: number; title: string; slug: string } | null
  nextArticle: { id: number; title: string; slug: string } | null
}

/** 评论 */
export interface Comment {
  id: number
  articleId: number
  parentId: number | null
  replyToId: number | null
  authorName: string
  authorEmail?: string | null
  authorWebsite?: string | null
  content: string
  status: CommentStatus
  createdAt: string
  children: Comment[]
  // 管理端额外字段
  articleTitle?: string
  ip?: string
  userAgent?: string
}

/** 搜索结果项 */
export interface SearchResultItem {
  id: number
  title: string
  slug: string
  summary: string
  coverImage: string | null
  category: { id: number; name: string; slug: string; description?: string | null; articleCount?: number; children?: Category[] | null } | null
  tags: { id: number; name: string; slug: string; articleCount?: number; weight?: number }[]
  author: { id: number; nickname: string; avatar?: string | null }
  viewCount: number
  publishedAt: string
  relevanceScore: number
}

/** 文章表单（创建/编辑） */
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

/** 分类表单 */
export interface CategoryForm {
  name: string
  slug?: string
  description?: string
  parentId?: number | null
  sortOrder?: number
}

/** 标签表单 */
export interface TagForm {
  name: string
  slug?: string
}

/** 评论表单 */
export interface CommentForm {
  authorName: string
  authorEmail?: string
  content: string
  parentId?: number | null
}

/** 评论审核操作 */
export type ReviewAction = 'APPROVE' | 'REJECT'

/** 归档项 */
export interface ArchiveItem {
  year: number
  month: number
  articles: ArticleListItem[]
}

/** 友链 */
export interface Link {
  id: number
  name: string
  url: string
  logo?: string | null
  description?: string | null
  status: number
  sortOrder: number
}

/** 登录表单 */
export interface LoginForm {
  username: string
  password: string
}

/** 注册表单 */
export interface RegisterForm {
  username: string
  password: string
  email?: string
  nickname?: string
}

/** 上传响应 */
export interface UploadResult {
  url: string
  originalName: string
  size: number
}

/** 排序方向 */
export type SortOrder = 'asc' | 'desc'

/** 文章排序字段 */
export type ArticleSortField = 'publishedAt' | 'viewCount'
