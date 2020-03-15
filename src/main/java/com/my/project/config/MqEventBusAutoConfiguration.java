package com.my.project.config;

import com.my.project.mq.MqEventBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jinguo_peng
 * @description TODO
 * @date 2020/3/14 上午11:10
 */
@Configuration
@EnableConfigurationProperties(RocketMqProperties.class)
@Slf4j
public class MqEventBusAutoConfiguration {

    @Autowired
    private RocketMqProperties properties;

    @Bean
    public DefaultMQProducer defaultMQProducer() {

        DefaultMQProducer producer = new DefaultMQProducer(properties.getProducerGroup());
        producer.setVipChannelEnabled(false);
        producer.setNamesrvAddr(properties.getNameServer());
        try {
            producer.start();
        } catch (MQClientException e) {
            log.error(e.getMessage(), e);
        }
        return producer;
    }

    @Bean
    public DefaultMQPushConsumer defaultMQPushConsumer() {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getConsumerGroup());
        consumer.setNamesrvAddr(properties.getNameServer());
        // 设置消息消费为广播模式, 消息模式分为：广播模式 集群模式(平均消费)
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        return consumer;
    }

    @Bean
    public RocketMqEventBusImpl rocketMqEventBus() {
        RocketMqEventBusImpl rocketMqEventBus = new RocketMqEventBusImpl(defaultMQProducer(), defaultMQPushConsumer());
        return rocketMqEventBus;
    }

    static final class RocketMqEventBusImpl implements MqEventBus {

        private DefaultMQProducer producer;
        private DefaultMQPushConsumer consumer;

        public RocketMqEventBusImpl(DefaultMQProducer producer, DefaultMQPushConsumer consumer) {
            this.producer = producer;
            this.consumer = consumer;
        }

        @Override
        public SendResult syncPublish(String topic, String message) {

            SendResult sendResult;
            Message msg = new Message(topic, message.getBytes());
            try {
                sendResult = producer.send(msg);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("event-publish-failed: " + e.getMessage());
            }
            return sendResult;
        }

        @Override
        public void asyncPublish(String topic, String message) {

            Message msg = new Message(topic, message.getBytes());
            try {
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("producer 异步发送消息 = [{}]", sendResult);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error("producer 异步发送消息出现异常：", throwable);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("event-publish-failed: " + e.getMessage());
            }
        }

        @Override
        public void oneWayPublish(String topic, String message) {

            Message msg = new Message(topic, message.getBytes());
            try {
                producer.sendOneway(msg);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("event-publish-failed: " + e.getMessage());
            }
        }

        @Override
        public void subscribe(String topic, MessageListenerConcurrently listener) {
            try {
                consumer.subscribe(topic, "*");
                consumer.registerMessageListener(listener);
                consumer.start();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
