package com.qianliu.demo.rabbitmq;

import com.qianliu.demo.rabbitmq.config.MQConfig;
import com.qianliu.demo.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    AmqpTemplate amqpTemplate; // 操作rabbitmq的工具类

    /**
     * direct模式
     * 发送message到rabbitmq
     * @param message
     */
    public void send(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("发送消息："+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME, msg); // 向MQConfig.QUEUE_NAME发送数据message
    }

    /**
     * 交换机模式：向指定的路由发送数据
     * @param message
     */
    public void sendTopic(Object message) {
		String msg = RedisService.beanToString(message);
		logger.info("send topic message:"+msg);
		// 向路由topic.key1和topic.key2发送数据
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg+"1");
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg+"2");
	}

    /**
     * fanout模式：广播类型的交换机
     * @param message
     */
    public void sendFanout(Object message) {
		String msg = RedisService.beanToString(message);
		logger.info("send fanout message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
	}

    /**
     * header模式：匹配header头部信息的交换机
     * @param message
     */
    public void sendHeader(Object message) {
        String msg = RedisService.beanToString(message);
        logger.info("send fanout message:"+msg);

        //给消息体初始化，并设置消息体的头部
        MessageProperties properties = new MessageProperties();
		properties.setHeader("header1", "value1");
		properties.setHeader("header2", "value2");
        Message obj = new Message(msg.getBytes(), properties);

        // 发送带头部的message消息体
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
    }

    /**
     * 将秒杀的订单信息发送到rabbitMQ
     */
    public void sendMiaoshaMessage(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("发送消息："+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_Miaosha, msg); // 向MQConfig.QUEUE_NAME发送数据message
    }
}
