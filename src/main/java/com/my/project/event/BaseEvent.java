package com.my.project.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author jinguo_peng
 * @description TODO
 * @date 2020/3/15 上午11:07
 */
public abstract class BaseEvent {

    public BaseEvent() {
    }

    public String getType() {
        return this.getClass().getName();
    }

    public static String serialize(BaseEvent event) {
        return JSON.toJSONString(event);
    }

    public static BaseEvent deserialize(String string) throws ClassNotFoundException {
        JSONObject json = JSONObject.parseObject(string);
        return (BaseEvent) JSON.parseObject(string, Class.forName(json.getString("type")));
    }
}
