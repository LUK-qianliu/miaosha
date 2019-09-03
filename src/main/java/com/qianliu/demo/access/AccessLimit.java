package com.qianliu.demo.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个注解表示seconds秒内最多访问maxCount次
 * needLogin = true表示需要登陆权限
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    int seconds(); // 访问时间
    int maxCount(); // 访问的最大次数
    boolean needLogin() default true; // 默认需要登陆
}
