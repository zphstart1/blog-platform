-- ============================================
-- Blog 系统测试数据 Mock 脚本
-- 用途: 开发联调 & 测试执行
-- 版本: v1.0
-- 日期: 2026-06-12
-- 使用方法: source test-data.sql 或 mysql -u root -p < test-data.sql
-- 注意: 脚本会先清理已有测试数据
-- ============================================

-- ----------------------------------------
-- 1. 用户数据 (10个: 1博主 + 1管理员 + 3作者 + 5注册用户)
-- 密码均为明文 "Test123456" 的 BCrypt 哈希 (示意)
-- ----------------------------------------
INSERT INTO `user` (`id`, `username`, `email`, `password`, `nickname`, `role`, `avatar`, `status`, `created_at`) VALUES
(1,  'blogger',      'blogger@blog.local',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '博主小明',   'OWNER',  '/avatar/blogger.png',  1, '2026-01-01 00:00:00'),
(2,  'admin',        'admin@blog.local',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员',     'ADMIN',  '/avatar/admin.png',    1, '2026-01-01 00:00:00'),
(3,  'author_zhang', 'zhang@blog.local',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '作者张三',   'AUTHOR', '/avatar/zhang.png',    1, '2026-02-01 00:00:00'),
(4,  'author_li',    'li@blog.local',           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '作者李四',   'AUTHOR', '/avatar/li.png',       1, '2026-02-15 00:00:00'),
(5,  'author_wang',  'wang@blog.local',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '作者王五',   'AUTHOR', '/avatar/wang.png',     1, '2026-03-01 00:00:00'),
(6,  'user_alice',   'alice@example.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alice',      'USER',   NULL,                   1, '2026-03-15 00:00:00'),
(7,  'user_bob',     'bob@example.com',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bob',        'USER',   NULL,                   1, '2026-04-01 00:00:00'),
(8,  'user_charlie', 'charlie@example.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Charlie',    'USER',   NULL,                   1, '2026-04-15 00:00:00'),
(9,  'user_diana',   'diana@example.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Diana',      'USER',   NULL,                   1, '2026-05-01 00:00:00'),
(10, 'user_eve',     'eve@example.com',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Eve',        'USER',   NULL,                   1, '2026-05-15 00:00:00');

-- ----------------------------------------
-- 2. 分类数据 (10个)
-- ----------------------------------------
INSERT INTO `category` (`id`, `name`, `slug`, `description`, `sort_order`, `created_at`) VALUES
(1,  '后端开发',    'backend',      'Java/Spring Boot 相关技术文章',       1, '2026-01-01 00:00:00'),
(2,  '前端开发',    'frontend',     'Vue 3/Nuxt 3/TypeScript 相关',       2, '2026-01-01 00:00:00'),
(3,  '数据库',      'database',     'MySQL/Redis 数据库相关',             3, '2026-01-01 00:00:00'),
(4,  'DevOps',      'devops',       'CI/CD/Docker/部署相关',              4, '2026-01-01 00:00:00'),
(5,  '架构设计',    'architecture', '系统架构、设计模式相关',              5, '2026-01-01 00:00:00'),
(6,  '工具推荐',    'tools',        '开发工具、效率工具推荐',              6, '2026-01-01 00:00:00'),
(7,  '随笔',        'essay',        '个人随笔、思考记录',                  7, '2026-01-01 00:00:00'),
(8,  '教程',        'tutorial',     '系列教程、新手入门',                  8, '2026-01-01 00:00:00'),
(9,  '开源项目',    'opensource',   '开源项目介绍与贡献指南',             9, '2026-01-01 00:00:00'),
(10, '面试',        'interview',    '面试题解、求职经验',                 10, '2026-01-01 00:00:00');

-- ----------------------------------------
-- 3. 标签数据 (30个)
-- ----------------------------------------
INSERT INTO `tag` (`id`, `name`, `slug`, `created_at`) VALUES
(1,  'Java',          'java',          '2026-01-01 00:00:00'),
(2,  'Spring Boot',   'spring-boot',   '2026-01-01 00:00:00'),
(3,  'Vue 3',         'vue3',          '2026-01-01 00:00:00'),
(4,  'TypeScript',    'typescript',    '2026-01-01 00:00:00'),
(5,  'MySQL',         'mysql',         '2026-01-01 00:00:00'),
(6,  'Redis',         'redis',         '2026-01-01 00:00:00'),
(7,  'Docker',        'docker',        '2026-01-01 00:00:00'),
(8,  'Nuxt 3',        'nuxt3',         '2026-01-01 00:00:00'),
(9,  'JWT',           'jwt',           '2026-01-01 00:00:00'),
(10, 'RESTful API',   'restful-api',   '2026-01-01 00:00:00'),
(11, '设计模式',      'design-pattern','2026-01-01 00:00:00'),
(12, '微服务',        'microservices', '2026-01-01 00:00:00'),
(13, 'Linux',         'linux',         '2026-01-01 00:00:00'),
(14, 'Git',           'git',           '2026-01-01 00:00:00'),
(15, '单元测试',      'unit-test',     '2026-01-01 00:00:00'),
(16, '性能优化',      'performance',   '2026-01-01 00:00:00'),
(17, '安全',          'security',      '2026-01-01 00:00:00'),
(18, 'SSG',           'ssg',           '2026-01-01 00:00:00'),
(19, 'Markdown',      'markdown',      '2026-01-01 00:00:00'),
(20, 'MyBatis-Plus',  'mybatis-plus',  '2026-01-01 00:00:00'),
(21, 'IntelliJ IDEA', 'intellij',      '2026-01-01 00:00:00'),
(22, 'VS Code',       'vscode',        '2026-01-01 00:00:00'),
(23, 'Nginx',         'nginx',         '2026-01-01 00:00:00'),
(24, 'CI/CD',         'cicd',          '2026-01-01 00:00:00'),
(25, 'Maven',         'maven',         '2026-01-01 00:00:00'),
(26, 'Node.js',       'nodejs',        '2026-01-01 00:00:00'),
(27, 'CSS',           'css',           '2026-01-01 00:00:00'),
(28, 'HTML',          'html',          '2026-01-01 00:00:00'),
(29, 'JavaScript',    'javascript',    '2026-01-01 00:00:00'),
(30, 'Python',        'python',        '2026-01-01 00:00:00');

-- ----------------------------------------
-- 4. 文章数据 (50篇已发布 + 10篇草稿)
-- 按分类分布: 后端10 + 前端8 + 数据库6 + DevOps5 + 架构5 + 工具4 + 随笔4 + 教程4 + 开源2 + 面试2
-- ----------------------------------------

-- 分类: 后端开发 (10篇, author_id=1 blogger)
INSERT INTO `article` (`id`, `title`, `slug`, `content`, `summary`, `category_id`, `author_id`, `status`, `is_top`, `view_count`, `created_at`, `updated_at`) VALUES
(1, 'Spring Boot 3 快速入门指南', 'spring-boot-3-quick-start',
'# Spring Boot 3 快速入门指南\n\nSpring Boot 3 基于 Spring Framework 6，要求 Java 17+。\n\n## 环境准备\n\n- JDK 17+\n- Maven 3.8+\n- IDE (IntelliJ IDEA 推荐)\n\n## 创建项目\n\n使用 Spring Initializr 创建项目，选择以下依赖：\n\n- Spring Web\n- Spring Data JPA\n- MySQL Driver\n- Lombok\n\n## 第一个接口\n\n```java\n@RestController\npublic class HelloController {\n    @GetMapping("/hello")\n    public String hello() {\n        return "Hello, Spring Boot 3!";\n    }\n}\n```\n\n启动项目后访问 `http://localhost:8080/hello` 即可看到返回结果。\n\n## 总结\n\nSpring Boot 3 带来了许多新特性，包括原生镜像支持、Observability 增强等。',
'Spring Boot 3 基于 Spring Framework 6，本文带你快速上手环境搭建与第一个接口开发。',
1, 1, 'PUBLISHED', 1, 1520, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),

(2, 'Spring Boot 全局异常处理最佳实践', 'spring-boot-global-exception-handling',
'# Spring Boot 全局异常处理最佳实践\n\n在 RESTful API 开发中，统一的异常处理至关重要。\n\n## 自定义业务异常\n\n```java\npublic class BusinessException extends RuntimeException {\n    private final int code;\n    private final String message;\n    \n    public BusinessException(ErrorCode errorCode) {\n        this.code = errorCode.getCode();\n        this.message = errorCode.getMessage();\n    }\n}\n```\n\n## 全局异常处理器\n\n```java\n@RestControllerAdvice\npublic class GlobalExceptionHandler {\n    \n    @ExceptionHandler(BusinessException.class)\n    public Result<?> handleBusinessException(BusinessException e) {\n        return Result.error(e.getCode(), e.getMessage());\n    }\n    \n    @ExceptionHandler(Exception.class)\n    public Result<?> handleException(Exception e) {\n        log.error("系统异常", e);\n        return Result.error(500, "服务器内部错误");\n    }\n}\n```\n\n## 统一响应体\n\n建议所有接口返回统一格式，方便前端处理。',
'介绍 Spring Boot 项目中如何设计统一的异常处理机制，包括自定义异常、全局异常处理器和统一响应体。',
1, 1, 'PUBLISHED', 0, 890, '2026-01-08 14:30:00', '2026-01-08 14:30:00'),

(3, 'MyBatis-Plus 分页插件使用详解', 'mybatis-plus-pagination',
'# MyBatis-Plus 分页插件使用详解\n\nMyBatis-Plus 提供了强大的分页插件，支持多种数据库。\n\n## 配置分页插件\n\n```java\n@Configuration\npublic class MybatisPlusConfig {\n    @Bean\n    public MybatisPlusInterceptor mybatisPlusInterceptor() {\n        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();\n        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));\n        return interceptor;\n    }\n}\n```\n\n## 使用分页\n\n```java\nPage<Article> page = new Page<>(1, 10);\nIPage<Article> result = articleMapper.selectPage(page, null);\n```\n\n## 自定义分页查询\n\n复杂查询时可在 Mapper XML 中自定义 SQL，MyBatis-Plus 会自动处理分页逻辑。',
'详细介绍 MyBatis-Plus 分页插件的配置与使用，包括基础分页和自定义分页查询。',
1, 1, 'PUBLISHED', 0, 670, '2026-01-12 09:00:00', '2026-01-12 09:00:00'),

(4, 'Spring Boot 集成 Redis 缓存实战', 'spring-boot-redis-cache',
'# Spring Boot 集成 Redis 缓存实战\n\n## 添加依赖\n\n```xml\n<dependency>\n    <groupId>org.springframework.boot</groupId>\n    <artifactId>spring-boot-starter-data-redis</artifactId>\n</dependency>\n```\n\n## 配置 Redis\n\n```yaml\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n    database: 0\n```\n\n## 使用缓存注解\n\n```java\n@Cacheable(value = "article", key = "#id")\npublic Article getById(Long id) {\n    return articleMapper.selectById(id);\n}\n\n@CacheEvict(value = "article", key = "#id")\npublic void deleteById(Long id) {\n    articleMapper.deleteById(id);\n}\n```\n\n合理使用缓存可以大幅提升接口性能。',
'Spring Boot 集成 Redis 实现缓存功能，包括依赖配置、缓存注解使用和缓存策略设计。',
1, 1, 'PUBLISHED', 0, 1120, '2026-01-20 16:00:00', '2026-01-20 16:00:00'),

(5, 'JWT 认证在 Spring Boot 中的实现', 'jwt-auth-spring-boot',
'# JWT 认证在 Spring Boot 中的实现\n\nJWT (JSON Web Token) 是无状态认证的常用方案。\n\n## JWT 工具类\n\n```java\npublic class JwtUtils {\n    private static final String SECRET = "your-secret-key";\n    private static final long EXPIRATION = 86400000; // 24小时\n    \n    public static String generateToken(Long userId, String role) {\n        return Jwts.builder()\n            .setSubject(String.valueOf(userId))\n            .claim("role", role)\n            .setIssuedAt(new Date())\n            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))\n            .signWith(SignatureAlgorithm.HS256, SECRET)\n            .compact();\n    }\n}\n```\n\n## 拦截器验证\n\n通过拦截器对每个请求的 Authorization Header 进行 JWT 校验，解析出用户信息和角色。\n\n## 安全建议\n\n- Secret 密钥使用环境变量，不硬编码\n- Token 过期时间合理设置\n- 敏感操作要求重新验证',
'详细讲解 JWT 在 Spring Boot 中的实现方案，包括 Token 生成、拦截器验证和安全注意事项。',
1, 1, 'PUBLISHED', 0, 930, '2026-01-25 11:00:00', '2026-01-25 11:00:00'),

(6, 'Spring Boot 参数校验完全指南', 'spring-boot-validation',
'# Spring Boot 参数校验完全指南\n\n使用 Jakarta Validation 可以优雅地完成参数校验。\n\n## 常用注解\n\n| 注解 | 说明 |\n|------|------|\n| @NotNull | 不能为 null |\n| @NotEmpty | 不能为 null 且不能为空字符串 |\n| @NotBlank | 不能为 null 且至少一个非空白字符 |\n| @Size(min, max) | 字符串/集合长度范围 |\n| @Email | 邮箱格式 |\n| @Pattern | 正则匹配 |\n\n## 分组校验\n\n```java\npublic interface Create {}\npublic interface Update {}\n\n@NotNull(groups = Update.class)\nprivate Long id;\n```\n\n## 自定义校验注解\n\n当内置注解不满足需求时，可以自定义校验注解。',
'Spring Boot 中 Jakarta Validation 参数校验的完整使用指南，包含常用注解、分组校验和自定义校验。',
1, 1, 'PUBLISHED', 0, 540, '2026-02-02 10:30:00', '2026-02-02 10:30:00'),

(7, 'RESTful API 设计规范与最佳实践', 'restful-api-best-practices',
'# RESTful API 设计规范与最佳实践\n\n## URL 命名规范\n\n- 使用名词复数: `/api/articles`\n- 层级关系: `/api/articles/{id}/comments`\n- 使用短横线: `/api/article-categories`\n\n## HTTP 方法\n\n| 方法 | 操作 | 幂等 |\n|------|------|------|\n| GET | 查询 | 是 |\n| POST | 创建 | 否 |\n| PUT | 全量更新 | 是 |\n| PATCH | 部分更新 | 否 |\n| DELETE | 删除 | 是 |\n\n## 状态码使用\n\n- 200: 成功\n- 201: 创建成功\n- 400: 参数错误\n- 401: 未认证\n- 403: 无权限\n- 404: 资源不存在\n- 500: 服务器错误\n\n## 版本管理\n\n建议 URL 路径版本: `/api/v1/articles`',
'总结 RESTful API 设计的核心规范，包括 URL 命名、HTTP 方法选择、状态码使用和版本管理策略。',
1, 1, 'PUBLISHED', 0, 1200, '2026-02-10 08:00:00', '2026-02-10 08:00:00'),

(8, 'Spring Boot 文件上传与下载', 'spring-boot-file-upload',
'# Spring Boot 文件上传与下载\n\n## 文件上传\n\n```java\n@PostMapping("/upload")\npublic Result<String> upload(@RequestParam("file") MultipartFile file) {\n    // 1. 校验文件类型\n    String contentType = file.getContentType();\n    if (!ALLOWED_TYPES.contains(contentType)) {\n        return Result.error("不支持的文件类型");\n    }\n    \n    // 2. 校验文件大小\n    if (file.getSize() > MAX_SIZE) {\n        return Result.error("文件过大");\n    }\n    \n    // 3. 重命名并保存\n    String filename = UUID.randomUUID() + getExtension(file);\n    Path path = Paths.get(uploadDir, filename);\n    Files.copy(file.getInputStream(), path);\n    \n    return Result.success(filename);\n}\n```\n\n## 安全注意事项\n\n- 校验文件类型（MIME + 扩展名）\n- 限制文件大小\n- 使用 UUID 重命名\n- 上传目录不放在静态资源目录下',
'Spring Boot 中文件上传下载功能的实现方案，包含安全校验、存储策略和路径规范。',
1, 1, 'PUBLISHED', 0, 450, '2026-02-18 15:00:00', '2026-02-18 15:00:00'),

(9, 'Spring Boot 定时任务详解', 'spring-boot-scheduled',
'# Spring Boot 定时任务详解\n\n## 启用定时任务\n\n```java\n@SpringBootApplication\n@EnableScheduling\npublic class Application {\n    public static void main(String[] args) {\n        SpringApplication.run(Application.class, args);\n    }\n}\n```\n\n## Cron 表达式\n\n| 表达式 | 说明 |\n|--------|------|\n| `0 0 2 * * ?` | 每天凌晨 2 点 |\n| `0 */30 * * * ?` | 每 30 分钟 |\n| `0 0 0 1 * ?` | 每月 1 号凌晨 |\n\n## 动态管理\n\n可以通过数据库配置来实现定时任务的动态启停和修改。',
'介绍 Spring Boot 定时任务的使用方式，包括 @Scheduled 注解、Cron 表达式和动态管理方案。',
1, 1, 'PUBLISHED', 0, 380, '2026-03-01 09:30:00', '2026-03-01 09:30:00'),

(10, 'Spring Boot 3 新特性一览', 'spring-boot-3-new-features',
'# Spring Boot 3 新特性一览\n\nSpring Boot 3 带来了许多激动人心的新特性。\n\n## 主要变化\n\n1. **最低 Java 17** — 不再支持 Java 8/11\n2. **Jakarta EE 9+** — `javax.*` → `jakarta.*`\n3. **原生镜像支持** — GraalVM Native Image\n4. **Observability** — Micrometer 自动配置\n5. **HTTP Interface** — 声明式 HTTP 客户端\n\n## 迁移注意事项\n\n从 Spring Boot 2.x 迁移到 3.x 需要注意包名变更和废弃 API 的替换。',
'梳理 Spring Boot 3 相比 2.x 的核心变化，帮助开发者快速了解新特性并完成迁移。',
1, 1, 'PUBLISHED', 0, 720, '2026-03-10 14:00:00', '2026-03-10 14:00:00');

-- 分类: 前端开发 (8篇)
INSERT INTO `article` (`id`, `title`, `slug`, `content`, `summary`, `category_id`, `author_id`, `status`, `is_top`, `view_count`, `created_at`, `updated_at`) VALUES
(11, 'Vue 3 Composition API 入门', 'vue3-composition-api',
'# Vue 3 Composition API 入门\n\nComposition API 是 Vue 3 最重要的新特性之一。\n\n## setup 函数\n\n```vue\n<script setup lang="ts">\nimport { ref, computed, onMounted } from ''vue''\n\nconst count = ref(0)\nconst doubled = computed(() => count.value * 2)\n\nfunction increment() {\n  count.value++\n}\n\nonMounted(() => {\n  console.log("Component mounted")\n})\n</script>\n\n<template>\n  <div>\n    <p>Count: {{ count }}</p>\n    <p>Doubled: {{ doubled }}</p>\n    <button @click="increment">+1</button>\n  </div>\n</template>\n```\n\n相比 Options API，Composition API 提供更好的逻辑复用和类型推导。',
'Vue 3 Composition API 快速入门，通过示例讲解 ref、computed、生命周期等核心概念。',
2, 1, 'PUBLISHED', 0, 1350, '2026-01-06 10:00:00', '2026-01-06 10:00:00'),

(12, 'TypeScript 泛型深入理解', 'typescript-generics-deep-dive',
'# TypeScript 泛型深入理解\n\n## 基础泛型\n\n```typescript\nfunction identity<T>(arg: T): T {\n    return arg;\n}\n```\n\n## 泛型约束\n\n```typescript\ninterface HasLength {\n    length: number;\n}\n\nfunction logLength<T extends HasLength>(arg: T): T {\n    console.log(arg.length);\n    return arg;\n}\n```\n\n## 泛型工具类型\n\nTypeScript 内置了许多实用的泛型工具类型：\n- `Partial<T>` — 所有属性可选\n- `Required<T>` — 所有属性必填\n- `Pick<T, K>` — 选取部分属性\n- `Omit<T, K>` — 排除部分属性\n- `Record<K, T>` — 构造对象类型',
'深入理解 TypeScript 泛型，涵盖基础用法、约束、工具类型和实战技巧。',
2, 1, 'PUBLISHED', 0, 980, '2026-01-15 11:00:00', '2026-01-15 11:00:00'),

(13, 'Nuxt 3 项目搭建与配置', 'nuxt3-project-setup',
'# Nuxt 3 项目搭建与配置\n\n## 创建项目\n\n```bash\nnpx nuxi@latest init my-blog\ncd my-blog\nnpm install\n```\n\n## 项目结构\n\n```\nmy-blog/\n├── pages/          # 页面路由\n├── components/     # 组件\n├── layouts/        # 布局\n├── composables/    # 组合式函数\n├── public/         # 静态资源\n├── server/         # 服务端 API\n└── nuxt.config.ts  # 配置文件\n```\n\n## SSG 配置\n\n```typescript\nexport default defineNuxtConfig({\n  ssr: true,\n  target: ''static'',\n  nitro: {\n    prerender: {\n      routes: [''/'', ''/articles'']\n    }\n  }\n})\n```',
'Nuxt 3 项目从零搭建指南，包括项目结构、SSG 配置和常用模块介绍。',
2, 1, 'PUBLISHED', 0, 760, '2026-02-05 14:00:00', '2026-02-05 14:00:00'),

(14, 'ByteMD — Vue 3 Markdown 编辑器集成', 'bytemd-vue3-integration',
'# ByteMD — Vue 3 Markdown 编辑器集成\n\nByteMD 是字节跳动开源的轻量级 Markdown 编辑器。\n\n## 安装\n\n```bash\nnpm install @bytemd/vue-next\nnpm install @bytemd/plugin-gfm @bytemd/plugin-highlight\n```\n\n## 使用\n\n```vue\n<script setup lang="ts">\nimport { Editor } from ''@bytemd/vue-next''\nimport gfm from ''@bytemd/plugin-gfm''\nimport highlight from ''@bytemd/plugin-highlight''\nimport ''bytemd/dist/index.css''\n\nconst value = ref('''')\nconst plugins = [gfm(), highlight()]\n</script>\n\n<template>\n  <Editor v-model="value" :plugins="plugins" />\n</template>\n```\n\nByteMD 支持插件扩展，可以方便地添加数学公式、Mermaid 图表等功能。',
'详细介绍如何在 Vue 3 项目中集成 ByteMD Markdown 编辑器，包括插件配置和自定义样式。',
2, 1, 'PUBLISHED', 0, 620, '2026-02-20 10:00:00', '2026-02-20 10:00:00'),

(15, 'Vue 3 状态管理 Pinia 入门', 'pinia-getting-started',
'# Vue 3 状态管理 Pinia 入门\n\nPinia 是 Vue 官方推荐的状态管理库。\n\n## 定义 Store\n\n```typescript\nimport { defineStore } from ''pinia''\n\nexport const useAuthStore = defineStore(''auth'', () => {\n  const token = ref<string | null>(null)\n  const user = ref<User | null>(null)\n  \n  const isLoggedIn = computed(() => !!token.value)\n  \n  async function login(username: string, password: string) {\n    const res = await api.login(username, password)\n    token.value = res.token\n    user.value = res.user\n  }\n  \n  function logout() {\n    token.value = null\n    user.value = null\n  }\n  \n  return { token, user, isLoggedIn, login, logout }\n})\n```\n\nPinia 相比 Vuex 更加简洁，且完全支持 TypeScript。',
'Pinia 状态管理库入门教程，通过认证 Store 示例讲解核心概念和使用方式。',
2, 1, 'PUBLISHED', 0, 850, '2026-03-05 09:00:00', '2026-03-05 09:00:00'),

(16, 'CSS Grid 布局完全指南', 'css-grid-complete-guide',
'# CSS Grid 布局完全指南\n\nCSS Grid 是二维布局的强大工具。\n\n## 基础网格\n\n```css\n.container {\n  display: grid;\n  grid-template-columns: repeat(3, 1fr);\n  grid-template-rows: auto;\n  gap: 16px;\n}\n```\n\n## 常用属性\n\n| 属性 | 说明 |\n|------|------|\n| grid-template-columns | 定义列 |\n| grid-template-rows | 定义行 |\n| gap | 间距 |\n| grid-column | 列跨越 |\n| grid-row | 行跨越 |\n\n## 响应式布局\n\n```css\n.container {\n  display: grid;\n  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));\n}\n```',
'CSS Grid 布局从基础到进阶的完整指南，包含响应式布局技巧和实际案例。',
2, 1, 'PUBLISHED', 0, 1100, '2026-03-15 15:00:00', '2026-03-15 15:00:00'),

(17, '前端性能优化实战', 'frontend-performance-optimization',
'# 前端性能优化实战\n\n## 核心指标\n\n- **FCP** (First Contentful Paint): 首次内容绘制\n- **LCP** (Largest Contentful Paint): 最大内容绘制\n- **TTI** (Time to Interactive): 可交互时间\n\n## 优化策略\n\n1. **图片优化**: WebP 格式、懒加载、响应式图片\n2. **代码分割**: 路由懒加载、动态 import\n3. **缓存策略**: CDN、Service Worker、HTTP 缓存\n4. **Bundle 优化**: Tree Shaking、压缩、按需引入\n5. **SSG/SSR**: 服务端渲染减少首屏白屏时间\n\n## Lighthouse 评分\n\n使用 Chrome DevTools 中的 Lighthouse 进行性能审计，目标评分 90+。',
'前端性能优化的完整方法论，包括核心指标解读、优化策略和工具使用。',
2, 1, 'PUBLISHED', 0, 680, '2026-04-01 08:00:00', '2026-04-01 08:00:00'),

(18, 'Vue 3 自定义指令详解', 'vue3-custom-directives',
'# Vue 3 自定义指令详解\n\n## 基础指令\n\n```typescript\nconst vFocus = {\n  mounted(el: HTMLElement) {\n    el.focus()\n  }\n}\n```\n\n## 带参数的指令\n\n```typescript\nconst vPermission = {\n  mounted(el: HTMLElement, binding: DirectiveBinding) {\n    const { value } = binding\n    if (!hasPermission(value)) {\n      el.parentNode?.removeChild(el)\n    }\n  }\n}\n\n// 使用: v-permission="''admin''"\n```\n\n自定义指令适合处理 DOM 相关的可复用逻辑。',
'Vue 3 自定义指令的创建与使用，包括生命周期钩子和参数传递。',
2, 1, 'PUBLISHED', 0, 420, '2026-04-10 13:00:00', '2026-04-10 13:00:00');

-- 分类: 数据库 (6篇)
INSERT INTO `article` (`id`, `title`, `slug`, `content`, `summary`, `category_id`, `author_id`, `status`, `is_top`, `view_count`, `created_at`, `updated_at`) VALUES
(19, 'MySQL 索引优化实战', 'mysql-index-optimization',
'# MySQL 索引优化实战\n\n## 索引类型\n\n| 类型 | 说明 |\n|------|------|\n| B+Tree | 默认索引类型 |\n| Hash | 等值查询快 |\n| FULLTEXT | 全文索引 |\n\n## 最左前缀原则\n\n复合索引 `(a, b, c)` 的有效查询：\n- `WHERE a = 1` ✅\n- `WHERE a = 1 AND b = 2` ✅\n- `WHERE a = 1 AND b = 2 AND c = 3` ✅\n- `WHERE b = 2` ❌\n\n## EXPLAIN 分析\n\n```sql\nEXPLAIN SELECT * FROM article WHERE title LIKE ''%Spring%'';\n```\n\n关注 `type`、`key`、`rows`、`Extra` 字段。',
'MySQL 索引优化的核心知识，包括索引类型、最左前缀原则和 EXPLAIN 执行计划分析。',
3, 1, 'PUBLISHED', 0, 1560, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),

(20, 'MySQL FULLTEXT 全文索引使用指南', 'mysql-fulltext-guide',
'# MySQL FULLTEXT 全文索引使用指南\n\n## 创建全文索引\n\n```sql\nALTER TABLE article ADD FULLTEXT INDEX ft_content (title, content);\n```\n\n## 全文搜索\n\n```sql\nSELECT * FROM article \nWHERE MATCH(title, content) AGAINST(''Spring Boot'' IN NATURAL LANGUAGE MODE);\n```\n\n## 搜索模式\n\n| 模式 | 说明 |\n|------|------|\n| NATURAL LANGUAGE MODE | 自然语言模式 |\n| BOOLEAN MODE | 布尔模式（支持 + - * 操作符） |\n| WITH QUERY EXPANSION | 查询扩展模式 |\n\n## 注意事项\n\n- 默认最小词长度为 3 (InnoDB)\n- 中文需要 ngram 分词器\n- 停用词会被忽略',
'MySQL FULLTEXT 全文索引的完整使用指南，包括创建索引、搜索语法和中文分词配置。',
3, 1, 'PUBLISHED', 0, 890, '2026-01-18 14:00:00', '2026-01-18 14:00:00'),

(21, 'Redis 缓存穿透/击穿/雪崩解决方案', 'redis-cache-problems',
'# Redis 缓存穿透/击穿/雪崩解决方案\n\n## 缓存穿透\n\n查询不存在的数据，请求穿过缓存直接打到数据库。\n\n**解决方案**: 布隆过滤器、缓存空值（短过期时间）\n\n## 缓存击穿\n\n热点 key 过期瞬间，大量请求打到数据库。\n\n**解决方案**: 互斥锁、永不过期 + 异步更新\n\n## 缓存雪崩\n\n大量 key 同时过期，数据库压力骤增。\n\n**解决方案**: 过期时间加随机值、多级缓存、限流降级',
'深入分析 Redis 缓存三大经典问题的成因与解决方案，附实战代码示例。',
3, 1, 'PUBLISHED', 0, 1300, '2026-02-08 09:00:00', '2026-02-08 09:00:00'),

(22, 'MySQL 慢查询分析与优化', 'mysql-slow-query-optimization',
'# MySQL 慢查询分析与优化\n\n## 开启慢查询日志\n\n```sql\nSET GLOBAL slow_query_log = ON;\nSET GLOBAL long_query_time = 1;\nSET GLOBAL slow_query_log_file = ''/var/log/mysql/slow.log'';\n```\n\n## 分析工具\n\n- **mysqldumpslow**: MySQL 自带\n- **pt-query-digest**: Percona Toolkit\n\n## 常见优化手段\n\n1. 添加合适的索引\n2. 优化 SQL 语句（避免 SELECT *，合理使用 JOIN）\n3. 分库分表\n4. 读写分离\n5. 使用缓存',
'MySQL 慢查询的发现、分析和优化全流程，包括日志配置、分析工具和优化策略。',
3, 1, 'PUBLISHED', 0, 740, '2026-02-25 11:00:00', '2026-02-25 11:00:00'),

(23, '数据库事务隔离级别详解', 'transaction-isolation-levels',
'# 数据库事务隔离级别详解\n\n## 四种隔离级别\n\n| 级别 | 脏读 | 不可重复读 | 幻读 |\n|------|:--:|:------:|:--:|\n| READ UNCOMMITTED | ✅ | ✅ | ✅ |\n| READ COMMITTED | ❌ | ✅ | ✅ |\n| REPEATABLE READ | ❌ | ❌ | ✅ |\n| SERIALIZABLE | ❌ | ❌ | ❌ |\n\nMySQL InnoDB 默认为 REPEATABLE READ。\n\n## MVCC 原理\n\nInnoDB 通过多版本并发控制 (MVCC) 实现事务隔离，基于 Undo Log 和 Read View。',
'深入理解数据库事务的四种隔离级别，以及 MySQL InnoDB MVCC 的实现原理。',
3, 1, 'PUBLISHED', 0, 560, '2026-03-20 10:00:00', '2026-03-20 10:00:00'),

(24, '数据库设计三范式与反范式', 'database-normalization',
'# 数据库设计三范式与反范式\n\n## 第一范式 (1NF)\n\n每个字段不可再分，保证原子性。\n\n## 第二范式 (2NF)\n\n非主键字段完全依赖于主键（消除部分依赖）。\n\n## 第三范式 (3NF)\n\n非主键字段不传递依赖于主键（消除传递依赖）。\n\n## 反范式化\n\n在实际项目中，适当的反范式化可以提升查询性能。比如文章表中冗余作者昵称，避免每次 JOIN 查询。\n\n**原则**: 先范式化设计，再根据性能需求进行反范式优化。',
'数据库设计三范式的通俗解释，以及何时应该进行反范式化来优化性能。',
3, 1, 'PUBLISHED', 0, 480, '2026-04-05 15:00:00', '2026-04-05 15:00:00');

-- 分类: DevOps (5篇)
INSERT INTO `article` (`id`, `title`, `slug`, `content`, `summary`, `category_id`, `author_id`, `status`, `is_top`, `view_count`, `created_at`, `updated_at`) VALUES
(25, 'Docker 容器化部署 Spring Boot 应用', 'docker-spring-boot-deploy',
'# Docker 容器化部署 Spring Boot 应用\n\n## Dockerfile\n\n```dockerfile\nFROM openjdk:17-slim\nWORKDIR /app\nCOPY target/*.jar app.jar\nEXPOSE 8080\nENTRYPOINT ["java", "-jar", "app.jar"]\n```\n\n## 构建与运行\n\n```bash\ndocker build -t blog-api:latest .\ndocker run -d -p 8080:8080 --name blog-api blog-api:latest\n```\n\n## Docker Compose\n\n```yaml\nservices:\n  api:\n    build: .\n    ports:\n      - "8080:8080"\n    depends_on:\n      - mysql\n      - redis\n  mysql:\n    image: mysql:8.0\n    environment:\n      MYSQL_ROOT_PASSWORD: root123\n  redis:\n    image: redis:7-alpine\n```',
'使用 Docker 和 Docker Compose 部署 Spring Boot 应用的完整指南。',
4, 1, 'PUBLISHED', 0, 1050, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),

(26, 'Nginx 反向代理配置详解', 'nginx-reverse-proxy',
'# Nginx 反向代理配置详解\n\n## 基础配置\n\n```nginx\nserver {\n    listen 80;\n    server_name blog.local;\n    \n    location /api/ {\n        proxy_pass http://localhost:8080/;\n        proxy_set_header Host $host;\n        proxy_set_header X-Real-IP $remote_addr;\n    }\n    \n    location / {\n        root /var/www/blog-frontend;\n        try_files $uri $uri/ /index.html;\n    }\n}\n```\n\n## 常用配置\n\n- Gzip 压缩\n- 静态资源缓存\n- 请求限流\n- CORS 处理',
'Nginx 反向代理的配置详解，包括前后端分离部署、静态资源服务和性能优化。',
4, 1, 'PUBLISHED', 0, 630, '2026-02-22 14:00:00', '2026-02-22 14:00:00'),

(27, 'Git 工作流最佳实践', 'git-workflow-best-practices',
'# Git 工作流最佳实践\n\n## Git Flow\n\n```\nmain     ─── ● ──────── ● ──────── ●\n              \\         / \\         /\ndevelop  ──── ● ── ● ── ● ── ● ── ●\n                \\   /\nfeature/xxx ──── ● ── ●\n```\n\n## 分支命名\n\n| 分支类型 | 命名 | 示例 |\n|----------|------|------|\n| feature | feature/xxx | feature/article-search |\n| bugfix | bugfix/xxx | bugfix/login-error |\n| hotfix | hotfix/xxx | hotfix/xss-vulnerability |\n\n## Commit 规范\n\n```\n<type>(<scope>): <subject>\n\nfeat(article): add fulltext search\nfix(auth): fix JWT expiration check\ndocs(readme): update API documentation\n```',
'Git 工作流的规范与最佳实践，包括分支策略、Commit 规范和团队协作流程。',
4, 1, 'PUBLISHED', 0, 410, '2026-03-12 09:00:00', '2026-03-12 09:00:00'),

(28, 'Linux 常用命令速查', 'linux-commands-cheatsheet',
'# Linux 常用命令速查\n\n## 文件操作\n\n```bash\nls -la           # 列出文件详情\ntail -f app.log  # 实时查看日志\nfind / -name "*.log"  # 查找文件\ndu -sh *         # 查看目录大小\n```\n\n## 进程管理\n\n```bash\nps aux | grep java    # 查看 Java 进程\ntop -p <pid>          # 监控指定进程\nkill -9 <pid>         # 强制终止进程\n```\n\n## 网络\n\n```bash\nnetstat -tlnp         # 查看监听端口\ncurl -v http://localhost:8080/api/health  # 测试接口\n```',
'Linux 常用命令速查手册，涵盖文件操作、进程管理和网络诊断。',
4, 1, 'PUBLISHED', 0, 920, '2026-03-25 16:00:00', '2026-03-25 16:00:00'),

(29, 'CI/CD 流水线搭建指南', 'cicd-pipeline-guide',
'# CI/CD 流水线搭建指南\n\n## GitHub Actions\n\n```yaml\nname: Build and Deploy\non:\n  push:\n    branches: [main]\njobs:\n  build:\n    runs-on: ubuntu-latest\n    steps:\n      - uses: actions/checkout@v3\n      - name: Set up JDK 17\n        uses: actions/setup-java@v3\n        with:\n          java-version: ''17''\n      - name: Build with Maven\n        run: mvn -B package\n      - name: Build Docker image\n        run: docker build -t blog-api .\n```\n\n## 流水线阶段\n\n1. 代码检查 (Lint)\n2. 单元测试\n3. 构建打包\n4. 镜像构建\n5. 部署到服务器',
'使用 GitHub Actions 搭建 CI/CD 流水线的完整指南，实现自动化构建、测试和部署。',
4, 1, 'PUBLISHED', 0, 350, '2026-04-12 11:00:00', '2026-04-12 11:00:00');

-- 分类: 架构设计 (5篇)
INSERT INTO `article` (`id`, `title`, `slug`, `content`, `summary`, `category_id`, `author_id`, `status`, `is_top`, `view_count`, `created_at`, `updated_at`) VALUES
(30, '前后端分离架构设计实践', 'frontend-backend-separation',
'# 前后端分离架构设计实践\n\n## 架构图\n\n```\n浏览器 → Nginx → 前端静态资源 (Vue 3 SPA)\n              → API 网关 → Spring Boot 后端\n                         → MySQL\n                         → Redis\n```\n\n## 接口规范\n\n前后端通过 RESTful API 通信，统一使用 JSON 格式，接口文档由 Swagger 自动生成。\n\n## 认证方案\n\nJWT 无状态认证：前端存储 Token，每次请求通过 Authorization Header 携带。\n\n## 部署方案\n\n前端部署到 Nginx 静态目录，后端部署为独立 Java 进程，通过 Nginx 反向代理统一入口。',
'前后端分离架构的完整设计方案，包括技术选型、接口规范、认证方案和部署策略。',
5, 1, 'PUBLISHED', 0, 780, '2026-01-22 10:00:00', '2026-01-22 10:00:00'),

(31, '设计模式在业务开发中的应用', 'design-patterns-in-business',
'# 设计模式在业务开发中的应用\n\n## 策略模式 — 评论审核策略\n\n```java\npublic interface AuditStrategy {\n    boolean shouldAudit(Comment comment, User user);\n}\n\npublic class VisitorAuditStrategy implements AuditStrategy {\n    public boolean shouldAudit(Comment comment, User user) {\n        return user == null; // 访客需要审核\n    }\n}\n\npublic class RegisteredAuditStrategy implements AuditStrategy {\n    public boolean shouldAudit(Comment comment, User user) {\n        return false; // 注册用户不需要审核\n    }\n}\n```\n\n## 模板方法模式\n\n适用于文章发布流程：校验 → 保存 → 生成静态页 → 通知。',
'结合实际业务场景，讲解策略模式、模板方法模式等设计模式在博客系统中的应用。',
5, 1, 'PUBLISHED', 0, 550, '2026-02-14 14:00:00', '2026-02-14 14:00:00'),

(32, '高并发系统的设计原则', 'high-concurrency-design-principles',
'# 高并发系统的设计原则\n\n## 架构层面\n\n1. **无状态设计**: 服务不存储会话状态，方便水平扩展\n2. **缓存分层**: 浏览器缓存 → CDN → Nginx缓存 → Redis → 数据库\n3. **异步解耦**: 消息队列处理非实时任务\n\n## 数据库层面\n\n1. **读写分离**: 主库写、从库读\n2. **分库分表**: 数据量大时水平拆分\n3. **连接池优化**: HikariCP 合理配置\n\n## 代码层面\n\n1. 避免长事务\n2. 合理使用连接池\n3. 批量操作代替循环单条',
'高并发系统的设计原则总结，从架构、数据库和代码三个层面进行分析。',
5, 1, 'PUBLISHED', 0, 490, '2026-03-08 09:00:00', '2026-03-08 09:00:00'),

(33, 'RBAC 权限模型设计', 'rbac-permission-model',
'# RBAC 权限模型设计\n\n## 核心概念\n\n- **用户 (User)**: 系统使用者\n- **角色 (Role)**: 权限的集合\n- **权限 (Permission)**: 操作的最小单位\n\n## 博客系统角色设计\n\n| 角色 | 文章管理 | 分类管理 | 评论审核 | 用户管理 |\n|------|:------:|:------:|:------:|:------:|\n| Owner | 全部 | 全部 | 全部 | 全部 |\n| Admin | 全部 | 全部 | 全部 | 全部 |\n| Author | 自己的 | 无 | 无 | 无 |\n| User | 无 | 无 | 无 | 无 |\n\n## 数据库设计\n\n```sql\nuser (id, username, ...)\nrole (id, name, ...)\nuser_role (user_id, role_id)\npermission (id, name, resource, action)\nrole_permission (role_id, permission_id)\n```',
'RBAC 权限模型在博客系统中的设计实现，包括角色定义、权限矩阵和数据库表设计。',
5, 1, 'PUBLISHED', 0, 670, '2026-03-22 11:00:00', '2026-03-22 11:00:00'),

(34, '技术选型决策框架', 'tech-selection-framework',
'# 技术选型决策框架\n\n## 评估维度\n\n| 维度 | 权重 | 说明 |\n|------|:--:|------|\n| 社区活跃度 | 25% | GitHub Stars、Issue 响应速度 |\n| 学习成本 | 20% | 团队熟悉度、文档质量 |\n| 性能表现 | 20% | 基准测试、实际案例 |\n| 生态兼容 | 15% | 与现有技术栈的兼容性 |\n| 商业支持 | 10% | 是否有商业公司支持 |\n| 许可证 | 10% | MIT/Apache 2.0 优先 |\n\n## 博客系统选型案例\n\n- 前端: Vue 3 + Nuxt 3 (社区活跃、MIT 开源)\n- Markdown: ByteMD (Vue 3 原生支持、轻量)\n- 搜索: MySQL FULLTEXT (零额外依赖，后续可升级 ES)',
'介绍一套系统化的技术选型决策框架，并以博客系统的实际选型过程作为案例。',
5, 1, 'PUBLISHED', 0, 380, '2026-04-08 15:00:00', '2026-04-08 15:00:00');

-- 后续文章(工具推荐4 + 随笔4 + 教程4 + 开源2 + 面试2) 简化
INSERT INTO `article` (`id`, `title`, `slug`, `content`, `summary`, `category_id`, `author_id`, `status`, `is_top`, `view_count`, `created_at`, `updated_at`) VALUES
(35, 'IntelliJ IDEA 必备插件推荐', 'intellij-must-have-plugins', 'IntelliJ IDEA 是 Java 开发的首选 IDE，本文推荐 10 个提升效率的必备插件...', '推荐 10 个 IntelliJ IDEA 必备插件，大幅提升 Java 开发效率。', 6, 1, 'PUBLISHED', 0, 2100, '2026-01-03 10:00:00', '2026-01-03 10:00:00'),
(36, 'VS Code 前端开发配置指南', 'vscode-frontend-setup', 'VS Code 是前端开发的主流编辑器，本文介绍如何配置一个高效的开发环境...', 'VS Code 前端开发环境配置指南，包括必备扩展和设置优化。', 6, 1, 'PUBLISHED', 0, 1600, '2026-01-08 14:00:00', '2026-01-08 14:00:00'),
(37, 'Postman 替代品 — Apifox 使用体验', 'apifox-vs-postman', 'Apifox 是集 API 文档、调试、Mock、测试于一体的工具...', '对比 Postman 和 Apifox，分享 Apifox 的 API 管理新体验。', 6, 1, 'PUBLISHED', 0, 450, '2026-03-10 09:00:00', '2026-03-10 09:00:00'),
(38, '命令行效率工具推荐', 'cli-productivity-tools', '终端是程序员的第二双手，本文推荐一些提升命令行效率的神器...', '推荐 zoxide、fzf、bat 等命令行效率工具。', 6, 1, 'PUBLISHED', 0, 320, '2026-04-02 16:00:00', '2026-04-02 16:00:00'),

(39, '我为什么开始写博客', 'why-i-started-blogging', '写博客是程序员最好的学习方式之一。本文记录我开始写博客的初衷...', '记录开始写博客的初衷和心路历程。', 7, 1, 'PUBLISHED', 0, 280, '2026-01-01 08:00:00', '2026-01-01 08:00:00'),
(40, '2026 年学习计划', '2026-learning-plan', '新的一年，新的目标。我制定了 2026 年的技术学习计划...', '分享 2026 年的技术学习计划和目标。', 7, 1, 'PUBLISHED', 0, 350, '2026-01-02 10:00:00', '2026-01-02 10:00:00'),
(41, '远程办公一年的感悟', 'remote-work-one-year', '远程办公一年了，分享一下我的经验和感受...', '远程办公一年的经验分享与感悟。', 7, 1, 'PUBLISHED', 0, 520, '2026-02-28 15:00:00', '2026-02-28 15:00:00'),
(42, '程序员的健康管理', 'programmer-health', '久坐、熬夜、眼睛疲劳——程序员常见的健康问题及应对方案...', '关注程序员健康，分享实用的健康管理建议。', 7, 1, 'PUBLISHED', 0, 680, '2026-03-18 12:00:00', '2026-03-18 12:00:00'),

(43, '从零搭建个人博客 — 后端篇', 'build-blog-backend', '本文将带你从零开始搭建个人博客的后端服务...', '从零搭建博客后端系列教程第一篇。', 8, 1, 'PUBLISHED', 0, 1900, '2026-01-12 09:00:00', '2026-01-12 09:00:00'),
(44, '从零搭建个人博客 — 前端篇', 'build-blog-frontend', '接上篇，本文将介绍博客前端项目的搭建过程...', '从零搭建博客前端系列教程第二篇。', 8, 1, 'PUBLISHED', 0, 1400, '2026-01-19 09:00:00', '2026-01-19 09:00:00'),
(45, '从零搭建个人博客 — 部署篇', 'build-blog-deploy', '最后一篇，介绍如何将博客部署到服务器上线...', '从零搭建博客部署上线系列教程终篇。', 8, 1, 'PUBLISHED', 0, 1100, '2026-01-26 09:00:00', '2026-01-26 09:00:00'),
(46, 'Git 新手入门教程', 'git-beginner-tutorial', 'Git 是程序员必须掌握的版本控制工具...', '面向零基础新手的 Git 入门教程。', 8, 1, 'PUBLISHED', 0, 2500, '2026-02-10 08:00:00', '2026-02-10 08:00:00'),

(47, 'Halo 博客系统源码分析', 'halo-blog-source-analysis', 'Halo 是一个优秀的开源博客系统，本文分析其架构设计...', '开源博客系统 Halo 的源码分析。', 9, 1, 'PUBLISHED', 0, 430, '2026-03-05 14:00:00', '2026-03-05 14:00:00'),
(48, '如何参与开源项目', 'how-to-contribute-opensource', '参与开源项目是提升技术的好方法，本文分享入门经验...', '开源项目贡献入门指南。', 9, 1, 'PUBLISHED', 0, 390, '2026-04-15 10:00:00', '2026-04-15 10:00:00'),

(49, 'Java 面试题精选 — 基础篇', 'java-interview-basics', '整理 Java 基础面试高频题，附详细解答...', 'Java 基础面试高频题精选与详解。', 10, 1, 'PUBLISHED', 0, 3200, '2026-02-15 08:00:00', '2026-02-15 08:00:00'),
(50, 'Spring Boot 面试题汇总', 'spring-boot-interview-questions', 'Spring Boot 面试常见问题与答案...', 'Spring Boot 面试题汇总，涵盖核心概念和常见考点。', 10, 1, 'PUBLISHED', 0, 2800, '2026-03-01 08:00:00', '2026-03-01 08:00:00');

-- 草稿文章 (10篇)
INSERT INTO `article` (`id`, `title`, `slug`, `content`, `summary`, `category_id`, `author_id`, `status`, `is_top`, `view_count`, `created_at`, `updated_at`) VALUES
(51, '微服务架构入门 [草稿]', 'microservices-draft', '这篇关于微服务的文章还在编写中...', '微服务架构入门草稿', 5, 1, 'DRAFT', 0, 0, '2026-04-20 10:00:00', '2026-04-20 10:00:00'),
(52, 'Kubernetes 学习笔记 [草稿]', 'k8s-notes-draft', 'K8s 学习笔记整理中...', 'Kubernetes 学习笔记草稿', 4, 1, 'DRAFT', 0, 0, '2026-04-22 14:00:00', '2026-04-22 14:00:00'),
(53, 'Python 爬虫入门 [草稿]', 'python-crawler-draft', 'Python 爬虫入门教程草稿...', 'Python 爬虫入门草稿', 8, 1, 'DRAFT', 0, 0, '2026-05-01 09:00:00', '2026-05-01 09:00:00'),
(54, 'WebSocket 实时通信 [草稿]', 'websocket-draft', 'WebSocket 实时通信文章草稿...', 'WebSocket 实时通信草稿', 1, 1, 'DRAFT', 0, 0, '2026-05-05 11:00:00', '2026-05-05 11:00:00'),
(55, 'GraphQL vs REST [草稿]', 'graphql-vs-rest-draft', 'GraphQL 和 REST 对比分析草稿...', 'GraphQL vs REST 对比草稿', 5, 1, 'DRAFT', 0, 0, '2026-05-10 15:00:00', '2026-05-10 15:00:00'),
(56, 'Go 语言入门 [草稿]', 'go-lang-draft', 'Go 语言入门教程草稿...', 'Go 语言入门草稿', 8, 1, 'DRAFT', 0, 0, '2026-05-12 10:00:00', '2026-05-12 10:00:00'),
(57, 'Elasticsearch 实战 [草稿]', 'elasticsearch-draft', 'ES 搜索引擎实战文章草稿...', 'Elasticsearch 实战草稿', 3, 1, 'DRAFT', 0, 0, '2026-05-15 14:00:00', '2026-05-15 14:00:00'),
(58, '设计模式之观察者模式 [草稿]', 'observer-pattern-draft', '观察者模式详解草稿...', '观察者模式草稿', 5, 1, 'DRAFT', 0, 0, '2026-05-18 09:00:00', '2026-05-18 09:00:00'),
(59, 'MongoDB 入门 [草稿]', 'mongodb-draft', 'MongoDB 入门教程草稿...', 'MongoDB 入门草稿', 3, 1, 'DRAFT', 0, 0, '2026-05-20 16:00:00', '2026-05-20 16:00:00'),
(60, '年终总结 2026 [草稿]', '2026-review-draft', '2026 年终总结草稿，还在写...', '2026 年终总结草稿', 7, 1, 'DRAFT', 0, 0, '2026-05-25 10:00:00', '2026-05-25 10:00:00');

-- ----------------------------------------
-- 5. 文章-标签关联数据
-- ----------------------------------------
-- 为每篇文章关联 2-4 个标签
INSERT INTO `article_tag` (`article_id`, `tag_id`) VALUES
-- 后端文章 (1-10)
(1,1),(1,2),(1,25),
(2,1),(2,2),(2,10),
(3,1),(3,2),(3,20),
(4,1),(4,2),(4,6),(4,16),
(5,1),(5,2),(5,9),(5,17),
(6,1),(6,2),
(7,1),(7,2),(7,10),
(8,1),(8,2),(8,17),
(9,1),(9,2),
(10,1),(10,2),
-- 前端文章 (11-18)
(11,3),(11,4),(11,29),
(12,4),(12,29),
(13,3),(13,8),(13,18),
(14,3),(14,4),(14,19),
(15,3),(15,4),
(16,27),(16,28),
(17,3),(17,16),(17,18),
(18,3),(18,4),
-- 数据库文章 (19-24)
(19,5),(19,16),
(20,5),(20,16),
(21,6),(21,16),
(22,5),(22,16),
(23,5),
(24,5),
-- DevOps (25-29)
(25,1),(25,2),(25,7),
(26,23),
(27,14),
(28,13),
(29,7),(29,24),
-- 架构 (30-34)
(30,10),(30,11),(30,12),
(31,11),
(32,11),(32,12),(32,16),
(33,9),(33,17),
(34,11),
-- 工具 (35-38)
(35,1),(35,21),
(36,4),(36,22),(36,26),
(37,10),
(38,13),
-- 随笔 (39-42)
(39,19),
(41,19),
-- 教程 (43-46)
(43,1),(43,2),(43,8),
(44,3),(44,8),
(45,7),(45,23),
(46,14),
-- 开源 (47-48)
(47,1),(47,2),
-- 面试 (49-50)
(49,1),
(50,1),(50,2);

-- ----------------------------------------
-- 6. 评论数据 (250条: 200已审核 + 50待审核)
-- 分布在文章 1-50 中
-- ----------------------------------------

-- 已审核评论 (200条, status=APPROVED)
-- 使用存储过程批量生成 (简化版: 手动插入代表性数据)
INSERT INTO `comment` (`id`, `article_id`, `parent_id`, `user_id`, `author_name`, `author_email`, `content`, `status`, `created_at`) VALUES
-- 文章1 的评论 (10条)
(1,  1,  NULL, 6,    NULL,   NULL,              'Spring Boot 3 真的很方便，感谢分享！', 'APPROVED', '2026-01-06 10:30:00'),
(2,  1,  1,    7,    NULL,   NULL,              '同意，相比 2.x 简化了不少配置。', 'APPROVED', '2026-01-06 11:00:00'),
(3,  1,  NULL, NULL, '路人甲', 'jia@test.com',   '请问支持 Java 21 吗？', 'APPROVED', '2026-01-06 14:00:00'),
(4,  1,  3,    1,    NULL,   NULL,              '支持的，Spring Boot 3.2+ 已经支持 Java 21 虚拟线程。', 'APPROVED', '2026-01-06 14:30:00'),
(5,  1,  NULL, NULL, '小明',   'ming@test.com',   '跟着教程搭建成功了，谢谢博主！', 'APPROVED', '2026-01-07 09:00:00'),
(6,  1,  NULL, 8,    NULL,   NULL,              '有没有推荐的学习路线？', 'APPROVED', '2026-01-07 10:00:00'),
(7,  1,  NULL, NULL, 'DevFan', 'dev@test.com',    'Spring Boot + Vue 3 是目前最流行的组合了吧。', 'APPROVED', '2026-01-08 15:00:00'),
(8,  1,  7,    9,    NULL,   NULL,              '是的，前后端分离的首选方案。', 'APPROVED', '2026-01-08 16:00:00'),
(9,  1,  NULL, NULL, '新手',   'new@test.com',    '这个和 Spring MVC 有什么区别？', 'APPROVED', '2026-01-09 08:00:00'),
(10, 1,  9,    1,    NULL,   NULL,              'Spring Boot 是基于 Spring MVC 的自动配置封装，简化了开发流程。', 'APPROVED', '2026-01-09 08:30:00'),

-- 文章49 (面试题) 的评论 (10条)
(11, 49, NULL, 6,    NULL,   NULL,              '正好在准备面试，太及时了！', 'APPROVED', '2026-02-16 09:00:00'),
(12, 49, NULL, 7,    NULL,   NULL,              'HashMap 的底层实现讲得很清楚。', 'APPROVED', '2026-02-16 10:00:00'),
(13, 49, NULL, NULL, '面霸',   'mianba@test.com', '能出一期并发编程的面试题吗？', 'APPROVED', '2026-02-16 11:00:00'),
(14, 49, 13,   1,    NULL,   NULL,              '好的，近期会安排。', 'APPROVED', '2026-02-16 11:30:00'),
(15, 49, NULL, 8,    NULL,   NULL,              '拿到了 Offer，特来还愿！', 'APPROVED', '2026-02-20 14:00:00'),
(16, 49, 15,   1,    NULL,   NULL,              '恭喜！🎉', 'APPROVED', '2026-02-20 14:30:00'),
(17, 49, NULL, NULL, '求职者A', 'a@test.com',     'JVM 内存模型那部分能再详细讲讲吗？', 'APPROVED', '2026-02-22 09:00:00'),
(18, 49, NULL, 9,    NULL,   NULL,              '已收藏，准备二刷。', 'APPROVED', '2026-03-01 08:00:00'),
(19, 49, NULL, 10,   NULL,   NULL,              '同求并发编程专题！', 'APPROVED', '2026-03-05 10:00:00'),
(20, 49, NULL, NULL, 'JavaBoy', 'java@test.com',  '补充一下：Java 8 之后 HashMap 在链表长度>8时会转红黑树。', 'APPROVED', '2026-03-10 11:00:00'),

-- 文章11 (Vue 3) 的评论 (8条)
(21, 11, NULL, 6,    NULL,   NULL,              'Composition API 比 Options API 好用太多了！', 'APPROVED', '2026-01-07 10:00:00'),
(22, 11, NULL, NULL, 'VueFan', 'vue@test.com',   'setup 语法糖让代码简洁了很多。', 'APPROVED', '2026-01-07 11:00:00'),
(23, 11, 22,   1,    NULL,   NULL,              '是的，`<script setup>` 是官方推荐的写法。', 'APPROVED', '2026-01-07 11:30:00'),
(24, 11, NULL, 7,    NULL,   NULL,              '从 Vue 2 迁移过来需要注意什么？', 'APPROVED', '2026-01-08 09:00:00'),
(25, 11, 24,   1,    NULL,   NULL,              '主要注意：v-model 语法变化、事件总线移除、过滤器移除。后续我会写迁移指南。', 'APPROVED', '2026-01-08 09:30:00'),
(26, 11, NULL, 8,    NULL,   NULL,              'ref 和 reactive 的选择有什么建议？', 'APPROVED', '2026-01-09 14:00:00'),
(27, 11, 26,   1,    NULL,   NULL,              '简单数据类型用 ref，复杂对象用 reactive。不过 ref 也可以包裹对象。', 'APPROVED', '2026-01-09 14:30:00'),
(28, 11, NULL, NULL, 'TSLover', 'ts@test.com',   '配合 TypeScript 使用体验更佳！', 'APPROVED', '2026-01-10 08:00:00'),

-- 文章19 (MySQL索引) 的评论 (7条)
(29, 19, NULL, 6,    NULL,   NULL,              '最左前缀原则终于搞懂了，感谢！', 'APPROVED', '2026-01-11 10:00:00'),
(30, 19, NULL, NULL, 'DBA小张', 'dba@test.com',  '补充：使用覆盖索引可以避免回表查询。', 'APPROVED', '2026-01-11 14:00:00'),
(31, 19, NULL, 7,    NULL,   NULL,              'EXPLAIN 那部分讲得很实用。', 'APPROVED', '2026-01-12 09:00:00'),
(32, 19, NULL, NULL, '后端新人', 'newbe@test.com','索引是不是越多越好？', 'APPROVED', '2026-01-12 15:00:00'),
(33, 19, 32,   1,    NULL,   NULL,              '不是的。索引会占用存储空间，且会降低写入性能。需要根据查询场景合理设计。', 'APPROVED', '2026-01-12 15:30:00'),
(34, 19, NULL, 8,    NULL,   NULL,              '联合索引的字段顺序怎么确定？', 'APPROVED', '2026-01-13 10:00:00'),
(35, 19, 34,   1,    NULL,   NULL,              '区分度高的字段放前面，经常作为查询条件的字段放前面。', 'APPROVED', '2026-01-13 10:30:00'),

-- 文章25 (Docker) 的评论 (5条)
(36, 25, NULL, 6,    NULL,   NULL,              'Docker Compose 真的方便，一键启动所有服务。', 'APPROVED', '2026-02-02 10:00:00'),
(37, 25, NULL, 7,    NULL,   NULL,              '建议加上健康检查配置。', 'APPROVED', '2026-02-02 11:00:00'),
(38, 25, NULL, NULL, 'OpsGuy', 'ops@test.com',   '生产环境建议用 docker-compose 还是 k8s？', 'APPROVED', '2026-02-02 14:00:00'),
(39, 25, 38,   1,    NULL,   NULL,              '小项目 docker-compose 足够，大规模服务建议 k8s。', 'APPROVED', '2026-02-02 14:30:00'),
(40, 25, NULL, 9,    NULL,   NULL,              '能不能出一期 Docker 网络配置的文章？', 'APPROVED', '2026-02-03 09:00:00'),

-- 文章43 (博客后端教程) 的评论 (5条)
(41, 43, NULL, 6,    NULL,   NULL,              '跟着教程做完了后端部分，期待前端篇！', 'APPROVED', '2026-01-13 10:00:00'),
(42, 43, NULL, NULL, '学习者', 'learn@test.com',  '请问接口文档在哪里看？', 'APPROVED', '2026-01-13 14:00:00'),
(43, 43, 42,   1,    NULL,   NULL,              '启动项目后访问 http://localhost:8080/doc.html 即可查看 Swagger 文档。', 'APPROVED', '2026-01-13 14:30:00'),
(44, 43, NULL, 7,    NULL,   NULL,              '代码已经跑起来了，很清晰。', 'APPROVED', '2026-01-14 09:00:00'),
(45, 43, NULL, NULL, 'Java菜鸟', 'rookie@test.com','数据库初始化脚本在哪里？', 'APPROVED', '2026-01-14 11:00:00'),

-- 文章30 (前后端分离) 的评论 (5条)
(46, 30, NULL, 6,    NULL,   NULL,              '架构图画得很清晰！', 'APPROVED', '2026-01-23 10:00:00'),
(47, 30, NULL, 7,    NULL,   NULL,              'JWT 过期后怎么处理刷新？', 'APPROVED', '2026-01-23 11:00:00'),
(48, 30, 47,   1,    NULL,   NULL,              '可以实现 Refresh Token 机制，或者前端在 401 时自动跳转登录页。', 'APPROVED', '2026-01-23 11:30:00'),
(49, 30, NULL, NULL, '架构师小王', 'arch@test.com', '建议加上 API 网关做统一鉴权和限流。', 'APPROVED', '2026-01-24 09:00:00'),
(50, 30, NULL, 8,    NULL,   NULL,              '已收藏，面试用得上。', 'APPROVED', '2026-01-25 15:00:00');

-- 简化剩余已审核评论: 为文章 2-10,12-18,20-24,26-29,31-50 各生成1-2条
INSERT INTO `comment` (`id`, `article_id`, `parent_id`, `user_id`, `author_name`, `author_email`, `content`, `status`, `created_at`) VALUES
(51,  2, NULL, 6,    NULL,   NULL,   '全局异常处理确实很重要，学习了。', 'APPROVED', '2026-01-09 10:00:00'),
(52,  3, NULL, 7,    NULL,   NULL,   '分页插件配置简单，好评。', 'APPROVED', '2026-01-13 10:00:00'),
(53,  4, NULL, NULL, 'CacheBoy', 'cache@test.com', 'Redis 缓存注解很方便，但要注意缓存一致性。', 'APPROVED', '2026-01-21 09:00:00'),
(54,  5, NULL, 6,    NULL,   NULL,   'JWT 实现很详细，已 Star。', 'APPROVED', '2026-01-26 10:00:00'),
(55,  6, NULL, 8,    NULL,   NULL,   '参数校验这块之前一直用 if-else，现在知道了更好的方式。', 'APPROVED', '2026-02-03 11:00:00'),
(56,  7, NULL, NULL, 'APIDesigner', 'api@test.com', 'URL 版本管理那段说得对，路径版本比 Header 版本更直观。', 'APPROVED', '2026-02-11 09:00:00'),
(57,  8, NULL, 9,    NULL,   NULL,   '文件上传安全那部分很重要，很多开发者会忽略。', 'APPROVED', '2026-02-19 10:00:00'),
(58,  9, NULL, 10,   NULL,   NULL,   '动态定时任务怎么实现？', 'APPROVED', '2026-03-02 14:00:00'),
(59,  9, 58,   1,    NULL,   NULL,   '可以用 SchedulingConfigurer 接口动态修改 cron 表达式。', 'APPROVED', '2026-03-02 14:30:00'),
(60, 10, NULL, 6,    NULL,   NULL,   '刚从 2.x 迁移到 3.x，Jakarta 包名改了不少。', 'APPROVED', '2026-03-11 10:00:00'),
(61, 12, NULL, 7,    NULL,   NULL,   '泛型工具类型那块很实用！', 'APPROVED', '2026-01-16 10:00:00'),
(62, 13, NULL, NULL, 'NuxtFan', 'nuxt@test.com', 'Nuxt 3 的 SSG 模式对博客来说太合适了。', 'APPROVED', '2026-02-06 11:00:00'),
(63, 14, NULL, 6,    NULL,   NULL,   'ByteMD 比 Toast UI Editor 轻量很多。', 'APPROVED', '2026-02-21 10:00:00'),
(64, 15, NULL, 8,    NULL,   NULL,   'Pinia 的 setup store 风格跟 Composition API 很搭。', 'APPROVED', '2026-03-06 09:00:00'),
(65, 16, NULL, NULL, 'CSSMaster', 'css@test.com', 'Grid 布局确实比 Flexbox 更适合二维布局。', 'APPROVED', '2026-03-16 14:00:00'),
(66, 17, NULL, 9,    NULL,   NULL,   'Lighthouse 评分从 60 优化到 95，成就感满满。', 'APPROVED', '2026-04-02 10:00:00'),
(67, 18, NULL, 10,   NULL,   NULL,   '自定义指令做权限控制这个思路不错。', 'APPROVED', '2026-04-11 11:00:00'),
(68, 20, NULL, 6,    NULL,   NULL,   'MySQL 的 FULLTEXT 中文支持确实是个坑。', 'APPROVED', '2026-01-19 10:00:00'),
(69, 21, NULL, 7,    NULL,   NULL,   '缓存三大问题分析得很透彻。', 'APPROVED', '2026-02-09 10:00:00'),
(70, 22, NULL, NULL, 'SQLGuru', 'sql@test.com', 'pt-query-digest 分析慢查询很好用。', 'APPROVED', '2026-02-26 09:00:00'),
(71, 23, NULL, 8,    NULL,   NULL,   'MVCC 原理讲得通俗易懂。', 'APPROVED', '2026-03-21 10:00:00'),
(72, 24, NULL, 9,    NULL,   NULL,   '反范式化确实需要权衡，不能过度。', 'APPROVED', '2026-04-06 11:00:00'),
(73, 26, NULL, 6,    NULL,   NULL,   'Nginx 配置示例可以直接用，感谢。', 'APPROVED', '2026-02-23 10:00:00'),
(74, 27, NULL, NULL, 'GitUser', 'git@test.com', 'Commit 规范对团队协作很重要。', 'APPROVED', '2026-03-13 09:00:00'),
(75, 28, NULL, 10,   NULL,   NULL,   '速查表已收藏，经常用到。', 'APPROVED', '2026-03-26 10:00:00'),
(76, 29, NULL, 7,    NULL,   NULL,   'GitHub Actions 免费额度够小项目用了。', 'APPROVED', '2026-04-13 10:00:00'),
(77, 31, NULL, 6,    NULL,   NULL,   '策略模式用于评论审核很优雅。', 'APPROVED', '2026-02-15 10:00:00'),
(78, 32, NULL, NULL, 'PerfGuy', 'perf@test.com', '读写分离需要处理主从延迟问题。', 'APPROVED', '2026-03-09 11:00:00'),
(79, 33, NULL, 8,    NULL,   NULL,   'RBAC 表结构设计很标准。', 'APPROVED', '2026-03-23 09:00:00'),
(80, 34, NULL, 9,    NULL,   NULL,   '决策框架很实用，以后选型可以参考。', 'APPROVED', '2026-04-09 10:00:00'),
(81, 35, NULL, 6,    NULL,   NULL,   '这些插件我大部分都在用，确实好用。', 'APPROVED', '2026-01-04 10:00:00'),
(82, 36, NULL, 7,    NULL,   NULL,   'Volar 比 Vetur 好用多了。', 'APPROVED', '2026-01-09 10:00:00'),
(83, 39, NULL, NULL, '读者A', 'reader@test.com', '加油，坚持写下去！', 'APPROVED', '2026-01-01 10:00:00'),
(84, 40, NULL, 6,    NULL,   NULL,   '学习计划很详细，向你学习。', 'APPROVED', '2026-01-03 09:00:00'),
(85, 44, NULL, NULL, '前端新手', 'fe@test.com', '前端篇终于来了，等了好久！', 'APPROVED', '2026-01-20 10:00:00'),
(86, 45, NULL, 8,    NULL,   NULL,   '部署篇很实用，已成功部署。', 'APPROVED', '2026-01-27 10:00:00'),
(87, 46, NULL, NULL, '新手小白', 'xiaobai@test.com', '终于找到一个能看懂的 Git 教程了！', 'APPROVED', '2026-02-11 10:00:00'),
(88, 47, NULL, 6,    NULL,   NULL,   'Halo 的插件机制设计得很好。', 'APPROVED', '2026-03-06 10:00:00'),
(89, 48, NULL, 7,    NULL,   NULL,   '准备开始贡献第一个 PR。', 'APPROVED', '2026-04-16 10:00:00'),
(90, 50, NULL, NULL, '面试准备中', 'hr@test.com', 'Spring Boot 自动配置原理讲得很透彻。', 'APPROVED', '2026-03-02 09:00:00');

-- 待审核评论 (50条, status=PENDING, 由访客提交)
INSERT INTO `comment` (`id`, `article_id`, `parent_id`, `user_id`, `author_name`, `author_email`, `content`, `status`, `created_at`) VALUES
(101, 1,  NULL, NULL, '新访客1', 'v1@test.com',   '博主能加个微信交流吗？', 'PENDING', '2026-06-10 10:00:00'),
(102, 1,  NULL, NULL, '新访客2', 'v2@test.com',   '这篇写得很好，转载可以吗？', 'PENDING', '2026-06-10 11:00:00'),
(103, 1,  NULL, NULL, '新访客3', 'v3@test.com',   '有没有配套的视频教程？', 'PENDING', '2026-06-10 14:00:00'),
(104, 11, NULL, NULL, '新访客4', 'v4@test.com',   'Vue 3 和 React 哪个更适合博客项目？', 'PENDING', '2026-06-10 15:00:00'),
(105, 11, NULL, NULL, '新访客5', 'v5@test.com',   '有没有 Nuxt 3 的进阶教程？', 'PENDING', '2026-06-10 16:00:00'),
(106, 19, NULL, NULL, '新访客6', 'v6@test.com',   '索引优化后查询速度提升明显吗？', 'PENDING', '2026-06-11 09:00:00'),
(107, 19, NULL, NULL, '新访客7', 'v7@test.com',   '能不能出一个 SQL 调优的专题？', 'PENDING', '2026-06-11 10:00:00'),
(108, 25, NULL, NULL, '新访客8', 'v8@test.com',   'docker-compose 和 k8s 怎么选？', 'PENDING', '2026-06-11 11:00:00'),
(109, 30, NULL, NULL, '新访客9', 'v9@test.com',   '前后端分离的认证方案还有其他选择吗？', 'PENDING', '2026-06-11 14:00:00'),
(110, 35, NULL, NULL, '新访客10','v10@test.com',  '有没有 VS Code 的推荐插件？', 'PENDING', '2026-06-11 15:00:00'),
(111, 43, NULL, NULL, '新访客11','v11@test.com',  '教程很详细，希望能出视频版。', 'PENDING', '2026-06-12 09:00:00'),
(112, 49, NULL, NULL, '新访客12','v12@test.com',  '今年面试确实卷，感谢分享。', 'PENDING', '2026-06-12 10:00:00'),
(113, 2,  NULL, NULL, '新访客13','v13@test.com',  '异常处理这块还有没有更优雅的方案？', 'PENDING', '2026-06-12 11:00:00'),
(114, 3,  NULL, NULL, '新访客14','v14@test.com',  '分页插件支持多数据源吗？', 'PENDING', '2026-06-12 11:30:00'),
(115, 4,  NULL, NULL, '新访客15','v15@test.com',  'Redis 集群模式怎么配置？', 'PENDING', '2026-06-12 12:00:00'),
(116, 5,  NULL, NULL, '新访客16','v16@test.com',  'JWT 和 OAuth2.0 有什么区别？', 'PENDING', '2026-06-12 12:30:00'),
(117, 7,  NULL, NULL, '新访客17','v17@test.com',  'RESTful 和 GraphQL 怎么选？', 'PENDING', '2026-06-12 13:00:00'),
(118, 8,  NULL, NULL, '新访客18','v18@test.com',  '大文件分片上传怎么实现？', 'PENDING', '2026-06-12 13:30:00'),
(119, 10, NULL, NULL, '新访客19','v19@test.com',  '升级到 Spring Boot 3 有什么坑吗？', 'PENDING', '2026-06-12 14:00:00'),
(120, 12, NULL, NULL, '新访客20','v20@test.com',  'TypeScript 的高级类型有哪些推荐学习资料？', 'PENDING', '2026-06-12 14:30:00'),
(121, 13, NULL, NULL, '新访客21','v21@test.com',  'Nuxt 3 的 ISR 和 SSG 怎么选？', 'PENDING', '2026-06-12 15:00:00'),
(122, 14, NULL, NULL, '新访客22','v22@test.com',  'ByteMD 支持自定义工具栏吗？', 'PENDING', '2026-06-12 15:30:00'),
(123, 15, NULL, NULL, '新访客23','v23@test.com',  'Pinia 和 Vuex 能共存吗？', 'PENDING', '2026-06-12 16:00:00'),
(124, 17, NULL, NULL, '新访客24','v24@test.com',  'SSG 对 SEO 的提升效果有多大？', 'PENDING', '2026-06-12 16:30:00'),
(125, 20, NULL, NULL, '新访客25','v25@test.com',  'MySQL FULLTEXT 和 Elasticsearch 性能差距有多大？', 'PENDING', '2026-06-12 17:00:00'),
(126, 21, NULL, NULL, '新访客26','v26@test.com',  '布隆过滤器怎么和 Redis 结合使用？', 'PENDING', '2026-06-12 17:30:00'),
(127, 26, NULL, NULL, '新访客27','v27@test.com',  'Nginx 的限流配置怎么写？', 'PENDING', '2026-06-12 18:00:00'),
(128, 28, NULL, NULL, '新访客28','v28@test.com',  '有没有 Mac 下的替代命令？', 'PENDING', '2026-06-12 18:30:00'),
(129, 31, NULL, NULL, '新访客29','v29@test.com',  '设计模式在实际项目中用的多吗？', 'PENDING', '2026-06-12 19:00:00'),
(130, 32, NULL, NULL, '新访客30','v30@test.com',  '高并发场景下数据库连接池怎么配置？', 'PENDING', '2026-06-12 19:30:00'),
(131, 33, NULL, NULL, '新访客31','v31@test.com',  'RBAC 的动态权限怎么实现？', 'PENDING', '2026-06-12 20:00:00'),
(132, 34, NULL, NULL, '新访客32','v32@test.com',  '技术选型中最容易忽略的是什么？', 'PENDING', '2026-06-12 20:30:00'),
(133, 36, NULL, NULL, '新访客33','v33@test.com',  'VS Code 远程开发配置有教程吗？', 'PENDING', '2026-06-12 21:00:00'),
(134, 39, NULL, NULL, '新访客34','v34@test.com',  '一起加油，坚持写作！', 'PENDING', '2026-06-12 21:30:00'),
(135, 40, NULL, NULL, '新访客35','v35@test.com',  '学习计划能分享一下模板吗？', 'PENDING', '2026-06-12 22:00:00'),
(136, 44, NULL, NULL, '新访客36','v36@test.com',  '前端项目源码有 GitHub 链接吗？', 'PENDING', '2026-06-12 22:30:00'),
(137, 46, NULL, NULL, '新访客37','v37@test.com',  'Git 分支管理有什么推荐策略？', 'PENDING', '2026-06-12 23:00:00'),
(138, 48, NULL, NULL, '新访客38','v38@test.com',  '第一次贡献 PR 有什么注意事项？', 'PENDING', '2026-06-12 23:30:00'),
(139, 50, NULL, NULL, '新访客39','v39@test.com',  '面试中 Spring Boot 启动流程问的多吗？', 'PENDING', '2026-06-13 08:00:00'),
(140, 1,  101,  NULL, '博主粉丝', 'fan@test.com',  '同求微信交流群！', 'PENDING', '2026-06-13 08:30:00'),
(141, 1,  102,  1,    NULL,   NULL,              '可以转载，请注明出处即可。', 'PENDING', '2026-06-13 09:00:00'),
(142, 49, NULL, NULL, '面试狗', 'dog@test.com',   '刷了三遍，终于拿到字节的 Offer！', 'PENDING', '2026-06-13 09:30:00'),
(143, 35, NULL, NULL, '新访客40','v40@test.com',  '有没有免费的替代品推荐？', 'PENDING', '2026-06-13 10:00:00'),
(144, 41, NULL, NULL, '新访客41','v41@test.com',  '远程办公怎么保持效率？', 'PENDING', '2026-06-13 10:30:00'),
(145, 42, NULL, NULL, '新访客42','v42@test.com',  '颈椎病有什么好的缓解方法？', 'PENDING', '2026-06-13 11:00:00'),
(146, 6,  NULL, NULL, '新访客43','v43@test.com',  '自定义校验注解怎么写？', 'PENDING', '2026-06-13 11:30:00'),
(147, 9,  NULL, NULL, '新访客44','v44@test.com',  '分布式定时任务怎么保证不重复执行？', 'PENDING', '2026-06-13 12:00:00'),
(148, 16, NULL, NULL, '新访客45','v45@test.com',  'Grid 和 Flexbox 在响应式布局中怎么配合使用？', 'PENDING', '2026-06-13 12:30:00'),
(149, 18, NULL, NULL, '新访客46','v46@test.com',  '自定义指令和组件的选择标准是什么？', 'PENDING', '2026-06-13 13:00:00'),
(150, 29, NULL, NULL, '新访客47','v47@test.com',  '有没有 Jenkins 的替代方案推荐？', 'PENDING', '2026-06-13 13:30:00');

-- ----------------------------------------
-- 7. 补充 content_html 和 published_at 字段
-- BUG-005 修复: INSERT 语句不含 content_html (DDL NOT NULL) 和 published_at
-- ----------------------------------------

-- 已发布文章的 published_at = created_at (首版发布时间同创建时间)
UPDATE `article` SET `published_at` = `created_at` WHERE `status` = 'PUBLISHED' AND `published_at` IS NULL;

-- content_html 设为空字符串占位 (实际应由后端 MarkdownUtil.renderToHtml() 渲染)
UPDATE `article` SET `content_html` = '' WHERE `content_html` IS NULL OR `content_html` = '';
