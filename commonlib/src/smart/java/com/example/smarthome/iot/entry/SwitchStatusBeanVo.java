package com.example.smarthome.iot.entry;

/**
 *
 * 开关状态
 *
 */
public class SwitchStatusBeanVo {
    private String switchName;
    private boolean switchStatus;

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public boolean isSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(boolean switchStatus) {
        this.switchStatus = switchStatus;
    }
}
