package com.qianliu.demo.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");//1开头的十个数字

    /**
     * 对传入的手机号校验
     * @param s 需要校验的手机号
     * @return
     */
    public static Boolean isMobile(String s){
        if(StringUtils.isEmpty(s)){
            return false;
        }

        Matcher mobile_matcher = mobile_pattern.matcher(s);

        return mobile_matcher.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("12345678901"));
        System.out.println(isMobile("1234567890"));
    }
}
