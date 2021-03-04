package com.example.smarthome.iot.entry;


/**
 * author:
 * date:
 * description: 获取某房间完整树形结构
 * update:
 * version:
 */
public class RoomDevsVo {
    private SmartInfoVo.FamilysBean.RoomsBean room;

    /*public class RoomDetail {
        private String roomId;
        private String roomName;
        private String devNum;
        private String devOnlineNum;
        private String icon;
        private String familyId;
        private List<RoomDeviceInfo> deviceInfo;

        public class RoomDeviceInfo {
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
            private RoomDeviceInfoField field;

            public class RoomDeviceInfoField {
                private String switch1;
                private String switch2;
                private String switch3;
                private String switch4;
                private String which_button;
                private String mode;
                private String battery_state;
                private String alarm;

                public String getSwitch1() {
                    return switch1;
                }

                public void setSwitch1(String switch1) {
                    this.switch1 = switch1;
                }

                public String getSwitch2() {
                    return switch2;
                }

                public void setSwitch2(String switch2) {
                    this.switch2 = switch2;
                }

                public String getSwitch3() {
                    return switch3;
                }

                public void setSwitch3(String switch3) {
                    this.switch3 = switch3;
                }

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

                public String getMode() {
                    return mode;
                }

                public void setMode(String mode) {
                    this.mode = mode;
                }

                public String getBattery_state() {
                    return battery_state;
                }

                public void setBattery_state(String battery_state) {
                    this.battery_state = battery_state;
                }

                public String getAlarm() {
                    return alarm;
                }

                public void setAlarm(String alarm) {
                    this.alarm = alarm;
                }
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

            public RoomDeviceInfoField getField() {
                return field;
            }

            public void setField(RoomDeviceInfoField field) {
                this.field = field;
            }

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

        public String getDevNum() {
            return devNum;
        }

        public void setDevNum(String devNum) {
            this.devNum = devNum;
        }

        public String getDevOnlineNum() {
            return devOnlineNum;
        }

        public void setDevOnlineNum(String devOnlineNum) {
            this.devOnlineNum = devOnlineNum;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getFamilyId() {
            return familyId;
        }

        public void setFamilyId(String familyId) {
            this.familyId = familyId;
        }

        public List<RoomDeviceInfo> getDeviceInfo() {
            return deviceInfo;
        }

        public void setDeviceInfo(List<RoomDeviceInfo> deviceInfo) {
            this.deviceInfo = deviceInfo;
        }
    }*/

    public SmartInfoVo.FamilysBean.RoomsBean getRoom() {
        return room;
    }

    public void setRoom(SmartInfoVo.FamilysBean.RoomsBean room) {
        this.room = room;
    }
}
