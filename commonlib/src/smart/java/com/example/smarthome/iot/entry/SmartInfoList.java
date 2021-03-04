package com.example.smarthome.iot.entry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * author: glq
 * date: 2019/6/12 15:46
 * description:
 * update: 2019/6/12
 * version:
 */
public class SmartInfoList implements Parcelable {

    /**
     * familys : [{"familyName":"我的家","familyId":"1131471654363467776","roomNum":3,"devNum":3,"devOnlineNum":2,"masterUserid":"0007","rooms":[{"roomId":"1131471654363467777","roomName":"主卧","devNum":0,"devOnlineNum":0,"icon":"icon_iot_room_master","familyid":"1131471654363467776","deviceInfo":[]},{"roomId":"1131471654363467778","roomName":"次卧","devNum":1,"devOnlineNum":1,"icon":"icon_iot_room_guest","familyid":"1131471654363467776","deviceInfo":[{"id":"5cf75bf6db65eb369cb32725","deviceId":"000D6F001066C4E5","gatewayId":"F88479057B4A","deviceType":"120400","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令温湿度传感器","deviceIcon":"xx","online":true,"field":{"battery_state":"0","alarm":"0"}}]},{"roomId":"1131471654363467779","roomName":"客厅","devNum":1,"devOnlineNum":1,"icon":"icon_iot_room_living","familyid":"1131471654363467776","deviceInfo":[{"id":"5cf73f34db65eb2580c21184","deviceId":"D0CF5EFFFE3A0B33","gatewayId":"F88479057B4A","deviceType":"131303","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令六路开关","deviceIcon":"xx","online":true,"field":{"switch3":"0","switch2":"1","switch1":"0","which_button":"0"}}]}],"gateways":[{"gatewayId":"F88479057B4A","familyId":"1131471654363467776","gatewayName":"","deviceInfo":[{"id":"5cf73f34db65eb2580c21184","deviceId":"D0CF5EFFFE3A0B33","gatewayId":"F88479057B4A","deviceType":"131303","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令六路开关","deviceIcon":"xx","online":true,"field":{"switch3":"0","switch2":"1","switch1":"0","which_button":"0"}},{"id":"5cf75bf6db65eb369cb32725","deviceId":"000D6F001066C4E5","gatewayId":"F88479057B4A","deviceType":"120400","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令温湿度传感器","deviceIcon":"xx","online":true,"field":{"battery_state":"0","alarm":"0"}},{"id":"5cf7706adb65eb48d8628f07","deviceId":"000D6F000E731A06","gatewayId":"F88479057B4A","deviceType":"130201","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令温湿度传感器1","deviceIcon":"xx","online":true,"field":{"battery_state":"0","temperature":"25.4","humidity":"54"}}]}]}]
     * userid : 0007
     */

    private String userid;
    private List<FamilysBean> familys;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SmartInfoVo{" +
                "userid='" + userid + '\'' +
                ", familys=" + familys +
                ", user=" + user +
                '}';
    }

    public class User{
        private boolean isProperty;
        private String id;
        private String userId;

        public boolean isProperty() {
            return isProperty;
        }

        public void setProperty(boolean property) {
            isProperty = property;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "User{" +
                    "isProperty=" + isProperty +
                    ", id='" + id + '\'' +
                    ", userId='" + userId + '\'' +
                    '}';
        }
    }



    public SmartInfoList() {
    }

    protected SmartInfoList(Parcel in) {
        userid = in.readString();
    }

    public static final Creator<SmartInfoVo> CREATOR = new Creator<SmartInfoVo>() {
        @Override
        public SmartInfoVo createFromParcel(Parcel in) {
            return new SmartInfoVo(in);
        }

        @Override
        public SmartInfoVo[] newArray(int size) {
            return new SmartInfoVo[size];
        }
    };

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<FamilysBean> getFamilys() {
        return familys;
    }

