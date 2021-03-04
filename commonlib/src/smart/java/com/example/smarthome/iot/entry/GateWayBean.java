package com.example.smarthome.iot.entry;

public class GateWayBean {

    private String proto;
    private String modelId;

    public GateWayBean(String proto, String modelId) {
        this.proto = proto;
        this.modelId = modelId;
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
