package com.qianliu.demo.domain;

import lombok.Data;

@Data
public class Goods {
	private Long id;
	private String goodsName;
	private String goodsTitle;
	private String goodsImg;
	private String goodsDetail;
	private Double goodsPrice;  // 降格
	private Integer goodsStock; // 库存
}
