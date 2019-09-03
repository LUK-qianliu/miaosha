package com.qianliu.demo.config;



import com.qianliu.demo.access.AccessValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * 用户登陆拦截user是否已经登陆过的处理器
     */
    @Autowired
    UserArgumentResolver userArgumentResolver;

    /**
     * 拦截user是否登陆，和对访问秒杀url的频率作出限制
     */
    @Autowired
    AccessValidator accessValidator;

    /**
     * jsr303参数校验的handler
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    public WebConfig() {
        super();
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessValidator);
    }
}
