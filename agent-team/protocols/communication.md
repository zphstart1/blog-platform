# 团队协作协议 (Communication Protocol)

## 通信架构

```
用户 (You)
  │
  │  ⇄  唯一交互入口
  │
  ▼
团队领导 (Team Lead)
  │
  ├── ⇄ 产品经理 (Product Manager)
  ├── ⇄ 架构师 (Architect)  
  ├── ⇄ Java资深开发 (Senior Dev)
  ├── ⇄ 测试工程师 (QA Tester)
  └── ⇄ 运维工程师 (DevOps)

直接通道（v4 新增，仅限低风险信息交换）：
  Senior Dev ⇄ QA Tester     — Bug 澄清、测试数据确认
  Frontend Dev ⇄ QA Tester   — 前端 Bug 澄清、UI 异常复现步骤
  Senior Dev ⇄ Frontend Dev  — API 契约变更通知
  QA Tester  ⇄ DevOps        — 测试环境部署确认
```

**核心规则：**
- 用户只与 Team Lead 交互
- 任务派发和审批类消息必须通过 Team Lead 中转
- v4 新增直接通道仅限**信息澄清**，不等同于任务派发。产生决策的沟通仍需经 Team Lead

## 消息传递机制

所有团队成员使用 `send_message` 工具进行通信：

### Team Lead → 成员（任务派发）
```
send_message(
  type="message",
  recipient="{member-id}",
  content="<任务描述，包含具体要求和交付格式>",
  summary="<5-10字任务摘要>"
)
```

### 成员 → Team Lead（成果交付）
```
send_message(
  type="message", 
  recipient="team-lead",
  content="<完成的交付物，使用 Markdown 格式>",
  summary="<5-10字交付摘要>"
)
```

## 任务派发格式规范

Team Lead 向成员派发任务时，必须遵循以下格式：

```markdown
## 任务：{任务名称}
### 背景
{需求背景简述}

### 输入材料
{上阶段交付物内容或引用}

### 交付要求
{具体的交付物和格式要求}

### 截止时间
{预期完成时间}
```

## 交付物格式规范

成员向 Team Lead 交付成果时，必须遵循以下格式：

```markdown
## 交付：{阶段名称}
### 交付物清单
- [ ] 文件1
- [ ] 文件2

### 核心内容
{完整的交付内容，使用 Markdown}

### 风险和待确认项
{如有}
```

## 直接通信协议（v4 新增）

### 允许的直接通信场景

| 场景 | 发起方 | 接收方 | 限制 |
|------|--------|--------|------|
| Bug 信息澄清 | QA | Dev (backend/frontend) | 仅澄清复现步骤，不派活 |
| 前端 Bug 澄清 | QA | Frontend Dev | 仅澄清 UI 异常/console error |
| API 契约变更通知 | Backend Dev | Frontend Dev | 通知路径/参数变化 |
| 测试环境确认 | QA | DevOps | 确认环境就绪状态 |

### 直接通信格式

```
send_message(
  type="message",
  recipient="{member-id}",
  content="<仅限信息交换>",
  summary="<注明 [Direct] 前缀>"
)
```

### 直接通信红线

| ✅ 允许 | ❌ 不允许 |
|---------|----------|
| "这个 Bug 的复现步骤能否再详细描述？" | "这个 Bug 你修一下" |
| "API 返回字段从 `name` 改为 `title`" | "顺带把前端对接逻辑也改了" |
| "测试环境 8080 端口是否已启动？" | "帮我在测试环境部署一下" |

**违反红线的后果**：接收方应拒绝执行，并转发给 Team Lead 裁决。

### 接收方忙碌时的处理规则（v4）

**问题场景**：A 向 B 发直接消息，但 B 正在执行长任务（如批量编码），消息仅进入 B 的收件箱，B 要到下一个 turn 才会读到。

```
规则 1：必须 CC Team Lead
  — 所有直接通信同时 CC 给 team-lead
  — Team Lead 知道有未决消息，可在必要时介入

规则 2：B 收到消息后优先处理
  — 下一个 turn 开始时 → 先检查收件箱 → 如有直接消息，暂停当前任务回复
  — 回复原则：只回答信息，不接新任务

规则 3：超时自动升级
  — B 在 2 个 turn 内未回复 → Team Lead 介入
  — Team Lead 直接联系 B 询问状态，或另找途径解决

规则 4：紧急问题走 TL
  — 需立即决策的问题不通过直接通信 → 发送给 Team Lead
  — TL 可中断/重新分配任务
```

**发起方操作示例**：

```
send_message(
  type="message",
  recipient="frontend-dev",
  content="<澄清问题>",
  summary="[Direct] console error 复现步骤确认"
)
send_message(
  type="message",
  recipient="team-lead",           ← 必须 CC
  content="[CC] 已向 frontend-dev 发起 console error 确认，等待回复。",
  summary="[CC] QA→Frontend 直接通信"
)
```

**接收方操作示例**（下一个 turn 开始）：

```
1. 检查收件箱，发现来自 qa-tester 的 [Direct] 消息
2. 暂停当前编码任务（记录进度）
3. 回复 qa-tester 澄清信息
4. 恢复暂停的编码任务
```

## 团队状态管理

### Team Lead 需要在每个阶段开始时广播状态
```
send_message(
  type="broadcast",
  content="📢 当前阶段：{阶段名} | 负责人：{成员} | 状态：进行中",
  summary="阶段状态更新"
)
```

### 阶段推进规则
- 当前阶段交付物必须经 Team Lead 审核通过，才能进入下一阶段
- 审核不通过，打回给负责人修改，标注修改要求
- Team Lead 发现阻塞问题，立即向用户汇报

