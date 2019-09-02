package com.qianliu.demo.redis.domain.impl;

import com.qianliu.demo.redis.domain.BaseKeyPrefix;

public class OrderKey extends BaseKeyPrefix {
    private OrderKey( String prefix) {
        super( prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
