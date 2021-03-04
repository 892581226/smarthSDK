package com.example.smarthome.iot.entry;

/**
 * 增加设备 getDev返回bean
 *
 */
public class DeviceBaseInfoVo {
    private DeviceBaseInfo deviceBaseInfo;

    public class DeviceBaseInfo {
        private String devNo;
        private String icon1;
        private String icon2;
        private String tips1;
        private String tips2;
        private String supplierId;

        public String getDevNo() {
            return devNo;
        }

        public void setDevNo(String devNo) {
            this.devNo = devNo;
        }

        public String getIcon1() {
            return icon1;
        }

        public void setIcon1(String icon1) {
            this.icon1 = icon1;
        }

        public String getIcon2() {
            return icon2;
        }

        public void setIcon2(String icon2) {
            this.icon2 = icon2;
        }

        public String getTips1() {
            return tips1;
        }

        public void setTips1(String tips1) {
            this.tips1 = tips1;
        }

        public String getTips2() {
            return tips2;
        }

        public void setTips2(String tips2) {
            this.tips2 = tips2;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }
    }

    public DeviceBaseInfo getDeviceBaseInfo() {
        return deviceBaseInfo;
    }

    public void setDeviceBaseInfo(DeviceBaseInfo deviceBaseInfo) {
        this.deviceBaseInfo = deviceBaseInfo;
    }
}
