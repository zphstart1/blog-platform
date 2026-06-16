# 团队共享上下文 (Shared Context)

> **用途**：跨会话和跨 Agent 共享项目状态。Team Lead 在每次任务开始时读取并更新此文件。Agent 在完成任务后可以追加关键决策。

---

## 项目技术栈

| 项 | 值 |
|----|-----|
| 后端框架 | Spring Boot 2.7 + MyBatis Plus |
| 前端框架 | Vue 3 + TypeScript + Vite |
| UI 库 | Element Plus |
| 数据库 | MySQL 8 |
| 缓存 | Redis |
| 反向代理 | Nginx |
| 容器化 | Docker Compose |
| Markdown 渲染 | marked（前端） |

---

## 项目结构

```
blog-platform/
├── blog-frontend/    # 读者端 (port 3000)
│   └── src/pages/    # ArticleDetail, Home, Archive, Search 等
├── blog-admin/       # 管理后台 (port 3001)
│   └── src/pages/    # Dashboard, ArticleList, ArticleEditor 等
├── blog-server/      # 后端服务 (port 8080)
│   └── src/main/java/com/blog/
│       ├── controller/  # ArticleController, AdminArticleController
│       ├── service/     # ArticleService
│       ├── mapper/      # ArticleMapper
│       └── utils/       # MarkdownUtil
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
| 1 | 后端 Docker 镜像需重建（Docker Hub 网络不通） | ⚠️ 待处理 |
| 2 | 数据库 content_html 字段为历史遗留 | ⚠️ 不影响功能 |

---

## 最近的改动

| 日期 | 改动 | 影响 |
|------|------|------|
| 2026-06-16 | MD 渲染：双字段改单数据源，前端 marked | ArticleDetail.vue, ArticleService.java |
| 2026-06-16 | 修复 \n 转义序列导致 MD 不渲染 | ArticleDetail.vue |
| 2026-06-16 | 深色背景：改用 github-markdown-light.css | ArticleDetail.vue |

---

## git 信息

| 项 | 值 |
|----|-----|
| 仓库 | https://github.com/zphstart1/blog-platform |
| 分支 | master |
| 最新提交 | docs: 添加项目总 README |
