package com.xhwl.commonlib.uiutils;

/**
 * 快速点击
 * <click>添加溢出时间</click>
 * Created by xiaowu on 18-5-17.
 */
public class ClickUtil {
    private static long lastClickTime;//   溢出时间

    /**
     * 防止重复点击
     *
     * @return 是否快速点击
     */
    public static boolean isFastDoubleClick(int delayTime) {
        long currentTime = System.currentTimeMillis();
        long timeOffer = currentTime - lastClickTime;
        if (timeOffer > 0 && timeOffer < delayTime) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }
}
