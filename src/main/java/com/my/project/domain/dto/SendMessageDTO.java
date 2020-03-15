package com.my.project.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jinguo_peng
 * @description TODO
 * @date 2020/3/12 下午9:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDTO {

    private Integer sendType;
    private String topic;
    private String message;
}
