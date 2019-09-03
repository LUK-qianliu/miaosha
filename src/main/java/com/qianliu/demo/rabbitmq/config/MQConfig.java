package com.qianliu.demo.rabbitmq.config;

import com.qianliu.demo.redis.RedisService;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MQConfig {

    // 直连模式
    public static final String QUEUE_NAME = "queue";
    // 交换机模式
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE = "topicExchage";
    // 广播类型交换机
    public static final String FANOUT_EXCHANGE = "fanoutxchage";
    // header模式
    public static final String HEADER_QUEUE = "header.queue";
    public static final String HEADERS_EXCHANGE = "headersExchage";
    //秒杀
    public static final String QUEUE_Miaosha = "miaosha_Queue";

    /**
     * 直连（direct模式）
     *
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); // 消息队列名称为queue，true表示持久化
    }

    /**
     * 交换机模式
     */
    @Bean
    public Queue topicQueue1() {
        return new Queue(MQConfig.TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(MQConfig.TOPIC_QUEUE2, true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(MQConfig.TOPIC_EXCHANGE);
    }

    // 表示路由key是topic.key1的消息，进入交换机绑定到topicQueue1()消息队列上
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }

    // 表示路由key是topic.#的消息，进入交换机绑定到topicQueue2()消息队列上（#是占位符，表示0个以上的数据）
    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }

    /**
     * fanout模式：广播
     */
    @Bean
    public FanoutExchange fanoutExchage() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    // topicQueue1和topicQueue2都绑定到了这个广播类型的交换机上
    @Bean
    public Binding FanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchage());
    }

    @Bean
    public Binding FanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchage());
    }

    /**
     * header模式：指定消息体的头部信息，匹配这一头部信息才可以将数据发入到绑定的消息队列上
     */
    @Bean
    public HeadersExchange headersExchage() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headerQueue1() {
        return new Queue(HEADER_QUEUE, true);
    }

    // whereAll(map).match() 表示只有满足这个map中的<key,value>出现才会往消息队列中放入数据
    @Bean
    public Binding headerBinding() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headerQueue1()).to(headersExchage()).whereAll(map).match();
    }

    /**
     * 秒杀
     */
    @Bean
    public Queue queue_Miaosha() {
        return new Queue(QUEUE_Miaosha, true);
    }
}