## 🛑 Team Lead 行为红线

Team Leader **只能做决策和协调**，以下操作**绝对禁止**：

| ❌ 禁止行为 | 说明 |
|-----------|------|
| 修改源文件（.java/.vue/.ts/.sql/.yml/.xml 等） | 给 dev 做 |
| 执行构建命令（npm/mvn/docker build） | 给 devops 做 |
| 执行部署命令（docker restart/nginx reload） | 给 devops 做 |
| 执行测试命令（playwright-cli/curl） | 给 qa-tester 做 |
| 写交付文档（自检/测试报告等） | 给对应 specialist 做 |

**Leader 只做三件事：接收 → 分配 → 审核汇总。**

## 动态扩容协议

### 触发条件
当任务规模超过单个开发者容量时（由 Team Lead 在 Stage 0 判定）：

| 规模 | 判定 |
|------|------|
| S（1-3 接口 / 1-2 页面） | 无需扩容，1 backend + 1 frontend |
| M（4-8 接口 / 3-5 页面） | 可选扩容 1 个 dev |
| L/X（9+ 接口 / 6+ 页面） | 必须扩容 |

### 扩容步骤
1. Team Lead 评估任务量 → 决定 spawn 数量
2. 使用 `task` 工具 spawn 额外 agent：
   ```
   task(
     name="{role}-{N}",
     team_name="dev-team",
     prompt="你负责以下模块：[模块清单]. 完成后通知 team-lead.",
     mode="acceptEdits"
   )
   ```
3. 接口/页面清单拆分为子任务，每个 agent 明确范围
4. 公共模块（Service/组件/工具类）由编号最小的 dev 负责
5. 所有 agent 完成后 → 指定 merger agent → 集成合并
6. Team Lead 审核合并结果

### 子任务分片规范
- **后端**：按 Controller 拆分，每个 dev 2-3 个 Controller
- **前端**：按页面拆分，每个 dev 3-5 个页面
- **公共代码**：编号 1 的 dev 负责，完成后通知其他 dev

## QA 工具权限要求

qa-tester 必须拥有以下工具才能执行浏览器功能测试：
- `playwright-cli`：浏览器自动化（goto/snapshot/screenshot/fill/click）
- `agent-browser`：备选浏览器方案
- `read_file`：日志读取
- `write_to_file`：报告输出
- `execute_command`：API 接口测试

如果 qa-tester 未获得这些工具，由 Team Lead 向主 Agent 申请授权。

## 🔴 强制规则：完成后必须通知主 Agent (main)

**所有成员（含 Team Lead）完成工作后，必须执行以下两步，缺一不可：**

```
步骤 1: write_to_file   → 将交付物写入 docs/ 目录
步骤 2: send_message     → 通知主 Agent（recipient="main"）
```

### 通知格式

```
send_message(
  type="message",
  recipient="main",           ← 必须！不能只是 team-lead
  content="<简述完成的工作和产出文件清单>",
  summary="<5-10字摘要>"
)
```

### 禁止行为

| ❌ 禁止 | ✅ 正确 |
|--------|--------|
| 只写文件不通知 | 写文件 + send_message(main) |
| 只发 team-lead 不发 main | 同时发 team-lead + main |
| 完成工作后什么都不做 | 主动汇报 |

### Team Lead 额外规则

- 每阶段开始时 → send_message(main) 告知当前阶段和负责人
- 每阶段完成时 → send_message(main) 告知审核结果
- 遇到阻塞时 → 立即 send_message(main)
- 交付时 → send_message(main) 汇总全部产出

---

## 构建与部署交接协议

**关键原则：Dev 构建，Devops 部署。Team Lead 只做路径中转，不替任何一方做判断。**

```
    Dev 修复代码 + npm build / mvn package
      │
      │  send_message → TL: "构建完成，产物路径：{dist-or-jar-path}"
      ▼
    Team Lead 审核自检通过
      │
      │  send_message → Devops: "请部署，前端产物：{path}，后端产物：{path}"
      ▼
    Devops 直接使用指定路径部署（信任上游，不验证是否最新）
```

### 各方职责

| 角色 | 做什么 | 不做什么 |
|------|--------|---------|
| Dev | 修复代码 → 构建 → 告知 TL 产物路径 | 不部署、不判断部署环境 |
| Team Lead | 审核自检 → 传递产物路径给 Devops | 不构建、不部署、不验证产物新旧 |
| Devops | 拿指定路径产物 → 部署 → 健康检查 | 不判断产物是否最新、不重新构建 |

### Devops 收到的部署消息格式
```
send_message(
  type="message",
  recipient="devops",
  content="部署任务：
    前端产物：blog-frontend/dist/
    后端产物：blog-server/target/blog-server.jar
    docker-compose.yml 在项目根目录
    请部署并验证。",
  summary="Bug修复后的部署"
)
```

**Devops 不需要、也不应该检查产物是否"最新"**——如果部署后 QA 测试发现问题，那说明 Dev 的构建步骤有问题，流程回退到 Dev 修复，而不是 Devops 去判断。

---

## 异常处理

| 场景 | 处理方式 |
|------|---------|
| 成员超时未响应 | Team Lead 催促一次，超时未响应则告知用户 |
| 交付物质量不达标 | 打回修改，最多2次打回，第3次 Team Lead 介入 |
| 成员间依赖阻塞 | Team Lead 协调，重新排期 |
| 技术风险升级 | Team Lead 召集架构师和开发讨论，必要时告知用户 |
