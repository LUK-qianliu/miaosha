package com.qianliu.demo.service;

import java.util.Date;

import com.qianliu.demo.dao.OrderDao;
import com.qianliu.demo.domain.MiaoshaOrder;
import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.domain.OrderInfo;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.redis.domain.impl.OrderKey;
import com.qianliu.demo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class OrderService {
	
	@Resource
	OrderDao orderDao;

	@Resource
	RedisService redisService;

	/**
	 * 通过userID和goodsId查询是否秒杀过了
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
		//return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		return redisService.get(OrderKey.getMiaoshaOrderByUidGid,userId+"_"+goodsId,MiaoshaOrder.class);
	}

	/**
	 * 创建订单
	 * @param user
	 * @param goods
	 * @return
	 */
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
		// 插入orederInfo
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);// 0表示卖出
		orderInfo.setUserId(user.getId());
		orderDao.insert(orderInfo);

		// 将MiaoshaOrderInfo插入数据库
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderInfo.getId());
		miaoshaOrder.setUserId(user.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);

		// 插入缓存
		redisService.set(OrderKey.getMiaoshaOrderByUidGid,user.getId()+"_"+goods.getId(),orderDao.getMiaoshaOrderByUserIdGoodsId(user.getId(),goods.getId()));

		return orderInfo;
	}

	/**
	 * 通过id查询订单
	 * @param orderId
	 * @return
	 */
	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}
}
