export const ARTICLE_STATUS_MAP: Record<string, string> = {
  DRAFT: '草稿',
  PUBLISHED: '已发布'
}

export const COMMENT_STATUS_MAP: Record<string, string> = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝'
}

export const COMMENT_STATUS_COLOR: Record<string, string> = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger'
}

export const USER_ROLE_MAP: Record<string, string> = {
  OWNER: '博主',
  ADMIN: '管理员',
  AUTHOR: '作者',
  VISITOR: '访客'
}

export const PAGINATION = {
  defaultPage: 1,
  defaultSize: 10,
  maxSize: 50,
  pageSizes: [10, 20, 30, 50]
}

export const UPLOAD_LIMITS = {
  maxSize: 5 * 1024 * 1024,
  allowedTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
  allowedExtensions: ['.jpg', '.jpeg', '.png', '.gif', '.webp']
}
