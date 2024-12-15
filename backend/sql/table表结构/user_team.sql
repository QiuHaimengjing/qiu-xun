-- auto-generated definition
create table user_team
(
    id          bigint auto_increment comment '用户队伍关系id'
        primary key,
    user_id     bigint                             not null comment '用户id',
    team_id     bigint                             not null comment '队伍id',
    is_leader   tinyint  default 0                 not null comment '是否为创建者（0否，1是）',
    join_time   datetime default CURRENT_TIMESTAMP null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint  default 0                 not null comment '是否删除 0 1（默认值0）'
)
    comment '用户队伍关系';

