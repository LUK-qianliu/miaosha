package com.qianliu.demo.domain;

import lombok.Data;

import java.util.Date;

@Data
public class OrderInfo {
	private Long id;
	private Long userId;
	private Long goodsId;
	private Long  deliveryAddrId;  // 收货地址
	private String goodsName;
	private Integer goodsCount;
	private Double goodsPrice;
	private Integer orderChannel;
	private Integer status;
	private Date createDate;
	private Date payDate;
}
