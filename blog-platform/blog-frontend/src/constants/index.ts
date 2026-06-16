// ============================================
// 通用常量定义
// ============================================

/** 文章状态映射 */
export const ARTICLE_STATUS_MAP: Record<string, string> = {
  DRAFT: '草稿',
  PUBLISHED: '已发布'
}

/** 评论状态映射 */
export const COMMENT_STATUS_MAP: Record<string, string> = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝'
}

/** 评论状态颜色 */
export const COMMENT_STATUS_COLOR: Record<string, string> = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger'
}

/** 用户角色映射 */
export const USER_ROLE_MAP: Record<string, string> = {
  OWNER: '博主',
  ADMIN: '管理员',
  AUTHOR: '作者',
  VISITOR: '访客'
}

/** 分页默认配置 */
export const PAGINATION = {
  defaultPage: 1,
  defaultSize: 10,
  maxSize: 50,
  pageSizes: [10, 20, 30, 50]
}

/** 评论分页默认配置 */
export const COMMENT_PAGINATION = {
  defaultPage: 1,
  defaultSize: 20,
  maxSize: 50
}

/** 图片上传限制 */
export const UPLOAD_LIMITS = {
  maxSize: 5 * 1024 * 1024, // 5MB
  allowedTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
  allowedExtensions: ['.jpg', '.jpeg', '.png', '.gif', '.webp']
}

/** 表单校验规则 */
export const VALIDATION_RULES = {
  username: {
    pattern: /^[a-zA-Z][a-zA-Z0-9_]{3,19}$/,
    message: '用户名需4-20字符，字母开头，可包含字母数字下划线'
  },
  password: {
    min: 6,
    max: 30,
    message: '密码长度需在6-30字符之间'
  },
  articleTitle: {
    min: 1,
    max: 200,
    message: '文章标题长度需在1-200字符之间'
  },
  commentContent: {
    min: 1,
    max: 1000,
    message: '评论内容长度需在1-1000字符之间'
  },
  commentAuthorName: {
    min: 1,
    max: 20,
    message: '评论者名称长度需在1-20字符之间'
  }
}
