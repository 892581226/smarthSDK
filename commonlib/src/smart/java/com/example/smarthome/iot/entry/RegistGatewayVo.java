package com.example.smarthome.iot.entry;

/**
 * 注册网关接口请求bean
 *
 */
public class RegistGatewayVo {
    private String familyId;
    private String gatewayId;
    private String gatewayName;
    private String productName;
    private String supplierId;
    private String userId;
    private String gatewayIcon;
    private String deviceType;

    public String getGatewayIcon() {
        return gatewayIcon;
    }

    public void setGatewayIcon(String gatewayIcon) {
        this.gatewayIcon = gatewayIcon;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "RegistGatewayVo{" +
                "familyId='" + familyId + '\'' +
                ", gatewayId='" + gatewayId + '\'' +
                ", gatewayName='" + gatewayName + '\'' +
                ", productName='" + productName + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", userId='" + userId + '\'' +
                ", gatewayIcon='" + gatewayIcon + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}
