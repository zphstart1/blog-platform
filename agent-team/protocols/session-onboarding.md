# 会话接入协议 (Session Onboarding Protocol)

> **用途**：每次新会话开始时，Team Lead 执行此协议以恢复项目上下文，确保开发连续性。
> **版本**：v4

---

## 触发时机

每当用户开始一个新会话并提出开发相关需求时，Team Lead 必须先执行此协议，再开始正常的任务流程。

---

## 三步接入流程

### Phase 1：恢复上下文（30 秒）

```
读取顺序：
  1. agent-team/knowledge/context.md       — 技术栈、项目结构、系统状态
  2. agent-team/knowledge/backlog.md       — 待办清单、进行中任务
  3. agent-team/knowledge/learned-lessons.md — 历史陷阱
```

**产出**：一句话状态摘要
> "当前项目状态：blog-platform，前后端分离 + Docker。7 个组件全部运行中。待办 2 项，无进行中任务。最近改动：Agent v4 升级。"

### Phase 2：执行健康快照（可选，取决于任务类型）

| 任务类型 | 是否执行 | 检查内容 |
|---------|---------|---------|
| 新功能开发 | ✅ 是 | `curl localhost:8080/actuator/health` + `docker ps` |
| Bug 修复 | ✅ 是 | 确认环境可用 |
| 纯文档/方案 | ❌ 跳过 | - |

**异常处理**：如果服务不在运行 → 通知用户环境问题 → 等待用户修复后再继续

### Phase 3：同步 git 状态

```bash
git --no-pager log --oneline -5   # 了解最近提交
git --no-pager status --short     # 检查未提交改动
```

**注意**：此 Phase 只在需要写代码时执行，纯讨论/方案阶段跳过。

**异常处理**：
- 有未提交代码 → 告知用户，确认是否继续
- 远程有更新 → 告知用户，建议先 pull

---

## 接入完成后的声明

Team Lead 在接入完成后向用户发送：

```
会话接入完成：
  • 项目状态：{摘要}
  • 待办任务：{N} 项
  • 上次改动：{最近一条改动}
  • 环境状态：{健康/异常}

已就绪，请描述你的需求。
```

---

## 与复杂度评估的关系

```
会话接入（本协议）
    │
    ▼
复杂度评估（complexity-assessment.md）
    │
    ▼
正常流水线（pipeline.md）
```

接入协议只恢复状态，不决定任务流程——复杂度评估来决定。
