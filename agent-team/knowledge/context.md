# 团队共享上下文 (Shared Context)

> **用途**：跨会话和跨 Agent 共享项目状态。Team Lead 在每次任务开始时读取并更新此文件。Agent 在完成任务后可以追加关键决策。

---

## 项目技术栈

| 项 | 值 |
|----|-----|
| 架构风格 | 前后端分离 + DDD 四层架构（domain/application/infrastructure/interfaces） |
| 后端框架 | Spring Boot 2.7 + MyBatis Plus |
| 前端框架 | Vue 3 + TypeScript + Vite |
| UI 库 | Element Plus |
| 数据库 | MySQL 8 |
| 缓存 | Redis |
| 反向代理 | Nginx |
| 容器化 | Docker Compose |
| Markdown 渲染 | marked（前端） |

---

## Bounded Context 一览

| Context | 聚合根 | 说明 |
|---------|--------|------|
| article | Article | 文章管理，含分类、标签、搜索 |
| user | User | 用户注册登录，权限控制 |
| comment | Comment | 评论提交、审核、嵌套回复 |
| upload | (通用域) | 图片上传，暂不完整 DDD |

## 服务访问地址 (Nginx 统一入口)

| 服务 | 地址 |
|------|------|
| 读者前端 | http://localhost:3000/ |
| 管理后台 | http://localhost:3000/admin/ |
| 后端 API | http://localhost:3000/api/ (代理→backend:8080) |
| Dashboard | http://localhost:3000/dashboard/ |

> **注意**：系统通过 Nginx 统一在 3000 端口暴露，不直接访问 8080/3001 等端口。

---

## 项目结构

```
blog-platform/
├── blog-frontend/    # 读者端 (port 3000)
├── blog-admin/       # 管理后台 (port 3001)
├── blog-server/      # 后端 (port 8080)
│   └── src/main/java/com/blog/
│       ├── article/       # Bounded Context: 文章 (DDD 四层)
│       │   ├── domain/         # 聚合根 + 值对象 + 仓储接口
│       │   ├── application/    # ApplicationService + Command
│       │   ├── infrastructure/ # PO + Mapper + RepositoryImpl
│       │   └── interfaces/     # Controller (AdminArticle, Category, Tag, Search, Article)
│       ├── user/          # Bounded Context: 用户 (DDD 四层)
│       │   ├── domain/         # User 聚合根 + 值对象
│       │   ├── application/    # AuthApplicationService
│       │   ├── infrastructure/ # UserPO + Mapper + RepositoryImpl
│       │   └── interfaces/     # AuthController
│       ├── comment/       # Bounded Context: 评论 (DDD 四层)
│       │   ├── domain/         # Comment 聚合根 + 值对象
│       │   ├── application/    # CommentApplicationService
│       │   ├── infrastructure/ # CommentPO + Mapper + RepositoryImpl
│       │   └── interfaces/     # CommentController
│       ├── upload/        # Bounded Context: 上传 (仅 interfaces)
│       │   └── interfaces/
│       ├── shared/        # 跨 Context 共享 (BaseEntity, Result, AuthContext, DomainException)
│       └── (entity/mapper/dto/vo/service 保留给旧读侧查询)
├── nginx/            # Nginx 配置
├── docs/blog/        # 交付文档
└── agent-team/       # Agent 配置
```

---

## 当前系统状态

| 组件 | 状态 | 备注 |
|------|------|------|
| 前端读者端 | ✅ 运行中 | marked 渲染 MD，背景白色 |
| 前端管理端 | ✅ 运行中 | Element Plus 组件 |
| 后端 API | ✅ 运行中 | API 返回 content 字段（无 contentHtml） |
| MySQL | ✅ 运行中 | 含 article、comment、user 表 |
| Redis | ✅ 运行中 | 文章缓存，TTL 30min |
| Nginx | ✅ 运行中 | 前端代理 + API 转发 |
| Docker | ✅ 运行中 | docker-compose 管理 |

---

## 已知问题

| # | 问题 | 状态 |
|---|------|------|
| 1 | User.java BCryptPasswordEncoder 在 domain 层（P0 遗留） | ⚠️ 待提取接口 |
| 2 | 7 个文件 domain 层使用 `org.springframework.util.StringUtils`（P1 遗留） | ⚠️ 待替换 |
| 3 | AdminArticleController 直接注入 ArticleMapper（P1 遗留） | ⚠️ 待封装 |
| 4 | Comment/Link Controller 返回 domain 对象（P2 遗留） | ⚠️ 待定义 VO |
| 5 | 数据库 content_html 字段为历史遗留 | ⚠️ 不影响功能 |

---

## 最近的改动

| 日期 | 改动 | 影响 |
|------|------|------|
| 2026-06-17 | P0 修复：Tag/Category ApplicationService 走 Repository | TagRepository, CategoryRepository, TagApplicationService, CategoryApplicationService, TagRepositoryImpl, CategoryRepositoryImpl |
| 2026-06-17 | 前端 14 项修复：类型对齐 + 防御性处理 + Bug 修复 | blog-frontend, blog-admin (api, types, pages) |
| 2026-06-17 | v2 契约 4 项修正（与后端实际代码对齐） | api-contract-v2.md |
| 2026-06-17 | Ret<T> → Result<T> 统一响应包装 | GlobalExceptionHandler, JwtInterceptor, ArticleController, SearchController, UploadController, ArticleReadService, SearchApplicationService, FileService |
| 2026-06-17 | AdminArticleController 使用 PageResult<ArticleVO> | AdminArticleController |
| 2026-06-17 | 草稿列表 total 使用数据库真实计数 | ArticleRepository, ArticleRepositoryImpl, ArticleApplicationService |
| 2026-06-17 | Tag/Category Controller 返回 VO 替代领域对象 | TagController, CategoryController, TagApplicationService, CategoryApplicationService |
| 2026-06-16 | MD 渲染：双字段改单数据源，前端 marked | ArticleDetail.vue, ArticleService.java |
| 2026-06-16 | 修复 \n 转义序列导致 MD 不渲染 | ArticleDetail.vue |
| 2026-06-16 | DDD 架构重构 - article/user/comment/upload 四个 Context | 全部 Controller/Service |

---

## git 信息

| 项 | 值 |
|----|-----|
| 仓库 | https://github.com/zphstart1/blog-platform |
| 分支 | master |
| 最新提交 | feat: Agent 集群 v4 升级 - 7项核心优化 |

---

## 部署历史

| 日期 | 版本 | 变更摘要 | 部署人 | 状态 |
|------|------|---------|--------|------|
| 2026-06-16 | v1.0 | 博客平台首次上线 | devops | ✅ 运行中 |
| 2026-06-16 | v1.1 | MD 渲染修复（单数据源 + marked） | devops | ✅ 运行中 |
| 2026-06-16 | v2.0 | DDD 架构重构部署（4 Context + 单阶段 Dockerfile） | devops | ✅ 运行中 |
| 2026-06-17 | v2.1 | DDD 架构修复 + API 契约 v2 对齐（Result/PageResult/VO 统一、P0 Mapper 泄漏修复） | team-lead | ✅ 运行中 |

---

## 关联资源

| 资源 | 路径 | 说明 |
|------|------|------|
| 任务待办 | `agent-team/knowledge/backlog.md` | 跨会话持久化任务清单 |
| 经验库 | `agent-team/knowledge/learned-lessons.md` | 历史陷阱和修复方案 |
| 会话接入协议 | `agent-team/protocols/session-onboarding.md` | 新会话启动流程 |
