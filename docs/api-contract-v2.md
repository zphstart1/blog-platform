# 博客平台 API 接口契约文档 v2

> **版本**：v2.0  
> **生效日期**：2026-06-17  
> **适用范围**：DDD 重构后的全量接口  
> **Base URL**：`http://localhost:3000/api`（通过 Nginx 反向代理至 `backend:8080`）

---

## 目录

- [1. 公共规范](#1-公共规范)
  - [1.1 统一响应包装 `Result<T>`](#11-统一响应包装-resultt)
  - [1.2 分页响应 `PageResult<T>`](#12-分页响应-pageresultt)
  - [1.3 错误码规范](#13-错误码规范)
  - [1.4 VO 序列化规范](#14-vo-序列化规范)
- [2. 接口清单](#2-接口清单)
  - [2.1 认证 (Auth)](#21-认证-auth)
  - [2.2 文章-公开 (Article)](#22-文章-公开-article)
  - [2.3 文章-管理端 (Admin Article)](#23-文章-管理端-admin-article)
  - [2.4 分类 (Category)](#24-分类-category)
  - [2.5 标签 (Tag)](#25-标签-tag)
  - [2.6 搜索 (Search)](#26-搜索-search)
  - [2.7 评论 (Comment)](#27-评论-comment)
  - [2.8 友链 (Link)](#28-友链-link)
  - [2.9 文件上传 (Upload)](#29-文件上传-upload)

---

## 1. 公共规范

### 1.1 统一响应包装 `Result<T>`

**所有接口**统一使用 `Result<T>` 作为外层包装，不再使用 `Ret<T>`。

```json
{
  "code": 0,
  "message": "success",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | `int` | 业务状态码，`0` 表示成功，非 0 表示业务错误 |
| `message` | `string` | 提示信息，成功时为 `"success"` |
| `data` | `T` / `null` | 业务数据，类型取决于具体接口；无数据时为 `null` |

> **设计原则**：
> - HTTP 状态码统一返回 `200`，业务状态通过 `code` 字段区分
> - 异常也不抛 HTTP 错误，由 `GlobalExceptionHandler` 统一包装为 `Result`
> - 前端通过 `code === 0` 判断请求是否成功

### 1.2 分页响应 `PageResult<T>`

所有**列表/分页接口**统一使用 `PageResult<T>` 嵌套在 `Result.data` 中：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [ ... ],
    "total": 42,
    "page": 1,
    "size": 10,
    "pages": 5
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `records` | `T[]` | 当前页数据列表 |
| `total` | `long` | 符合条件的总记录数（非当前页条数） |
| `page` | `int` | 当前页码，从 1 开始 |
| `size` | `int` | 每页大小 |
| `pages` | `int` | 总页数，计算方式 `ceil(total / size)` |

**分页请求参数规范**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | `int` | 否 | `1` | 页码，≥1 |
| `size` | `int` | 否 | `10` | 每页条数，1~50 |

### 1.3 错误码规范

| code | 说明 | 触发场景 |
|------|------|----------|
| `0` | 成功 | 正常返回 |
| `400` | 参数校验失败 | 请求参数不合法、表单验证失败 |
| `401` | 未认证 | Token 缺失或无效 |
| `403` | 无权限 | 角色权限不足 |
| `404` | 资源不存在 | 文章、分类、评论等不存在 |
| `409` | 资源冲突 | 重复创建（如 slug 已存在） |
| `429` | 请求过于频繁 | 触发频率限制 |
| `500` | 服务器内部错误 | 未预期的系统异常 |

**错误响应示例**：

```json
{
  "code": 404,
  "message": "文章不存在",
  "data": null
}
```

### 1.4 VO 序列化规范

#### 1.4.1 TagVO — 标签

```json
{
  "id": 1,
  "name": "Java",
  "slug": "java",
  "articleCount": 15,
  "weight": 15
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 标签 ID |
| `name` | `string` | 标签名称 |
| `slug` | `string` | URL 友好的唯一标识 |
| `articleCount` | `number` | 关联文章数 |
| `weight` | `number` | 标签云权重（等于 articleCount） |

> **修复项**：TagVO 必须序列化为标准 JSON 对象，**禁止**出现 Java `toString()` 格式 `"@{id=1; name=Java}"`。  
> 原因：`@Data` 注解生成的 getter 配合 Jackson 即可正确序列化，但需确保 `ArticleVO.tags` 的 MyBatis 映射使用 `collection` 标签或 `@Result` 注解正确映射。

#### 1.4.2 CategoryVO — 分类

```json
{
  "id": 1,
  "name": "技术",
  "slug": "tech",
  "description": "技术类文章",
  "articleCount": 30,
  "children": [
    {
      "id": 2,
      "name": "后端",
      "slug": "backend",
      "description": "后端开发",
      "articleCount": 20,
      "children": null
    }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 分类 ID |
| `name` | `string` | 分类名称 |
| `slug` | `string` | URL 友好标识 |
| `description` | `string` | 分类描述（可为 null） |
| `articleCount` | `number` | 关联文章数 |
| `children` | `CategoryVO[]` / `null` | 子分类列表（树形结构） |

#### 1.4.3 AuthorVO — 作者

```json
{
  "id": 1,
  "nickname": "张三",
  "avatar": "https://example.com/avatar.jpg"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 用户 ID |
| `nickname` | `string` | 昵称 |
| `avatar` | `string` / `null` | 头像 URL |

#### 1.4.4 ArticleVO — 文章列表项

```json
{
  "id": 1,
  "title": "Spring Boot 入门",
  "slug": "spring-boot-intro",
  "content": "# Spring Boot 入门\n...",
  "contentHtml": "<h1>Spring Boot 入门</h1>\n...",
  "summary": "Spring Boot 快速入门指南",
  "coverImage": "https://example.com/cover.jpg",
  "category": { "id": 2, "name": "后端", "slug": "backend", "description": null, "articleCount": 20, "children": null },
  "tags": [
    { "id": 1, "name": "Java", "slug": "java", "articleCount": 15, "weight": 15 }
  ],
  "author": { "id": 1, "nickname": "张三", "avatar": null },
  "viewCount": 1280,
  "isTop": false,
  "publishedAt": "2026-06-15T10:30:00",
  "createdAt": "2026-06-15T10:00:00",
  "updatedAt": "2026-06-16T14:00:00",
  "status": "PUBLISHED",
  "relevanceScore": null
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 文章 ID |
| `title` | `string` | 文章标题 |
| `slug` | `string` | URL 友好标识 |
| `content` | `string` | Markdown 原文（列表接口可能不含） |
| `contentHtml` | `string` / `null` | 服务端预渲染的 HTML |
| `summary` | `string` / `null` | 文章摘要 |
| `coverImage` | `string` / `null` | 封面图 URL |
| `category` | `CategoryVO` / `null` | 所属分类 |
| `tags` | `TagVO[]` | 标签列表 |
| `author` | `AuthorVO` | 作者信息 |
| `viewCount` | `number` | 浏览次数 |
| `isTop` | `boolean` | 是否置顶 |
| `publishedAt` | `string` (ISO 8601) | 发布时间 |
| `createdAt` | `string` (ISO 8601) | 创建时间 |
| `updatedAt` | `string` (ISO 8601) | 更新时间 |
| `status` | `string` | 状态：`DRAFT` / `PUBLISHED`（仅管理端可见） |
| `relevanceScore` | `number` / `null` | 搜索相关度分数（仅搜索结果含） |

#### 1.4.5 ArticleDetailVO — 文章详情

在 `ArticleVO` 基础上增加：

```json
{
  "...": "...",
  "categoryId": 2,
  "tagIds": [1, 3, 5],
  "prevArticle": { "id": 0, "title": "上一篇文章", "slug": "prev-slug" },
  "nextArticle": { "id": 2, "title": "下一篇文章", "slug": "next-slug" }
}
```

| 新增字段 | 类型 | 说明 |
|----------|------|------|
| `categoryId` | `number` / `null` | 分类 ID（管理端编辑回填用） |
| `tagIds` | `number[]` | 标签 ID 列表（管理端编辑回填用） |
| `prevArticle` | `NavArticle` / `null` | 上一篇文章导航 |
| `nextArticle` | `NavArticle` / `null` | 下一篇文章导航 |

**NavArticle** 结构：

```json
{
  "id": 1,
  "title": "文章标题",
  "slug": "article-slug"
}
```

#### 1.4.6 Comment — 评论

```json
{
  "id": 1,
  "articleId": 1,
  "parentId": null,
  "replyToId": null,
  "authorName": "读者A",
  "authorEmail": "reader@example.com",
  "authorWebsite": "https://example.com",
  "content": "写得好！",
  "status": "APPROVED",
  "createdAt": "2026-06-15T12:00:00",
  "children": [
    {
      "id": 2,
      "articleId": 1,
      "parentId": 1,
      "replyToId": null,
      "authorName": "作者",
      "authorEmail": null,
      "authorWebsite": null,
      "content": "谢谢！",
      "status": "APPROVED",
      "createdAt": "2026-06-15T12:30:00",
      "children": []
    }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 评论 ID |
| `articleId` | `number` | 所属文章 ID |
| `parentId` | `number` / `null` | 父评论 ID（嵌套回复） |
| `replyToId` | `number` / `null` | 被回复的评论 ID |
| `authorName` | `string` | 评论者昵称 |
| `authorEmail` | `string` / `null` | 评论者邮箱（不公开） |
| `authorWebsite` | `string` / `null` | 评论者网站 |
| `content` | `string` | 评论内容 |
| `status` | `string` | `PENDING` / `APPROVED` / `REJECTED` |
| `createdAt` | `string` (ISO 8601) | 评论时间 |
| `children` | `Comment[]` | 子回复列表（嵌套结构） |
| `articleTitle` | `string` | 文章标题（仅管理端含） |
| `ip` | `string` | 评论者 IP（仅管理端含） |
| `userAgent` | `string` | 浏览器 UA（仅管理端含） |

---

## 2. 接口清单

> **标记说明**：🔢 表示该接口返回分页数据，使用 `PageResult<T>` 格式。  
> **路径前缀**：所有路径均相对于 `http://localhost:3000/api`。  
> **认证**：管理端接口需携带 `Authorization: Bearer <token>` 请求头。

---

### 2.1 认证 (Auth)

#### 2.1.1 用户注册

```
POST /api/auth/register
```

**请求体**：

```json
{
  "username": "newuser",
  "password": "123456",
  "email": "newuser@example.com",
  "nickname": "新用户"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | `string` | 是 | 用户名 |
| `password` | `string` | 是 | 密码 |
| `email` | `string` | 否 | 邮箱 |
| `nickname` | `string` | 否 | 昵称 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 2,
    "username": "newuser",
    "nickname": "新用户",
    "role": "AUTHOR",
    "createdAt": "2026-06-17T14:00:00"
  }
}
```

#### 2.1.2 用户登录

```
POST /api/auth/login
```

**请求体**：

```json
{
  "username": "admin",
  "password": "123456"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | `string` | 是 | 用户名 |
| `password` | `string` | 是 | 密码 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "avatar": null,
      "role": "OWNER",
      "email": "admin@example.com"
    }
  }
}
```

#### 2.1.3 获取当前用户信息

```
GET /api/auth/me
```

**需要认证**：是

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": null,
    "role": "OWNER",
    "email": "admin@example.com"
  }
}
```

---

### 2.2 文章-公开 (Article)

#### 2.2.1 🔢 文章列表（公开）

```
GET /api/articles
```

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | `int` | 否 | `1` | 页码 |
| `size` | `int` | 否 | `10` | 每页条数 |
| `categoryId` | `long` | 否 | — | 按分类筛选 |
| `tagId` | `long` | 否 | — | 按标签筛选 |
| `keyword` | `string` | 否 | — | 关键词搜索（标题+内容） |
| `sort` | `string` | 否 | `publishedAt` | 排序字段：`publishedAt` / `viewCount` |
| `order` | `string` | 否 | `desc` | 排序方向：`asc` / `desc` |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "Spring Boot 入门",
        "slug": "spring-boot-intro",
        "summary": "Spring Boot 快速入门指南",
        "coverImage": "https://example.com/cover.jpg",
        "category": { "id": 2, "name": "后端", "slug": "backend", "description": null, "articleCount": 20, "children": null },
        "tags": [
          { "id": 1, "name": "Java", "slug": "java", "articleCount": 15, "weight": 15 }
        ],
        "author": { "id": 1, "nickname": "张三", "avatar": null },
        "viewCount": 1280,
        "isTop": false,
        "publishedAt": "2026-06-15T10:30:00"
      }
    ],
    "total": 42,
    "page": 1,
    "size": 10,
    "pages": 5
  }
}
```

> **注意**：列表接口不返回 `content`、`contentHtml`、`status` 字段以减小响应体积。

#### 2.2.2 文章详情（公开）

```
GET /api/articles/{slug}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `slug` | `string` | 文章 URL 标识（非 ID） |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "title": "Spring Boot 入门",
    "slug": "spring-boot-intro",
    "content": "# Spring Boot 入门\n\nSpring Boot 是...",
    "contentHtml": "<h1>Spring Boot 入门</h1>\n<p>Spring Boot 是...</p>",
    "summary": "Spring Boot 快速入门指南",
    "coverImage": "https://example.com/cover.jpg",
    "category": { "id": 2, "name": "后端", "slug": "backend", "description": "后端开发", "articleCount": 20, "children": null },
    "tags": [
      { "id": 1, "name": "Java", "slug": "java", "articleCount": 15, "weight": 15 }
    ],
    "author": { "id": 1, "nickname": "张三", "avatar": null },
    "viewCount": 1280,
    "isTop": false,
    "publishedAt": "2026-06-15T10:30:00",
    "createdAt": "2026-06-15T10:00:00",
    "updatedAt": "2026-06-16T14:00:00",
    "prevArticle": { "id": 0, "title": "上一篇文章", "slug": "prev-slug" },
    "nextArticle": { "id": 2, "title": "下一篇文章", "slug": "next-slug" }
  }
}
```

> **注意**：公开详情不含 `categoryId`、`tagIds`、`status` 等管理端字段。

---

### 2.3 文章-管理端 (Admin Article)

> **需要认证**：所有管理端接口需携带 JWT Token。

#### 2.3.1 🔢 管理端文章列表

```
GET /api/admin/articles
```

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | `int` | 否 | `1` | 页码 |
| `size` | `int` | 否 | `10` | 每页条数 |
| `status` | `string` | 否 | — | 按状态筛选：`DRAFT` / `PUBLISHED` |
| `categoryId` | `long` | 否 | — | 按分类筛选 |
| `authorId` | `long` | 否 | — | 按作者筛选 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "Spring Boot 入门",
        "slug": "spring-boot-intro",
        "summary": "Spring Boot 快速入门指南",
        "coverImage": null,
        "category": { "id": 2, "name": "后端", "slug": "backend", "description": null, "articleCount": 20, "children": null },
        "tags": [
          { "id": 1, "name": "Java", "slug": "java", "articleCount": 15, "weight": 15 }
        ],
        "author": { "id": 1, "nickname": "张三", "avatar": null },
        "viewCount": 1280,
        "isTop": false,
        "status": "PUBLISHED",
        "publishedAt": "2026-06-15T10:30:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10,
    "pages": 5
  }
}
```

> **管理端特有**：含 `status` 字段。

#### 2.3.2 管理端文章详情（编辑回填）

```
GET /api/admin/articles/{id}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 文章 ID |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "title": "Spring Boot 入门",
    "slug": "spring-boot-intro",
    "content": "# Spring Boot 入门\n...",
    "summary": "Spring Boot 快速入门指南",
    "coverImage": "https://example.com/cover.jpg",
    "category": { "id": 2, "name": "后端", "slug": "backend", "description": "后端开发", "articleCount": 20, "children": null },
    "tags": [
      { "id": 1, "name": "Java", "slug": "java", "articleCount": 15, "weight": 15 }
    ],
    "author": { "id": 1, "nickname": "张三", "avatar": null },
    "viewCount": 1280,
    "isTop": false,
    "status": "PUBLISHED",
    "publishedAt": "2026-06-15T10:30:00",
    "createdAt": "2026-06-15T10:00:00",
    "updatedAt": "2026-06-16T14:00:00",
    "categoryId": 2,
    "tagIds": [1, 3, 5]
  }
}
```

> **管理端特有**：含 `categoryId`、`tagIds`、`status` 字段。

**错误响应**（404）：

```json
{
  "code": 404,
  "message": "文章不存在",
  "data": null
}
```

#### 2.3.3 创建文章

```
POST /api/admin/articles
```

**请求体**：

```json
{
  "title": "新文章标题",
  "slug": "new-article",
  "content": "# 新文章\n\n正文内容...",
  "summary": "文章摘要",
  "coverImage": "https://example.com/cover.jpg",
  "categoryId": 2,
  "tagIds": [1, 3],
  "status": "PUBLISHED",
  "isTop": 0
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `title` | `string` | 是 | 文章标题 |
| `content` | `string` | 是 | Markdown 正文。草稿时后端不强制校验，但前端表单必填 |
| `slug` | `string` | 否 | URL 标识（不填则自动生成） |
| `summary` | `string` | 否 | 摘要 |
| `coverImage` | `string` | 否 | 封面图 URL |
| `categoryId` | `number` | 否 | 分类 ID |
| `tagIds` | `number[]` | 否 | 标签 ID 列表 |
| `status` | `string` | 否 | `DRAFT`（默认）/ `PUBLISHED` |
| `isTop` | `number` | 否 | 是否置顶，`0`/`1` |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 51
  }
}
```

#### 2.3.4 更新文章

```
PUT /api/admin/articles/{id}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 文章 ID |

**请求体**（所有字段可选）：

```json
{
  "title": "修改后的标题",
  "content": "# 修改后的内容",
  "status": "PUBLISHED"
}
```

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "title": "修改后的标题",
    "updatedAt": "2026-06-17T15:00:00"
  }
}
```

#### 2.3.5 删除文章

```
DELETE /api/admin/articles/{id}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 文章 ID |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

#### 2.3.6 🔢 草稿列表

```
GET /api/admin/articles/drafts
```

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | `int` | 否 | `1` | 页码 |
| `size` | `int` | 否 | `10` | 每页条数 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 52,
        "title": "未完成的草稿",
        "slug": "draft-slug",
        "summary": null,
        "coverImage": null,
        "category": null,
        "tags": [],
        "author": { "id": 1, "nickname": "张三", "avatar": null },
        "viewCount": 0,
        "isTop": false,
        "status": "DRAFT",
        "publishedAt": null
      }
    ],
    "total": 3,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

---

### 2.4 分类 (Category)

#### 2.4.1 分类列表（公开，树形）

```
GET /api/categories
```

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "技术",
      "slug": "tech",
      "description": "技术类文章",
      "articleCount": 30,
      "children": [
        {
          "id": 2,
          "name": "后端",
          "slug": "backend",
          "description": "后端开发",
          "articleCount": 20,
          "children": null
        }
      ]
    }
  ]
}
```

> **注意**：分类列表不分页，返回完整树形结构。

#### 2.4.2 创建分类

```
POST /api/admin/categories
```

**需要认证**：是

**请求体**：

```json
{
  "name": "前端",
  "slug": "frontend",
  "description": "前端开发",
  "parentId": null,
  "sortOrder": 1
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `string` | 是 | 分类名称 |
| `slug` | `string` | 否 | URL 标识 |
| `description` | `string` | 否 | 描述 |
| `parentId` | `number` | 否 | 父分类 ID |
| `sortOrder` | `number` | 否 | 排序权重 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 3,
    "name": "前端",
    "slug": "frontend",
    "description": "前端开发",
    "articleCount": 0,
    "children": null
  }
}
```

#### 2.4.3 更新分类

```
PUT /api/admin/categories/{id}
```

**需要认证**：是

**请求体**：同创建。

**成功响应**：返回更新后的 `CategoryVO`。

#### 2.4.4 删除分类

```
DELETE /api/admin/categories/{id}
```

**需要认证**：是

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

---

### 2.5 标签 (Tag)

#### 2.5.1 标签列表（公开）

```
GET /api/tags
```

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    { "id": 1, "name": "Java", "slug": "java", "articleCount": 15, "weight": 15 },
    { "id": 2, "name": "Spring", "slug": "spring", "articleCount": 12, "weight": 12 }
  ]
}
```

> **注意**：标签列表不分页，按 articleCount 降序排列。

#### 2.5.2 标签云

```
GET /api/tags/cloud
```

**成功响应**：格式同标签列表，按 `weight` 降序排列，用于标签云展示。

```json
{
  "code": 0,
  "message": "success",
  "data": [
    { "id": 1, "name": "Java", "slug": "java", "articleCount": 15, "weight": 15 },
    { "id": 3, "name": "MySQL", "slug": "mysql", "articleCount": 10, "weight": 10 }
  ]
}
```

#### 2.5.3 创建标签

```
POST /api/admin/tags
```

**需要认证**：是

**请求体**：

```json
{
  "name": "Docker",
  "slug": "docker"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `string` | 是 | 标签名称 |
| `slug` | `string` | 否 | URL 标识 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 4,
    "name": "Docker",
    "slug": "docker",
    "articleCount": 0,
    "weight": 0
  }
}
```

#### 2.5.4 更新标签

```
PUT /api/admin/tags/{id}
```

**需要认证**：是

**请求体**：同创建。

**成功响应**：返回更新后的 `TagVO`。

#### 2.5.5 删除标签

```
DELETE /api/admin/tags/{id}
```

**需要认证**：是

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

---

### 2.6 搜索 (Search)

#### 2.6.1 🔢 全文搜索

```
GET /api/search
```

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `keyword` | `string` | 是 | — | 搜索关键词（1~100 字符） |
| `page` | `int` | 否 | `1` | 页码 |
| `size` | `int` | 否 | `10` | 每页条数 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "Spring Boot 入门",
        "slug": "spring-boot-intro",
        "summary": "Spring Boot 快速入门指南",
        "coverImage": null,
        "category": { "id": 2, "name": "后端", "slug": "backend", "description": null, "articleCount": 20, "children": null },
        "tags": [
          { "id": 1, "name": "Java", "slug": "java", "articleCount": 15, "weight": 15 }
        ],
        "author": { "id": 1, "nickname": "张三", "avatar": null },
        "viewCount": 1280,
        "publishedAt": "2026-06-15T10:30:00",
        "relevanceScore": 9.85
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10,
    "pages": 1,
    "keyword": "Spring"
  }
}
```

> **特有字段**：搜索结果含 `relevanceScore`（相关度分数，越高越相关）和 `keyword`（回显搜索词）。  
> **注意**：当前搜索接口返回的 `data` 包含 `keyword` 字段和分页字段，建议统一为 `PageResult<T>` 格式（将 `keyword` 放在外层或作为独立字段）。

#### 2.6.2 搜索热词

```
GET /api/search/hot
```

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `topN` | `int` | 否 | `20` | 返回热词数量 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": ["Spring", "Java", "Docker", "MySQL", "Redis"]
}
```

---

### 2.7 评论 (Comment)

#### 2.7.1 🔢 文章评论列表（公开）

```
GET /api/articles/{articleId}/comments
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `articleId` | `number` | 文章 ID |

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | `int` | 否 | `1` | 页码 |
| `size` | `int` | 否 | `20` | 每页条数 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "articleId": 1,
        "parentId": null,
        "replyToId": null,
        "authorName": "读者A",
        "content": "写得很好！",
        "status": "APPROVED",
        "createdAt": "2026-06-15T12:00:00",
        "children": [
          {
            "id": 2,
            "articleId": 1,
            "parentId": 1,
            "replyToId": null,
            "authorName": "作者",
            "content": "谢谢支持！",
            "status": "APPROVED",
            "createdAt": "2026-06-15T12:30:00",
            "children": []
          }
        ]
      }
    ],
    "total": 15,
    "page": 1,
    "size": 20,
    "pages": 1
  }
}
```

> **公开接口**：仅返回 `status=APPROVED` 的评论。不含 `authorEmail`、`ip`、`userAgent` 等敏感字段。

#### 2.7.2 提交评论

```
POST /api/articles/{articleId}/comments
```

**请求体**：

```json
{
  "content": "写得很好，学习了！",
  "authorName": "读者A",
  "authorEmail": "reader@example.com",
  "authorWebsite": "https://example.com",
  "parentId": null,
  "replyToId": null
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `content` | `string` | 是 | 评论内容 |
| `authorName` | `string` | 是 | 评论者昵称 |
| `authorEmail` | `string` | 否 | 邮箱（不公开） |
| `authorWebsite` | `string` | 否 | 个人网站 |
| `parentId` | `number` | 否 | 父评论 ID（嵌套回复时使用） |
| `replyToId` | `number` | 否 | 被回复的评论 ID |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 16,
    "status": "PENDING"
  }
}
```

> **注意**：新提交的评论默认为 `PENDING` 状态，需管理员审核后公开显示。

#### 2.7.3 🔢 管理端待审核评论

```
GET /api/admin/comments/pending
```

**需要认证**：是

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | `int` | 否 | `1` | 页码 |
| `size` | `int` | 否 | `20` | 每页条数 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 16,
        "articleId": 1,
        "articleTitle": "Spring Boot 入门",
        "parentId": null,
        "replyToId": null,
        "authorName": "读者A",
        "authorEmail": "reader@example.com",
        "content": "写得很好，学习了！",
        "status": "PENDING",
        "ip": "127.0.0.1",
        "userAgent": "Mozilla/5.0 ...",
        "createdAt": "2026-06-17T14:00:00",
        "children": []
      }
    ],
    "total": 3,
    "page": 1,
    "size": 20,
    "pages": 1
  }
}
```

> **管理端特有**：含 `articleTitle`、`authorEmail`、`ip`、`userAgent` 字段。

#### 2.7.4 审核评论

```
PUT /api/admin/comments/{id}/review
```

**需要认证**：是

**请求体**：

```json
{
  "action": "approve"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `action` | `string` | 是 | `approve`（通过）/ `reject`（拒绝），大小写敏感 |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

#### 2.7.5 删除评论

```
DELETE /api/admin/comments/{id}
```

**需要认证**：是

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

---

### 2.8 友链 (Link)

#### 2.8.1 友链列表（公开，已审核）

```
GET /api/links
```

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "示例博客",
      "url": "https://example.com",
      "logo": "https://example.com/logo.png",
      "description": "一个优秀的博客",
      "status": 1,
      "sortOrder": 1
    }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `number` | 友链 ID |
