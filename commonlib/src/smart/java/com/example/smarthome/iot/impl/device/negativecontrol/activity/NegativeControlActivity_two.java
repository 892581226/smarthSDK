package com.example.smarthome.iot.impl.device.negativecontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.DeviceBean;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NegativeControlActivity_two extends BaseActivity implements View.OnClickListener {

    private final String TAG=this.getClass().getSimpleName();
    private LinearLayout top_back;
    private TextView top_title;
    private TextView top_btn;
    private Integer switch_number;
    private ImageView image_bitmap;
    private TextView tv_bind_1;
    private TextView tv_bind_2;
    private TextView tv_bind_3;
    private TextView tv_bind_4;
    private String deviceId;
    private JSONObject jsonObject;
    private List<TextView> tv_binds;
    private DeviceBean deviceBean;
    private String suffix;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negative_control_two);
        Intent intent=getIntent();
        if (intent!=null){
            switch_number = intent.getIntExtra("switch_number",0);
            deviceId = intent.getStringExtra("deviceId");
            Log.e("设备ID",deviceId);
        }
        initView();
        initDate();
    }
    private void initView(){
        top_back = findViewById(R.id.top_back);
        top_title = findViewById(R.id.top_title);
        top_btn = findViewById(R.id.top_btn);
        top_back.setOnClickListener(this);
        image_bitmap = findViewById(R.id.image_bitmap);
        tv_bind_1 = findViewById(R.id.tv_bind_1);
        tv_bind_2 = findViewById(R.id.tv_bind_2);
        tv_bind_3 = findViewById(R.id.tv_bind_3);
        tv_bind_4 = findViewById(R.id.tv_bind_4);
        tv_bind_1.setOnClickListener(this);
        tv_bind_2.setOnClickListener(this);
        tv_bind_3.setOnClickListener(this);
        tv_bind_4.setOnClickListener(this);
    }
    private void initDate(){

        switch (switch_number){
            default:
                break;
            case 1:
                top_title.setText("一路开关");
                image_bitmap.setImageResource(R.drawable.icon_iot_single_bit_switch_bg);
                tv_bind_2.setVisibility(View.VISIBLE);
                break;
            case 2:
                top_title.setText("二路开关");
                image_bitmap.setImageResource(R.drawable.icon_iot_double_bit_switch_bg);
                tv_bind_1.setVisibility(View.VISIBLE);
                tv_bind_4.setVisibility(View.VISIBLE);
                break;
            case 3:
                top_title.setText("三路开关");
                image_bitmap.setImageResource(R.drawable.icon_iot_three_bit_switch_bg);
                tv_bind_1.setVisibility(View.VISIBLE);
                tv_bind_3.setVisibility(View.VISIBLE);
                tv_bind_4.setVisibility(View.VISIBLE);
                break;
            case 4:
                top_title.setText("四路开关");
                image_bitmap.setImageResource(R.drawable.icon_iot_four_bit_switch_bg);
                tv_bind_1.setVisibility(View.VISIBLE);
                tv_bind_2.setVisibility(View.VISIBLE);
                tv_bind_3.setVisibility(View.VISIBLE);
                tv_bind_4.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceById(deviceId);
    }

    private void getDeviceById(String devId){
        Log.e("devId",devId);
        OkGo.<String>get(Constant.HOST+Constant.Get_Device_By_Id)
                .params("devId",devId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("getDevById","onSubscribe"+d.isDisposed()+"====");
                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        Log.e("getDevById","onNext====");
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
                            deviceBean = JSON.parseObject(commonResp.getResult(), DeviceBean.class);
                            Integer length= deviceBean.getDeviceInfo().getAuxiliaryDevStatusMap().length();
                            String auxiliaryDevStatusMap= deviceBean.getDeviceInfo().getAuxiliaryDevStatusMap();
                            jsonObject = JSON.parseObject(auxiliaryDevStatusMap);
                            tv_binds=new ArrayList<>();
                            tv_binds.clear();
                            tv_binds.add(tv_bind_1);
                            tv_binds.add(tv_bind_2);
                            tv_binds.add(tv_bind_3);
                            tv_binds.add(tv_bind_4);
                            Log.e(TAG,jsonObject.toJSONString());
                            if (jsonObject!=null){

                                Iterator<String> iterators=jsonObject.keySet().iterator();
                                for (Iterator<String> it = iterators; it.hasNext(); ) {
                                    String key = it.next();
                                    Log.e(TAG,key);
                                    String values=jsonObject.getString(key);
                                    String prefix=values.substring(0,values.lastIndexOf("-"));
                                    suffix = values.substring(values.lastIndexOf("-")+1);
                                    Log.e(TAG,prefix+" "+ suffix+" "+key);
                                    getDeviceById2(prefix,key,suffix);
                                }

//                                    Log.e(TAG,jsonObject.keySet().iterator().next());
//
//                                for (int i=1;i<=length;i++){
//
//                                    switch (i){
//
//                                    }
//                                    if (i==1){
//                                        String name=jsonObject.getString((i)+"");
//                                        Log.e(TAG,name);
//                                        tv_bind_1.setText(name);
//                                    }
//                                    if (i==2){
//                                        String name=jsonObject.getString((i)+"");
//                                        Log.e(TAG,name);
//                                        tv_bind_2.setText(name);
//                                    }
//                                    if (i==3){
//                                        String name=jsonObject.getString((i)+"");
//                                        Log.e(TAG,name);
//                                        tv_bind_3.setText(name);
//                                    }
//                                    if (i==4){
//                                        String name=jsonObject.getString((i)+"");
//                                        Log.e(TAG,name);
//                                        tv_bind_4.setText(name);
//                                    }
////                                    String name=jsonObject.getString((i+1)+"");
////                                    if (!TextUtils.isEmpty(name)){
////                                        tv_binds.get(i).setText(name);
////                                    }else {
////                                        tv_binds.get(i).setText("未绑定");
////                                    }
//                                }
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getDevById","onError====");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("getDevById","onComplete====");
                    }
                });
    }

    private void getDeviceById2(String deviceId,String key,String suffix){
        OkGo.<String>get(Constant.HOST+Constant.Get_Device_By_Id)
                .params("devId",deviceId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        DeviceBean d=JSON.parseObject(commonResp.getResult(), DeviceBean.class);
                        Log.e("辅控数据",d.toString());
                        String key_number="";
                        switch (suffix){
                            default:
                                break;
                            case "1":
                                key_number="键位一";
                                break;
                            case "2":
                                key_number="键位二";
                                break;
                            case "3":
                                key_number="键位三";
                                break;
                            case "4":
                                key_number="键位四";
                                break;
                        }
                        tv_binds.get(Integer.parseInt(key)-1).setText(d.getDeviceInfo().getRoomName()+d.getDeviceInfo().getDeviceName()+"-"+key_number);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        Intent intent=new Intent();
        intent.setClass(NegativeControlActivity_two.this,NegativeControlActivity_Three.class);
        intent.putExtra("deviceInfo",deviceBean.getDeviceInfo());
        if (id==R.id.top_back){
            finish();
            return;
        }else if (id==R.id.tv_bind_1){
            intent.putExtra("position",1);
            intent.putExtra("roomName",tv_bind_1.getText().toString());
        }else if (id==R.id.tv_bind_2){
            intent.putExtra("position",2);
            intent.putExtra("roomName",tv_bind_2.getText().toString());
        }else if (id==R.id.tv_bind_3){
            intent.putExtra("position",3);
            intent.putExtra("roomName",tv_bind_3.getText().toString());
        }else if (id==R.id.tv_bind_4){
            intent.putExtra("position",4);
            intent.putExtra("roomName",tv_bind_4.getText().toString());
        }
        startActivity(intent);
        finish();
    }
}
