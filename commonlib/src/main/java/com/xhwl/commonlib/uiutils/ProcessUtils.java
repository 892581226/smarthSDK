package com.xhwl.commonlib.uiutils;

import android.app.ActivityManager;
import android.content.Context;

import com.xhwl.commonlib.application.MyAPP;

import java.util.List;

/**
 * Project Name Estate
 * <p>   判断运行中的进程    </p>
 * Created by xiaowu on 2018/5/9 0009.
 */

public class ProcessUtils {
    //判断某个服务是否正在运行的方法
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(200);
        // List<RunningAppProcessInfo> apps = myAM.getRunningAppProcesses();

        if (myList.size() <= 0) {
            return false;
        }

        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }

        return isWork;
    }

    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public static boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) MyAPP.getIns().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = MyAPP.getIns().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
