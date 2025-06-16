-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
    ) comment '用户' collate = utf8mb4_unicode_ci;

-- 分类表 (支持多级分类)
CREATE TABLE `category` (
                            `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                            `name` VARCHAR(100) NOT NULL,
                            `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID',
                            `level` TINYINT DEFAULT 1 COMMENT '分类层级',
                            `sort_order` INT DEFAULT 0,
                            FOREIGN KEY (`parent_id`) REFERENCES `category`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 标签表
CREATE TABLE `tag` (
                       `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                       `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名',
                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 文章表 (增加分类关联)
CREATE TABLE `article` (
                           `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                           `title` VARCHAR(200) NOT NULL,
                           `author_id` BIGINT NOT NULL,
                           `category_id` BIGINT COMMENT '所属分类',
                           `status` ENUM('DRAFT', 'PUBLISHED', 'DELETED') DEFAULT 'DRAFT',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 文章标签关联表 (多对多关系)
CREATE TABLE `article_tag` (
                               `article_id` BIGINT NOT NULL,
                               `tag_id` BIGINT NOT NULL,
                               PRIMARY KEY (`article_id`, `tag_id`),
                               FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE,
                               FOREIGN KEY (`tag_id`) REFERENCES `tag`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 图片元数据表 (保持不变)
CREATE TABLE `image_meta` (
                              `id` CHAR(32) PRIMARY KEY,
                              `article_id` BIGINT NOT NULL,
                              `file_name` VARCHAR(255) NOT NULL,
                              `storage_path` VARCHAR(500) NOT NULL,
                              `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 文章内容块表 (保持不变)
CREATE TABLE `content_block` (
                                 `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 `article_id` BIGINT NOT NULL,
                                 `block_type` ENUM('TEXT', 'IMAGE') NOT NULL,
                                 `content` TEXT,
                                 `image_id` CHAR(32),
                                 `sort_order` INT NOT NULL,
                                 FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE,
                                 FOREIGN KEY (`image_id`) REFERENCES `image_meta`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 新增索引
CREATE INDEX idx_article_category ON `article` (`category_id`);
CREATE INDEX idx_article_status ON `article` (`status`);
CREATE INDEX idx_tag_name ON `tag` (`name`);



-- 文章表 (增加分类关联)
CREATE TABLE `blog_article` (
                           `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                           `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
                           `author_id` BIGINT NOT NULL COMMENT '作者id',
                           `content` TEXT COMMENT '文章内容',
                           `tags` VARCHAR(200) COMMENT '标签集合',
                           `category_id` BIGINT COMMENT '所属分类 category -> id',
                           `status` ENUM('DRAFT', 'PUBLISHED', 'DELETED') DEFAULT 'DRAFT',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
) comment '博客文章表' collate = utf8mb4_unicode_ci;
