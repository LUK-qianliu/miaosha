package com.qianliu.demo.redis.domain;

import lombok.AllArgsConstructor;

/**
 * 抽象类作为模板
 */
@AllArgsConstructor
public abstract class BaseKeyPrefix implements KeyPrefix{

    //参数为0表示永不过期
    private int expireSeconds;

    //前缀
    private String prefix;

    /**
     * 设置前缀，过期时间为默认的0(0表示永不过期)
     * @param prefix 前缀
     */
    public BaseKeyPrefix(String prefix) {
        this.expireSeconds = 0;
        this.prefix = prefix;
    }


    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    /**
     * 真正的前缀为：继承该类(BaseKeyPrefix)的类名+":"+key
     * @return
     */
    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":"+prefix;
    }
}
