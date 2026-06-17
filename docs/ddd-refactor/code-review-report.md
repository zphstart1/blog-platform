# DDD 重构代码审查报告（第二轮）

> 审查日期：2026-06-17 | 审查范围：article / user / comment / shared  
> 审查维度：四层依赖方向、聚合设计、Repository、CQRS、Controller 薄层、VO 组装

---

## 一、审查概览

| 指标 | 数值 |
|------|------|
| 审查文件数 | 88 |
| Bounded Context | 3 (article / user / comment) |
| 严重违规 (P0) | 2 |
| 高违规 (P1) | 8 |
| 中违规 (P2) | 3 |
| 设计异味 | 4 |
| 总体评分 | ***68/100***（较上次 78 分下降，因新增跨层违规） |

---

## 二、架构总览

```
blog-server/src/main/java/com/blog/
├── article/   (57 文件) << domain/app/infra/interfaces
├── user/      (12 文件) << domain/app/infra/interfaces
├── comment/   (11 文件) << domain/app/infra/interfaces
├── shared/    (5 文件)  BaseEntity/DomainException/Result/AuthContext/NotFoundException
├── config/    (13 文件)  Spring 配置
├── vo/        (12 文件)  视图对象 — 存放位置不符合 DDD 规范
├── dto/       (13 文件)  数据传输对象 — 存放位置不符合 DDD 规范
├── upload/    (接口层)
├── entity/mapper/service/controller/ — 旧目录残留（空）
└── BlogApplication.java
```

**⚠️ 结构问题**：`vo/` 和 `dto/` 位于顶层包 `com.blog` 下，未归入各自的 Bounded Context。应放在对应 Context 的 `application/` 或 `interfaces/` 包内。

---

## 三、违规清单

### 🚨 P0 (2项) — 严重架构违规

| # | 文件 | 违规 | 修复建议 |
|---|------|------|---------|
| 1 | `article/application/TagApplicationService.java:5` | **Application 层直接 import `article.infrastructure.ArticleTagMapper`**，越过 domain 层调用 Mapper 统计文章数 | 在 `TagRepository` 中新增 `countArticlesByTagId(TagId id)` 方法，由 `TagApplicationService` 调用 Repository 接口 |
| 2 | `article/application/CategoryApplicationService.java:5,9,10` | **Application 层直接 import `article.infrastructure.ArticleMapper`、`ArticlePO`**，并使用 MyBatis `LambdaQueryWrapper` 拼 SQL 查询文章数 | 在 `CategoryRepository` 中新增 `countArticles(CategoryId id)` 方法，由 `CategoryApplicationService` 调用 Repository 接口 |

> **严重性说明**：P0 违规直接破坏了 DDD 依赖方向——Application 层不应知道 infrastructure 层的 Mapper 和 PO 存在。当前代码中 `TagApplicationService` 和 `CategoryApplicationService` 分别注入了 `ArticleTagMapper` 和 `ArticleMapper`，在 `toTagVO()` / `toCategoryVO()` 方法中直接调用 Mapper 进行统计查询。这是从传统三层架构迁移到 DDD 时的典型泄漏。

### ⚠️ P0 延续 — domain 层依赖 Spring Security（未修复）

| # | 文件 | 违规 | 修复建议 |
|---|------|------|---------|
| 3 | `user/domain/User.java:5,15` | `import BCryptPasswordEncoder` + `static ENCODER` | domain 层定义 `PasswordEncoder` 接口，infrastructure 提供 Spring 实现 |

### ⚠️ P1 (8项) — domain 层依赖 Spring 工具类（未修复）

| # | 文件 | 违规 import | 修复 |
|---|------|------------|------|
| 4 | `article/domain/Article.java:7` | `org.springframework.util.StringUtils` | 替换为 `value.isBlank()` |
| 5 | `article/domain/Category.java:6` | `org.springframework.util.StringUtils` | 同上 |
| 6 | `article/domain/Title.java:4` | `org.springframework.util.StringUtils` | 同上 |
| 7 | `article/domain/Content.java:3` | `org.springframework.util.StringUtils` | 同上 |
| 8 | `article/domain/Link.java:5` | `org.springframework.util.StringUtils` | 同上 |
| 9 | `article/domain/Tag.java:6` | `org.springframework.util.StringUtils` | 同上 |
| 10 | `article/application/ArticleApplicationService.java:16` | `org.springframework.util.StringUtils` | Application 层也使用了（第 60、64、69 等行），替换为 `StringUtils.hasText()` 的纯 Java 实现 |
| 11 | `article/domain/ArticleDomainService.java:7` | `@Service` + `@Slf4j` | domain Service 应为纯 POJO，去掉 Spring 注解 |

