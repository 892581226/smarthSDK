package com.xhwl.commonlib.http.retrofit;



import com.example.smarthome.BuildConfig;
import com.xhwl.commonlib.http.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiRetrofit {

    private final String BASE_SERVER_URL = BuildConfig.SERVER_URL +  Constant.SERVERNAME;
    private volatile static ApiRetrofit apiRetrofit;
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;

    private String TAG = "ApiRetrofit";
    private LogInterceptor interceptor;

    private ApiRetrofit(){
        interceptor = new LogInterceptor();
        okHttpClient = new OkHttpClient.Builder()
                //添加log拦截器
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                //支持RxJava2
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

    }

    public static ApiRetrofit getInstance() {
        if (apiRetrofit == null) {
            synchronized (ApiRetrofit.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit();
                }
            }
        }
        return apiRetrofit;
    }

    public <T> T createApiServer(Class<T> cls){
        return retrofit.create(cls);
    }
}