| `name` | `string` | 站点名称 |
| `url` | `string` | 站点 URL |
| `logo` | `string` / `null` | Logo URL |
| `description` | `string` / `null` | 站点描述 |
| `status` | `number` | `1` = 已审核 |
| `sortOrder` | `number` | 排序权重 |

#### 2.8.2 管理端友链列表（全部）

```
GET /api/admin/links
```

**需要认证**：是

**成功响应**：格式同公开列表，但包含所有状态的友链。

#### 2.8.3 创建友链

```
POST /api/admin/links
```

**需要认证**：是

**请求体**：

```json
{
  "name": "新友链",
  "url": "https://newsite.com",
  "logo": "https://newsite.com/logo.png",
  "description": "站点描述",
  "sortOrder": 1
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `string` | 是 | 站点名称 |
| `url` | `string` | 是 | 站点 URL |
| `logo` | `string` | 否 | Logo URL |
| `description` | `string` | 否 | 站点描述 |
| `sortOrder` | `number` | 否 | 排序权重 |

**成功响应**：返回创建的 `Link` 对象。

#### 2.8.4 更新友链

```
PUT /api/admin/links/{id}
```

**需要认证**：是

**请求体**：同创建（所有字段可选）。

**成功响应**：返回更新后的 `Link` 对象。

#### 2.8.5 删除友链

```
DELETE /api/admin/links/{id}
```

**需要认证**：是

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

---

### 2.9 文件上传 (Upload)

#### 2.9.1 上传图片

```
POST /api/upload/image
```

**需要认证**：是

**请求格式**：`multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `file` | `File` | 是 | 图片文件（≤5MB） |

