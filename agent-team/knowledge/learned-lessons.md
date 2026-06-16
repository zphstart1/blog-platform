# 团队经验知识库

> **自动注入规则**：每次开始新任务时，Team Lead 应读取此文件并将相关经验注入到对应角色的任务描述中。角色完成工作后如有新发现，回写到此文件。

---

## 前端陷阱

### MD 渲染相关
| ID | 场景 | 根因 | 修复 | 影响角色 |
|----|------|------|------|---------|
| MD-001 | `###` 不渲染为 `<h3>` | content 字段含字面量 `\n` 而非真换行符 | 渲染前 `replace(/\\n/g, '\n')` | frontend-dev |
| MD-002 | 页面背景变黑 | `github-markdown-css` 含 `@prefers-color-scheme: dark` | 改用 `github-markdown-light.css` | frontend-dev |
| MD-003 | 双字段不一致 | content_html 与 content 不同步 | 单数据源：只存 content，前端 marked 渲染 | team-lead |

### 构建相关
| ID | 场景 | 根因 | 修复 | 影响角色 |
|----|------|------|------|---------|
| BUILD-001 | 改代码后构建 hash 不变 | dist 目录旧产物残留 | 构建前 `rmdir /s /q dist` | frontend-dev |
| BUILD-002 | npm run build 报 chunk 超大警告 | 未做代码分割 | 不影响功能，后续优化时可做 manualChunks | frontend-dev |

### Vue + Element Plus
| ID | 场景 | 根因 | 修复 | 影响角色 |
|----|------|------|------|---------|
| VUE-001 | v-html 内容样式丢失 | scoped CSS 不穿透 v-html | 用非 scoped `<style>` 块引入全局 CSS | frontend-dev |
| VUE-002 | Element Plus 自动导入不生效 | unplugin 配置缺失 | 检查 vite.config.ts AutoImport + Components | frontend-dev |

---

## 后端陷阱

| ID | 场景 | 根因 | 修复 | 影响角色 |
|----|------|------|------|---------|
| JAVA-001 | 阅读量计数不准确 | updateById 忽略 null 字段 | 用 `@Update` 注解写原生 SQL | senior-dev |
| JAVA-002 | Redis 缓存命中后跳过重渲染逻辑 | Cache-Aside 模式绕过后处理 | 重渲染放在缓存写入前 | senior-dev |
| JAVA-003 | 版本快照丢失 content_html | 字段移除后未同步修改 | 批量修改所有引用点 | senior-dev |

---

## DevOps 陷阱

| ID | 场景 | 根因 | 修复 | 影响角色 |
|----|------|------|------|---------|
| OPS-001 | Docker build 网络超时 | Docker Hub 国内连通性问题 | 配置国内镜像源或使用代理 | devops |
| OPS-002 | nginx restart 后文件未更新 | Docker volume bind mount 延迟 | `docker restart` 后验证 HTTP 响应 | devops |
| OPS-003 | 镜像仓库认证失败 | Token 过期或权限不足 | 检查 Personal Access Token 有效期 | devops |

---

## QA 陷阱

| ID | 场景 | 根因 | 修复 | 影响角色 |
|----|------|------|------|---------|
| QA-001 | 浏览器截图与肉眼不一致 | 字体/CSS 加载时序 | `wait --load networkidle` 后再截图 | qa-tester |
| QA-002 | DOM 快照看不到 v-html 完整内容 | agent-browser snapshot 截断 | 用 screenshot 辅助验证 | qa-tester |

---

## 使用方式

### 任务开始时（Team Lead）
```
1. 读取本文件
2. 根据任务类型筛选相关经验条目
3. 在派发任务时附上相关经验
```

### 任务完成后（所有角色）
```
1. 本次开发中遇到的新陷阱 → 追加到对应分类
2. 已有的经验被验证有效 → 更新确认
3. 通知 Team Lead 知识库已更新
```
