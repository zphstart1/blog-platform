# Agent 研发团队集群 v4 — 使用指南

> **v4 更新**：复杂度智能评估 + 自动阶段跳转 + 自动化质量门禁 + 共享知识库自学习 + 直接通信通道

## 🎯 快速开始

你现在拥有一个完整的 7 人虚拟研发团队，只需告诉 Team Lead 你的需求即可。

### 对话方式

你在对话框里直接和我对话即可，我作为 Team Lead 会驱动整个团队。

**示例**：
> "我需要开发一个用户管理模块，包含注册、登录、权限管理功能"

> "帮我做一个商品库存管理系统，支持库存扣减和预警"

> "改一下页面上的按钮颜色"  ← v4 可识别为 Tiny 任务，自动跳过评审阶段

### 团队响应

Team Lead 会自动：
1. 分析你的需求 + **评估复杂度（Tiny~XLarge）**
2. 派发给对应成员 + **注入历史经验陷阱**
3. 审核交付物 + **执行自动化质量门禁**
4. **按复杂度智能跳转阶段**（小任务不再走全流程）
5. **每个阶段完成后主动通知你**
6. 最终交付完整成果 + **回写知识库**

## 👥 团队阵容

| 成员 | 角色 | 汇报机制 | 直接通信 |
|------|------|:--:|:--:|
| **team-lead** | 技术团队领导 👑 | 每阶段完成汇报 | 全员 |
| product-manager | 产品经理 📋 | 完成即汇报 | TL only |
| architect | 架构师 🏗️ | 完成即汇报 | TL only |
| senior-dev | Java资深开发 💻 | 完成/修复即汇报 | QA, Frontend |
| frontend-dev | 资深前端开发 ⚛️ | 完成/修复即汇报 | Backend |
| qa-tester | 测试工程师 🔍 | 完成即汇报 | Dev, DevOps |
| devops | 运维工程师 🚀 | 完成即汇报 | QA |

## 🔄 交付流水线

```
你的任务 → Team Lead 接收
              │
              ├─→ ⓪ 复杂度评估（v4 新增）← 决定跳转策略
              │     Tiny: 跳过①②⑥ → 直接③④⑤
              │     Small: 跳过① → ②简化版③④⑤
              │
              ├─→ ① 产品经理：需求评审 ──→ 通知main ✅
              ├─→ ② 架构师：技术方案 ──→ 通知main ✅
              ├─→ ③ 代码实现（前后端并行）──→ 通知main ✅
              │    ├── Java后端
              │    └── 前端开发
              ├─→ ④ 测试：质量验证 ──→ 通知main ✅
              │    └── 自动执行 Gate 3-4 质量门禁
              │         ↓ (Bug→打回③→修复→重测，循环直到P0/P1=0)
              ├─→ ⑤ 运维：部署上线 ──→ 通知main ✅
              │    └── 自动执行 Gate 4-5 健康检查
              ├─→ ⑥ Team Lead：交付产品 ──→ 通知main ✅
              └─→ ⑦ 知识回写（v4 新增）──→ 更新知识库
```

## 🆕 v4 核心特性

### 1. 智能复杂度评估
任务自动分级：Tiny / Small / Medium / Large / XLarge
- 文案修改、小 Bug → 直接进入编码，跳过评审阶段
- 新功能 → 完整 6 阶段流水线
- 大项目 → 完整 + 扩容 + 审批

详见 `workflows/complexity-assessment.md`

### 2. 自动化质量门禁
5 道自动化检查点，不通过则阻断：
- Gate 1: Lint 检查 → Gate 2: 构建验证 → Gate 3: 测试覆盖率 → Gate 4: 浏览器验证 → Gate 5: 健康检查

详见 `protocols/quality-gates.md`

### 3. 自学习知识库
- 每次任务自动注入历史陷阱经验
- 任务完成后自动回写新发现
- 跨会话持续积累团队经验

详见 `knowledge/learned-lessons.md`

### 4. 直接通信通道
开发与测试间可进行信息澄清，减少 TL 中转瓶颈

详见 `protocols/communication.md`

### 5. CI/CD 集成
支持 GitHub Actions 自动触发构建和门禁检查

详见 `.github/workflows/ci.yml`

## 📁 项目结构

