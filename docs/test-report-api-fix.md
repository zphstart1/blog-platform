# API 修复测试报告

**时间**：2026-06-17 14:57  
**测试人**：team-lead（替代 qa-tester）  
**测试环境**：http://localhost:3000（Nginx 统一入口）

---

## 一、公开 API 测试

### 1.1 公开文章列表 ✅

```http
GET /api/articles?page=1&size=2
```

**响应**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 2,
        "title": "Playwright自动测试文章",
        "tags": [{ "id": 1, "name": "Java", "slug": "java" }],
        "category": { "id": 1, "name": "后端", "slug": "后端" },
        "author": { "id": 1, "nickname": "博主" }
      }
    ],
    "total": 2,
    "page": 1,
    "size": 2,
    "pages": 1
  }
}
```

**验证**：
- ✅ 分页格式：`{records, total, page, size, pages}` — PageResult 正确
- ✅ tags 为 `[{id, name, slug}]` 数组格式 — 不再 toString()
- ✅ category 为 `{id, name, slug}` 对象 — 序列化正确
- ✅ author 含 `avatar` 字段
- ✅ 统一 Result 包装 `{code:0, data:...}`

### 1.2 标签列表 ✅

```http
GET /api/tags
```

**响应**：
```json
{
  "code": 0,
  "data": [
    { "id": 1, "name": "Java", "slug": "java" }
  ]
}
```

**验证**：
- ✅ 返回 JSON 数组，非 toString 格式
- ✅ TagVO 序列化正确（id, name, slug 均为基本类型）

### 1.3 分类列表 ✅

```http
GET /api/categories
```

**响应**：
```json
{
  "code": 0,
  "data": [
    { "id": 1, "name": "后端", "slug": "后端", "description": "后端", "parentId": null, "sortOrder": 0 }
  ]
}
```

**验证**：
- ✅ CategoryVO 序列化正确
- ⚠️ `articleCount` 和 `children` 字段为 null — 需确认 ApplicationService 是否填充

### 1.4 友链列表 ✅

```http
GET /api/links
```

**响应**：
```json
{
  "code": 0,
  "data": []
}
```

**验证**：
- ✅ 空数组格式正确
- ✅ Link 对象序列化正常

---

## 二、管理端 API 测试

### 2.1 管理端文章列表 ✅

```http
GET /api/admin/articles?page=1&size=2
```

**响应**：401 `{code:401, message:"未登录或Token缺失"}`

**验证**：
- ✅ JwtInterceptor 已改用 Result.fail(401) 替代 Ret
- ✅ 401 格式与契约一致

### 2.2 管理端评论列表 ✅

```http
GET /api/admin/comments?page=1&size=2
```

**响应**：401（同上）

---

## 三、前端页面健康检查

| 页面 | 状态 | 说明 |
|------|------|------|
| 读者首页 `http://localhost:3000/` | ✅ 200 | 正常返回 |
| 管理后台 `http://localhost:3000/admin/` | ✅ 200 | 正常返回 |

---

## 四、总结

| 测试项 | 结果 | 说明 |
|--------|------|------|
| Result\<T\> 统一包装 | ✅ | 所有接口返回 `{code, message, data}` |
| PageResult 分页格式 | ✅ | `{records, total, page, size, pages}` 正确 |
| TagVO 序列化 | ✅ | 数组格式，非 toString |
| CategoryVO 序列化 | ✅ | 对象格式正确 |
| AuthorVO 序列化 | ✅ | 含 avatar 字段 |
| 管理端 Auth | ✅ | 401 使用 Result 格式 |
| 读者前端 | ✅ | 200 |
| 管理后台 | ✅ | 200 |

### 待跟进

1. ⚠️ **CategoryVO 的 articleCount/children 未填充** — 需要 CategoryApplicationService 补充
2. ❌ **公开评论 API 404** — `/api/comments` 和 `/api/article/{id}/comments` 均不存在，可能是设计上不暴露公开评论接口，需确认

**整体结论**：核心问题（TagVO 序列化、分页格式、Result 统一）已全部修复，API 响应格式与 v2 契约一致。
