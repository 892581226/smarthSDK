package com.example.smarthome.iot.impl.device.infraredscreen.bean;

import java.io.Serializable;
import java.util.List;

public class InFraredScreenBean implements Serializable {
    private List<InFraredScreenData> list;

    public List<InFraredScreenData> getList() {
        return list;
    }

    public void setList(List<InFraredScreenData> list) {
        this.list = list;
    }

    public class InFraredScreenData implements Serializable{
        private String unit;
        private String code;
        private String createTime;
        private String fromUser;
        private String projectCode;
        private Integer isDelete;
        private String name;
        private String pathCode;
        private String tempCode;
        private String id;
        private String roomCode;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getFromUser() {
            return fromUser;
        }

        public void setFromUser(String fromUser) {
            this.fromUser = fromUser;
        }

        public String getProjectCode() {
            return projectCode;
        }

        public void setProjectCode(String projectCode) {
            this.projectCode = projectCode;
        }

        public Integer getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(Integer isDelete) {
            this.isDelete = isDelete;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPathCode() {
            return pathCode;
        }

        public void setPathCode(String pathCode) {
            this.pathCode = pathCode;
        }

        public String getTempCode() {
            return tempCode;
        }

        public void setTempCode(String tempCode) {
            this.tempCode = tempCode;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRoomCode() {
            return roomCode;
        }

        public void setRoomCode(String roomCode) {
            this.roomCode = roomCode;
        }

        @Override
        public String toString() {
            return "InFraredScreenData{" +
                    "unit='" + unit + '\'' +
                    ", code='" + code + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", fromUser='" + fromUser + '\'' +
                    ", projectCode='" + projectCode + '\'' +
                    ", isDelete=" + isDelete +
                    ", name='" + name + '\'' +
                    ", pathCode='" + pathCode + '\'' +
                    ", tempCode='" + tempCode + '\'' +
                    ", id='" + id + '\'' +
                    ", roomCode='" + roomCode + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "InFraredScreenBean{" +
                "list=" + list +
                '}';
    }
}
