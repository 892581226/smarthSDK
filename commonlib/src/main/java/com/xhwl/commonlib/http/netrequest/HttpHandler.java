package com.xhwl.commonlib.http.netrequest;

import android.content.Context;
import android.text.TextUtils;

import com.example.smarthome.R;
import com.xhwl.commonlib.application.MyAPP;
import com.xhwl.commonlib.http.resp.BaseResult;
import com.xhwl.commonlib.http.resp.ServerTip;
import com.xhwl.commonlib.uiutils.JsonUtils;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 标准接口的处理
 * <p>
 * 1. onFailure()方法删除 SocketTimeoutException 的情况.
 */
public abstract class HttpHandler<T> implements CallBack, Callback {

    private static final String TAG = HttpHandler.class.getSimpleName();

    protected Context mAppContext;

    protected Type mGsonType;

    protected ParseType mParseType = ParseType.object;    // data解析类型  是数组还是object

    protected Class<T> entityClass;   //T.class 泛型的class类型  用于gson解析\

    public HttpHandler() {
        this.mAppContext = MyAPP.getIns();
        try {
            entityClass = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {
            e.printStackTrace();
            entityClass = (Class<T>) Object.class;
        }
    }

    @Override
    public final void onFailure(final IOException e) {
        LogUtils.d(TAG, "onFailure " + e.toString());
        if (e instanceof UnknownHostException || e instanceof SocketException) {
            onFailureOnUIThread(new ServerTip(ErrorType.E_LOCAL, "网络连接失败", false, ""));
        } else {
            onFailureOnUIThread(new ServerTip(ErrorType.E_IO_EXCEPTION, "服务器地址有误，请检查", false, ""));
        }
    }

    @Override
    public final void onFinish(String respBodyStr, int code) {
        if (code == 200) {
            //请求码成功
//                final String httpUrl = response.request().url().toString();
//                LogUtils.w(TAG, "respBodyStr    " + httpUrl + "\r\n :" + respBodyStr);
            if (!TextUtils.isEmpty(respBodyStr)) {
                parseResult(respBodyStr);
            } else {
                onFailureOnUIThread(new ServerTip(ErrorType.E_NETWORK, mAppContext.getString(R.string.http_error_msg), false, ""));
            }
        } else {
            onFailureOnUIThread(new ServerTip(ErrorType.E_NETWORK, mAppContext.getString(R.string.http_error_msg), false, ""));
        }
    }

    @Override
    public void onError(Exception e) {
        onFailureOnUIThread(new ServerTip(ErrorType.E_NETWORK, mAppContext.getString(R.string.http_request_error), false, ""));
    }


    /**
     * 解析请求结果
     *
     * @param respBodyStr
     */
    protected void parseResult(String respBodyStr) {
        try {
            BaseResult resp = JsonUtils.parseObject(respBodyStr, BaseResult.class);
            if (resp != null) {
                if (resp.errorCode() == ErrorType.E_OK) {
                    //请求成功
                    //后台没有返回data类型
                    if (resp.result == null) {
                        onSuccessOnUIThread(resp, null);
                    } else {
                        T data = JsonUtils.parseObject(resp.result, entityClass);
                        if (data != null) {
                            onSuccessOnUIThread(resp, data);
                        } else {
                            onFailureOnUIThread(new ServerTip(ErrorType.E_JSON_PARSE, mAppContext.getString(R.string.json_parse_error), false, ""));
                        }
                    }
                } else {
                    onFailureOnUIThread(resp);
                }
            } else {
                onFailureOnUIThread(new ServerTip(ErrorType.E_JSON_PARSE, mAppContext.getString(R.string.json_parse_error), false, ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            onFailureOnUIThread(new ServerTip(ErrorType.E_JSON_PARSE, mAppContext.getString(R.string.json_parse_error), false, ""));
        }
    }

    private final void onSuccessOnUIThread(final ServerTip serverTip, final T t) {
        MyAPP.runUiThread(() -> {
            onSuccess(serverTip, t);
            onFinish();
        });
    }

    private final void onFailureOnUIThread(final ServerTip serverTip) {
        MyAPP.runUiThread(() -> {
            if (serverTip.errorCode == ErrorType.E_NEED_LOGIN
                    || serverTip.errorCode == ErrorType.E_TOKEN_EXPIRE) {
                //无Token或者被拉黑
//                MyAPP app = MyAPP.getIns();
                LogUtils.w(TAG, "logout app from token invalid");
                MyAPP.Logout(null);

               // LogUtils.e(TAG, "httphandler  token" + MyAPP.getIns().getToken());
                if (!StringUtils.isEmpty(serverTip.message)) {
                    ToastUtil.showSingleToast(serverTip.message);
                }
//                Intent intent = new Intent(mAppContext, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//清空栈
//                mAppContext.startActivity(intent);
            } else {
                // if (serverTip.errorCode == ErrorType.E_IO_EXCEPTION) {
                // EventBus.getDefault().post(new EventBusType(Constants.NET_ERROR));
                // }
                onFailure(serverTip);
            }
            onFinish();
        });
    }

    public abstract void onSuccess(ServerTip serverTip, T t);

    /**
     * 需要对错误进行特殊处理 请覆写这个方法
     *
     * @param serverTip
     */
    public void onFailure(ServerTip serverTip) {
        LogUtils.e(TAG, "Code:" + serverTip.errorCode() + "  Msg:" + serverTip.message());
        if (ErrorType.isNeedTipToUser(serverTip.errorCode())) {
            ToastUtil.showCenterToast(serverTip.message());
        } else if (ErrorType.E_JSON_PARSE == serverTip.errorCode()) {
            //112表明当前应用是最新版本
            ToastUtil.showDebug("数据解析错误");
        } else if (ErrorType.E_IO_EXCEPTION == serverTip.errorCode()) {
            //112表明当前应用是最新版本
            ToastUtil.showDebug("网络连接失败，请稍后再试");
        }
    }

    /**
     * 网络请求开始
     */
    public void onStart() {

    }

    /**
     * 网络请求结束
     */
    public void onFinish() {

    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
                final String httpUrl = response.request().url().toString();
                LogUtils.w(TAG, "respBodyStr    " + httpUrl);
    }

    /**
     * data 解析类型
     */
    public enum ParseType {
        object, array
    }
}
