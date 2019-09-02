package com.qianliu.demo.redis;

import com.qianliu.demo.redis.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 将redisPool注入spring容器中
 */
@Service
public class JedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    /**
     * 通过@Bean注解奖jedisPool注入到spring容器中，这样就可以使用@Autowired来管理jedisPool对象
     * @return
     */
    @Bean
    public JedisPool JedisPoolFactory(){
        //JedisPoolConfig初始化
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);//秒转化为毫秒

        //jedisPool初始化
        JedisPool jedisPool = new JedisPool(poolConfig,redisConfig.getHost(),redisConfig.getPort(),
                redisConfig.getTimeout()*1000,redisConfig.getPassword(),0);

        return jedisPool;
    }
}
