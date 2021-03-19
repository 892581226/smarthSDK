package com.example.smarthome.iot.impl.device.negativecontrol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceInfoVo;
import com.example.smarthome.iot.entry.SmartControlVo;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.DeviceBean;
import com.example.smarthome.iot.net.Constant;
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

public class YesOrNoBindDialog2 extends Dialog implements View.OnClickListener {


    public YesOrNoBindCallBack2 yesOrNoBindCallBack2;
    DeviceBean.DeviceData deviceData;
    String deviceId1;
    Integer position1;
    Context context;

    public void setYesOrNoBindCallBack2(YesOrNoBindCallBack2 yesOrNoBindCallBack2) {
        this.yesOrNoBindCallBack2 = yesOrNoBindCallBack2;
    }

    public interface YesOrNoBindCallBack2{
        void CallBack();
    }

    public YesOrNoBindDialog2(@NonNull Context context, int themeResId,DeviceBean.DeviceData deviceData,
                              String deviceId1,int position1) {
        super(context, themeResId);
        this.context=context;
        this.deviceData=deviceData;
        this.deviceId1=deviceId1;
        this.position1=position1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_yes_no_bind2);
        TextView turnoutNo=findViewById(R.id.turnout_no);
        TextView turnoutYes=findViewById(R.id.turnout_yes);
        turnoutNo.setOnClickListener(this);
        turnoutYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.turnout_no){
            dismiss();
        }else if (v.getId()==R.id.turnout_yes){
            //解绑
            switchControl(context,deviceData,deviceId1,position1);
        }
    }

    /**
     * 辅控 绑定
     */
    public void switchControl(Context context, DeviceBean.DeviceData deviceInfoBean, String src_dev_id, int src_dev_ep) {
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
        String cmd = "{ \"method\" : " + "\"unbind_auxiliary\"" + ",\"src_dev_id\" : \"" +src_dev_id+ "\",\"src_dev_ep\" : " + src_dev_ep + "}";
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
                        Log.e("解绑","onNext"+resp.getMessage().toString());
                        if (resp.getErrorCode().equalsIgnoreCase("200")){
                            dismiss();
                            yesOrNoBindCallBack2.CallBack();
                        }else {
                            Toast.makeText(context,resp.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete","============");
                    }
                });
    }
}
