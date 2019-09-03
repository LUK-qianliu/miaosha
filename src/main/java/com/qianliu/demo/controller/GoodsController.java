package com.qianliu.demo.controller;


import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.redis.domain.impl.GoodsKey;
import com.qianliu.demo.result.Result;
import com.qianliu.demo.service.GoodsService;
import com.qianliu.demo.service.MiaoshaUserService;
import com.qianliu.demo.vo.GoodsDetailVo;
import com.qianliu.demo.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;



@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;// Thymeleaf的渲染方式

    @Autowired
    ApplicationContext applicationContext;

	/**
	 * 跳转到商品页面
	 * @param model
	 * @return
	 */
    @RequestMapping("/to_list")
    @ResponseBody
    public String list(Model model, MiaoshaUser user, HttpServletResponse response, HttpServletRequest request) {
    	model.addAttribute("user",user);

        //优化：页面缓存
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        //查询list<GoodsVo>
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);

        //手动渲染
        WebContext ctx = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    /**
     * 跳转到商品详情页
     * @param request
     * @param response
     * @param model
     * @param user 查询user是否已经登陆，user的登陆验证在拦截器上
     * @param goodsId
     * @return
     */
//    @RequestMapping("/to_detail2/{goods_id}")
//    @ResponseBody
//    public String detail2(HttpServletRequest request,HttpServletResponse response,Model model, MiaoshaUser user, @PathVariable("goods_id")long goodsId) {
//        model.addAttribute("user",user);
//
//        //取缓存
//        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
//        if(!StringUtils.isEmpty(html)) {
//            return html;
//        }
//
//        //查询某个商品的详情
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods", goods);
//
//        long startAt = goods.getStartDate().getTime();
//        long endAt = goods.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//
//        int miaoshaStatus = 0;
//        int remainSeconds = 0;
//        if(now < startAt ) {//秒杀还没开始，倒计时
//            miaoshaStatus = 0;
//            remainSeconds = (int)((startAt - now )/1000);
//        }else  if(now > endAt){//秒杀已经结束
//            miaoshaStatus = 2;
//            remainSeconds = -1;
//        }else {//秒杀进行中
//            miaoshaStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("miaoshaStatus", miaoshaStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//
//        //手动渲染
//        WebContext ctx = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
//        if(!StringUtils.isEmpty(html)) {
//            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
//        }
//
//        return html;
//    }

    /**
     * 改造页面详情页：改成静态页面以后，就不需要缓存html
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                                        @PathVariable("goodsId")long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return Result.success(vo);
    }
    
}
