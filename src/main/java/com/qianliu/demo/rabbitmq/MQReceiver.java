package com.qianliu.demo.rabbitmq;

import com.qianliu.demo.domain.MiaoshaOrder;
import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.domain.OrderInfo;
import com.qianliu.demo.rabbitmq.config.MQConfig;
import com.qianliu.demo.rabbitmq.domain.MiaoshaMessage;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.service.GoodsService;
import com.qianliu.demo.service.MiaoshaService;
import com.qianliu.demo.service.OrderService;
import com.qianliu.demo.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /**
     * direct模式
     * @param message
     */
    @RabbitListener(queues = MQConfig.QUEUE_NAME) // 监听消息队列中的消息
    public void receive(String message){
        logger.info("收到消息："+ RedisService.beanToString(message));
    }

    /**
     * 交换机模式，和fanout模式
     * @param message
     */
    @RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        logger.info(" topic queue1 message:"+message);
    }

    @RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        logger.info(" topic queue2 message:"+message);
    }

    /**
     * hedder模式，因为sender的数据message为byte[]，此处与之匹配
     */
    @RabbitListener(queues=MQConfig.HEADER_QUEUE)
    public void receiveHeaderQueue(byte[] message) {
        logger.info(" header  queue message:"+new String(message));
    }

    /**
     * 秒杀商品订单的信息监听
     */
    @RabbitListener(queues = MQConfig.QUEUE_Miaosha)
    public void receiveMiaoshaMessage(String message){
        // 接收消息体
        MiaoshaMessage miaoshaMessage = RedisService.stringToBean(message, MiaoshaMessage.class);
        logger.info("收到消息："+miaoshaMessage);
        
        //从消息中拿到goodId和MiaoshaUser
        Long goodsId = miaoshaMessage.getGoodsId();
        MiaoshaUser miaoshaUser = miaoshaMessage.getMiaoshaUser();
        
        //判断库存
        GoodsVo goodsVoByGoodsId = goodsService.getGoodsVoByGoodsId(goodsId);
        if(goodsVoByGoodsId.getStockCount()<=0){
            return;
        }

        // 判断是否秒杀过了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
        if(miaoshaOrder!=null){
            return;
        }

        // 生成秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser,goodsVoByGoodsId);

    }

}
