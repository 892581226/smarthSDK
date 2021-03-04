package com.example.smarthome.iot.entry;

/**
 * author: glq
 * date: 2019/5/9 13:48
 * description: 设备接入流程实体类
 * update: 2019/5/9
 * version:
 */
public class DeviceConnectStepItemVo {
    private int deviceImg; // 设备图片
    private  int deviceStepNumber; // 设备接入步骤
    private String deviceStepIntro; // 设备接入简介
    private String icon;
    private String tips;
    private String step;

    public DeviceConnectStepItemVo() {
    }

    public DeviceConnectStepItemVo(int deviceStepNumber, String deviceStepIntro) {
        this.deviceStepNumber = deviceStepNumber;
        this.deviceStepIntro = deviceStepIntro;

    }
//
//    public DeviceConnectStepItemVo(int deviceImg, String deviceStepNumber, String deviceStepIntro) {
//        this.deviceImg = deviceImg;
//        this.deviceStepNumber = deviceStepNumber;
//        this.deviceStepIntro = deviceStepIntro;
//    }

    public int getDeviceImg() {
        return deviceImg;
    }

    public void setDeviceImg(int deviceImg) {
        this.deviceImg = deviceImg;
    }

    public int getDeviceStepNumber() {
        return deviceStepNumber;
    }

    public void setDeviceStepNumber(int deviceStepNumber) {
        this.deviceStepNumber = deviceStepNumber;
    }

    public String getDeviceStepIntro() {
        return deviceStepIntro;
    }

    public void setDeviceStepIntro(String deviceStepIntro) {
        this.deviceStepIntro = deviceStepIntro;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