**成功响应**：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "url": "/uploads/images/2026/06/abc123.jpg",
    "originalName": "photo.jpg",
    "size": 204800
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `url` | `string` | 图片访问路径（相对路径，前端拼接域名） |
| `originalName` | `string` | 原始文件名 |
| `size` | `number` | 文件大小（字节） |

**错误响应**（文件过大）：

```json
{
  "code": 400,
  "message": "文件大小不能超过5MB",
  "data": null
}
```

---

## 附录 A：前后端对齐检查清单

### A.1 当前不一致项（需修复）

| # | 问题 | 影响范围 | 修复方案 |
|---|------|----------|----------|
| 1 | `ArticleController` 使用 `Ret<T>` 而非 `Result<T>` | `GET /articles`、`GET /articles/{slug}` | 将 `Ret` 替换为 `Result` |
| 2 | `SearchController` 使用 `Ret<T>` 而非 `Result<T>` | `GET /search`、`GET /search/hot` | 将 `Ret` 替换为 `Result` |
| 3 | `UploadController` 使用 `Ret<T>` 而非 `Result<T>` | `POST /upload/image` | 将 `Ret` 替换为 `Result` |
| 4 | `GlobalExceptionHandler` 返回 `Ret<T>` 而非 `Result<T>` | 所有异常响应 | 将 `Ret` 替换为 `Result` |
| 5 | `AdminArticleController.list()` 手动拼装 Map 而非 `PageResult` | `GET /admin/articles` | 改用 `PageResult<ArticleVO>` |
| 6 | `AdminArticleController.drafts()` total 取 `list.size()` 而非真实总数 | `GET /admin/articles/drafts` | 改用分页查询获取真实 total |
| 7 | `SearchResultVO` 直接包含分页字段而非使用 `PageResult` 包装 | `GET /search` | 改用 `PageResult<ArticleVO>` + 独立 keyword 字段 |
| 8 | TagVO 可能存在 toString() 序列化问题 | 所有含 tags 字段的接口 | 确保 MyBatis 映射正确，Jackson 正确序列化 |

