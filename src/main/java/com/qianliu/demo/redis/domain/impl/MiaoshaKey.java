package com.qianliu.demo.redis.domain.impl;

import com.qianliu.demo.redis.domain.BaseKeyPrefix;
import com.qianliu.demo.redis.domain.KeyPrefix;

public class MiaoshaKey extends BaseKeyPrefix {



	private MiaoshaKey(String prefix) {
		super(prefix);
	}
	private MiaoshaKey(int time,String prefix) {
		super(time,prefix);
	}
	public static MiaoshaKey isGoodsOver = new MiaoshaKey("goodsIsOver");// 是否秒杀了该商品
	public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "miaoshaPath");// 秒杀path
	public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(120,"verifyCode");// 验证码的结果

}
