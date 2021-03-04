package com.example.smarthome.iot.entry;

import java.util.List;

public class SmartDevNumb {

    private List<ListNub> list;

    public List<ListNub> getList() {
        return list;
    }

    public void setList(List<ListNub> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "SmartDevNumb{" +
                "list=" + list +
                '}';
    }

    public class ListNub {
        private String gatewayId;
        private String   deviceId;
        private String deviceType;

        public String getGatewayId() {
            return gatewayId;
        }

        public void setGatewayId(String gatewayId) {
            this.gatewayId = gatewayId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        @Override
        public String toString() {
            return "ListNub{" +
                    "gatewayId='" + gatewayId + '\'' +
                    ", deviceId='" + deviceId + '\'' +
                    ", deviceType='" + deviceType + '\'' +
                    '}';
        }
    }
}
