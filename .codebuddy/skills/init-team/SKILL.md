---
name: init-team
description: Initialize the development team for a new session. This skill should be used when the user starts a new conversation and needs to spawn all agents (architect, senior-dev, frontend-dev, qa-tester, product-manager, code-reviewer, dashboard-daemon, devops) as a coordinated team. Triggers include: "初始化团队", "启动团队", "init team", "组建团队", "开始工作", "继续之前的工作", or any request to start multi-agent development work.
---

# Init Team (CodeBuddy 适配)

> 本 skill 是 `agent-team/workflows/init-team.md` 的 **CodeBuddy 平台实现**。
> 平台无关的通用流程、角色定义、服务地址请查阅 `agent-team/` 目录。

## 前置条件

- `.codebuddy/agents/` 包含所有 agent 定义文件（由 `agent-team/roles/*.md` 转换而来）
- `.codebuddy/skills/init-team/` 本 skill 目录

## 执行流程

以 `agent-team/workflows/init-team.md` 为蓝本，在 CodeBuddy 平台上执行：

### Step 1: 加载平台无关定义

先读取以下文件建立上下文：
- `agent-team/workflows/init-team.md` — 通用初始化流程
- `agent-team/knowledge/context.md` — 项目上下文（含服务地址表）
- `agent-team/team-config.yml` — 团队结构和流水线定义

### Step 2: 清理旧数据

```powershell
Remove-Item -Recurse -Force ".codebuddy/teams/ddd-review" -ErrorAction SilentlyContinue
```

### Step 3: 创建团队

使用 `team_create` 工具创建团队，名称 `ddd-review`，描述根据当前任务填写。

### Step 4: 产卵所有 Agent

使用 `task` 工具并行产卵 7 个 worker agent（team-lead 是主 agent，无需产卵）：

| Agent | `subagent_name` | 职责 |
|-------|-----------------|------|
| architect | `architect` | API 设计、技术方案 |
| senior-dev | `senior-dev` | Java DDD 后端开发 |
| frontend-dev | `frontend-dev` | Vue 3 前端开发 |
| qa-tester | `qa-tester` | 测试验证 |
| product-manager | `product-manager` | 需求评审 |
| code-reviewer | `code-reviewer` | DDD 代码审查 |
| dashboard-daemon | `dashboard-daemon` | 系统状态监控 |

每个 agent 使用 `team_name="ddd-review"`、`mode="acceptEdits"`、`name={agent_name}`。

Prompt 需包含：当前任务描述 + 指示读取 `agent-team/knowledge/context.md` + 角色职责 + 通过 `send_message` 向 team-lead 汇报。

### Step 5: 流水线推进

按 `agent-team/team-config.yml` 定义的流水线依次推进：
```
architect → senior-dev + frontend-dev(并行) → code-reviewer → qa-tester → devops
```

product-manager 在需要需求评审时介入，dashboard-daemon 持续后台监控。

### Step 6: 工作完成后关闭

所有阶段完成后，向每个 agent 发送 `shutdown_request`，收到响应后调用 `team_delete`。

## 关键文件

| 文件 | 说明 |
|------|------|
| `agent-team/workflows/init-team.md` | 平台无关初始化流程 |
| `agent-team/team-config.yml` | 团队定义和流水线 |
| `agent-team/knowledge/context.md` | 项目上下文 + 服务地址 |
| `agent-team/protocols/communication.md` | 通信协议 |
| `.codebuddy/agents/*.md` | CodeBuddy agent 适配 |