### ⚠️ P1 — AdminArticleController 分页直接调 Mapper

| # | 文件 | 违规 | 修复 |
|---|------|------|------|
| 12 | `article/interfaces/AdminArticleController.java:8,31,41` | Controller 注入 `ArticleMapper` 并直接调用 `articleMapper.selectAdminArticlePage()` | 应通过 `ArticleReadService` 或 `ArticleApplicationService` 调用，Controller 不应持有 Mapper 引用 |

### ⚠️ P2 (3项) — 分层细节问题

| # | 文件 | 违规 | 修复 |
|---|------|------|------|
| 13 | `comment/interfaces/CommentController.java:22` | `Result<PageResult<Comment>>` 直接返回 domain 对象 | 定义 `CommentVO` DTO，在 application 层转换 |
| 14 | `article/interfaces/LinkController.java:25,31,37` | `Result<List<Link>>` / `Result<Link>` 直接返回 domain 对象 | 定义 `LinkVO` DTO |
| 15 | `article/application/ArticleApplicationService.java:103-109` | `createArticle()` 返回 `Map<String, Object>` 手动拼装 | 定义 `ArticleCreateResult` DTO 或使用 `ArticleVO` |

### ⚠️ P2 — 值对象在 VO 组装时的拆解

| # | 文件 | 分析 | 状态 |
|---|------|------|------|
| — | `TagApplicationService.toTagVO():96` | `tag.getSlug().value()` — Slug → String，✅ 正确拆解 | ✅ |
| — | `CategoryApplicationService.toCategoryVO():112` | `category.getSlug().value()` — Slug → String，✅ 正确拆解 | ✅ |
| — | `ArticleApplicationService.listDrafts():243-254` | 手动从 domain 拆解到 VO，正确但冗长 | ⚠️ 建议用 Converter |

### ⚠️ 设计异味 (4项)

| # | 文件 | 描述 | 修复 |
|---|------|------|------|
| 16 | `user/domain/User.java:81-87` | `setBaseId()` 用反射注入 ID | `BaseEntity` 提供 `protected setId()` |
| 17 | `comment/domain/Comment.java:75-81` | 同上，反射注入 ID | 同 |
| 18 | `comment/domain/Comment.java:14` | `articleId` 用裸 `Long` | 使用 `ArticleId` 值对象 |
| 19 | `vo/` 和 `dto/` 目录 | 顶层包存放，不属于任何 Bounded Context | 迁移到对应 Context 的 `application/` 或 `interfaces/` 包 |

---

## 四、重点问题详细分析

### 4.1 TagApplicationService 越过 Domain 层（P0）

```java
// TagApplicationService.java:92-101
private TagVO toTagVO(Tag tag) {
    TagVO vo = new TagVO();
    vo.setId(tag.getId());
    vo.setName(tag.getName());
    vo.setSlug(tag.getSlug().value());
    int articleCount = articleTagMapper.countByTagId(tag.getId());  // ← 直接调 Mapper!
    vo.setArticleCount(articleCount);
    vo.setWeight(articleCount);
    return vo;
}
```

**问题**：Application 层注入了 `ArticleTagMapper`（infrastructure），用于统计标签下的文章数。这违反了 DDD 依赖方向：`application → domain ← infrastructure`。

**正确做法**：在 `TagRepository` 接口（domain 层）中定义 `int countArticles(TagId tagId)`，由 `TagRepositoryImpl` 实现，ApplicationService 通过 Repository 接口调用。

### 4.2 CategoryApplicationService 越过 Domain 层（P0）

```java
// CategoryApplicationService.java:108-120
private CategoryVO toCategoryVO(Category category) {
    // ...
    LambdaQueryWrapper<ArticlePO> wrapper = new LambdaQueryWrapper<>();  // ← 直接拼 SQL!
    wrapper.eq(ArticlePO::getCategoryId, category.getId());
    Long count = articleMapper.selectCount(wrapper);
    vo.setArticleCount(count != null ? count.intValue() : 0);
    return vo;
}
```

**问题**：同上，Application 层直接使用 MyBatis-Plus 的 `LambdaQueryWrapper` 和 `ArticleMapper`。且 `ArticlePO` 是 infrastructure 层的对象，更不应该出现在 Application 层。

