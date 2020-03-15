package com.my.project.rocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.my.project.event.BaseEvent;
import com.my.project.mq.MqEventBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinguo_peng
 * @description TODO
 * @date 2020/3/14 下午10:26
 */
@Component
@Slf4j
public class RocketMqMessageSubmit implements InitializingBean {

    @Autowired
    private MqEventBus mqEventBus;
    @Value("${rocket-mq-topic}")
    private String topic;

    @Override
    public void afterPropertiesSet() throws Exception {
        AtomicInteger count = new AtomicInteger(0);
        new Thread(() -> {
           try {
               mqEventBus.subscribe(topic, (messages, context) -> {

                   for (Message message:messages) {
                       try {
                           String body = new String(message.getBody(), "utf-8");
                           JSONObject json = JSON.parseObject(body);
                           BaseEvent event = (BaseEvent)JSON.parseObject(body, Class.forName(json.getString("type")));
                            if (event instanceof LogEvent) {
                                log.info("日志消息事件通知...");
                                count.incrementAndGet();
                                log.info("count = [{}]", count.get());
                            } else if (event instanceof TransferEvent) {
                                log.info("转账消息事件通知...");
                            }
                       } catch (Exception e) {
                           log.error(e.getMessage(), e);
                           return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                       }
                   }
                   return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
               });
           } catch (Exception e) {
               log.error(e.getMessage(), e);
           }
        }).start();
    }
}
