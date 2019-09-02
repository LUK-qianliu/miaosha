package com.qianliu.demo.redis;

import com.alibaba.fastjson.JSON;
import com.qianliu.demo.redis.domain.KeyPrefix;
import com.qianliu.demo.redis.domain.impl.UserKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 提供redis相关的所有服务
 */
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取数据
     * @param keyPrefix 不同类型的前缀
     * @param key 输入key
     * @param clazz 获取的是一个T类型的数据，用于不同数据类型的判断
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix,String key, Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();

            //根据前缀生成真正的key
            String realKey = keyPrefix.getPrefix()+key;
            String s = jedis.get(realKey);

            T t = stringToBean(s,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }


    /**
     * 给key设置value
     * @param keyPrefix 前缀，用于区分不同种类的key（如：商品key，用户key）
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> Boolean set(KeyPrefix keyPrefix,String key,T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String s = beanToString(value);
            if(s == null || s.length() <= 0 ){
                return false;
            }

            //生成前缀
            String realKey = keyPrefix.getPrefix()+key;

            //判断是否需要设置过期时间
            if(keyPrefix.expireSeconds() <=0 ){
                jedis.set(realKey,s);
            }else {
                jedis.setex(realKey,keyPrefix.expireSeconds(),s);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除：根据前缀和key删除value
     * */
    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            long ret =  jedis.del(realKey);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 根绝前缀判断是否存在某个key
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Boolean exists(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成前缀
            String realKey = keyPrefix.getPrefix()+key;

            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 在为数字的value上加1，如果不是数字或者为空会设置为0然后加1
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成前缀
            String realKey = keyPrefix.getPrefix()+key;

            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 在为数字的value上减1，如果不是数字或者为空会设置为0然后减1
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成前缀
            String realKey = keyPrefix.getPrefix()+key;

            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 工具类:将Bean转化为string
     * @param value 任意类型的value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value) {
        if(value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class) { //int类型
            return ""+value;
        }else if(clazz == String.class) { //string类型
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class) { //long类型
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * 工具类：将string字符串转化为一个Bean
     * @param <T>
     * @param str 字符串
     * @param clazz 期望读取出来的class类型
     * @return
     */
    @SuppressWarnings("unchecked") //让代码的警告不显示在控制台：下面代码有强制转化，存在警告
    public static  <T> T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if(clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class) {
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class) {
            return  (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * 关闭jedis
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        //关闭jedis：其实就是将这个jedis线程返回到线程池
        if(jedis!=null){
            jedis.close();
        }
    }



}
