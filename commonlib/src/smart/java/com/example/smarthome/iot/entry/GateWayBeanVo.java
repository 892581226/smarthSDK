package com.example.smarthome.iot.entry;

import java.io.Serializable;
import java.util.List;

public class GateWayBeanVo implements Serializable {
    private List<GateWayBeanVoData> gatewayTypeList;

    public List<GateWayBeanVoData> getGatewayTypeList() {
        return gatewayTypeList;
    }

    public void setGatewayTypeList(List<GateWayBeanVoData> gatewayTypeList) {
        this.gatewayTypeList = gatewayTypeList;
    }

    @Override
    public String toString() {
        return "GateWayBeanVo{" +
                "gatewayTypeList=" + gatewayTypeList +
                '}';
    }

    public class GateWayBeanVoData implements Serializable {
        private String id;
        private String deviceType;
        private String listPicture;
        private String exhibitionPicture;
        private String deviceName;
        private String proto;
        private String supplierId;
        private boolean registe;
        private String ip;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getId() {
            return id;
        }

        public boolean isRegiste() {
            return registe;
        }

        public void setRegiste(boolean registe) {
            this.registe = registe;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getListPicture() {
            return listPicture;
        }

        public void setListPicture(String listPicture) {
            this.listPicture = listPicture;
        }

        public String getExhibitionPicture() {
            return exhibitionPicture;
        }

        public void setExhibitionPicture(String exhibitionPicture) {
            this.exhibitionPicture = exhibitionPicture;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getProto() {
            return proto;
        }

        public void setProto(String proto) {
            this.proto = proto;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        @Override
        public String toString() {
            return "GateWayBeanVoData{" +
                    "id='" + id + '\'' +
                    ", deviceType='" + deviceType + '\'' +
                    ", listPicture='" + listPicture + '\'' +
                    ", exhibitionPicture='" + exhibitionPicture + '\'' +
                    ", deviceName='" + deviceName + '\'' +
                    ", proto='" + proto + '\'' +
                    ", supplierId='" + supplierId + '\'' +
                    ", registe=" + registe +
                    '}';
        }
    }
}
