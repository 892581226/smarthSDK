package com.example.smarthome.iot.impl.device.negativecontrol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceInfoVo;
import com.example.smarthome.iot.entry.SmartControlVo;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.DeviceBean;
import com.example.smarthome.iot.impl.device.negativecontrol.activity.NegativeControlActivity_Three;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DeviceControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class YesOrNoBindDialog extends Dialog implements View.OnClickListener {

    private Context context;
    DeviceBean.DeviceData deviceData;
    String deviceId1,deviceId2;
    int position1,position2;
    String roomName;


    public YesOrNoBindCallBack yesOrNoBindCallBack;
    private ProgressBar progress_bar;

    public YesOrNoBindDialog(@NonNull Context context, int themeResId, DeviceBean.DeviceData deviceData,
                             String deviceId1,int position1,String deviceId2,int position2,String roomName) {
        super(context, themeResId);
        this.context = context;
        this.deviceData=deviceData;
        this.deviceId1=deviceId1;
        this.deviceId2=deviceId2;
        this.position1=position1;
        this.position2=position2;
        this.roomName=roomName;
    }

    public void setYesOrNoBindCallBack(YesOrNoBindCallBack yesOrNoBindCallBack) {
        this.yesOrNoBindCallBack = yesOrNoBindCallBack;
    }

    public interface YesOrNoBindCallBack{
        void CallBack();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.turnout_no){
            dismiss();
        }else if (v.getId()==R.id.turnout_yes){
            //绑定
            if (roomName.equalsIgnoreCase("")){
                Log.e("roomName","未绑定");
                switchControl3(context,deviceData,deviceId1,position1,deviceId2,position2);
            }else {
                Log.e("roomName","更新绑定");
                switchControl4(context,deviceData,deviceId1,position1,deviceId2,position2);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_qg_confirm);
        TextView turnoutNo=findViewById(R.id.turnout_no);
        TextView turnoutYes=findViewById(R.id.turnout_yes);
        turnoutNo.setOnClickListener(this);
        turnoutYes.setOnClickListener(this);
        progress_bar = findViewById(R.id.progress_bar);
    }

    /**
     * 辅控 绑定
     */
    public  void switchControl3(Context context, DeviceBean.DeviceData deviceInfoBean, String src_dev_id,int src_dev_ep,String dst_dev_id,int dst_dev_ep) {
        String userId = SPUtils.get(context, SpConstant.SP_USER_TELEPHONE, "");
        String familyId = SPUtils.get(context, "familyId"+userId, "");
        String mode="";
//        switch (state){
//            default:
//                break;
//            case 1:
//                mode="State";
//                break;
//            case 2:
//                mode="Temperature";
//                break;
//            case 3:
//                mode="WorkMode";
//                break;
//            case 4:
//                mode="FanMode";
//                break;
//        }
        String cmd = "{ \"method\" : \"" + "bind_auxiliary" + "\",\"src_dev_id\" : \"" + src_dev_id + "\",\"src_dev_ep\" : " + src_dev_ep + ",\"dst_dev_id\" : \"" + dst_dev_id + "\",\"dst_dev_ep\" : " + dst_dev_ep + "}";
        LogUtils.e("switchControl:", cmd);
        String encodeCmd = Base64.encodeToString(cmd.getBytes(), Base64.NO_WRAP);
        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
        deviceInfoVo.setDeviceId(deviceInfoBean.getDeviceId());
        deviceInfoVo.setDeviceType(deviceInfoBean.getDeviceType());
        deviceInfoVo.setGatewayId(deviceInfoBean.getGatewayId());
        deviceInfoVo.setSupplierId(deviceInfoBean.getSupplierId());
        SmartControlVo smartControlVo = new SmartControlVo();
        smartControlVo.setUserId(userId);
        smartControlVo.setFamilyId(familyId);
        smartControlVo.setCmdType("control");
        smartControlVo.setCmd(encodeCmd);
        smartControlVo.setDeviceInfo(deviceInfoVo);

//        NetWorkWrapper.smartDeviceControl(smartControlVo, new HttpHandler<String>() {
//
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String json = response.body().string();
//                LogUtils.e("onResponse----", json + "===");
//
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                super.onFailure(call, e);
//
//            }
//        });

        progress_bar.setVisibility(View.VISIBLE);
        OkGo.<String>post(Constant.HOST+Constant.Device_control)
                .upJson(JSON.toJSONString(smartControlVo))
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        LogUtils.e("resp",resp.getState()+"=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        progress_bar.setVisibility(View.GONE);
                        if (resp.getErrorCode().equalsIgnoreCase("200")){
                            dismiss();
                            yesOrNoBindCallBack.CallBack();
                        }else {
                            Toast.makeText(context,resp.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        progress_bar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        progress_bar.setVisibility(View.GONE);
//                        dismissLoading();
                        LogUtils.e("onComplete","============");
                    }
                });
    }
    /**
     * 辅控 绑定
     */
    public  void switchControl4(Context context, DeviceBean.DeviceData deviceInfoBean, String src_dev_id,int src_dev_ep,String dst_dev_id,int dst_dev_ep) {
        String userId = SPUtils.get(context, SpConstant.SP_USER_TELEPHONE, "");
        String familyId = SPUtils.get(context, "familyId"+userId, "");
        String mode="";
//        switch (state){
//            default:
//                break;
//            case 1:
//                mode="State";
//                break;
//            case 2:
//                mode="Temperature";
//                break;
//            case 3:
//                mode="WorkMode";
//                break;
//            case 4:
//                mode="FanMode";
//                break;
//        }
        String cmd = "{ \"method\" : \"" + "update_auxiliary" + "\",\"src_dev_id\" : \"" + src_dev_id + "\",\"src_dev_ep\" : " + src_dev_ep + ",\"dst_dev_id\" : \"" + dst_dev_id + "\",\"dst_dev_ep\" : " + dst_dev_ep + "}";
        LogUtils.e("switchControl:", cmd);
        String encodeCmd = Base64.encodeToString(cmd.getBytes(), Base64.NO_WRAP);
        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
        deviceInfoVo.setDeviceId(deviceInfoBean.getDeviceId());
        deviceInfoVo.setDeviceType(deviceInfoBean.getDeviceType());
        deviceInfoVo.setGatewayId(deviceInfoBean.getGatewayId());
        deviceInfoVo.setSupplierId(deviceInfoBean.getSupplierId());
        SmartControlVo smartControlVo = new SmartControlVo();
        smartControlVo.setUserId(userId);
        smartControlVo.setFamilyId(familyId);
        smartControlVo.setCmdType("control");
        smartControlVo.setCmd(encodeCmd);
        smartControlVo.setDeviceInfo(deviceInfoVo);

//        NetWorkWrapper.smartDeviceControl(smartControlVo, new HttpHandler<String>() {
//
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String json = response.body().string();
//                LogUtils.e("onResponse----", json + "===");
//
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                super.onFailure(call, e);
//
//            }
//        });

        progress_bar.setVisibility(View.VISIBLE);
        OkGo.<String>post(Constant.HOST+Constant.Device_control)
                .upJson(JSON.toJSONString(smartControlVo))
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        LogUtils.e("resp",resp.getState()+"=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        progress_bar.setVisibility(View.GONE);
                        if (resp.getErrorCode().equalsIgnoreCase("200")){
                            dismiss();
                            yesOrNoBindCallBack.CallBack();
                        }else {
                            Toast.makeText(context,resp.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        progress_bar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        progress_bar.setVisibility(View.GONE);
                        LogUtils.e("onComplete","============");
                    }
                });
    }
}
