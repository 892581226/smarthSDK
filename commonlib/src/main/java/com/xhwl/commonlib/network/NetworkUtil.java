package com.xhwl.commonlib.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <p>网络检测  <p/>
 * Created by zw on 2017/9/5 14:38.
 */
public class NetworkUtil {
    /**
     * 没有网络
     */
    public static final int NONETWORK = 0;
    /**
     * 当前是wifi连接
     */
    public static final int WIFI = 1;
    /**
     * 不是wifi连接
     */
    public static final int NOWIFI = 2;

    /**
     * 当前WIFI可用
     */
    public static final int WIFI_OK = 98;

    /**
     * 当前移动网络可用
     */
    public static final int MOBILE_OK = 99;

    /**
     * 当前没有网络连接
     */
    public static final int NONE_OK = 100;


    /**
     * 检测当前网络的类型 是否是wifi
     *
     * @param context
     * @return
     */
    public static int checkedNetWorkType(Context context) {
        if (!checkedNetWork(context)) {
            return NONETWORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()) {
            return WIFI;
        } else {
            return NOWIFI;
        }
    }


    /**
     * 检查是否连接网络
     *
     * @param context
     * @return
     */
    public static boolean checkedNetWork(Context context) {
        // 1.获得连接设备管理器
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        /**
         * 获取网络连接对象
         */
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }


}
