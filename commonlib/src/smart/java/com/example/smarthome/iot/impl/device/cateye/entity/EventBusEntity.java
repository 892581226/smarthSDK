package com.example.smarthome.iot.impl.device.cateye.entity;

import android.os.Handler;

public class EventBusEntity {

    public static final String GET_ALARM_LIST= "get_alarm_list";
    private int resultCode;
    private int actionCode;
    private String action;
    
    private String inComingCallPicFid;

    private long startTime;
    private long endTime;
    private String bid;
    private Handler mHandler;

    public EventBusEntity() {
        super();
        // TODO Auto-generated constructor stub
    }

    public EventBusEntity(int resultCode, String action) {
        super();
        this.resultCode = resultCode;
        this.action = action;
    }

    public EventBusEntity(int actionCode, int resultCode) {
        this.resultCode = resultCode;
        this.actionCode = actionCode;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getInComingCallPicFid() {
        return inComingCallPicFid;
    }

    public void setInComingCallPicFid(String inComingCallPicFid) {
        this.inComingCallPicFid = inComingCallPicFid;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public String toString() {
        return "EventBusEntity{" +
                "resultCode=" + resultCode +
                ", actionCode=" + actionCode +
                ", action='" + action + '\'' +
                ", inComingCallPicFid='" + inComingCallPicFid + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", bid='" + bid + '\'' +
                ", mHandler=" + mHandler +
                '}';
    }
}
