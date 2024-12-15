package com.qiu.qiuxun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(value = "com.qiu.qiuxun.mapper")
@EnableScheduling
public class QiuxunApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiuxunApplication.class, args);
    }

}
