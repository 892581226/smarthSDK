package com.example.smarthome.iot.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.uiutils.LogUtils;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author: glq
 * date: 2019/6/13 11:18
 * description:
 * update: 2019/6/13
 * version:
 */
public class DeviceListAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.DeviceInfoBean, BaseViewHolder> {

    private boolean isMydevice;
    private ImageView imageView1;
    private TextView devOnline;
    private TextView deviceName;

    public boolean isMydevice() {
        return isMydevice;
    }

    public void setMydevice(boolean mydevice) {
        isMydevice = mydevice;
    }

    public DeviceListAdapter(List<SmartInfoVo.FamilysBean.DeviceInfoBean> data) {
        super(R.layout.item_iot_device_layout_cons, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.DeviceInfoBean item) {
        if(item !=null){
            helper.addOnClickListener(R.id.item_iot_device_linear);
            helper.addOnClickListener(R.id.item_iot_device_switch);

            deviceName = helper.getView(R.id.item_iot_device_name);
            deviceName.setText(item.getDeviceName());
//            helper.setText(R.id.item_iot_device_name, item.getDeviceName());
            if(isMydevice){
                helper.setText(R.id.item_iot_device_address, item.getRoomName());
            } else {
                helper.setText(R.id.item_iot_device_address, "");
//                if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_DOOR_MAGNETIC)){
//                    helper.setText(R.id.item_iot_device_address, item.getField().getAlarm());
//                } else if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_HUMAN_INFRARED)){
//                    helper.setText(R.id.item_iot_device_address, item.getField().getAlarm());
//                } else if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SMOKE_ALARM)){
//                    helper.setText(R.id.item_iot_device_address, "");
//                }else if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_GAS_ALARM)){
//                    helper.setText(R.id.item_iot_device_address, "");
//                }else if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_TEMP_HUN)){
//                    helper.setText(R.id.item_iot_device_address, "温度:"+item.getField().getTemperature()+",湿度:"+item.getField().getHumidity());
//                } else if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_CURTAIN)){
//                    helper.setText(R.id.item_iot_device_address, item.getField().getMode());
//                }else if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SCENE_SWITCH_6)){
//                    helper.setText(R.id.item_iot_device_address, "");
//                }else if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)
//                        ||item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_2)
//                        ||item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_3)
//                        ||item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_4)){
//                    helper.setText(R.id.item_iot_device_address, "");
////                    helper.setText(R.id.item_iot_device_address, item.getField().getSwitch1().equals("1")?"开":"关");
//                }else if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_GATEWAY)){
//                    helper.setText(R.id.item_iot_device_address, "");
//                } else {
//                    helper.setText(R.id.item_iot_device_address, "");
//                }

            }
            ImageView imageView = helper.getView(R.id.item_iot_device_switch);
            imageView1 = helper.getView(R.id.item_iot_device_online_type_img);
            devOnline = helper.getView(R.id.item_iot_device_online_type_text);
            if (item.isOnline()) { // 是否在线
                imageView1.setImageResource(R.drawable.icon_iot_device_online);
                devOnline.setText("在线");
                devOnline.setTextColor(mContext.getResources().getColor(R.color.color_3E3E3E));
//                helper.setText(R.id.item_iot_device_online_type_text, "在线");
                deviceName.setTextColor(mContext.getResources().getColor(R.color.color_333333));
                deviceName.getPaint().setFakeBoldText(true);
                if(item.getDeviceType().equalsIgnoreCase(DeviceType.YS_CAMERA)){
                    imageView1.setVisibility(View.GONE);
                    devOnline.setVisibility(View.GONE);
                } else {
                    imageView1.setVisibility(View.VISIBLE);
                    devOnline.setVisibility(View.VISIBLE);
                }
            } else {
                imageView1.setImageResource(R.drawable.icon_iot_device_offline);
                devOnline.setText("离线");
                devOnline.setTextColor(mContext.getResources().getColor(R.color.color_9E9E9E));
//                helper.setText(R.id.item_iot_device_online_type_text, "离线");
                deviceName.setTextColor(mContext.getResources().getColor(R.color.color_9E9E9E));
                deviceName.getPaint().setFakeBoldText(false);
            }

//            int icon = MyAPP.getIns().getResource(item.getDeviceIcon(),"drawable",R.drawable.icon_iot_curtain_control_switch);
//            helper.setImageResource(R.id.item_iot_device_img, icon);

            Glide.with(mContext).load(item.getDeviceIcon()).error(R.drawable.icon_iot_curtain_control_switch).into((ImageView) helper.getView(R.id.item_iot_device_img));
            if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)){
                imageView.setImageResource(R.drawable.icon_iot_scene_open);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }


            Log.e("首页网关数据",item.toString());

            if (item.getSupplierId().contains("网关")/*||item.getDeviceName().contains("音乐")*/){
//                imageView1.setImageResource(R.drawable.icon_iot_device_online);
//                devOnline.setText("在线");
//                devOnline.setTextColor(mContext.getResources().getColor(R.color.color_3E3E3E));
////                helper.setText(R.id.item_iot_device_online_type_text, "在线");
//                deviceName.setTextColor(mContext.getResources().getColor(R.color.color_333333));
//                deviceName.getPaint().setFakeBoldText(true);
//                if(item.getDeviceType().equalsIgnoreCase(DeviceType.YS_CAMERA)){
//                    imageView1.setVisibility(View.GONE);
//                    devOnline.setVisibility(View.GONE);
//                } else {
//                    imageView1.setVisibility(View.VISIBLE);
//                    devOnline.setVisibility(View.VISIBLE);
//                }

                onLine(item.getId(),item,helper);

            }

        }
    }


    private void onLine(String gatewayId, SmartInfoVo.FamilysBean.DeviceInfoBean item, BaseViewHolder helper){
        OkGo.<String>get(Constant.HOST+Constant.Gateway_getActive)
                .params("gatewayId",gatewayId)
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
                        Log.e(TAG,"getActive inNext"+resp.getErrorCode());
                        if (resp.getErrorCode().equalsIgnoreCase("200")){
                            JSONObject jsonObject=JSON.parseObject(resp.getResult());
                            String active=jsonObject.getString("active");
                            if (active.equalsIgnoreCase("false")){
                                helper.setImageResource(R.id.item_iot_device_online_type_img,R.drawable.icon_iot_device_offline);
                                helper.setText(R.id.item_iot_device_online_type_text,"离线");
                                helper.setTextColor(R.id.item_iot_device_online_type_text,mContext.getResources().getColor(R.color.color_9E9E9E));
                                helper.setTextColor(R.id.item_iot_device_name,mContext.getResources().getColor(R.color.color_9E9E9E));
                                deviceName.getPaint().setFakeBoldText(false);

                                /*imageView1.setImageResource(R.drawable.icon_iot_device_offline);
                                devOnline.setText("离线");
                                devOnline.setTextColor(mContext.getResources().getColor(R.color.color_9E9E9E));
//                helper.setText(R.id.item_iot_device_online_type_text, "离线");
                                deviceName.setTextColor(mContext.getResources().getColor(R.color.color_9E9E9E));
                               */
                            }else {
                                helper.setImageResource(R.id.item_iot_device_online_type_img,R.drawable.icon_iot_device_online);
                                helper.setText(R.id.item_iot_device_online_type_text,"在线");
                                helper.setTextColor(R.id.item_iot_device_online_type_text,mContext.getResources().getColor(R.color.color_3E3E3E));
                                helper.setTextColor(R.id.item_iot_device_name,mContext.getResources().getColor(R.color.color_333333));

                                /*imageView1.setImageResource(R.drawable.icon_iot_device_online);
                                devOnline.setText("在线");
                                devOnline.setTextColor(mContext.getResources().getColor(R.color.color_3E3E3E));
//                helper.setText(R.id.item_iot_device_online_type_text, "在线");
                                deviceName.setTextColor(mContext.getResources().getColor(R.color.color_333333));*/
                                deviceName.getPaint().setFakeBoldText(true);
                                if(item.getDeviceType().equalsIgnoreCase(DeviceType.YS_CAMERA)){
                                    imageView1.setVisibility(View.GONE);
                                    devOnline.setVisibility(View.GONE);
                                } else {
                                    imageView1.setVisibility(View.VISIBLE);
                                    devOnline.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG,"getActive onError"+e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
//                        LogUtils.e("onComplete","============");
                        Log.e(TAG,"getActive onComplete");
                    }
                });
    }

}
