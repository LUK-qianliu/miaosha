package com.qianliu.demo.access;

import com.qianliu.demo.domain.MiaoshaUser;

/**
 * 将拦截器拦截下来的用户信息保存在ThreadLocal中
 */
public class UserContext {

    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }
}
