package com.example.smarthome.iot.impl.device.infraredscreen.bean;

import java.io.Serializable;
import java.util.List;

public class DeviceBean implements Serializable {
    private DeviceData deviceInfo;
//    private List<Object> mainDevStatusSet;


    @Override
    public String toString() {
        return "DeviceBean{" +
                "deviceInfo=" + deviceInfo +
                '}';
    }

    public DeviceData getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceData deviceInfo) {
        this.deviceInfo = deviceInfo;
    }


    public class DeviceData implements Serializable {


        private String createTime;
        private String updateTime;
        private Integer sceneFlag;
        private String properField;


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
        private DeviceData2 field;


        private String auxiliaryDevStatusMap;


        public String getAuxiliaryDevStatusMap() {
            return auxiliaryDevStatusMap;
        }

        public void setAuxiliaryDevStatusMap(String auxiliaryDevStatusMap) {
            this.auxiliaryDevStatusMap = auxiliaryDevStatusMap;
        }

        public String getProperField() {
            return properField;
        }

        public void setProperField(String properField) {
            this.properField = properField;
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

        public DeviceData2 getField() {
            return field;
        }

        public void setField(DeviceData2 field) {
            this.field = field;
        }

        public class DeviceData2 implements Serializable{
            private String endpoint;
            private String state;

            public String getEndpoint() {
                return endpoint;
            }

            @Override
            public String toString() {
                return "DeviceData2{" +
                        "endpoint='" + endpoint + '\'' +
                        ", state='" + state + '\'' +
                        '}';
            }

            public void setEndpoint(String endpoint) {
                this.endpoint = endpoint;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }
        }
    }

}
