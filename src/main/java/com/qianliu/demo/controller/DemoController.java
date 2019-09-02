package com.qianliu.demo.controller;

import com.qianliu.demo.domain.User;
import com.qianliu.demo.rabbitmq.MQSender;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.redis.domain.impl.UserKey;
import com.qianliu.demo.result.CodeMsg;
import com.qianliu.demo.result.Result;
import com.qianliu.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试环境搭建
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    /**
     * 测试springboot是否搭建成功
     * @return
     */
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "hello world!";
    }

    /**
     * 按照result格式输出数据：success
     * @return
     */
    @RequestMapping("/success")
    @ResponseBody
    public Result sayHelloByResult(){
        //return new Result(0,"success","hello,world!");
        return Result.success("success");
    }

    /**
     * 按照result格式输出数据：error
     * @return
     */
    @RequestMapping("/error")
    @ResponseBody
    public Result sayHelloByResultError(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }


    /**
     * 测试thymaleaf是否正常运行
     * @param model
     * @return
     */
    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","qianliu");
        return "hello";
    }

    /**
     * 测试mybatis和jdbc是否正常运行
     * @return
     */
    @RequestMapping("/mybatis")
    @ResponseBody
    public Result<User> mybatis(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    /**
     * 测试mybatis的事务：已知数据库中存在一行id为1的数据，我们此时插入一条数据为2的，
     * 再插入一条数据为1的数据，看是否回滚
     * @return
     */
    @RequestMapping("/mybatisTx")
    @ResponseBody
    public Result<String> mybatisTx(){
        userService.tx();
        return Result.success("true");
    }

    /**
     * 测试：能否从redis中获取数据
     * @return
     */
//    @RequestMapping("/redis/get")
//    @ResponseBody
//    public Result<String> redisGet(){
//        String str = redisService.get("key2",String.class);
//        return Result.success(str);
//    }

    /**
     * 测试：向redis中插入数据
     * @return
     */
//    @RequestMapping("/redis/set")
//    @ResponseBody
//    public Result<String> redisSet(){
//        Boolean b = redisService.set("key3","test");
//        String str = redisService.get("key3",String.class);
//        return Result.success(str);
//    }


    /**
     * 测试：在自定义前缀以后，能否从redis中获取正确的数据
     * @return
     */
    @RequestMapping("/redis/getKey")
    @ResponseBody
    public Result<User> redisGetKey(){
        User user = redisService.get(UserKey.getById,"1",User.class);
        return Result.success(user);
    }

    /**
     * 测试：在自定义前缀以后，向redis中插入数据
     * @return
     */
    @RequestMapping("/redis/setKey")
    @ResponseBody
    public Result<Boolean> redisSetKey(){
        User user = new User();
        user.setId(1);
        user.setName("ql");
        Boolean b = redisService.set(UserKey.getById,"1",user);
        return Result.success(b);
    }

    /**
     * 测试：消息队列整合是否成功:direct模式
     * @return
     */
    @RequestMapping("/testmq")
    @ResponseBody
    public Result<String> testmq(){
        mqSender.send("test for rabbitmq..");
        return Result.success("success mq");
    }

    /**
     * 测试：消息队列整合是否成功:交换机模式
     * @return
     */
    @RequestMapping("/testmq2")
    @ResponseBody
    public Result<String> testmq2(){
        mqSender.sendTopic("test for rabbitmq by exchange..");
        return Result.success("success mq");
    }

    /**
     * 测试：消息队列整合是否成功:fanout模式
     * @return
     */
    @RequestMapping("/testmq3")
    @ResponseBody
    public Result<String> testmq3(){
        mqSender.sendFanout("test for rabbitmq by fanout..");
        return Result.success("success mq");
    }

    /**
     * 测试：消息队列整合是否成功:fanout模式
     * @return
     */
    @RequestMapping("/testmq4")
    @ResponseBody
    public Result<String> testmq4(){
        mqSender.sendHeader("test for rabbitmq by header..");
        return Result.success("success mq");
    }
}
