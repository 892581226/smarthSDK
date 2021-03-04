package com.example.smarthome.iot.entry;

/**
 * author: glq
 * date: 2019/4/10 15:10
 * description: 添加设备实体类
 * update: 2019/4/10
 * version:
 */
public class DeviceAppendItemVo {
    private String img;
    private String name;

    public DeviceAppendItemVo() {
    }

    public DeviceAppendItemVo(String img, String name) {
        this.img = img;
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
