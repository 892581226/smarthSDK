package com.xhwl.commonlib.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.multidex.MultiDex;
import android.util.Log;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveRoomConfig;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.xhwl.commonlib.base.BaseOtherActivity;
import com.xhwl.commonlib.constant.ActionConstant;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.http.Constant;
import com.xhwl.commonlib.network.NetWorkReceiver;
import com.xhwl.commonlib.network.NetworkUtil;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.ShowToast;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.qcloud.CloseQCloudVideoBus;
import com.xhwl.commonlib.uiutils.qcloud.MessageObservable;
import com.xhwl.commonlib.uiutils.qcloud.StopServiceBus;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2018/2/26.MultiDexApplication
 * 初始化类
 */

public class MyAPP {

    private static final MyAPP mInstance = new MyAPP();
    private static final String TAG = "MyAPP";
    private static Context myApp;
    public static final String APP_ID = "wxc5e20b28cf976b5c";    //这个APP_ID就是注册APP的时候生成的
    public static final String APP_SECRET = "57039eb96054557def20c354f21a709c";
    public static final String QQAPP_ID = "1106374982";//这是QQ的APPid

    public static String projectCode = "";//项目Code
    public static String granterPhone = "";//用户手机号
    public static String proName = "";//项目名
    public static String userName = "";
    public static int index, deviceIndex;
    public static BaseOtherActivity baseActivity;
    // 上一次点击时间
    long lastTimeMillis = 0;
    public static int mNetWorkStatus;
    public static Handler mHandler;
    private NetWorkReceiver mNetWorkReceiver;

    public static synchronized MyAPP instance() {
        return mInstance;
    }
    public  void init(Application context) {
        myApp = context;
        mHandler = new Handler(Looper.getMainLooper());
        new ShowToast(myApp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            closeAndroidPDialog();
        }
        SPUtils.spContext(context);
        setNetworkListener();
    }

    /**
     * tencent video call
     */
    private void initTencentCloud() {
        if (MsfSdkUtils.isMainProcess(myApp)) {
            ILiveSDK.getInstance().initSdk(myApp, Constant.TENCENT_CLOUD_SDKAPPID);
            // 初始化iLiveSDK房间管理模块
            ILiveRoomManager.getInstance().init(new ILiveRoomConfig<>().setRoomMsgListener(MessageObservable.getInstance()));
        }
    }

    /**
     * 去掉Android P上的弹框提醒
     */
    private static void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.contentpm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Context getIns() {
        return myApp;
    }

    public String getToken() {
        return SPUtils.get(myApp, "userToken", "");
    }

    public void setToken(String token) {
        SPUtils.put(myApp, "userToken", token);
    }

    /**
     * 获取登录设备mac地址
     *
     * @return Mac地址
     */
    public String getMacAddress() {
        WifiManager wm = (WifiManager) myApp.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wm.getConnectionInfo();
        String mac = connectionInfo.getMacAddress();
        return mac == null ? "" : mac;
    }

    private static Map<String, Activity> destoryMap = new HashMap<>();

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */
    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        if (keySet.size() > 0) {
            for (String key : keySet) {
                if (activityName.equals(key)) {
                    destoryMap.get(key).finish();
                }
            }
        }
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    //退出登录 清空数据
    public static void Logout(Context context) {
        LogUtils.w(TAG, "logout app context");
        EventBus.getDefault().post(new StopServiceBus().setStopSelf(true)
                .setDelayTime(ActionConstant.TENCENT_ISCALLING ? 2000L : 0)
        );
        EventBus.getDefault().post(new CloseQCloudVideoBus().setNeedClose(true));//通话页面再就销毁
        //MyAPP instance = myApp.getIns();
        SPUtils.put(myApp, "result", "");
        SPUtils.put(myApp, "sysUser", "");
        SPUtils.put(myApp, "userName", "");
        SPUtils.put(myApp, "userTelephone", "");
        SPUtils.put(myApp, "userSex", "");
        SPUtils.put(myApp, "userSysAccount", "");
        SPUtils.put(myApp, "userToken", "");
        SPUtils.put(myApp, "userWechatNickName", "");
        SPUtils.put(myApp, "userQQNickName", "");
        SPUtils.put(myApp, "userNickName", "");
        SPUtils.put(myApp, "userImageUrl", "");
        SPUtils.put(myApp, "userWeiboNickName", "");
        SPUtils.put(myApp, "proCode", "");
        SPUtils.put(myApp, "proName", "");
        SPUtils.put(myApp, "nodeType", "");
        SPUtils.put(myApp, "nodeID", "");
        SPUtils.put(myApp, "proID", "");
        SPUtils.put(myApp, "entranceCode", "");
        SPUtils.put(myApp, "imageUrl", "");
        SPUtils.put(myApp, "nickName", "");
        SPUtils.put(myApp, "id", "");
        SPUtils.put(myApp, "sex", "");
        SPUtils.put(myApp, "name", "");
        SPUtils.put(myApp, "sysUserName", "");
        SPUtils.put(myApp, "weiboNickName", "");
        SPUtils.put(myApp, "weChatNickName", "");
        SPUtils.put(myApp, "qqnickName", "");
        SPUtils.put(myApp, "token", "");
        SPUtils.put(myApp, "roomList", "");
        SPUtils.put(myApp, "exent", "");
        SPUtils.put(myApp, "term", "");
        SPUtils.put(myApp, "description", "");
        SPUtils.put(myApp, "Hum", "");
        SPUtils.put(myApp, "Mood", "");
        SPUtils.put(myApp, "Tmp", "");
        SPUtils.put(myApp, "Uv", "");
        SPUtils.put(myApp, "Code", "");
        SPUtils.put(myApp, "userType", "");
        SPUtils.put(myApp, "bingPhone", "");
        SPUtils.put(myApp, "isLogin", false);
        SPUtils.put(myApp, "matchDoorVoDoorList", "");
        SPUtils.put(myApp, "isWorkstation", true);
        SPUtils.put(myApp, "doorListResult", "");
        //云对讲
        SPUtils.put(myApp, "roomId", "");
        SPUtils.put(myApp, "roomCode", "");
        SPUtils.put(myApp, "roomName", "");
        SPUtils.put(myApp, "webType", "");
        SPUtils.put(myApp, SpConstant.SP_GUEST, false);
        SPUtils.put(myApp, SpConstant.SP_PROJECTLIST, "");
        SPUtils.put(myApp, SpConstant.TRACKLIST, "");
        SPUtils.put(myApp, SpConstant.SP_USER_NAME, "");
        SPUtils.put(myApp, SpConstant.SP_USER_PHONE, "");

         //MyAPP.getIns().setExit(true);
  /*      JPushInterface.setAlias(MyAPP.getIns(), 1, "");
        //删除别名
        JPushInterface.deleteAlias(MyAPP.getIns(), 1);*/
    }


