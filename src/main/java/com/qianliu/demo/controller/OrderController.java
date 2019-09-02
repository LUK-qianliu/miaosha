package com.qianliu.demo.controller;

import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.domain.OrderInfo;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.result.CodeMsg;
import com.qianliu.demo.result.Result;
import com.qianliu.demo.service.GoodsService;
import com.qianliu.demo.service.MiaoshaUserService;
import com.qianliu.demo.service.OrderService;
import com.qianliu.demo.vo.GoodsVo;
import com.qianliu.demo.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;
	
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoshaUser user,
									  @RequestParam("orderId") long orderId) {
		System.out.println("orderId:"+orderId);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}

    	OrderInfo order = orderService.getOrderById(orderId);
		System.out.println(order);
    	if(order == null) {
    		return Result.error(CodeMsg.ORDER_NOT_EXIST);
    	}

    	long goodsId = order.getGoodsId();
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	OrderDetailVo vo = new OrderDetailVo();
    	vo.setOrder(order);
    	vo.setGoods(goods);
    	return Result.success(vo);
    }
    
}
