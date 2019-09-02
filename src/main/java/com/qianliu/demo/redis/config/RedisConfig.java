package com.qianliu.demo.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 此配置文件会与application.properties中的redis.host和redis.port字段关联起来，
 * application.properties中的redis.host=192.168.48.142会将RedisConfig类中的host初始化为192.168.48.142
 */
@Data
@Component
@ConfigurationProperties(prefix = "redis") //这个注解会将application.properties中以redis开头的文件读取进来
public class RedisConfig {
    private String host;
    private int port;
    private int timeout;//秒
    private String password;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;//秒
}
