# 任务复杂度评估与阶段跳转规则

## 评估维度

| 维度 | 权重 | 说明 |
|------|------|------|
| 代码变更范围 | 25% | 涉及文件数量 + 行数 |
| 新增功能量 | 20% | 新接口数 + 新页面数 |
| 架构影响 | 15% | 是否改数据库/缓存/中间件 |
| DDD 额外开销 | 20% | 涉及的 Bounded Context 数量 + 聚合复杂度 |
| 风险等级 | 20% | 数据丢失/安全/兼容性风险 |

## DDD 额外开销评分规则

DDD 比传统三层架构多出大量胶水代码（Repository 实现、Converter、PO、Command/Query），评估时需额外加权：

| DDD 场景 | 加分数 | 典型示例 |
|---------|--------|---------|
| 单 Context 内扩展现有聚合（加字段/方法） | +3 | 给 Article 加点赞方法 |
| 单 Context 内新增 1 个 ValueObject | +5 | 新增 Slug、ViewCount 值对象 |
| 单 Context 内新增 1 个 Aggregate | +10 | 新增 ArticleVersion 聚合 |
| 单 Context 内新增 2+ Aggregate | +15 | 新增 Category + Tag 聚合 |
| 跨 2 个 Context（现存）交互 | +8 | Article 引用 User 的 AuthorId |
| 跨 3+ Context 交互 | +15 | Article 发布 → Comment 开放 + Search 索引 |
| 新增 Bounded Context | +20 | 新增 Notification Context |
| 引入领域事件（跨 Context） | +10 | ArticlePublishedEvent 通知 Comment/Search |
| 新增 CQRS 读写分离 | +8 | QueryService 直接查库绕过 Domain |

**DDD 评分公式**：`DDD 开销 = SUM(上述规则匹配项)`，上限 40 分。

**与传统项目对比**：
```
同样的"加点赞"功能：
  传统三层：Model +1 / Mapper +1 / Service +1 / Controller +1 = 4 文件
  DDD：Aggregate +1 / VO +1 / Repo接口 +1 / Repo实现 +1 / Converter +1 / 
       AppService +1 / Controller +1 / Command +1 / Query +1 = 9 文件
→ DDD 额外开销 +13 分（新增 VO + Aggregate，跨 2 个 Context）
```

## 复杂度等级

| 等级 | 得分 | 典型场景 | 管道策略 |
|------|------|----------|---------|
| **Tiny** | 0-15 | 文案修改、配置调整、CSS微调 | **跳过 S1-S2，直接进入 S3** |
| **Small** | 16-30 | Bug Fix、单文件小改动 | **跳过 S1，从 S2 开始** |
| **Medium** | 31-60 | 单功能新增、单页面重构 | **完整流水线（S1-S6）** |
| **Large** | 61-85 | 多模块改造、跨服务变更 | **完整 + 扩容** |
| **XLarge** | 86-100 | 全新系统、重大架构调整 | **完整 + 扩容 + 加审批** |

## 自动跳转规则

### Tiny 任务
```
Pipeline: S3（代码实现）→ S4（测试）→ S5（部署）
跳过的阶段：S1（需求）S2（技术方案）S6（交付文档）
说明：Team Lead 直接派发给 dev，修复后 QA 验证 → DevOps 部署
```

### Small 任务
```
Pipeline: S2（技术方案-简化版）→ S3（代码）→ S4（测试）→ S5（部署）
跳过的阶段：S1（需求）
简化 S2：架构师不用出完整文档，只确认影响范围和实现方向
```

### Medium+ 任务
```
完整 6 阶段流水线
```

## 快速评估表

| 任务示例 | 等级 | DDD 加分 | 策略 |
|----------|------|---------|------|
| 修改按钮颜色 | Tiny | 0 | 直接改 |
| 修复 `\n` 转义序列 Bug | Tiny | 0 | 直接改 + QA 验证 |
| 给 Article 加 viewCount 字段 | Small | +3 (扩展现有聚合) | S2 简化 → S3 → S4 → S5 |
| 给文章添加点赞功能 | Medium | +13 (新增VO+聚合, 跨2Context) | 完整流水线 |
| 新增评论审核功能 | Medium | +10 (新增 Aggregate, 跨2Context) | 完整流水线 |
| 新增用户管理模块 | Medium | +10 (新增 Aggregate, 跨2Context) | 完整流水线 |
| 新增通知 Context（邮件/站内信） | Large | +28 (新Context+事件+跨3Context) | 完整 + 扩容 |
| 将传统三层重构为 DDD | Large | - (重构，按文件数评估) | 完整 + 扩容 |
| 重构支付流程 | Large | - | 完整 + 扩容 |
| 新建微服务系统 | XLarge | - | 完整 + 扩容 + 审批 |

## Team Lead 执行流程

```
收到用户任务
  │
  ├─→ 评估复杂度（查表）
  │
  ├─→ Tiny → 直接派发给对应 dev，通知 QA 准备测试
  │
  ├─→ Small → 派发 S2（简化版）给 architect → ...
  │
  └─→ Medium+ → 启动完整 6 阶段流水线
```
