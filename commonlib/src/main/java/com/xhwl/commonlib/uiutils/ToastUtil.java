package com.xhwl.commonlib.uiutils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.example.smarthome.BuildConfig;
import com.xhwl.commonlib.application.MyAPP;


public class ToastUtil {

    // null
    private static Toast mToast = null;

    public static void show(Context context, String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        if (context != null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.setText(text);
            //mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.show();
        }
    }

    public static void show(Context context, int resId) {
        if (context != null) {
            context = context.getApplicationContext();
            show(context, context.getString(resId));
        }
    }

    public static void showDebug(String msg) {
        if (BuildConfig.ENV.equals("dev")) {
            Context appContext = MyAPP.getIns();
            Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void show(String msg) {
        Context appContext = MyAPP.getIns();
        Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleToast(String msg) {
        Context appContext = MyAPP.getIns();
        if (mToast == null) {
            mToast = Toast.makeText(appContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static void showCenter(String msg) {
        Context appContext = MyAPP.getIns();
        mToast =  Toast.makeText(appContext, msg, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }


    public static void showCenter(Context context,String msg) {
        mToast =  Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    //toast居中显示
    public static void showCenterToast(String msg){
        showCenter(msg);
//        MyAPP appContext = MyAPP.getIns();
//        if (mToast == null) {
//            mToast = Toast.makeText(appContext, msg, Toast.LENGTH_LONG);
//        } else {
//            mToast.setText(msg);
//        }
    //        mToast.setGravity(Gravity.CENTER, 0, 0);
//        mToast.show();

    }

    public static void showError(Context context, int error) {
//        if (error == HttpCode.E_TOKEN ) {
//            show(context, R.string.login_failed);
//            MainApplication.get().setToken("");
//            MainApplication.get().setUser(null);
//            Intent intent = new Intent(context, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//清空栈
//            context.startActivity(intent);
//            return;
//        } //else if(error == HttpCode.E_MISS_PARAM){//即缺少参数
//            showMiddle(context, R.string.net_error);
//            return;
//        }     else if(error == HttpCode.E_INVAL_PARAM){
//            showMiddle(context, R.string.param_error);
//            return;
//        } else if(error == HttpCode.E_DATABASE){
//            showMiddle(context, R.string.data_error);
//            return;
//        } else if(error == HttpCode.E_INNER){
//            showMiddle(context, R.string.inner_error);
//            return;
//        } else if(error == HttpCode.E_CODE){
//            showMiddle(context, R.string.code_error);
//            return;
//        } else if(error == HttpCode.E_GET_CODE){
//            showMiddle(context, R.string.get_code_error);
//            return;
//        } else if(error == HttpCode.E_USED_PHONE){
//            showMiddle(context, R.string.phone_error);
//            return;
//        } else if(error < HttpCode.E_SERVER){
//            showMiddle(context, R.string.server_error);
//            return;
//        }
    }
}
