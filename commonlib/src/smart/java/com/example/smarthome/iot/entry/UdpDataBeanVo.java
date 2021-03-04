package com.example.smarthome.iot.entry;

public class UdpDataBeanVo {
    private String gw_id;
    private String ip;
    private String proto;
    private String gw_model_id;
    private String state;
    private String version;

    public String getGw_id() {
        return gw_id;
    }

    public void setGw_id(String gw_id) {
        this.gw_id = gw_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public String getGw_model_id() {
        return gw_model_id;
    }

    public void setGw_model_id(String gw_model_id) {
        this.gw_model_id = gw_model_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
