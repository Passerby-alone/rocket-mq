package com.my.project.rocket;

import com.alibaba.fastjson.JSON;
import com.my.project.event.BaseEvent;
import com.my.project.mq.MqEventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jinguo_peng
 * @description TODO
 * @date 2020/3/15 上午11:15
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RocketMqTest.class)
@ComponentScan("com.my")
public class RocketMqTest {

    @Value("${rocket-mq-topic}")
    private String topic;
    @Autowired
    private MqEventBus mqEventBus;

    @Test
    public void testConsumer() {

        BaseEvent logEvent = new LogEvent();
        mqEventBus.syncPublish(topic, JSON.toJSONString(logEvent));
    }

    @Test
    public void testConsumerCount() {

        BaseEvent logEvent = new LogEvent();
        for (int count = 0; count < 1000; count ++) {
            mqEventBus.syncPublish(topic, JSON.toJSONString(logEvent));
        }
    }
}
