package com.qianliu.demo.rabbitmq.domain;

import com.qianliu.demo.domain.MiaoshaUser;
import lombok.Data;

@Data
public class MiaoshaMessage {
    private MiaoshaUser miaoshaUser;
    private Long goodsId;
}
