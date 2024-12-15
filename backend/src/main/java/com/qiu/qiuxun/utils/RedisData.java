package com.qiu.qiuxun.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: RedisData实体类
 * @className: RedisData.java
 * @author: qiu
 * @createTime: 2024/2/27 15:23
 */
@Data
public class RedisData {

    // key过期时间
    private LocalDateTime expireTime;
    // value值
    private Object data;
}
