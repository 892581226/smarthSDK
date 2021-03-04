package com.example.smarthome.iot.impl.device.negativecontrol.bean;

import java.io.Serializable;
import java.util.List;

public class NegativeControlBean implements Serializable {
    private List<NegativeControlData> devices;

    public List<NegativeControlData> getDevices() {
        return devices;
    }

    public void setDevices(List<NegativeControlData> devices) {
        this.devices = devices;
    }

    public class NegativeControlData implements Serializable{
        private String id;
        private String deviceId;
        private String gatewayId;
        private String deviceType;
        private String supplierId;
        private String roomId;
        private String roomName;
        private String deviceName;
        private String deviceIcon;
        private String location;
        private boolean online;
        private String field;
        private Object auxiliaryDevStatusMap;
//        private List<String> mainDevStatusSet;
        private String createTime;
        private String updateTime;
        private Integer sceneFlag;
        private String properField;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceIcon() {
            return deviceIcon;
        }

        public void setDeviceIcon(String deviceIcon) {
            this.deviceIcon = deviceIcon;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }


        public Object getAuxiliaryDevStatusMap() {
            return auxiliaryDevStatusMap;
        }

        public void setAuxiliaryDevStatusMap(Object auxiliaryDevStatusMap) {
            this.auxiliaryDevStatusMap = auxiliaryDevStatusMap;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public Integer getSceneFlag() {
            return sceneFlag;
        }

        public void setSceneFlag(Integer sceneFlag) {
            this.sceneFlag = sceneFlag;
        }

        public String getProperField() {
            return properField;
        }

        public void setProperField(String properField) {
            this.properField = properField;
        }

        public class NegativeControlData2 implements Serializable{
            private String switch4;
            private String which_button;
            private String switch3;
            private String switch2;
            private String switch1;

            public String getSwitch4() {
                return switch4;
            }

            public void setSwitch4(String switch4) {
                this.switch4 = switch4;
            }

            public String getWhich_button() {
                return which_button;
            }

            public void setWhich_button(String which_button) {
                this.which_button = which_button;
            }

            public String getSwitch3() {
                return switch3;
            }

            public void setSwitch3(String switch3) {
                this.switch3 = switch3;
            }

            public String getSwitch2() {
                return switch2;
            }

            public void setSwitch2(String switch2) {
                this.switch2 = switch2;
            }

            public String getSwitch1() {
                return switch1;
            }

            public void setSwitch1(String switch1) {
                this.switch1 = switch1;
            }
        }
    }

}
