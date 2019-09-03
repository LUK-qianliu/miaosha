package com.qianliu.demo.redis.domain.impl;

import com.qianliu.demo.redis.domain.BaseKeyPrefix;

public class GoodsKey extends BaseKeyPrefix {

    /**
     * 设置前缀，过期时间为默认的0(0表示永不过期)
     * @param prefix 前缀
     */
    public static final int Goods_EXPIRE = 60;// 页面缓存可以减少服务器频繁接受申请html渲染，缓存60s
    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }


    public static GoodsKey getGoodsList = new GoodsKey(Goods_EXPIRE,"goods");
    public static GoodsKey getGoodsDetail = new GoodsKey(Goods_EXPIRE,"goodsDetail");
    public static GoodsKey getMiaoshaGoodsStock= new GoodsKey(0, "gs");

}
