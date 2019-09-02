package com.qianliu.demo.redis.domain;

/**
 * 模板模式：不同的pojo存储在redis中会增加不同的前缀（<前缀+key，value>）
 */
public interface KeyPrefix {

    //获取过期时间
    public int expireSeconds();

    //获取前缀
    public String getPrefix();
}
