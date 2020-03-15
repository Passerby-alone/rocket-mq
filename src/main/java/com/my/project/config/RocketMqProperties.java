package com.my.project.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jinguo_peng
 * @description TODO
 * @date 2020/3/14 上午11:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "event-bus")
public class RocketMqProperties {

    private String nameServer;
    private String producerGroup;
    private String consumerGroup;
}
