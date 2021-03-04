package com.example.smarthome.iot.impl.device.gateway.event;

public class UdpEvent {

    private String msg;

    public UdpEvent(String msg) {
        this.msg=msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
