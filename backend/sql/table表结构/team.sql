-- auto-generated definition
create table team
(
    id          bigint auto_increment comment '队伍id'
        primary key,
    name        varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment '队伍描述',
    max_num     int      default 1                 not null comment '最大人数',
    current_num int      default 0                 not null comment '当前人数',
    expire_time datetime                           null comment '过期时间',
    leader      bigint                             not null comment '创建人id',
    status      tinyint  default 0                 not null comment '0-公开，1-私有，2-加密',
    password    varchar(512)                       null comment '队伍密码',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint  default 0                 not null comment '是否删除 0 1（默认值0）'
)
    comment '队伍';

