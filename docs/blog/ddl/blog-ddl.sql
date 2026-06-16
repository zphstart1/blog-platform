-- =============================================
-- 博客系统 DDL（MySQL 8.0）
-- =============================================
CREATE DATABASE IF NOT EXISTS `blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `blog`;

-- -----------------------------------------
-- 1. 用户表
-- -----------------------------------------
CREATE TABLE `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`      VARCHAR(50)     NOT NULL                 COMMENT '用户名',
    `password`      VARCHAR(255)    NOT NULL                 COMMENT '密码(BCrypt)',
    `email`         VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    `nickname`      VARCHAR(50)     DEFAULT NULL             COMMENT '昵称',
    `avatar`        VARCHAR(255)    DEFAULT NULL             COMMENT '头像URL',
    `role`          VARCHAR(20)     NOT NULL DEFAULT 'VISITOR' COMMENT '角色: OWNER/AUTHOR/ADMIN/VISITOR',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 1正常 0禁用',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------
-- 2. 分类表
-- -----------------------------------------
CREATE TABLE `category` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `name`          VARCHAR(50)     NOT NULL                 COMMENT '分类名称',
    `slug`          VARCHAR(50)     NOT NULL                 COMMENT 'URL别名',
    `description`   VARCHAR(255)    DEFAULT NULL             COMMENT '分类描述',
    `parent_id`     BIGINT          DEFAULT NULL             COMMENT '父分类ID(自关联)',
    `sort_order`    INT             NOT NULL DEFAULT 0       COMMENT '排序',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- -----------------------------------------
-- 3. 标签表
-- -----------------------------------------
CREATE TABLE `tag` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '标签ID',
    `name`          VARCHAR(50)     NOT NULL                 COMMENT '标签名称',
    `slug`          VARCHAR(50)     NOT NULL                 COMMENT 'URL别名',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- -----------------------------------------
-- 4. 文章表
-- -----------------------------------------
CREATE TABLE `article` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '文章ID',
    `title`         VARCHAR(200)    NOT NULL                 COMMENT '文章标题',
    `slug`          VARCHAR(200)    NOT NULL                 COMMENT 'URL别名',
    `content`       MEDIUMTEXT      NOT NULL                 COMMENT 'Markdown原文',
    `content_html`  MEDIUMTEXT      NOT NULL                 COMMENT '渲染后HTML',
    `summary`       VARCHAR(500)    DEFAULT NULL             COMMENT '文章摘要',
    `cover_image`   VARCHAR(255)    DEFAULT NULL             COMMENT '封面图URL',
    `category_id`   BIGINT          DEFAULT NULL             COMMENT '分类ID',
    `author_id`     BIGINT          NOT NULL                 COMMENT '作者ID',
    `status`        VARCHAR(20)     NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PUBLISHED',
    `is_top`        TINYINT         NOT NULL DEFAULT 0       COMMENT '是否置顶: 1是 0否',
    `view_count`    INT             NOT NULL DEFAULT 0       COMMENT '阅读量',
    `published_at`  DATETIME        DEFAULT NULL             COMMENT '发布时间',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status_published_at` (`status`, `published_at`),
    KEY `idx_is_top_published_at` (`is_top`, `published_at`),
    KEY `idx_view_count` (`view_count`),
    FULLTEXT KEY `ft_title_content` (`title`, `content`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- -----------------------------------------
-- 5. 文章-标签关联表
-- -----------------------------------------
CREATE TABLE `article_tag` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '关联ID',
    `article_id`    BIGINT          NOT NULL                 COMMENT '文章ID',
    `tag_id`        BIGINT          NOT NULL                 COMMENT '标签ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- -----------------------------------------
-- 6. 评论表
-- -----------------------------------------
CREATE TABLE `comment` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '评论ID',
    `article_id`      BIGINT        NOT NULL                 COMMENT '文章ID',
    `user_id`         BIGINT        DEFAULT NULL             COMMENT '用户ID(注册用户)',
    `parent_id`       BIGINT        DEFAULT NULL             COMMENT '父评论ID(嵌套回复)',
    `reply_to_id`     BIGINT        DEFAULT NULL             COMMENT '回复目标评论ID',
    `author_name`     VARCHAR(50)   NOT NULL                 COMMENT '评论者名称',
    `author_email`    VARCHAR(100)  DEFAULT NULL             COMMENT '评论者邮箱',
    `author_website`  VARCHAR(255)  DEFAULT NULL             COMMENT '评论者网站',
    `content`         TEXT          NOT NULL                 COMMENT '评论内容(纯文本)',
    `status`          VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED',
    `user_agent`      VARCHAR(500)  DEFAULT NULL             COMMENT '浏览器UA',
    `ip`              VARCHAR(45)   DEFAULT NULL             COMMENT 'IP地址',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id_status` (`article_id`, `status`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- -----------------------------------------
-- 7. 文章版本历史表 (P2)
-- -----------------------------------------
CREATE TABLE `article_version` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '版本ID',
    `article_id`    BIGINT          NOT NULL                 COMMENT '文章ID',
    `version_no`    INT             NOT NULL                 COMMENT '版本号',
    `content`       MEDIUMTEXT      NOT NULL                 COMMENT 'Markdown原文快照',
    `content_html`  MEDIUMTEXT      NOT NULL                 COMMENT '渲染后HTML快照',
    `change_note`   VARCHAR(255)    DEFAULT NULL             COMMENT '变更说明',
    `editor_id`     BIGINT          NOT NULL                 COMMENT '编辑者ID',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_version` (`article_id`, `version_no`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_editor_id` (`editor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章版本历史表';

-- -----------------------------------------
-- 8. 友链表 (P1)
-- -----------------------------------------
CREATE TABLE `link` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '友链ID',
    `name`          VARCHAR(100)    NOT NULL                 COMMENT '网站名称',
    `url`           VARCHAR(255)    NOT NULL                 COMMENT '网站URL',
    `logo`          VARCHAR(255)    DEFAULT NULL             COMMENT 'Logo URL',
    `description`   VARCHAR(255)    DEFAULT NULL             COMMENT '描述',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 1显示 0隐藏',
    `sort_order`    INT             NOT NULL DEFAULT 0       COMMENT '排序',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友链表';

-- -----------------------------------------
-- 默认管理员账号由 DataInitializer 首次启动自动创建
-- 用户名: admin  密码: admin123  角色: OWNER
-- -----------------------------------------