```
agent-team/
├── README.md                          # 本文件
├── team-config.yml                    # 团队配置（v4：含质量门禁 + 知识回写）
├── roles/                             # 角色定义文件
│   ├── team-lead.md                   # 技术团队领导（v4：复杂度评估 + 知识注入）
│   ├── product-manager.md             # 产品经理
│   ├── architect.md                   # 架构师
│   ├── senior-dev.md                  # Java后端开发
│   ├── frontend-dev.md                # 前端开发
│   ├── qa-tester.md                   # 测试工程师（v4：质量门禁）
│   └── devops.md                      # 运维工程师（v4：CI/CD 集成）
├── protocols/                         # 协作协议
│   ├── communication.md               # 通信协议（v4：直接通信通道）
│   └── quality-gates.md               # 质量门禁定义（v4 新增）
├── workflows/                         # 工作流定义
│   ├── pipeline.md                    # 交付流水线（v4：复杂度跳转 + 门禁）
│   └── complexity-assessment.md       # 复杂度评估规则（v4 新增）
├── knowledge/                         # 共享知识库（v4 新增）
│   ├── context.md                     # 项目上下文（跨会话共享）
│   └── learned-lessons.md             # 经验陷阱库（自学习）
├── scripts/                           # 自动化脚本（v4 新增）
│   ├── lint-check.ps1                 # 前端质量门禁
│   └── backend-check.ps1              # 后端质量门禁
└── integrations/                      # 外部集成配置
    ├── user-config.yml                # 用户环境配置
    ├── integration-points.md          # 集成点定义
    └── examples/                      # 配置示例
```

## 📁 输出文件结构

```
docs/{req-name}/
├── 01-需求评估报告.md          (产品经理)
├── 02-架构设计文档.md          (架构师)
├── 03-前端接口文档.md          (架构师)
├── 04-后端自检报告.md          (Java开发)
├── 05-前端自检报告.md          (前端开发)
├── 06-测试报告.md             (测试工程师)
├── 07-上线方案.md             (运维工程师)
├── 08-上线报告.md             (运维工程师)
├── 09-交付报告.md             (Team Lead)
├── 10-知识沉淀文档.md          (Team Lead)
└── ddl/{req-name}-ddl.sql     (Java开发)
```

## 🔌 外部集成

团队负责产出代码、DDL、部署脚本、监控配置等**制品**，运行时依赖的外部中间件和基础设施由你按需接入。

### 可插拔集成点

| 集成点 | 说明 | 配置方式 |
|--------|------|---------|
| **版本控制** | Git 远程仓库推送、分支管理 | 填写 `integrations/user-config.yml` |
| **数据库** | MySQL/PostgreSQL 连接 | 同上 |
| **缓存** | Redis 集群/单机 | 同上 |
| **消息队列** | Kafka/RocketMQ | 同上 |
| **构建服务** | Maven 编译 + Docker 打包 | 同上 |
| **镜像仓库** | Harbor/Docker Hub 推送 | 同上 |
| **部署目标** | K8s / SSH / Cloud Studio | 同上 |
| **监控系统** | Prometheus + Grafana | 同上 |
| **前端技术栈** | React / Vue / 小程序 | 同上 |

### 接入步骤

1. 复制 `integrations/user-config.yml`，填写你的环境信息
2. 每个集成点可单独 `enabled: true/false` 控制开关
3. 参考 `integrations/examples/` 中的本地/云端示例
4. 团队产出时会读取配置，生成适配你环境的脚本

> 详见 [`integrations/integration-points.md`](integrations/integration-points.md) 了解每个集成点的完整契约定义。

### ⚡ 一键接入（IDE 集成）

以下服务可在 IDE 集成面板中一键登录，无需手动配置连接串：

| 服务 | 用途 | 支持能力 |
|------|------|---------|
| **Supabase** | PostgreSQL 数据库 | 数据库 + 认证 + 存储 |
| **CloudBase** | 腾讯云开发 | 数据库 + 云函数 + 部署 |
| **Cloud Studio** | 远程开发环境 | 开发 + 部署 |
| **Lighthouse** | 轻量服务器 | 实例管理 + 部署 |
| **EdgeOne Pages** | 前端部署 | 静态站点上线 |
