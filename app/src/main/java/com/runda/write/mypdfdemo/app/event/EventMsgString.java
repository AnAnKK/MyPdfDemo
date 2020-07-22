package com.runda.write.mypdfdemo.app.event;

/**
 * 总线发送的消息！
 */

public class EventMsgString {
    public EventMsgString(int sign, String msg) {
        this.sign = sign;
        this.msg = msg;
    }

    public int sign = -1;
    public String msg;
}
