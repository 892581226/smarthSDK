package com.example.smarthome.iot.impl.device.cateye.entity;

import java.util.List;

public class AlarmMessageInfo {

    private String aid;
    private int type;
    private List<String> pvids;
    private List<String> fids;
    private String bid;
    private long alarmTime;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public List<String> getPvids() {
        return pvids;
    }

    public void setPvids(List<String> pvids) {
        this.pvids = pvids;
    }

    public List<String> getFids() {
        return fids;
    }

    public void setFids(List<String> fids) {
        this.fids = fids;
    }

    @Override
    public String toString() {
        return "AlarmMessageInfo{" +
                "aid='" + aid + '\'' +
                ", type=" + type +
                ", pvids=" + pvids +
                ", fids=" + fids +
                ", bid='" + bid + '\'' +
                ", alarmTime=" + alarmTime +
                '}';
    }
}