    public void setFamilys(List<FamilysBean> familys) {
        this.familys = familys;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
    }

    public static class FamilysBean implements Parcelable {
        /**
         * familyName : 我的家
         * familyId : 1131471654363467776
         * roomNum : 3
         * devNum : 3
         * devOnlineNum : 2
         * masterUserid : 0007
         * rooms : [{"roomId":"1131471654363467777","roomName":"主卧","devNum":0,"devOnlineNum":0,"icon":"icon_iot_room_master","familyid":"1131471654363467776","deviceInfo":[]},{"roomId":"1131471654363467778","roomName":"次卧","devNum":1,"devOnlineNum":1,"icon":"icon_iot_room_guest","familyid":"1131471654363467776","deviceInfo":[{"id":"5cf75bf6db65eb369cb32725","deviceId":"000D6F001066C4E5","gatewayId":"F88479057B4A","deviceType":"120400","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令温湿度传感器","deviceIcon":"xx","online":true,"field":{"battery_state":"0","alarm":"0"}}]},{"roomId":"1131471654363467779","roomName":"客厅","devNum":1,"devOnlineNum":1,"icon":"icon_iot_room_living","familyid":"1131471654363467776","deviceInfo":[{"id":"5cf73f34db65eb2580c21184","deviceId":"D0CF5EFFFE3A0B33","gatewayId":"F88479057B4A","deviceType":"131303","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令六路开关","deviceIcon":"xx","online":true,"field":{"switch3":"0","switch2":"1","switch1":"0","which_button":"0"}}]}]
         * gateways : [{"gatewayId":"F88479057B4A","familyId":"1131471654363467776","gatewayName":"","deviceInfo":[{"id":"5cf73f34db65eb2580c21184","deviceId":"D0CF5EFFFE3A0B33","gatewayId":"F88479057B4A","deviceType":"131303","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令六路开关","deviceIcon":"xx","online":true,"field":{"switch3":"0","switch2":"1","switch1":"0","which_button":"0"}},{"id":"5cf75bf6db65eb369cb32725","deviceId":"000D6F001066C4E5","gatewayId":"F88479057B4A","deviceType":"120400","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令温湿度传感器","deviceIcon":"xx","online":true,"field":{"battery_state":"0","alarm":"0"}},{"id":"5cf7706adb65eb48d8628f07","deviceId":"000D6F000E731A06","gatewayId":"F88479057B4A","deviceType":"130201","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令温湿度传感器1","deviceIcon":"xx","online":true,"field":{"battery_state":"0","temperature":"25.4","humidity":"54"}}]}]
         */

        private String familyName;
        private String familyId;
        private int roomNum;
        private int devNum;
        private int devOnlineNum;
        private int jurisdiction;
        private String masterUserid;
        private List<RoomsBean> rooms;
        private List<GatewaysBean> gateways;
        private Project project;
        private List<ScenesBean> scenes;
        private boolean defense;

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public boolean isDefense() {
            return defense;
        }

        public void setDefense(boolean defense) {
            this.defense = defense;
        }

        public FamilysBean() {
        }

        public int getJurisdiction() {
            return jurisdiction;
        }

