package com.example.smarthome.iot.entry;

/**
 * author: glq
 * date: 2019/6/13 9:35
 * description:
 * update: 2019/6/13
 * version:
 */
public class DeviceInfoVo {
    private String deviceType;
    private String deviceId;
    private String gatewayId;
    private String supplierId;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public String toString() {
        return "DeviceInfoVo{" +
                "deviceType='" + deviceType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", gatewayId='" + gatewayId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                '}';
    }
}