### A.2 前端兼容性

前端 `request.ts` 响应拦截器当前逻辑：
- 只校验 `response.data.code === 0`
- 成功后返回 `response.data.data`（即解包一层）
- 不依赖具体的包装类名

因此 `Result` 和 `Ret` 的切换对前端是**透明**的，但统一为 `Result` 后更清晰。

---

## 附录 B：接口总览

| # | 方法 | 路径 | 分页 | 认证 | 说明 |
|---|------|------|------|------|------|
| 1 | `POST` | `/api/auth/register` | — | — | 用户注册 |
| 2 | `POST` | `/api/auth/login` | — | — | 用户登录 |
| 3 | `GET` | `/api/auth/me` | — | ✅ | 当前用户信息 |
| 4 | `GET` | `/api/articles` | 🔢 | — | 文章列表 |
| 5 | `GET` | `/api/articles/{slug}` | — | — | 文章详情 |
| 6 | `GET` | `/api/admin/articles` | 🔢 | ✅ | 管理端文章列表 |
| 7 | `GET` | `/api/admin/articles/{id}` | — | ✅ | 管理端文章详情 |
| 8 | `POST` | `/api/admin/articles` | — | ✅ | 创建文章 |
| 9 | `PUT` | `/api/admin/articles/{id}` | — | ✅ | 更新文章 |
| 10 | `DELETE` | `/api/admin/articles/{id}` | — | ✅ | 删除文章 |
| 11 | `GET` | `/api/admin/articles/drafts` | 🔢 | ✅ | 草稿列表 |
| 12 | `GET` | `/api/categories` | — | — | 分类列表 |
| 13 | `POST` | `/api/admin/categories` | — | ✅ | 创建分类 |
| 14 | `PUT` | `/api/admin/categories/{id}` | — | ✅ | 更新分类 |
| 15 | `DELETE` | `/api/admin/categories/{id}` | — | ✅ | 删除分类 |
| 16 | `GET` | `/api/tags` | — | — | 标签列表 |
| 17 | `GET` | `/api/tags/cloud` | — | — | 标签云 |
| 18 | `POST` | `/api/admin/tags` | — | ✅ | 创建标签 |
| 19 | `PUT` | `/api/admin/tags/{id}` | — | ✅ | 更新标签 |
| 20 | `DELETE` | `/api/admin/tags/{id}` | — | ✅ | 删除标签 |
| 21 | `GET` | `/api/search` | 🔢 | — | 全文搜索 |
| 22 | `GET` | `/api/search/hot` | — | — | 搜索热词 |
| 23 | `GET` | `/api/articles/{articleId}/comments` | 🔢 | — | 文章评论 |
| 24 | `POST` | `/api/articles/{articleId}/comments` | — | — | 提交评论 |
| 25 | `GET` | `/api/admin/comments/pending` | 🔢 | ✅ | 待审核评论 |
| 26 | `PUT` | `/api/admin/comments/{id}/review` | — | ✅ | 审核评论 |
| 27 | `DELETE` | `/api/admin/comments/{id}` | — | ✅ | 删除评论 |
| 28 | `GET` | `/api/links` | — | — | 友链列表 |
| 29 | `GET` | `/api/admin/links` | — | ✅ | 管理端友链 |
| 30 | `POST` | `/api/admin/links` | — | ✅ | 创建友链 |
| 31 | `PUT` | `/api/admin/links/{id}` | — | ✅ | 更新友链 |
| 32 | `DELETE` | `/api/admin/links/{id}` | — | ✅ | 删除友链 |
| 33 | `POST` | `/api/upload/image` | — | ✅ | 上传图片 |

**统计**：共 33 个接口，其中分页接口 7 个（🔢），需认证接口 20 个（✅）。

---

## 附录 C：变更历史

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2026-06-17 | v2.0 | 初始版本：统一 API 规范、定义所有接口契约、标注不一致项 |
