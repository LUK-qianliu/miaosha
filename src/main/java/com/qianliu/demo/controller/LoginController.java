package com.qianliu.demo.controller;

import com.qianliu.demo.exception.GlobalException;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.result.CodeMsg;
import com.qianliu.demo.result.Result;
import com.qianliu.demo.service.MiaoshaUserService;
import com.qianliu.demo.util.ValidatorUtil;
import com.qianliu.demo.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


/**
 * 实现登陆
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    /**
     * 登陆到login页面
     * @return
     */
    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    /**
     * login表单提交
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(@Valid LoginVo vo, HttpServletResponse response){
        //logger.info("登陆用户："+vo);
        //登录
        userService.login(response,vo);// 登陆失败就直接报错，并会被拦截器捕获
        String token = userService.login(response, vo);
        return Result.success(token);
    }
}
