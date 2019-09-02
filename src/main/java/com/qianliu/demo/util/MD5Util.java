package com.qianliu.demo.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    private static final String salt = "1a2b3c";

    /**
     * 直接md5加密
     * @param pass 密码
     * @return
     */
    public static String md5(String pass){
        return DigestUtils.md5Hex(pass);
    }

    /**
     * 加盐（盐是固定的）md5加密
     * @param pass 密码
     * @return
     */
    public static String inputPassToFormPass(String pass){
        String s = "" + salt.charAt(0)+salt.charAt(2)+pass+salt.charAt(5)+salt.charAt(4);
        return md5(s);
    }

    /**
     * 传入一个盐加密
     * @param pass 密码
     * @param salt 盐
     * @return
     */
    public static String formPassToDBPass(String pass,String salt){
        String s = "" + salt.charAt(0)+salt.charAt(2)+pass+salt.charAt(5)+salt.charAt(4);
        return md5(s);
    }

    /**
     * 原本是先固定盐加密，后加盐加密两次加密，这个方法用于验证两次加盐加密
     * @param pass
     * @return
     */
    public static String inputPassToDBPass(String pass,String saltDB){
        return formPassToDBPass(inputPassToFormPass(pass),saltDB);
    }

    public static void main(String[] args) {
        //System.out.println(inputPassToDBPass("123456",salt));
        System.out.println(inputPassToFormPass("123456"));
    }
}
