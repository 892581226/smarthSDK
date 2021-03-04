package com.xhwl.commonlib.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcelable;


/**
 * <p>网络监听  <p/>
 * Created by zw on 2017/9/5 14:38.
 */

public class NetWorkReceiver extends BroadcastReceiver {
    private static final String TAG = NetWorkReceiver.class.getSimpleName();
    private Handler mHandler;
    private Context mContext;
    private boolean NetAvailable;
    private NetWorkConnectListener mNetWorkConnectListener;

    public NetWorkReceiver() {
    }

    public NetWorkReceiver(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        //wifi开关监听
        isOpenWifi(intent);
        //是否连接wifi
        isConnectionWifi(intent);
        //监听网络连接设置
        isConnection(intent, context);
    }


    private void isOpenWifi(Intent intent) {
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                //Wifi关闭
                case WifiManager.WIFI_STATE_DISABLED:
                    Logger.e(TAG, "wifiState:wifi关闭！");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Logger.e(TAG, "wifiState:wifi打开！");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 连接有用的wifi（有效无线路由）
     * WifiManager.WIFI_STATE_DISABLING与WIFI_STATE_DISABLED的时候，根本不会接到这个广播
     */
    private void isConnectionWifi(Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                //wifi连接
                if (isConnected) {
                    Logger.e(TAG, "isConnected:isWifi:true");
                }
            }
        }
    }

    /**
     * 监听网络连接的设置，包括wifi和移动数据的打开和关闭。(推荐)
     * 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
     */
    private void isConnection(Intent intent, Context context) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) {

                // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        Logger.e(TAG, "当前WiFi连接可用 ");
                        mNetWorkConnectListener.netWorkConnectListener(NetworkUtil.WIFI_OK);
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        mNetWorkConnectListener.netWorkConnectListener(NetworkUtil.MOBILE_OK);
                        Logger.e(TAG, "当前移动网络连接可用 ");
                    }
                    NetAvailable = true;
                } else {
                    Logger.e(TAG, "当前没有网络连接呢，请确保你已经打开网络 ");
                    NetAvailable = false;
                    if (mNetWorkConnectListener != null) {
                        mNetWorkConnectListener.netWorkConnectListener(NetworkUtil.NONE_OK);
                    }
                }
                Logger.d(TAG, "TypeName：" + activeNetwork.getTypeName());
                Logger.d(TAG, "SubtypeName：" + activeNetwork.getSubtypeName());
                Logger.d(TAG, "State：" + activeNetwork.getState());
                Logger.d(TAG, "DetailedState："
                        + activeNetwork.getDetailedState().name());
                Logger.d(TAG, "ExtraInfo：" + activeNetwork.getExtraInfo());
                Logger.d(TAG, "Type：" + activeNetwork.getType());

            } else {   // not connected to the internet
                Logger.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                NetAvailable = false;
                if (mNetWorkConnectListener != null) {
                    mNetWorkConnectListener.netWorkConnectListener(NetworkUtil.NONE_OK);
                }
            }
        }
    }

    public boolean isNetAvailable() {
        return NetAvailable;
    }

    public void setNetAvailable(boolean netAvailable) {
        NetAvailable = netAvailable;
    }

    public interface NetWorkConnectListener {
        void netWorkConnectListener(int netWorkStatus);
    }

    public NetWorkConnectListener getNetWorkConnectListener() {
        return mNetWorkConnectListener;
    }

    public void setNetWorkConnectListener(NetWorkConnectListener netWorkConnectListener) {
        mNetWorkConnectListener = netWorkConnectListener;
    }
}