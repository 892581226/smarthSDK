package com.xhwl.commonlib.uiutils.qcloud;

/**
 * <p> 退出腾讯云sdk  </p>
 * create by zw at 2018/8/14 0014
 */
public class StopServiceBus {

    private boolean stopSelf;//是否停止sdk
    private boolean needReloginSDK; //是否需要重新刷新sdk
    private boolean isCalling;// 是否通话中，被挤下线要处理
    private long delayTime;//延时退出sdk时间

    public boolean isStopSelf() {
        return stopSelf;
    }

    public StopServiceBus setStopSelf(boolean stopSelf) {
        this.stopSelf = stopSelf;
        return this;
    }

    public boolean isNeedReloginSDK() {
        return needReloginSDK;
    }

    public StopServiceBus setNeedReloginSDK(boolean needReloginSDK) {
        this.needReloginSDK = needReloginSDK;
        return this;
    }

    public boolean isCalling() {
        return isCalling;
    }

    public StopServiceBus setCalling(boolean calling) {
        isCalling = calling;
        return this;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public StopServiceBus setDelayTime(long delayTime) {
        this.delayTime = delayTime;
        return this;
    }
}
