-- 创建库
create database if not exists wanwu_code_api;

-- 切换库
use wanwu_code_api;
-- 接口信息表
create table if not exists wanwu_code_api.`interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '创建人',
    `name` varchar(256) not null comment '接口名称',
    `method` varchar(256) not null comment '请求类型',
    `example` varchar(256)  null comment '示例请求',
    `description` varchar(256) null comment '描述',
    `requestParams` text null comment '请求参数',
    `url` varchar(512) not null comment '接口地址',
    `requestHeader` text null comment '请求头',
    `status` int default 0 not null comment '接口状态（0-关闭  1-开启）',
    `responseHeader` text null comment '响应头',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '接口信息表';

insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (30912, 'ZVFZu', '梁明轩', '姚聪健', 'www.terina-kuhn.info', '胡建辉', 0, '严煜城');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (9, 'r7A', '毛瑾瑜', '谢明轩', 'www.dean-oberbrunner.name', '覃涛', 0, '尹健雄');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (534779, 'BQ', '侯鹏涛', '秦子骞', 'www.kendrick-watsica.com', '陆语堂', 0, '许修杰');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (8752016170, 'On', '邓晓啸', '罗立轩', 'www.miquel-towne.com', '范正豪', 0, '余雪松');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (2214, 'HS', '郝嘉熙', '覃弘文', 'www.jack-gleichner.io', '薛煜城', 0, '董耀杰');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (6500684, '31', '唐苑博', '陶黎昕', 'www.gustavo-strosin.org', '阎雪松', 0, '郭君浩');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (63832, 'Xy', '尹航', '严烨磊', 'www.leora-homenick.info', '邱浩轩', 0, '陆昊然');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (439, 'cZ', '赵修洁', '沈子涵', 'www.norberto-leffler.net', '梁鹏煊', 0, '顾正豪');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (69304, 't9afB', '龙航', '高乐驹', 'www.refugio-mcdermott.co', '邓泽洋', 0, '叶靖琪');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (177777922, 'RYFTg', '刘鹏涛', '郝哲瀚', 'www.ewa-steuber.co', '孔子默', 0, '马健柏');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (6753, 'Wa', '卢鹏涛', '姜浩轩', 'www.ching-stanton.net', '萧明', 0, '赵熠彤');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (167270855, 'DId', '江伟诚', '唐文昊', 'www.filiberto-hayes.net', '史哲瀚', 0, '曹驰');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (195799213, 'dl4d', '段峻熙', '于明杰', 'www.nicolas-windler.org', '龚锦程', 0, '郑鹤轩');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (751, 'eVT7T', '姜瑞霖', '董思淼', 'www.liane-koss.co', '薛文博', 0, '龙苑博');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (82, 'DjFj', '郝嘉懿', '钱潇然', 'www.minh-purdy.info', '叶熠彤', 0, '覃思');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (38, '0Hn5D', '洪鹏涛', '姜思远', 'www.teressa-brekke.io', '许晓啸', 0, '戴鹭洋');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (314693, 'zAN', '戴荣轩', '宋鑫磊', 'www.stanley-schaefer.info', '赵鸿煊', 0, '董志强');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (70144, 'As', '王志强', '郑俊驰', 'www.mireille-sauer.biz', '叶弘文', 0, '王笑愚');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (590618, 'WB', '范浩', '邱文昊', 'www.jann-wiegand.com', '王浩轩', 0, '贺致远');
insert into wanwu_code_api.`interface_info` (`userId`, `name`, `method`, `description`, `url`, `requestHeader`, `status`, `responseHeader`) values (7577, 'BOYi', '叶立诚', '许文轩', 'www.roman-walsh.io', '程天宇', 0, '陆钰轩');


create table if not exists wanwu_code_api.`user_interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '调用用户',
    `interfaceInfoId` bigint not null comment '接口id',
    `totalNum` int default 0 not null comment '总调用次数',
    `leftNum` int default 0 not null comment '剩余调用次数',
    `status` int default 0 not null comment '接口状态（0-正常  1-禁用）',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户调用接口关系';