        public void setJurisdiction(int jurisdiction) {
            this.jurisdiction = jurisdiction;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getFamilyId() {
            return familyId;
        }

        public void setFamilyId(String familyId) {
            this.familyId = familyId;
        }

        public int getRoomNum() {
            return roomNum;
        }

        public void setRoomNum(int roomNum) {
            this.roomNum = roomNum;
        }

        public int getDevNum() {
            return devNum;
        }

        public void setDevNum(int devNum) {
            this.devNum = devNum;
        }

        public int getDevOnlineNum() {
            return devOnlineNum;
        }

        public void setDevOnlineNum(int devOnlineNum) {
            this.devOnlineNum = devOnlineNum;
        }

        public String getMasterUserid() {
            return masterUserid;
        }

        public void setMasterUserid(String masterUserid) {
            this.masterUserid = masterUserid;
        }

        public List<RoomsBean> getRooms() {
            return rooms;
        }

        public void setRooms(List<RoomsBean> rooms) {
            this.rooms = rooms;
        }

        public List<GatewaysBean> getGateways() {
            return gateways;
        }

        public void setGateways(List<GatewaysBean> gateways) {
            this.gateways = gateways;
        }

        public List<ScenesBean> getScenes() {
            return scenes;
        }

        public void setScenes(List<ScenesBean> scenes) {
            this.scenes = scenes;
        }

        public class Project{
            private String projectCode;

            public String getProjectCode() {
                return projectCode;
            }

            public void setProjectCode(String projectCode) {
                this.projectCode = projectCode;
            }
        }
        public static class RoomsBean implements Parcelable {
            /**
             * roomId : 1131471654363467777
             * roomName : 主卧
             * devNum : 0
             * devOnlineNum : 0
             * icon : icon_iot_room_master
             * familyid : 1131471654363467776
             * deviceInfo : []
             */

            private String roomId;
            private String roomName;
            private int devNum;
            private int devOnlineNum;
            private String icon;
            private String familyid;
            private List<DeviceInfoBean> deviceInfo;
            private boolean checked;//设备详情页标识选择状态

            public RoomsBean() {
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

            public int getDevNum() {
                return devNum;
            }

            public void setDevNum(int devNum) {
                this.devNum = devNum;
            }

            public int getDevOnlineNum() {
                return devOnlineNum;
            }

            public void setDevOnlineNum(int devOnlineNum) {
                this.devOnlineNum = devOnlineNum;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getFamilyid() {
                return familyid;
            }

            public void setFamilyid(String familyid) {
                this.familyid = familyid;
            }

            public List<DeviceInfoBean> getDeviceInfo() {
                return deviceInfo;
            }

            public void setDeviceInfo(List<DeviceInfoBean> deviceInfo) {
                this.deviceInfo = deviceInfo;
            }

            public boolean isChecked() {
                return checked;
            }

            public void setChecked(boolean checked) {
                this.checked = checked;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.roomId);
                dest.writeString(this.roomName);
                dest.writeInt(this.devNum);
                dest.writeInt(this.devOnlineNum);
                dest.writeString(this.icon);
                dest.writeString(this.familyid);
                dest.writeTypedList(this.deviceInfo);
                dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
            }

            protected RoomsBean(Parcel in) {
                this.roomId = in.readString();
                this.roomName = in.readString();
                this.devNum = in.readInt();
                this.devOnlineNum = in.readInt();
                this.icon = in.readString();
                this.familyid = in.readString();
                this.deviceInfo = in.createTypedArrayList(DeviceInfoBean.CREATOR);
                this.checked = in.readByte() != 0;
            }

            public static final Creator<RoomsBean> CREATOR = new Creator<RoomsBean>() {
                @Override
                public RoomsBean createFromParcel(Parcel source) {
                    return new RoomsBean(source);
                }

                @Override
                public RoomsBean[] newArray(int size) {
                    return new RoomsBean[size];
                }
            };

            @Override
            public String toString() {
                return "RoomsBean{" +
                        "roomId='" + roomId + '\'' +
                        ", roomName='" + roomName + '\'' +
                        ", devNum=" + devNum +
                        ", devOnlineNum=" + devOnlineNum +
                        ", icon='" + icon + '\'' +
                        ", familyid='" + familyid + '\'' +
                        ", deviceInfo=" + deviceInfo +
                        ", checked=" + checked +
                        '}';
            }
        }

        public static class DeviceInfoBean implements Parcelable {

            /**
             * id : 5cf75bf6db65eb369cb32725
             * deviceId : 000D6F001066C4E5
             * gatewayId : F88479057B4A
             * deviceType : 120400
             * supplierId : DYxjSE290221
             * roomName :
             * deviceName : 海令温湿度传感器
             * deviceIcon : xx
             * online : true
             * field : {"battery_state":"0","alarm":"0"}
             */

            private String id;
            private String deviceId;
            private String gatewayId;
            private String deviceType;
            private String supplierId;
            private String roomName;
            private String deviceName;
            private String deviceIcon;
            private boolean online;
            private FieldBean field;
            private boolean isCheck;
            private String roomId;
            private String location;
            private int delayTime;//场景延时时间 单位是毫秒
            private String sceneFlag;//判断场景的绑定状态 默认值是0的时候可以被删除 其余值都不能被删除
            private String state;

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public DeviceInfoBean() {
            }

            public String getSceneFlag() {
                return sceneFlag;
            }

            public void setSceneFlag(String sceneFlag) {
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

            public boolean isOnline() {
                return online;
            }

            public void setOnline(boolean online) {
                this.online = online;
            }

            public FieldBean getField() {
                return field;
            }

            public void setField(FieldBean field) {
                this.field = field;
            }

            public boolean isCheck() {
                return isCheck;
            }

            public void setCheck(boolean check) {
                isCheck = check;
            }

            public String getRoomId() {
                return roomId;
            }

            public void setRoomId(String roomId) {
                this.roomId = roomId;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public int getDelayTime() {
                return delayTime;
            }

            public void setDelayTime(int delayTime) {
                this.delayTime = delayTime;
            }

            public static class FieldBean implements Parcelable {
                /**
                 * battery_state : 0
                 * alarm : 0
                 */

                private String battery_state;
                private String alarm;

                private String switch3;
                private String switch2;
                private String switch1;
                private String which_button;
                private String switch4;
                private String mode;
                private String state;

                public String getState() {
                    return state;
                }

                public void setState(String state) {
                    this.state = state;
                }

                public FieldBean() {
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

                public String getWhich_button() {
                    return which_button;
                }

                public void setWhich_button(String which_button) {
                    this.which_button = which_button;
                }

                public String getSwitch4() {
                    return switch4;
                }

                public void setSwitch4(String switch4) {
                    this.switch4 = switch4;
                }

                public String getMode() {
                    return mode;
                }

                public void setMode(String mode) {
                    this.mode = mode;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.battery_state);
                    dest.writeString(this.alarm);
                    dest.writeString(this.switch3);
                    dest.writeString(this.switch2);
                    dest.writeString(this.switch1);
                    dest.writeString(this.which_button);
                    dest.writeString(this.switch4);
                    dest.writeString(this.mode);
                    dest.writeString(this.state);
                }

                protected FieldBean(Parcel in) {
                    this.battery_state = in.readString();
                    this.alarm = in.readString();
                    this.switch3 = in.readString();
                    this.switch2 = in.readString();
                    this.switch1 = in.readString();
                    this.which_button = in.readString();
                    this.switch4 = in.readString();
                    this.mode = in.readString();
                    this.state=in.readString();
                }

                public static final Creator<FieldBean> CREATOR = new Creator<FieldBean>() {
                    @Override
                    public FieldBean createFromParcel(Parcel source) {
                        return new FieldBean(source);
                    }

                    @Override
                    public FieldBean[] newArray(int size) {
                        return new FieldBean[size];
                    }
                };

                @Override
                public String toString() {
                    return "FieldBean{" +
                            "battery_state='" + battery_state + '\'' +
                            ", alarm='" + alarm + '\'' +
                            ", switch3='" + switch3 + '\'' +
                            ", switch2='" + switch2 + '\'' +
                            ", switch1='" + switch1 + '\'' +
                            ", which_button='" + which_button + '\'' +
                            ", switch4='" + switch4 + '\'' +
                            ", mode='" + mode + '\'' +
                            ", state='" + state + '\'' +
                            '}';
                }
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.id);
                dest.writeString(this.deviceId);
                dest.writeString(this.gatewayId);
                dest.writeString(this.deviceType);
                dest.writeString(this.supplierId);
                dest.writeString(this.roomName);
                dest.writeString(this.deviceName);
                dest.writeString(this.deviceIcon);
                dest.writeByte(this.online ? (byte) 1 : (byte) 0);
                dest.writeParcelable(this.field, flags);
                dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
                dest.writeString(this.roomId);
                dest.writeString(this.location);
                dest.writeInt(this.delayTime);
                dest.writeString(this.sceneFlag);
            }

            protected DeviceInfoBean(Parcel in) {
                this.id = in.readString();
                this.deviceId = in.readString();
                this.gatewayId = in.readString();
                this.deviceType = in.readString();
                this.supplierId = in.readString();
                this.roomName = in.readString();
                this.deviceName = in.readString();
                this.deviceIcon = in.readString();
                this.online = in.readByte() != 0;
                this.field = in.readParcelable(FieldBean.class.getClassLoader());
                this.isCheck = in.readByte() != 0;
                this.roomId = in.readString();
                this.location = in.readString();
                this.delayTime = in.readInt();
                this.sceneFlag = in.readString();
            }

            public static final Creator<DeviceInfoBean> CREATOR = new Creator<DeviceInfoBean>() {
                @Override
                public DeviceInfoBean createFromParcel(Parcel source) {
                    return new DeviceInfoBean(source);
                }

                @Override
                public DeviceInfoBean[] newArray(int size) {
                    return new DeviceInfoBean[size];
                }
            };

            @Override
            public String toString() {
                return "DeviceInfoBean{" +
                        "id='" + id + '\'' +
                        ", deviceId='" + deviceId + '\'' +
                        ", gatewayId='" + gatewayId + '\'' +
                        ", deviceType='" + deviceType + '\'' +
                        ", supplierId='" + supplierId + '\'' +
                        ", roomName='" + roomName + '\'' +
                        ", deviceName='" + deviceName + '\'' +
                        ", deviceIcon='" + deviceIcon + '\'' +
                        ", online=" + online +
                        ", field=" + field +
                        ", isCheck=" + isCheck +
                        ", roomId='" + roomId + '\'' +
                        ", location='" + location + '\'' +
                        ", delayTime=" + delayTime +
                        ", sceneFlag='" + sceneFlag + '\'' +
                        '}';
            }
        }


        public static class GatewaysBean implements Parcelable {
            /**
             * gatewayId : F88479057B4A
             * familyId : 1131471654363467776
             * gatewayName :
             * deviceInfo : [{"id":"5cf73f34db65eb2580c21184","deviceId":"D0CF5EFFFE3A0B33","gatewayId":"F88479057B4A","deviceType":"131303","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令六路开关","deviceIcon":"xx","online":true,"field":{"switch3":"0","switch2":"1","switch1":"0","which_button":"0"}},{"id":"5cf75bf6db65eb369cb32725","deviceId":"000D6F001066C4E5","gatewayId":"F88479057B4A","deviceType":"120400","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令温湿度传感器","deviceIcon":"xx","online":true,"field":{"battery_state":"0","alarm":"0"}},{"id":"5cf7706adb65eb48d8628f07","deviceId":"000D6F000E731A06","gatewayId":"F88479057B4A","deviceType":"130201","supplierId":"DYxjSE290221","roomName":"","deviceName":"海令温湿度传感器1","deviceIcon":"xx","online":true,"field":{"battery_state":"0","temperature":"25.4","humidity":"54"}}]
             */

            private String gatewayId;
            private String familyId;
            private String gatewayName;
            private List<DeviceInfoBean> deviceInfo;
            private String deviceType;

            public String getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(String deviceType) {
                this.deviceType = deviceType;
            }

            public GatewaysBean() {
            }

            protected GatewaysBean(Parcel in) {
                gatewayId = in.readString();
                familyId = in.readString();
                gatewayName = in.readString();
                deviceInfo = in.createTypedArrayList(DeviceInfoBean.CREATOR);
            }

            public static final Creator<GatewaysBean> CREATOR = new Creator<GatewaysBean>() {
                @Override
                public GatewaysBean createFromParcel(Parcel in) {
                    return new GatewaysBean(in);
                }

                @Override
                public GatewaysBean[] newArray(int size) {
                    return new GatewaysBean[size];
                }
            };

            public String getGatewayId() {
                return gatewayId;
            }

            public void setGatewayId(String gatewayId) {
                this.gatewayId = gatewayId;
            }

            public String getFamilyId() {
                return familyId;
            }

            public void setFamilyId(String familyId) {
                this.familyId = familyId;
            }

            public String getGatewayName() {
                return gatewayName;
            }

            public void setGatewayName(String gatewayName) {
                this.gatewayName = gatewayName;
            }

            public List<DeviceInfoBean> getDeviceInfo() {
                return deviceInfo;
            }

            public void setDeviceInfo(List<DeviceInfoBean> deviceInfo) {
                this.deviceInfo = deviceInfo;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(gatewayId);
                dest.writeString(familyId);
                dest.writeString(gatewayName);
                dest.writeTypedList(deviceInfo);
            }

        }

        public static class ScenesBean {

            /**
             * senceId : 1144127622138171393
             * sceneName : test0
             * icon : icon_iot_room_living
             * familyId : 1131471654363467776
             * sceneType : create
             * devOnlineNum : 2
             * devNum : 2
             * iseffective : true
             * taskTpye : once
             * tasks : [{"deviceInfo":{"deviceId":"000D6FFFFE5D9E15","gatewayId":"F88479057B4A","deviceType":"131304","supplierId":"DYxjSE290221","deviceName":"海令四路开关","deviceIcon":"https://img.xhmind.com/20190620110548632.png","online":true,"field":{"switch3":"1","switch2":"1","switch1":"1","switch4":"0","which_button":"0"}},"taskInfo":{"isdelayed":true,"delayTime":36000}},{"deviceInfo":{"deviceId":"000D6FFFFE5D9E15","gatewayId":"F88479057B4A","deviceType":"131304","supplierId":"DYxjSE290221","deviceName":"海令四路开关","deviceIcon":"https://img.xhmind.com/20190620110548632.png","online":true,"field":{"switch1":"0","which_button":"0"}},"taskInfo":{"isdelayed":false,"delayTime":0}}]
             */

            private String sceneId;
            private String sceneName;
            private String icon;
            private String familyId;
            private String sceneType;
            private int devOnlineNum;
            private int devNum;
            private boolean iseffective;
            private String taskTpye;
            private List<TasksBean> tasks;
            private boolean checked;//关联场景

            public boolean isChecked() {
                return checked;
            }

            public void setChecked(boolean checked) {
                this.checked = checked;
            }

            public String getSceneId() {
                return sceneId;
            }

            public void setSceneId(String sceneId) {
                this.sceneId = sceneId;
            }

            public String getSceneName() {
                return sceneName;
            }

            public void setSceneName(String sceneName) {
                this.sceneName = sceneName;
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

            public String getSceneType() {
                return sceneType;
            }

            public void setSceneType(String sceneType) {
                this.sceneType = sceneType;
            }

            public int getDevOnlineNum() {
                return devOnlineNum;
            }

            public void setDevOnlineNum(int devOnlineNum) {
                this.devOnlineNum = devOnlineNum;
            }

            public int getDevNum() {
                return devNum;
            }

            public void setDevNum(int devNum) {
                this.devNum = devNum;
            }

            public boolean isIseffective() {
                return iseffective;
            }

            public void setIseffective(boolean iseffective) {
                this.iseffective = iseffective;
            }

            public String getTaskTpye() {
                return taskTpye;
            }

            public void setTaskTpye(String taskTpye) {
                this.taskTpye = taskTpye;
            }

            public List<TasksBean> getTasks() {
                return tasks;
            }

            public void setTasks(List<TasksBean> tasks) {
                this.tasks = tasks;
            }

            public static class TasksBean {
                /**
                 * deviceInfo : {"deviceId":"000D6FFFFE5D9E15","gatewayId":"F88479057B4A","deviceType":"131304","supplierId":"DYxjSE290221","deviceName":"海令四路开关","deviceIcon":"https://img.xhmind.com/20190620110548632.png","online":true,"field":{"switch3":"1","switch2":"1","switch1":"1","switch4":"0","which_button":"0"}}
                 * taskInfo : {"isdelayed":true,"delayTime":36000}
                 */

                private FamilysBean.DeviceInfoBean deviceInfo;
                private TaskInfoBean taskInfo;

                public FamilysBean.DeviceInfoBean getDeviceInfo() {
                    return deviceInfo;
                }

                public void setDeviceInfo(FamilysBean.DeviceInfoBean deviceInfo) {
                    this.deviceInfo = deviceInfo;
                }

                public TaskInfoBean getTaskInfo() {
                    return taskInfo;
                }

                public void setTaskInfo(TaskInfoBean taskInfo) {
                    this.taskInfo = taskInfo;
                }

                public static class TaskInfoBean {
                    /**
                     * isdelayed : true
                     * delayTime : 36000
                     */

                    private boolean isdelayed;
                    private int delayTime;
                    private String timeScope;
                    private String executeTime;


                    public boolean isIsdelayed() {
                        return isdelayed;
                    }

                    public void setIsdelayed(boolean isdelayed) {
                        this.isdelayed = isdelayed;
                    }

                    public int getDelayTime() {
                        return delayTime;
                    }

                    public void setDelayTime(int delayTime) {
                        this.delayTime = delayTime;
                    }

                    public String getTimeScope() {
                        return timeScope;
                    }

                    public void setTimeScope(String timeScope) {
                        this.timeScope = timeScope;
                    }

                    public String getExecuteTime() {
                        return executeTime;
                    }

                    public void setExecuteTime(String executeTime) {
                        this.executeTime = executeTime;
                    }
                }
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.familyName);
            dest.writeString(this.familyId);
            dest.writeInt(this.roomNum);
            dest.writeInt(this.devNum);
            dest.writeInt(this.devOnlineNum);
            dest.writeInt(this.jurisdiction);
            dest.writeString(this.masterUserid);
            dest.writeTypedList(this.rooms);
            dest.writeTypedList(this.gateways);
        }

        protected FamilysBean(Parcel in) {
            this.familyName = in.readString();
            this.familyId = in.readString();
            this.roomNum = in.readInt();
            this.devNum = in.readInt();
            this.devOnlineNum = in.readInt();
            this.jurisdiction = in.readInt();
            this.masterUserid = in.readString();
            this.rooms = in.createTypedArrayList(RoomsBean.CREATOR);
            this.gateways = in.createTypedArrayList(GatewaysBean.CREATOR);
        }

        public static final Creator<FamilysBean> CREATOR = new Creator<FamilysBean>() {
            @Override
            public FamilysBean createFromParcel(Parcel source) {
                return new FamilysBean(source);
            }

            @Override
            public FamilysBean[] newArray(int size) {
                return new FamilysBean[size];
            }
        };

        @Override
        public String toString() {
            return "FamilysBean{" +
                    "familyName='" + familyName + '\'' +
                    ", familyId='" + familyId + '\'' +
                    ", roomNum=" + roomNum +
                    ", devNum=" + devNum +
                    ", devOnlineNum=" + devOnlineNum +
                    ", jurisdiction=" + jurisdiction +
                    ", masterUserid='" + masterUserid + '\'' +
                    ", rooms=" + rooms +
                    ", gateways=" + gateways +
                    ", scenes=" + scenes +
                    '}';
        }
    }

}
