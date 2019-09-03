package com.qianliu.demo.controller;

import com.qianliu.demo.access.AccessLimit;
import com.qianliu.demo.domain.MiaoshaGoods;
import com.qianliu.demo.domain.MiaoshaOrder;
import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.domain.OrderInfo;
import com.qianliu.demo.rabbitmq.MQSender;
import com.qianliu.demo.rabbitmq.domain.MiaoshaMessage;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.redis.domain.impl.AccessKey;
import com.qianliu.demo.redis.domain.impl.GoodsKey;
import com.qianliu.demo.redis.domain.impl.MiaoshaKey;
import com.qianliu.demo.result.CodeMsg;
import com.qianliu.demo.result.Result;
import com.qianliu.demo.service.GoodsService;
import com.qianliu.demo.service.MiaoshaService;
import com.qianliu.demo.service.MiaoshaUserService;
import com.qianliu.demo.service.OrderService;
import com.qianliu.demo.util.MD5Util;
import com.qianliu.demo.util.UUIDUtil;
import com.qianliu.demo.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

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

	@Autowired
	MQSender mqSender;

	/**
	 * 减少redis网络开销，使用map
	 */
	private Map<Long,Boolean> localOverMap = new ConcurrentHashMap();

	/**
	 * 秒杀
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
//    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
//	@ResponseBody
//    public Result<OrderInfo> list(Model model, MiaoshaUser user,
//					   @RequestParam("goodsId")long goodsId) {
//    	model.addAttribute("user", user);
//    	if(user == null) {
//    		return Result.error(CodeMsg.SESSION_ERROR);
//    	}
//    	//判断库存
//    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//    	int stock = goods.getStockCount();
//    	if(stock <= 0) {
//    		return Result.error(CodeMsg.MIAO_SHA_OVER);
//    	}
//    	//判断是否已经秒杀到了
//		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
//    	if(miaoshaOrder!=null){
//			return Result.error(CodeMsg.REPEATE_MIAOSHA);
//		}
//
//    	//用一个事务来处理这三步：减库存 下订单 写入秒杀订单
//		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//
//        return Result.success(orderInfo);
//    }

    /**
     * 校验验证码，生成秒杀的path验证令牌
     * @param user
     * @param goodsId
     * @param verifyCode  前端用户输入的验证码
     * @return
     */
    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true) // 这个标签表示5秒内最多访问5次，而且需要登陆
	@RequestMapping(value = "/path",method = RequestMethod.GET)
	@ResponseBody
	public Result<String> path(HttpServletRequest request,MiaoshaUser user,
                               @RequestParam("goodsId")long goodsId,
                               @RequestParam("verifyCode")int verifyCode) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

        // 校验验证码
        Boolean aBoolean = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
		if(!aBoolean){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        // 生成path
        String path = miaoshaService.createPath(user,goodsId);

		return Result.success(path);
	}

    /**
     * 生成验证码
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                              @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

	/**
	 * 秒杀
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value = "/do_miaosha/{path}",method = RequestMethod.POST)
	@ResponseBody
	public Result<Integer> list(Model model, MiaoshaUser user,
                                @RequestParam("goodsId")long goodsId, @PathVariable("path")String path) {
		model.addAttribute("user", user);
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

		//校验path
		Boolean check = miaoshaService.checkPath(user,goodsId,path);
		if(!check){
		    return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

		//从本地map判断是否秒杀结束（防止重复秒杀）
		Boolean is_over = localOverMap.get(goodsId);
		if(is_over){
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}

		//判断库存：从redis中去取出商品数量
		long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);//10
		if(stock < 0) {
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}

		//判断是否已经秒杀到了
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
		if(miaoshaOrder!=null){
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}

		//商品入rabbitmq队列:像消息队列发送一个秒杀消息
		MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
		miaoshaMessage.setMiaoshaUser(user);
		miaoshaMessage.setGoodsId(goodsId);
		mqSender.sendMiaoshaMessage(miaoshaMessage);

		return Result.success(0); // 返回0表示订单排队中
	}

	/**
	 * orderId：成功
	 * -1：秒杀失败
	 * 0： 排队中
	 * */
	@RequestMapping(value="/result", method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
									  @RequestParam("goodsId")long goodsId) {
		model.addAttribute("user", user);
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result  = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
		return Result.success(result);
	}


	/**
	 * 在项目初始化的时候就将mysql中秒杀商品的数量压入缓存
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if(goodsList == null) {
			return;
		}
		for(GoodsVo goods : goodsList) {
			System.out.println("goods:"+goods);
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}
}
