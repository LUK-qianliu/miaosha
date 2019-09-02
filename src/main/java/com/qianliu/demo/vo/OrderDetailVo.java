package com.qianliu.demo.vo;


import com.qianliu.demo.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {
	private GoodsVo goods;
	private OrderInfo order;

}
