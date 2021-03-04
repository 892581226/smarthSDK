package com.xhwl.commonlib.http.netrequest;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.smarthome.BuildConfig;
import com.xhwl.commonlib.application.MyAPP;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.http.Constant;
import com.xhwl.commonlib.http.resp.ServerTip;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*import cn.jpush.android.api.JPushInterface;*/
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by zw on 2018/08/08.
 * 处理网络请求
 */
public class HttpUtils {

    private static final String TAG = "HttpUtils";
    private static String serverUrl = "/ssh/v1/appBusiness/infomationFeedback";

    /**
     * base url
     * http://seven.xy-mind.com:8006/ssh/
     *
     * @param params
     * @param callback
     */
    public static void HttpGET(String params, HttpHandler callback) {
        HttpGETRequest(Constant.HOST2 + Constant.SERVERNAME + params, callback);
    }

    public static void HttpsGET(String params, HttpHandler callback) {
        HttpGETRequest(params, callback);
    }

    /**
     * base url
     * http://seven.xy-mind.com:8006/ssh/
     *
     * @param url
     * @param params
     * @param callback
     */
    public static void HttpPOST(String url, HttpParams params, HttpHandler callback) {
        HttpPOSTRequest(Constant.HOST2 + Constant.SERVERNAME + url, params, callback);
    }

    public static void Post(String url, HttpParams params, HttpHandler callback) {
        HttpPOSTRequest(url, params, callback);
    }
    /**
     * 智能家居 base url
     * http://58.250.164.144:18080/
     *
     * @param url
     * @param params
     * @param callback
     */
    public static void HttpPOST(String host, String url, HttpParams params, HttpHandler callback) {
        HttpPOSTRequest(host + url, params, callback);
    }

    public static void HttpGET(String host, String params, HttpHandler callback) {
        HttpGETReq(host + params, callback);
    }