### 4.3 AdminArticleController 直接调 Mapper（P1）

```java
// AdminArticleController.java:31,40-41
private final ArticleMapper articleMapper;  // Controller 注入 Mapper

@GetMapping
public Result<PageResult<ArticleVO>> list(...) {
    Page<ArticleVO> p = new Page<>(page, size);
    List<ArticleVO> records = articleMapper.selectAdminArticlePage(p, ...);  // Controller 直接调 Mapper
    ...
}
```

**问题**：Controller 层越过 Application 层和 Domain 层，直接调用 infrastructure 层的 Mapper。CQRS 读侧允许直接使用 Mapper，但应该封装在 Application 层的 ReadService 中，Controller 应保持薄层。

---

## 五、合规项 ✅

| 检查项 | 状态 | 说明 |
|--------|:---:|------|
| Repository 接口在 domain/ | ✅ | 全部 3 个 Context 正确 |
| Repository 实现在 infrastructure/ | ✅ | `*RepositoryImpl` 全部在 infra |
| Aggregate Root 有业务行为 | ✅ | publish/approve/reject/rename 等 |
| ValueObject 不可变 | ✅ | `@final class` + `private final` 字段 |
| ArticleController 薄层 | ✅ | 只做参数校验 + 调用 ReadService |
| TagController/CategoryController 薄层 | ✅ | 只做路由转发 |
| SearchController 薄层 | ✅ | 只做参数校验 + 调用 SearchApplicationService |
| DDD 目录结构 | ✅ | 4 层完整，按 Bounded Context 划分 |
| PO → Domain 转换 | ✅ | `ArticleConverter` 统一转换（手写，非 MapStruct） |
| 依赖注入方式 | ✅ | 构造器注入 |
| CommentApplicationService 合规 | ✅ | 完全通过 Repository 接口访问 |
| AuthApplicationService 合规 | ✅ | 完全通过 Repository 接口访问 |
| ArticleRepository 新增方法 | ✅ | `countDraftsByAuthor()` 在 Repository 接口定义，Impl 实现 |
| Tag/Category Controller 返回 VO | ✅ | 不再返回 domain 对象 |
| ArticleApplicationService.listDrafts | ✅ | 使用 Repository 接口查询草稿 |

---

## 六、修复优先级

```
第1批 (预计 45 分钟) — P0 修复：
  □ TagApplicationService: ArticleTagMapper → TagRepository.countArticles()
  □ CategoryApplicationService: ArticleMapper → CategoryRepository.countArticles()
  □ User.java: BCryptPasswordEncoder → PasswordEncoder 接口

第2批 (预计 30 分钟) — P1 修复：
  □ AdminArticleController: 移除 ArticleMapper，改用 ReadService
  □ ArticleDomainService: 去 @Service
  □ 7个文件的 StringUtils → isBlank()

第3批 (预计 25 分钟) — P2 修复：
  □ CommentController: 引入 CommentVO
  □ LinkController: 引入 LinkVO
  □ ArticleApplicationService.createArticle: Map → DTO

第4批 (预计 20 分钟) — 设计异味：
  □ BaseEntity 提供 protected setId()
  □ Comment.articleId → ArticleId 值对象
  □ vo/ dto/ 目录迁移到各 Context
```

---

## 七、总体评价

本次代码修改引入了 **2 个新的 P0 违规**（TagApplicationService 和 CategoryApplicationService 直接注入 Mapper），导致评分从 78 分降至 68 分。这两个问题是本次修改的核心关注点——在添加"统计文章数"功能时，开发者选择了最直接的方式（注入 Mapper），而没有遵循 DDD 的 Repository 模式。

**正面改进**：
- `ArticleRepository` 正确新增了 `countDraftsByAuthor()` 方法，遵循了 Repository 模式
- Tag/Category Controller 改为返回 VO，不再返回 domain 对象
- `ArticleApplicationService.listDrafts()` 通过 Repository 接口查询，正确遵循依赖方向

**核心建议**：
- Tag 和 Category 的"文章数统计"功能应立即修复，将 Mapper 调用下沉到 Repository 实现
- AdminArticleController 的 Mapper 引用应移除，统一通过 ApplicationService
- 上次审查报告的 P0/P1 遗留问题仍未修复（User.java BCrypt、StringUtils），需一并处理
- 修复所有违规后预计评分可达 **90+/100**
