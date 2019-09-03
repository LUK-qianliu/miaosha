package com.qianliu.demo.redis.domain.impl;

import com.qianliu.demo.redis.domain.BaseKeyPrefix;

public class AccessKey extends BaseKeyPrefix {

	private AccessKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}


	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}


	// 访问次数计数器
	//public static AccessKey accessCount = new AccessKey(5,"goods");
}
