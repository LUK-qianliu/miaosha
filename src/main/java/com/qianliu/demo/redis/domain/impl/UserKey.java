package com.qianliu.demo.redis.domain.impl;

import com.qianliu.demo.redis.domain.BaseKeyPrefix;

public class UserKey extends BaseKeyPrefix {

    /**
     * 设置前缀，过期时间为默认的0(0表示永不过期)
     * @param prefix 前缀
     */
    private static final int TOKEN_EXPIRE = 3600*24 * 2;
    private UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }


    public static UserKey getById = new UserKey(0,"id");
    public static UserKey token = new UserKey(TOKEN_EXPIRE,"token");
}
