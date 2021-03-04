package com.example.smarthome.iot.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceInfoVo;
import com.example.smarthome.iot.entry.SmartControlVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
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

/**
 *  设备控制
 */

public class DeviceControlUtil {

    private static String userId, familyId;
    private static final String TAG="DeviceControlUtil";

    /**
     * 开关设备
     */
    public static void switchControl(Context context, SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean, int devEpId, int state) {
        userId = SPUtils.get(context, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(context, "familyId"+userId, "");
        String cmd = "{ \"dev_ep_id\" : " + devEpId + ",\"state\" : " + state + "}";
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

    /**
     * 开关设备
     */
    public static void switchControl2(Context context, SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean, int devEpId, int state,String modeValue) {
        userId = SPUtils.get(context, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(context, "familyId"+userId, "");
        String mode="";
        String devEpIdS=devEpId+"";
        if (deviceInfoBean.getDeviceType().equalsIgnoreCase("130602")){
            switch (state) {
                default:
                    break;
                case 1:
                    mode = "On";
                    devEpIdS="1";
                    break;
                case 2:
                    mode = "Temperature";
                    devEpIdS="2";
                    break;
                case 3:
                    mode = "Pattern";
                    devEpIdS="3";
                    break;
                case 4:
                    mode = "Wind_num";
                    devEpIdS="4";
                    break;
            }
            //deviceInfoBean.setDeviceId("80488F5513690101");
        }else if(deviceInfoBean.getDeviceType().equalsIgnoreCase("233000")){
            switch (state) {
                default:
                    break;
                case 1:
                    mode = "SwitchChildLock";
                    break;
            }
        }else {
            switch (state) {
                default:
                    break;
                case 1:
                    mode = "State";
                    break;
                case 2:
                    mode = "Temperature";
                    break;
                case 3:
                    mode = "WorkMode";
                    break;
                case 4:
                    mode = "FanMode";
                    break;
            }

        }

        String cmd = "{ \"dev_ep_id\" : \"" + devEpIdS + "\",\""+mode+"\" : \""+ modeValue + "\"}";
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
                        if (resp.getErrorCode().equalsIgnoreCase("200")){

                        }else {

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

    /**
     * 辅控 绑定
     */
    public static void switchControl3(Context context, DeviceBean.DeviceData deviceInfoBean, String src_dev_id,int src_dev_ep,String dst_dev_id,int dst_dev_ep) {
        userId = SPUtils.get(context, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(context, "familyId"+userId, "");
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
        String cmd = "{ \"method\" : " + "bind_auxiliary" + ",\"src_dev_id\" : " + src_dev_id + ",\"src_dev_ep\" : " + src_dev_ep + ",\"dst_dev_id\" : " + dst_dev_id + ",\"dst_dev_ep\" : " + dst_dev_ep + "}";
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
