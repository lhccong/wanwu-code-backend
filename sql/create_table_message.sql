# 建表脚本


-- 创建库
create database if not exists wanwu_code;

-- 切换库
use wanwu_code;

DROP TABLE IF EXISTS `message`;
create table message
(
    id           bigint unsigned auto_increment comment 'id' primary key,
    roomId      bigint                                   not null comment '会话表id',
    fromUid     bigint                                   not null comment '消息发送者uid',
    content      varchar(1024)                            null comment '消息内容',
    replyMsgId bigint                                   null comment '回复的消息内容',
    status       int                                      not null comment '消息状态 0正常 1删除',
    gapCount    int                                      null comment '与回复的消息间隔多少条',
    type         int         default 1                    null comment '消息类型 1正常文本 2.撤回消息',
    extra        json                                     null comment '扩展信息',
    createTime  datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    updateTime  datetime(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment '修改时间'
)
    comment '消息表' collate = utf8mb4_unicode_ci
                     row_format = DYNAMIC;

create index idx_create_time
    on message (createTime);

create index idx_from_uid
    on message (fromUid);

create index idx_room_id
    on message (roomId);

create index idx_update_time
    on message (updateTime);

DROP TABLE IF EXISTS `room`;
create table room
(
    id          bigint unsigned auto_increment comment 'id'   primary key,
    type        int                                      not null comment '房间类型 1群聊 2私聊',
    hotFlag    int         default 0                    null comment '是否全员展示 0否 1是',
    activeTime datetime(3) default CURRENT_TIMESTAMP(3) not null comment '群最后消息的更新时间（热点群不需要写扩散，只更新这里）',
    lastMsgId bigint                                   null comment '会话中的最后一条消息id',
    extJson    json                                     null comment '额外信息（根据不同类型房间有不同存储的东西）',
    createTime datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    updateTime datetime(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment '修改时间'
)
    comment '房间表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on room (createTime);

create index idx_update_time
    on room (updateTime);


DROP TABLE IF EXISTS `room_friend`;
create table room_friend
(
    id          bigint unsigned auto_increment comment 'id'
        primary key,
    roomId     bigint                                   not null comment '房间id',
    uid1        bigint                                   not null comment 'uid1（更小的uid）',
    uid2        bigint                                   not null comment 'uid2（更大的uid）',
    roomKey    varchar(64)                              not null comment '房间key由两个uid拼接，先做排序uid1_uid2',
    status      int                                      not null comment '房间状态 0正常 1禁用(删好友了禁用)',
    createTime datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    updateTime datetime(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment '修改时间',
    constraint roomKey
        unique (roomKey)
)
    comment '单聊房间表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on room_friend (createTime);

create index idx_room_id
    on room_friend (roomId);

create index idx_update_time
    on room_friend (updateTime);

DROP TABLE IF EXISTS `room_group`;
create table room_group
(
    id            bigint unsigned auto_increment comment 'id' primary key,
    roomId       bigint                                   not null comment '房间id',
    name          varchar(16)                              not null comment '群名称',
    avatar        varchar(256)                             not null comment '群头像',
    extJson      json                                     null comment '额外信息（根据不同类型房间有不同存储的东西）',
    deleteStatus int(1)      default 0                    not null comment '逻辑删除(0-正常,1-删除)',
    createTime   datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    updateTime   datetime(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment '修改时间'
)
    comment '群聊房间表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on room_group (createTime);

create index idx_room_id
    on room_group (roomId);

create index idx_update_time
    on room_group (updateTime);