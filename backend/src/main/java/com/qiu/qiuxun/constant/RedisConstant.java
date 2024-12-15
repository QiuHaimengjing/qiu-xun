package com.qiu.qiuxun.constant;

/**
 * @description: Redis常量接口
 * @className: RedisConstant.java
 * @author: qiu
 * @createTime: 2024/2/27 11:12
 */
public interface RedisConstant {

    // 用户推荐
    String USER_RECOMMEND_KEY = "qiuxun:user:recommend:";
    Long USER_RECOMMEND_TTL = 30L;
    Long DEFAULT_RECOMMEND_USERID = 0L;

    // 定时任务分布式锁
    String PRECACHE_JOB_LOCK = "qiuxun:precachejob:docache:lock";

    // 用户加入队伍分布式锁
    String USER_JOIN_TEAM_LOCK = "qiuxun:user:join_team:lock";

    // 头像上传限制
    String USER_UPLOAD_LIMIT_KEY = "qiuxun:user:upload:";
    Long USER_UPLOAD_LIMIT_TTL = 24L;
}
