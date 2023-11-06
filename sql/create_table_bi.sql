# 建表脚本
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists wanwu_code_bi;

-- 切换库
use wanwu_code_bi;


-- 图表信息表
create table if not exists chart
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             null comment '创建用户',
    `name`      varchar(128)                       null comment '图标名称',
    goal        text                               null comment '分析目标',
    chartData   text                               null comment '图表数据',
    chartType   varchar(128)                       null comment '图表类型',
    `status`      varchar(128)                       not null default 'wait' not null comment 'wait,succeed,failed',
    execMessage text                               null comment '执行信息',
    genChart    text                               null comment '生成的图表数据',
    genResult   text                               null comment '生成的分析结论',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
) comment '图表信息' collate = utf8mb4_unicode_ci;

-- 病症信息表
drop table disease;
create table if not exists disease
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             null comment '创建用户',
    `name`      varchar(128)                       null comment '用户确诊症状',
    sex         tinyint                            null comment '用户性别',
    diseaseType varchar(128)                       null comment '所属科',
    userDesc    text                               null comment '用户描述病症',
    genResult   text                               null comment '生成的治疗计划',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
) comment '病症信息表' collate = utf8mb4_unicode_ci;


