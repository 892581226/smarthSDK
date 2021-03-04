package com.xhwl.commonlib.uiutils.qcloud;

/**
 * <p>挤下线销毁通话中页面   </p>
 * create by zw at 2018/8/29 0029
 */
public class CloseQCloudVideoBus {
    private boolean needClose;//关闭房间退出sdk

    public boolean isNeedClose() {
        return needClose;
    }

    public CloseQCloudVideoBus setNeedClose(boolean needClose) {
        this.needClose = needClose;
        return this;
    }
}
