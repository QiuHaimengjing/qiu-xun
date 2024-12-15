package com.qiu.qiuxun.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: Redisson 配置类
 * @className: RedissonConfig.java
 * @author: qiu
 * @createTime: 2024/2/28 12:54
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    @Value("${redissonDatabase}")
    private Integer redissonDatabase;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String address = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(address).setPassword(password).setDatabase(redissonDatabase);
        // 2. 创建实例
        return Redisson.create(config);
    }
}
