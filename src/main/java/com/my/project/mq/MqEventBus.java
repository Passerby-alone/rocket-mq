package com.my.project.mq;


import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * @author jinguo_peng
 * @description TODO
 * @date 2020/3/14 上午11:02
 */
public interface MqEventBus {

    /**
    * @description 同步发送
    * @param topic：发送到的主题
    * @param message: 要发送的消息
    * @return SendResult: 发送结果，发送成功, 发送失败
    * @date 2020/3/15 上午10:48
    */
    SendResult syncPublish(String topic, String message);

    /**
     * @description 异步发送
     * @param topic：发送到的主题
     * @param message: 要发送的消息
     * @date 2020/3/15 上午10:48
     */
    void asyncPublish(String topic, String message);

    /**
     * @description 单向发送
     * @param topic：发送到的主题
     * @param message: 要发送的消息
     * @date 2020/3/15 上午10:48
     */
    void oneWayPublish(String topic, String message);

    /**
    * @description 订阅消息
    * @param topic：订阅主题
    * @param listener: 注册监听的消息事件
    * @date 2020/3/15 上午11:00
    */
    void subscribe(String topic, MessageListenerConcurrently listener);
}
