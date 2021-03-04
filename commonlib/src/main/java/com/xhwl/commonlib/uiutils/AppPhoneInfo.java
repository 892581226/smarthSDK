package com.xhwl.commonlib.uiutils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;
import java.util.List;


public class AppPhoneInfo {
    /**
     * 手机屏幕宽
     *
     * @param activity:
     * @return int
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;

    }

    /**
     * 手机屏幕高
     *
     * @param activity:
     * @return int
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeigh = dm.heightPixels;
        return screenHeigh;

    }

    /**
     * 手机屏幕宽
     *
     * @param context:
     * @return int
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;

    }

    /**
     * 手机屏幕高
     *
     * @param context:
     * @return int
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;

    }

    /**
     * 获取手机状态栏 高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Activity context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
            Rect frame = new Rect();
            context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;
        }
        return statusBarHeight;
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context:
     * @param packageName：应用包名
     * @return boolean
     */
    private boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (null != packageManager) {
            List<PackageInfo> infoList = packageManager
                    .getInstalledPackages(PackageManager.GET_SERVICES);
            if (null != infoList && infoList.size() > 0) {
                for (PackageInfo info : infoList) {
                    if (packageName.equals(info.packageName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 手机品牌
     */
    public static String getPhoneBrand() {
        return Build.BRAND;// 手机品牌
    }
    /**
     * 手机系统定制厂商->刷过系统显示当前系统品牌风格
     */
    public static String MANUFACTURER() {
        return Build.MANUFACTURER;// 手机系统定制厂商
    }

    /**
     * 设备名称
     */
    public static String getPhoneModel() {
        return Build.MODEL;// 设备名称
    }

    /**
     * 手机系统版本
     */
    public static String getPhoneVersionRelease() {
        return Build.VERSION.RELEASE;
    }
}
