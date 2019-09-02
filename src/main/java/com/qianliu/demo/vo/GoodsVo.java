package com.qianliu.demo.vo;

import com.qianliu.demo.domain.Goods;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 这个类整合goods和miaosha_goods
 */
@Data
@ToString(callSuper = true)
public class GoodsVo extends Goods {
    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
