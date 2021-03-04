package com.example.smarthome.iot.entry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: glq
 * date: 2019/4/9 14:00
 * description: 设备管理实体类
 * update: 2019/4/9
 * version:
 */
public class DeviceManageItemVo implements Parcelable {
    private String deviceName;
    private String deviceAddress;
    private boolean deviceOnlineType;
    private boolean isHaveSwitch; // 是否有开关
    private boolean deviceSwitchState; // 开关开启状态
    private boolean isOptional; // 是否显示选中框
    private boolean isCheck;  // 是否已选中

    public DeviceManageItemVo() {
    }

    public DeviceManageItemVo(String deviceName, String deviceAddress, boolean deviceOnlineType, boolean isHaveSwitch, boolean deviceSwitchState, boolean isOptional, boolean isCheck) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.deviceOnlineType = deviceOnlineType;
        this.isHaveSwitch = isHaveSwitch;
        this.deviceSwitchState = deviceSwitchState;
        this.isOptional = isOptional;
        this.isCheck = isCheck;
    }

    protected DeviceManageItemVo(Parcel in) {
        deviceName = in.readString();
        deviceAddress = in.readString();
        deviceOnlineType = in.readByte() != 0;
        isHaveSwitch = in.readByte() != 0;
        deviceSwitchState = in.readByte() != 0;
        isOptional = in.readByte() != 0;
        isCheck = in.readByte() != 0;
    }

    public static final Creator<DeviceManageItemVo> CREATOR = new Creator<DeviceManageItemVo>() {
        @Override
        public DeviceManageItemVo createFromParcel(Parcel in) {
            return new DeviceManageItemVo(in);
        }

        @Override
        public DeviceManageItemVo[] newArray(int size) {
            return new DeviceManageItemVo[size];
        }
    };

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public boolean isDeviceOnlineType() {
        return deviceOnlineType;
    }

    public void setDeviceOnlineType(boolean deviceOnlineType) {
        this.deviceOnlineType = deviceOnlineType;
    }

    public boolean isHaveSwitch() {
        return isHaveSwitch;
    }

    public void setHaveSwitch(boolean haveSwitch) {
        isHaveSwitch = haveSwitch;
    }

    public boolean isDeviceSwitchState() {
        return deviceSwitchState;
    }

    public void setDeviceSwitchState(boolean deviceSwitchState) {
        this.deviceSwitchState = deviceSwitchState;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean optional) {
        isOptional = optional;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceName);
        dest.writeString(deviceAddress);
        dest.writeByte((byte) (deviceOnlineType ? 1 : 0));
        dest.writeByte((byte) (isHaveSwitch ? 1 : 0));
        dest.writeByte((byte) (deviceSwitchState ? 1 : 0));
        dest.writeByte((byte) (isOptional ? 1 : 0));
        dest.writeByte((byte) (isCheck ? 1 : 0));
    }
}