    private String CurrentBindid, CurrentBindpassword, snid;

    public String getCurrentBindid() {
        return CurrentBindid;
    }

    public void setCurrentBindid(String currentBindid) {
        CurrentBindid = currentBindid;
    }

    public String getCurrentBindpassword() {
        return CurrentBindpassword;
    }

    public void setCurrentBindpassword(String currentBindpassword) {
        CurrentBindpassword = currentBindpassword;
    }

    public String getSnid() {
        return snid;
    }

    public void setSnid(String snid) {
        this.snid = snid;
    }

    //是否退出应用
    public boolean isExit() {
        return SPUtils.get(myApp, "isExit", true);
    }

    public void setExit(boolean exit) {
        SPUtils.put(myApp, "isExit", exit);
    }

    public void setLastTouchTime() {
        long currentTimeMillis = System.currentTimeMillis();
        if (lastTimeMillis == 0) {
            lastTimeMillis = currentTimeMillis;
            //  startVerify();
        } else {
            long temp = currentTimeMillis - lastTimeMillis;
            if (temp < 1000 * 60 * 60 * 2) {
                // stopVerify(); 指纹识别
                // startVerify();
            }
        }
    }

    /**
     * run code on UI thread
     *
     * @param runnable
     */
    public static void runUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static void runUiThread(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }

    private  void setNetworkListener() {
        if (mNetWorkReceiver == null) {
            mNetWorkReceiver = new NetWorkReceiver();
        }
        mNetWorkReceiver.setNetWorkConnectListener(mNetWorkConnectListener);
        IntentFilter intentNetFilter = new IntentFilter();
        intentNetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentNetFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentNetFilter.addAction("android.net.wifi.STATE_CHANGE");
        myApp.registerReceiver(mNetWorkReceiver, intentNetFilter);
    }

    /**
     * 网络状态监听
     */
    private NetWorkReceiver.NetWorkConnectListener mNetWorkConnectListener = netWorkStatus -> {
        MyAPP.mNetWorkStatus = netWorkStatus;
        LogUtils.w(TAG, "netWorkConnectListener-------isConnect = " + netWorkStatus);
        if (netWorkStatus == NetworkUtil.NONE_OK) {
            ToastUtil.showSingleToast("网络不可用，请检查网络");
        } else {
            /*if (!MyAPP.getIns().isExit()) {
                // 将写入数据实时引擎的数据用户在线状态数据移除
            }*/
        }
    };


    /**
     * 根据图片名称获取资源下的图片
     * @param imageName
     * @param defType
     * @param defIcon
     * @return
     */
    public int getResource(String imageName, String defType, int defIcon) {
        try {
            Context ctx = myApp;
            //如果没有在"mipmap"下找到imageName,将会返回0
            int resId = myApp.getResources().getIdentifier(imageName, defType, ctx.getPackageName());
            if (resId == 0) {
                resId = defIcon;
            }
            return resId;
        } catch (Exception e) {
            Log.w(TAG, "getResource: " + e.getMessage());
        }
        return defIcon;
    }
}
