package com.xhwl.commonlib.http.retrofit;

import android.os.Build;
import android.util.Log;

import com.example.smarthome.BuildConfig;
import com.xhwl.commonlib.application.MyAPP;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.SPUtils;

import java.io.IOException;

/*import cn.jpush.android.api.JPushInterface;*/
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * created by long on 2019/11/4
 */
public class HeaderInterceptor implements Interceptor {

    private String TAG = "HeaderInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {

//        String clientCode = SPUtils.get(MyAPP.getIns(), SpConstant.REGISTRATION_ID,"");
       // String clientCode = JPushInterface.getRegistrationID(MyAPP.getIns());
        Request.Builder builder = chain.request().newBuilder();

        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
      //  builder.addHeader("clientCode", clientCode);
        builder.addHeader("clientType", "1");
        builder.addHeader("modelType", Build.MANUFACTURER + "_" + Build.MODEL);
        builder.addHeader("appVersion", BuildConfig.VERSION_NAME);
        builder.addHeader("systemVersion", Build.VERSION.RELEASE);
        builder.addHeader("serverVersion", BuildConfig.SERVER_VERSION);

     /*   Log.w(TAG, "clientCode: " + clientCode
                + "\nmodelType：" + Build.MANUFACTURER + "_" + Build.MODEL
                + "\nappVersion：" + BuildConfig.VERSION_NAME
                + "\nsystemVersion：" + Build.VERSION.RELEASE + "_" + Build.MODEL
                + "\nserverVersion：" + BuildConfig.SERVER_VERSION);*/

        return chain.proceed(builder.build());
    }
}
