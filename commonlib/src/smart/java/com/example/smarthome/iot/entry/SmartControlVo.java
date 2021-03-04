package com.example.smarthome.iot.entry;

/**
 *
 */
public class SmartControlVo {
    private String userId;
    private String familyId;
    private String cmdType;
    private String cmd;
    private DeviceInfoVo deviceInfo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public DeviceInfoVo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfoVo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Override
    public String toString() {
        return "SmartControlVo{" +
                "userId='" + userId + '\'' +
                ", familyId='" + familyId + '\'' +
                ", cmdType='" + cmdType + '\'' +
                ", cmd='" + cmd + '\'' +
                ", deviceInfo=" + deviceInfo +
                '}';
    }
}