    public static void HttpPOSTRequest(final String path, final HttpParams params, final HttpHandler callBack) {
        new Thread() {
            @Override
            public void run() {
                try {

                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST"); //默认请求 就是get  要大写
                    conn.setConnectTimeout(5000);
                    LogUtils.w(TAG, "request form param =================\r\n");
                    String formData = handleGetURL(params);
                    conn.setRequestProperty("Content-Length", formData.length() + "");
                    if (path.contains(serverUrl)) {
                        addFeedbackHeader(conn);
                    } else {
                        addHeader(conn);
                    }
                    conn.setDoOutput(true);//设置一个标记 允许输出
                    conn.getOutputStream().write(formData.getBytes());
                    int code = conn.getResponseCode(); //200  代表获取服务器资源全部成功  206请求部分资源
                    InputStream inputStream = conn.getInputStream();
                    String content = StreamTools.readStream(inputStream);
                    LogUtils.i(TAG, "=================\r\n"
                            + content +
                            "=================\r\n");
                    if (callBack != null) {
                        callBack.onFinish(content, code);
                    }
                } catch (IOException e) {
                    if (callBack != null) {
                        callBack.onFailure(e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callBack != null) {
                        callBack.onError(e);
                    }
                }
            }
        }.start();
    }

    private static void addHeader(HttpURLConnection conn) {
//        String clientCode = SPUtils.get(MyAPP.getIns(), SpConstant.REGISTRATION_ID,"");
       // String clientCode = JPushInterface.getRegistrationID(MyAPP.getIns());
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      //  conn.setRequestProperty("clientCode", clientCode);
        conn.setRequestProperty("clientType", "1");
        conn.setRequestProperty("modelType", Build.MANUFACTURER + "_" + Build.MODEL);
        conn.setRequestProperty("appVersion", BuildConfig.VERSION_NAME);
        conn.setRequestProperty("systemVersion", Build.VERSION.RELEASE);
        conn.setRequestProperty("serverVersion", BuildConfig.SERVER_VERSION);

       /* Log.w(TAG, "clientCode: " + clientCode
                + "\nmodelType：" + Build.MANUFACTURER + "_" + Build.MODEL
                + "\nappVersion：" + BuildConfig.VERSION_NAME
                + "\nsystemVersion：" + Build.VERSION.RELEASE + "_" + Build.MODEL
                + "\nserverVersion：" + BuildConfig.SERVER_VERSION);*/
    }

    private static void addFeedbackHeader(HttpURLConnection conn) {
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("phoneSystemVersion", Build.VERSION.RELEASE);
        conn.setRequestProperty("phoneType", Build.MANUFACTURER + "_" + Build.MODEL);
        conn.setRequestProperty("appVersion", BuildConfig.VERSION_NAME);
        conn.setRequestProperty("serverVersion", BuildConfig.SERVER_VERSION);

        Log.w(TAG, "phoneSystemVersion: " + Build.VERSION.RELEASE
                + "\nphoneType：" + Build.MANUFACTURER + "_" + Build.MODEL
                + "\nappVersion：" + BuildConfig.VERSION_NAME
                + "\nserverVersion：" + BuildConfig.SERVER_VERSION);
    }

    // 智能家居
    public static void HttpGETReq(final String path, final HttpHandler callBack) {

        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET"); //默认请求 就是get  要大写
                    conn.setConnectTimeout(5000);

                    int code = conn.getResponseCode(); //200  代表获取服务器资源全部成功  206请求部分资源
                    InputStream inputStream = conn.getInputStream();
                    String content = StreamTools.readStream(inputStream);
                    LogUtils.i(TAG, "===================\r\n"
                            + content +
                            "====================\r\n");
                    if (callBack != null) {
                        callBack.onFinish(content, code);
                    }
                } catch (IOException e) {
                    LogUtils.e(TAG,"error" + e.getMessage());
                    if (callBack != null) {
                        callBack.onFailure(e);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (callBack != null) {
                        callBack.onError(ex);
                    }
                }
            }
        }.start();
    }

    public static void HttpGETRequest(final String path, final HttpHandler callBack) {

        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET"); //默认请求 就是get  要大写
                    conn.setConnectTimeout(5000);

                    addHeader(conn);

                    int code = conn.getResponseCode(); //200  代表获取服务器资源全部成功  206请求部分资源
                    InputStream inputStream = conn.getInputStream();
                    String content = StreamTools.readStream(inputStream);
                    LogUtils.i(TAG, "==================\r\n"
                            + content +
                            "=====================\r\n");
                    if (callBack != null) {
                        callBack.onFinish(content, code);
                    }
                } catch (IOException e) {
                    LogUtils.e(TAG, e.getMessage());
                    if (callBack != null) {
                        callBack.onFailure(e);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (callBack != null) {
                        callBack.onError(ex);
                    }
                }
            }
        }.start();
    }

    /**
     * 处理POST请求参数
     */
    public static String handleGetURL(HttpParams httpParams) {
        if (httpParams != null) {
            LogUtils.w(TAG, "request form param=============\r\n");
            StringBuilder builder = new StringBuilder();
            try {
                Map<String, String> params = httpParams.getParams();
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    String formKey = entry.getKey();
                    String formValue = entry.getValue();
                    LogUtils.w(TAG, "key="
                            + formKey + "  value=" + formValue);
                    builder.append(formKey).append("=")
                            .append(URLEncoder.encode(formValue, "utf-8")).append("&");
                }
                builder.deleteCharAt(builder.length() - 1);
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /***
     * ---------------------------- OKHttp请求------------------------
     */
    /**
     * 连接超时
     */
    private static final long CONNECT_TIMEOUT_MILLIS = 10 * 1000;

    /**
     * 读取超时
     */
    private static final long READ_TIMEOUT_MILLIS = 10 * 1000;

    /**
     * 写入超时
     */
    private static final long WRITE_TIMEOUT_MILLIS = 10 * 1000;

    // 同步请求超时
    private static final long SYNC_TIMEOUT_MILLIS = 1500;

    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * OkHttpClient实例
     */
    private static OkHttpClient client;
    private static OkHttpClient syncClient;

    private static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder().addInterceptor(new RetryInterceptor())
                    .connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).build();
        }
        return client;
    }

    //智能家居请求超时时间增加
    private  OkHttpClient getSmartClient() {
        if (client == null) {
            client = new OkHttpClient.Builder().addInterceptor(new RetryInterceptor())
                    .connectTimeout(80 * 1000, TimeUnit.MILLISECONDS)
                    .readTimeout(80*1000, TimeUnit.MILLISECONDS)
                    .writeTimeout(80*1000, TimeUnit.MILLISECONDS).build();
        }
        return client;
    }

    private static OkHttpClient getSyncClient() {
        if (syncClient == null) {
            syncClient = new OkHttpClient.Builder().addInterceptor(new RetryInterceptor())
                    .connectTimeout(SYNC_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(SYNC_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(SYNC_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).build();
        }
        return syncClient;
    }

    /**
     * 默认处理
     */
    private final static HttpHandler nullHttpHandler = new HttpHandler() {
        @Override
        public void onSuccess(ServerTip serverTip, Object o) {
            LogUtils.d(TAG, o + "");
        }
    };


    /**
     * 包装Post的header参数
     *
     * @param url
     * @param params
     * @return
     */
    private static Request getPostRequest(String url, HttpParams params, Map<String, String> headers, Object tag) {
        //处理url
        url = handleurl(url);
        //获取参数
        FormBody.Builder formBody = new FormBody.Builder();
        HashMap<String, String> map = (HashMap<String, String>) params.getParams();
        Iterator iter = map.entrySet().iterator();
        LogUtils.w(TAG, "request form param==============\r\n");
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String formKey = entry.getKey().toString();
            String formValue = entry.getValue().toString();
            LogUtils.w(TAG, "key="
                    + formKey + "  value=" + formValue);
            formBody.add(formKey, formValue);
        }
        // LogUtils.i(TAG, "Request:\n" + url + "\n" + paramsJson);
        Request.Builder request = new Request.Builder().url(url).post(formBody.build()).tag(tag);
        if (headers != null) {
            Headers.Builder header = handleHeader(headers);
            request.headers(header.build());
        }
        return request.build();
    }

    @NonNull
    private static Headers.Builder handleHeader(Map<String, String> headers) {
        Headers.Builder header = new Headers.Builder();
        Iterator<Map.Entry<String, String>> entryIterator = headers.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, String> entry = entryIterator.next();
            String headerKey = entry.getKey();
            String headerValue = entry.getValue();
            LogUtils.w(TAG, "headerKey="
                    + headerKey + "  headerValue=" + headerValue);
            header.add(headerKey, headerValue);
        }
        return header;
    }


    //通用文件上传
    public static Request uploadFile(String url, HttpParams params) {
        //处理url
        url = handleurl(url);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody requestBody = null;
        Map<String, File> map = params.getMapParams();
        //遍历map中所有参数到builder
        for (String key : map.keySet()) {
            //  builder.addFormDataPart(key, map.get(key).toString());
            if (map.get(key).getName().contains(".mp4")) { // video/mpeg4, application/octet-stream
                //上传视频
                builder.addFormDataPart("files", map.get(key).getName(), RequestBody.create(MediaType.parse("video/mpeg4"), map.get(key)));
            } else {
                //上传图片
                builder.addFormDataPart("files", map.get(key).getName(), RequestBody.create(MediaType.parse("image/png"), map.get(key)));
            }
        }
        requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url).post(requestBody)//添加请求体
                .build();
        return request;
    }

    public static void uploadFileReq(String url, HttpParams params, final HttpHandler handler) {
        Request request = uploadFile(Constant.HOST2 + Constant.SERVERNAME + url, params);
        getClient().newCall(request).enqueue(handler != null ? handler : nullHttpHandler);
        notifyHandlerStart(handler);
    }

    //参数类型
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_AUDIO = MediaType.parse("audio/*");

    private static int it = 0;

    //多图上传（旧）
    public static Request uploadImg(String url, HttpParams params, Map<String, String> headers, List<File> imgUrl) {
        //处理url
        url = handleurl(url);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        HashMap<String, String> map = (HashMap<String, String>) params.getParams();
        //遍历map中所有参数到builder
        for (String key : map.keySet()) {
            builder.addFormDataPart(key, map.get(key));
        }
        for (File path : imgUrl) {
            if (url.contains("talkingBack")) {//软对讲传音频文件
                builder.addFormDataPart("audio", path.getName(), RequestBody.create(MEDIA_TYPE_AUDIO, path));
            } else {//海康云瞳传图片
                builder.addFormDataPart("name" + it++, path.getName(), RequestBody.create(MEDIA_TYPE_PNG, path));
            }
        }
        Request.Builder request = new Request.Builder()
                .url(url).post(builder.build());//添加请求体

        if (headers != null) {
            Headers.Builder header = handleHeader(headers);
            request.headers(header.build());
        }
        return request.build();
    }

    public static void postUploadImg(String url, HttpParams params, List<File> imgUrl, final HttpHandler handler) {
        Request request = uploadImg(url, params, null, imgUrl);
        getClient().newCall(request).enqueue(handler != null ? handler : nullHttpHandler);
        notifyHandlerStart(handler);
    }

    public static void postUploadImg(String url, HttpParams params, List<File> imgUrl, Map<String, String> headers, final HttpHandler handler) {
        Request request = uploadImg(url, params, headers, imgUrl);
        getClient().newCall(request).enqueue(handler != null ? handler : nullHttpHandler);
        notifyHandlerStart(handler);
    }

    /**
     * 包装get的参数
     *
     * @param url
     * @return
     */
    private static Request getRequest(String url, Object tag) {
        //处理url
        url = handleurl(url);
        // LogUtils.i(TAG, "Request:\n" + url + "\n" + paramsJson);
        Request request = new Request.Builder().url(url).get().tag(tag).build();
        return request;
    }

    public static void get(String url, final HttpHandler handler) {
        Request request = getRequest(url, null);
        getClient().newCall(request).enqueue(handler != null ? handler : nullHttpHandler);
        notifyHandlerStart(handler);
    }

    /**
     * 封装的异步Post请求
     *
     * @param url     URL的全路径或子路径(即: 除去url公共前缀之后的那部分路径)
     * @param params
     * @param handler
     */
    public static void post(String url, HttpParams params, final HttpHandler handler) {
        Request request = getPostRequest(url, params, null, null);
        getClient().newCall(request).enqueue(handler != null ? handler : nullHttpHandler);
        notifyHandlerStart(handler);
    }

    /**
     * 封装的带header异步Post请求
     *
     * @param url     URL的全路径或子路径(即: 除去url公共前缀之后的那部分路径)
     * @param params
     * @param headers
     * @param handler
     */
    public static void post(String url, HttpParams params, Map<String, String> headers, final HttpHandler handler) {
        Request request = getPostRequest(url, params, headers, null);
        getClient().newCall(request).enqueue(handler != null ? handler : nullHttpHandler);
        notifyHandlerStart(handler);
    }

    /**
     * 同步post请求
     *
     * @param url
     * @param params
     */
    public static String postSync(String url, final HttpParams params) {
        Request request = getPostRequest(url, params, null, null);
        try {
            Response response = getSyncClient().newCall(request).execute();
            final String result = response.body().string();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCompleteUrl(String addurl) {
        return Constant.HOST2 + Constant.SERVERNAME + addurl;
    }

    public static String handleurl(String url) {
        //判断是否为完整地址，如果不是，自动指向app服务器并补全
        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
            url = getCompleteUrl(url);
        }
        return url;
    }

    /**
     * 通知handler请求已经开始
     *
     * @param handler
     */
    static void notifyHandlerStart(final HttpHandler handler) {
        if (handler != null) {
            MyAPP.runUiThread(handler::onStart);
        }
    }

    public static void getNGBRet(String url, String ws_url, int num, int type, Callback handler) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("WS_URL", ws_url)
                .addHeader("WS_RETIP_NUM", String.valueOf(num))
                .addHeader("WS_URL_TYPE", String.valueOf(type))
                .build();
        getClient().newCall(request).enqueue(handler != null ? handler : nullHttpHandler);
    }

    /**
     * -1:未知异常，0：无网络，1：无须认证的网络（可以上网），2：须要认证的网络
     *
     * @param url
     * @return
     */
    public static int getReqForDGWifi(String url) {
        URL httpUrl = null;
        HttpURLConnection conn = null;
        int result = -1;
        try {
            httpUrl = new URL(url);
            conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setConnectTimeout(2 * 1000);
            conn.setReadTimeout(2 * 1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            LogUtils.e(TAG, conn.getResponseMessage() + "");
            int code = conn.getResponseCode();
            if (code >= 300 && code < 400) {//发生重定向
                String agr = conn.getHeaderField("Location");//获取重定向后的地址
                if (!StringUtils.isEmpty(agr) && agr.contains("wlanacname") && agr.contains("ssid") && agr.contains("wlanuserip")) {//是DGFree，需要认证
                    parseUrl(agr);
                    result = 2;
                }
            } else if (code >= 200 && code < 300) {//可以上网，无须认证
                result = 1;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            result = 0;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    /**
     * 解析出url参数并保存
     *
     * @param url
     */
    public static void parseUrl(String url) {
        String[] agrArray = url.trim().split("\\?");
        String[] mAgr = agrArray[1].split("&");

        for (String strSplit : mAgr) {
            String[] arrSplit;
            arrSplit = strSplit.split("=");
            //解析出键值
            if (arrSplit.length > 1) {
                //正确解析
                SPUtils.putForAgr(arrSplit[0], arrSplit[1]);
            } else {
                if (arrSplit[0] != "") {
                    //只有参数没有值，不加入
                }
            }
        }
    }

    /**
     * @param url
     * @param ws_url
     * @param num
     * @return
     */
    public static String getNGBRet(String url, String ws_url, int num) {
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setConnectTimeout(2 * 1000);
            conn.setReadTimeout(2 * 1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");

            conn.addRequestProperty("WS_URL", ws_url);
            conn.addRequestProperty("WS_RETIP_NUM", String.valueOf(num));
            conn.addRequestProperty("WS_URL_TYPE", "3");

            conn.connect();
            InputStream input = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content;
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理GET请求参数
     */
    public static String handleOkHttpGetURL(HttpParams httpParams) {
        if (httpParams != null) {
            StringBuilder builder = new StringBuilder();
            try {
                Map<String, String> params = httpParams.getParams();
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    builder.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue(), "utf-8")).append("&");
                }
                builder.deleteCharAt(builder.length() - 1);
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 重试策略
     */
    public static class RetryInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Response response = chain.proceed(original);
            int tryCount = 0;
            while (!response.isSuccessful() && tryCount < 2) {
                Log.d(TAG, "Retry " + tryCount++);
                response = chain.proceed(original);
            }
            return response;
        }
    }


    /**
     * RequestBody--json数据提交
     *
     * @param url     URL的全路径或子路径(即: 除去url公共前缀之后的那部分路径)
     *
     */
    public static void postJson(String url, String json,HttpHandler handler) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new RetryInterceptor())
                .connectTimeout(80 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(80*1000, TimeUnit.MILLISECONDS)
                .writeTimeout(80*1000, TimeUnit.MILLISECONDS).build();
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type","application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(handler != null ? handler : nullHttpHandler);

    }
}
