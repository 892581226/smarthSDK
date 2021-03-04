package com.example.smarthome.iot.entry;

import java.util.List;

/**
 *
 * 感知类设备信息
 *
 */
public class SensorInfoVo {
    private SensorMessageVo sensor;

    public SensorMessageVo getSensor() {
        return sensor;
    }

    public void setSensor(SensorMessageVo sensor) {
        this.sensor = sensor;
    }

    public class SensorMessageVo {
        private String deviceId;
        private String deviceType;
        private List<SensorMessages> msgs;

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

        public List<SensorMessages> getMsgs() {
            return msgs;
        }

        public void setMsgs(List<SensorMessages> msgs) {
            this.msgs = msgs;
        }
    }

    public static class SensorMessages {
        private String time;
        private String msg;

        public SensorMessages(String time, String msg) {
            this.time = time;
            this.msg = msg;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public class SensorMsg {
        private String alarm;

        public String getAlarm() {
            return alarm;
        }

        public void setAlarm(String alarm) {
            this.alarm = alarm;
        }
    }
}
