# 建表脚本


-- 创建库
create database if not exists wanwu_code;

-- 切换库
use wanwu_code;

-- 用户表
drop table user;
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    tags         varchar(1024)                          null comment '标签 json 列表',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    `accessKey`  varchar(512)                           null comment 'accessKey',
    `secretKey`  varchar(512)                           null comment 'secretKey',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    concernNum    int DEFAULT '0'                         COMMENT '关注',
    fansNum      int DEFAULT '0'                         COMMENT '粉丝',
    postNum      int DEFAULT '0'                         COMMENT '投稿数',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

INSERT INTO `user` (`userAccount`, `userPassword`, `unionId`, `mpOpenId`, `userName`, `userAvatar`, `userProfile`,
                    `userRole`, `isDelete`)
VALUES ('cong', '123456789', '123456789', 'mpopenid1', 'WanwuCode最高权限者🌈',
        'https://markdown-piggo.oss-cn-guangzhou.aliyuncs.com/img/image-20230904132030462.png',
        '我是WanwuCode的主宰者，有什么事请联系🎃', 'admin', 0);


-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    synopsis    text                               null comment '描述',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    postStatus  int      default 0                 not null comment '0-草稿 1-审核 2-发布',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子图片表
create table if not exists post_images
(
    id         bigint auto_increment comment 'id' primary key,
    imgUrl    text                               null comment '图片地址',
    postId   bigint                      not null comment '帖子id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '帖子图片表' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';
-- 用户关注表（硬删除）
create table if not exists user_attention
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '关注人id（点击关注那个人ID）',
    followedUserId bigint                             not null comment '被关注人id（被关注的那个人）',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_followId (followedUserId),
    index idx_userId (userId)
) comment ' 用户关注表';

-- 用户标签表
DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag`
(
    `id`          bigint     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parentId`   bigint     NOT NULL COMMENT '类别id',
    `name`        varchar(64)         DEFAULT NULL COMMENT '标签名',
    `color`       varchar(8)          DEFAULT NULL COMMENT '颜色',
    `createdBy`  bigint     NOT NULL COMMENT '创建用户',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `a_user_tag_pk` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 17
  DEFAULT CHARSET = utf8mb3;



LOCK TABLES `user_tag` WRITE;
/*!40000 ALTER TABLE `user_tag`
    DISABLE KEYS */;
INSERT INTO `user_tag`
VALUES (1, 1, '双一流', 'red', 2, '2023-03-07 09:04:39', '2023-03-07 09:04:39', 0),
       (2, 1, '985', 'purple', 2, '2023-03-07 15:16:23', '2023-03-07 15:16:23', 0),
       (3, 2, '计算机科学', 'red', 2, '2023-03-07 15:29:06', '2023-03-07 15:29:06', 0),
       (4, 4, '后端工程师', 'orange', 2, '2023-03-07 15:30:01', '2023-03-07 15:30:01', 0),
       (5, 5, '求职', 'cyan', 2, '2023-03-07 15:33:43', '2023-03-07 15:33:43', 0),
       (6, 3, '战地', 'purple', 2, '2023-03-07 15:35:50', '2023-03-07 15:35:50', 0),
       (7, 2, '通信工程', 'green', 2, '2023-03-07 15:38:38', '2023-03-07 15:38:38', 0),
       (8, 1, '研究生', 'orange', 2, '2023-03-07 15:39:56', '2023-03-07 15:39:56', 0),
       (9, 4, '安卓开发', 'orange', 2, '2023-03-07 15:41:33', '2023-03-07 15:41:33', 0),
       (10, 5, '工作', 'cyan', 2, '2023-03-07 15:51:35', '2023-03-07 15:51:35', 0),
       (11, 3, '二次元', 'orange', 2, '2023-03-07 15:52:39', '2023-03-07 15:52:39', 0),
       (12, 2, '人工智能', 'pink', 2, '2023-03-07 15:54:14', '2023-03-07 15:54:14', 0),
       (13, 2, '软件工程', 'green', 2, '2023-03-07 15:57:01', '2023-03-07 15:57:01', 0),
       (14, 5, '躺平', 'purple', 2, '2023-04-15 06:11:46', '2023-04-15 06:11:46', 0),
       (15, 1, '一本', 'pink', 3, '2023-05-27 14:45:14', '2023-05-27 14:45:14', 0),
       (16, 5, '学习', 'pink', 3, '2023-05-27 14:45:41', '2023-05-27 14:45:41', 0);
/*!40000 ALTER TABLE `user_tag`
    ENABLE KEYS */;
UNLOCK TABLES;