package com.qianliu.demo.controller;

import com.qianliu.demo.domain.MiaoshaOrder;
import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.domain.OrderInfo;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.result.CodeMsg;
import com.qianliu.demo.result.Result;
import com.qianliu.demo.service.GoodsService;
import com.qianliu.demo.service.MiaoshaService;
import com.qianliu.demo.service.MiaoshaUserService;
import com.qianliu.demo.service.OrderService;
import com.qianliu.demo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;

	@Autowired
	MiaoshaService miaoshaService;


	/**
	 * 秒杀
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
	@ResponseBody
    public Result<OrderInfo> list(Model model, MiaoshaUser user,
					   @RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	//判断是否已经秒杀到了
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
    	if(miaoshaOrder!=null){
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}

    	//用一个事务来处理这三步：减库存 下订单 写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);

        return Result.success(orderInfo);
    }
}
