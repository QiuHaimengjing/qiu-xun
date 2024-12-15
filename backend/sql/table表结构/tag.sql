-- auto-generated definition
create table tag
(
    id          bigint auto_increment comment '标签id'
        primary key,
    tag_name    varchar(256)                       not null comment '标签名称',
    user_id     bigint                             null comment '上传标签的用户id',
    parent_id   bigint                             null comment '父标签id',
    is_parent   tinyint  default 0                 not null comment '0 - 不是，1 - 父标签',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint  default 0                 not null comment '是否删除 0 1（默认值0）',
    constraint tag_name_index
        unique (tag_name)
)
    comment '标签';

create index parent_id_index
    on tag (parent_id);

create index user_id_index
    on tag (user_id);

