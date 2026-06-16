-- H2 内存数据库建表脚本（测试用，兼容H2语法）

CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT,
    `username`      VARCHAR(50)     NOT NULL,
    `password`      VARCHAR(255)    NOT NULL,
    `email`         VARCHAR(100)    DEFAULT NULL,
    `nickname`      VARCHAR(50)     DEFAULT NULL,
    `avatar`        VARCHAR(255)    DEFAULT NULL,
    `role`          VARCHAR(20)     NOT NULL DEFAULT 'VISITOR',
    `status`        TINYINT         NOT NULL DEFAULT 1,
    `created_at`    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE (`username`),
    UNIQUE (`email`)
);

CREATE TABLE IF NOT EXISTS `category` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(50)     NOT NULL,
    `slug`          VARCHAR(50)     NOT NULL,
    `description`   VARCHAR(255)    DEFAULT NULL,
    `parent_id`     BIGINT          DEFAULT NULL,
    `sort_order`    INT             NOT NULL DEFAULT 0,
    `created_at`    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE (`name`),
    UNIQUE (`slug`)
);

CREATE TABLE IF NOT EXISTS `tag` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(50)     NOT NULL,
    `slug`          VARCHAR(50)     NOT NULL,
    `created_at`    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE (`name`),
    UNIQUE (`slug`)
);

CREATE TABLE IF NOT EXISTS `article` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT,
    `title`         VARCHAR(200)    NOT NULL,
    `slug`          VARCHAR(200)    NOT NULL,
    `content`       CLOB            NOT NULL,
    `content_html`  CLOB            NOT NULL,
    `summary`       VARCHAR(500)    DEFAULT NULL,
    `cover_image`   VARCHAR(255)    DEFAULT NULL,
    `category_id`   BIGINT          DEFAULT NULL,
    `author_id`     BIGINT          NOT NULL,
    `status`        VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    `is_top`        TINYINT         NOT NULL DEFAULT 0,
    `view_count`    INT             NOT NULL DEFAULT 0,
    `published_at`  TIMESTAMP       DEFAULT NULL,
    `created_at`    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE (`slug`)
);

CREATE TABLE IF NOT EXISTS `article_tag` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT,
    `article_id`    BIGINT          NOT NULL,
    `tag_id`        BIGINT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`article_id`, `tag_id`)
);

CREATE TABLE IF NOT EXISTS `comment` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT,
    `article_id`      BIGINT        NOT NULL,
    `user_id`         BIGINT        DEFAULT NULL,
    `parent_id`       BIGINT        DEFAULT NULL,
    `reply_to_id`     BIGINT        DEFAULT NULL,
    `author_name`     VARCHAR(50)   NOT NULL,
    `author_email`    VARCHAR(100)  DEFAULT NULL,
    `author_website`  VARCHAR(255)  DEFAULT NULL,
    `content`         CLOB          NOT NULL,
    `status`          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    `user_agent`      VARCHAR(500)  DEFAULT NULL,
    `ip`              VARCHAR(45)   DEFAULT NULL,
    `created_at`      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `link` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(100)    NOT NULL,
    `url`           VARCHAR(255)    NOT NULL,
    `logo`          VARCHAR(255)    DEFAULT NULL,
    `description`   VARCHAR(255)    DEFAULT NULL,
    `status`        TINYINT         NOT NULL DEFAULT 1,
    `sort_order`    INT             NOT NULL DEFAULT 0,
    `created_at`    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);