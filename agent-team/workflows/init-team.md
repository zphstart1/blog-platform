# Init Team Workflow (平台无关)

> 本文档定义了初始化研发团队的通用流程，不绑定任何特定 AI 工具。
> 各平台的适配层（如 `.codebuddy/`）应基于此流程实现其特定版本。

## 前置条件

1. 项目根目录下 `agent-team/roles/` 包含角色定义文件
2. 项目根目录下 `agent-team/knowledge/context.md` 包含项目上下文
3. 所选 AI 工具支持多 agent/角色协作（或可由用户依次扮演）

## 流程

### Step 1: 清理旧数据

删除上一次运行的残留团队数据（如果存在）。对于绝大多数 AI 工具，这一步是清理临时文件或缓存目录。

### Step 2: 读取上下文

加载以下文件以了解项目现状：
- `agent-team/knowledge/context.md` — 项目技术栈、架构、当前状态
- `agent-team/knowledge/learned-lessons.md` — 历史陷阱和经验
- `agent-team/knowledge/backlog.md` — 任务待办清单

### Step 3: 加载角色定义

根据当前任务需要，从 `agent-team/roles/` 加载对应角色：

| 角色 | 文件 | 职责 |
|------|------|------|
| Team Lead | `roles/team-lead.md` | 任务协调、对外接口 |
| Product Manager | `roles/product-manager.md` | 需求评审、PRD |
| Architect | `roles/architect.md` | 技术方案、API 设计 |
| Senior Dev | `roles/senior-dev.md` | Java DDD 后端开发 |
| Frontend Dev | `roles/frontend-dev.md` | Vue 3 前端开发 |
| QA Tester | `roles/qa-tester.md` | 测试验证 |
| DevOps | `roles/devops.md` | Docker 部署 |
| Code Reviewer | `roles/code-reviewer.md` | DDD 架构审查 |
| Dashboard Daemon | `roles/dashboard-daemon.md` | 系统状态监控 |

### Step 4: 创建团队结构

按 `agent-team/team-config.yml` 中定义的团队结构和流水线，初始化团队。

流水线顺序：
```
复杂度评估 → 需求评审 → 技术方案 → 代码实现(前后端并行) → 测试验证 → 部署上线 → 交付产品 → 知识回写
```

### Step 5: 分配任务

根据用户需求，按流水线阶段依次向对应角色分配任务。各角色的输入/输出契约见 `team-config.yml` 中的 pipeline.stages。

### Step 6: 协作通信

角色间的通信遵循 `agent-team/protocols/communication.md` 定义的协议：
- 主代理（Team Lead）负责转发消息
- 下游角色完成任务后通知 Team Lead
- Team Lead 汇总结果后报告给用户

## 服务地址

本项目通过 Nginx 统一在 3000 端口暴露所有服务：

| 服务 | URL |
|------|-----|
| 读者前端 | http://localhost:3000/ |
| 管理后台 | http://localhost:3000/admin/ |
| 后端 API | http://localhost:3000/api/ |
| Dashboard | http://localhost:3000/dashboard/ |

> 如果你的环境不同，请修改 `agent-team/knowledge/context.md` 中的服务地址。

## 平台适配指南

### 如何为你的 AI 工具创建适配层

如果你的 AI 工具不是 CodeBuddy，请参考以下步骤创建适配层：

1. **创建平台目录**：在项目根目录创建你的工具专属目录（如 `.cursor/`、`.github/copilot/`）
2. **转换角色格式**：将 `agent-team/roles/*.md` 转换为你工具支持的 agent/assistant 格式
3. **实现初始化流程**：基于本文件实现初始化脚本或配置
4. **参考示例**：参见 `.codebuddy/` 目录了解 CodeBuddy 的适配实现

### CodeBuddy 适配层

- `.codebuddy/agents/*.md` — 将 roles 转为 CodeBuddy agent 格式（YAML frontmatter + markdown body）
- `.codebuddy/skills/init-team/` — 基于本 workflow 的 CodeBuddy skill 实现
- `.codebuddy/teams/` — 运行时数据（自动生成，已加入 `.gitignore`）
