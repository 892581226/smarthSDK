package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceRealStateVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DeviceControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.LogUtils;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class SmartSocketActivity extends BaseActivity implements View.OnClickListener {

    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;
    private List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans;
    private ImageView mSmartSocketIv;
    private boolean switchOne=false;
    private JSONObject jsonObject;
    private ImageView mSmartSocketChild;
    private boolean switchOneChild=false;
    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_socket);
        addDestoryActivity(this,"Activity");
        Intent intent = getIntent();
        if (intent!=null){
            gateDevices = (SmartInfoVo.FamilysBean.DeviceInfoBean) getIntent().getParcelableExtra("deviceItem");
            Log.e("智能插座", gateDevices.getDeviceName()+ gateDevices.getDeviceId()+" "+ gateDevices.getDeviceType()+" "+ gateDevices.getSupplierId());
        }

        init(gateDevices.getDeviceId(), gateDevices.getDeviceType(), gateDevices.getSupplierId());

        initview();
    }

    private void initview() {
        mSmartSocketIv = findViewById(R.id.smart_socket_iv);
        mSmartSocketChild = findViewById(R.id.smart_socket_child);
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mSmartSocketChild.setOnClickListener(this);
        mSmartSocketIv.setOnClickListener(this);
        mTopTitle = findViewById(R.id.top_title);
        mTopBt = findViewById(R.id.top_btn);
        mTopBt.setOnClickListener(this);
        mTopTitle.setText(gateDevices.getDeviceName());
        mTopBt.setText("管理");

    }

    private void init(String deviceId, String deviceType, String supplierId) {
        OkGo.<String>get(Constant.HOST+ Constant.Device_getRealMsg)
                .params("deviceId",deviceId)
                .params("deviceType",deviceType)
                .params("supplierId",supplierId)
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })
                .map(new Function<Response<String>, DeviceRealStateVo>() {
                    @Override
                    public DeviceRealStateVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        DeviceRealStateVo obj = null;
                        if(resp!=null && resp.getErrorCode().equalsIgnoreCase("200")){
                            obj = JSON.parseObject(resp.getResult(), DeviceRealStateVo.class);
                        }

                        LogUtils.e("resp",resp.getState()+"=====");
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<DeviceRealStateVo>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(DeviceRealStateVo realStateVo) {
                        List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosHongYan = realStateVo.getRealState().getStateInfos();
                        Log.e("智能插座", "ssss="+realStateVo+"----"+stateInfosHongYan);
                        if(realStateVo!=null &&stateInfosHongYan!=null && stateInfosHongYan.size()>0){
                            stateInfosBeans =stateInfosHongYan;
                            initSwitch(stateInfosBeans);
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete","============");
                    }
                });

    }


    private void initSwitch(List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans) {
        for (DeviceRealStateVo.RealStateBean.StateInfosBean datas:stateInfosBeans) {
            if (datas.getState()!=null){
                if (datas.getState().contains("SwitchChildLock")){
                    jsonObject =JSON.parseObject(datas.getState());
                    String SwitchChildLock= jsonObject.getString("SwitchChildLock");
                    if (SwitchChildLock.equalsIgnoreCase("0")){
                        mSmartSocketChild.setSelected(false);
                        switchOneChild =false;
                        mSmartSocketIv.setEnabled(true);
                    }else {
                        mSmartSocketIv.setEnabled(false);
                        switchOneChild=true;
                        mSmartSocketChild.setSelected(true);
                    }
                }else {
                    jsonObject =JSON.parseObject(datas.getState());
                    String state= jsonObject.getString("State");
                    if (state.equalsIgnoreCase("0")){
                        mSmartSocketIv.setSelected(false);
                        switchOne=false;
                    }else {
                        switchOne=true;
                        mSmartSocketIv.setSelected(true);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(v.getId() == R.id.top_back){
            finish();
        }else if(id == R.id.smart_socket_iv){
            // 开启/关闭 开关   state: 0关，1开，2 toggle
            if (switchOne) {
                switchOne = false;
                mSmartSocketIv.setSelected(false);
                DeviceControlUtil.switchControl(SmartSocketActivity.this,gateDevices,1,0);
            } else {
                switchOne = true;
                mSmartSocketIv.setSelected(true);
                DeviceControlUtil.switchControl(SmartSocketActivity.this,gateDevices,1,1);
            }
        }else if(id== R.id.smart_socket_child){
            if (switchOneChild) {
                switchOneChild = false;
                mSmartSocketChild.setSelected(false);
                mSmartSocketIv.setEnabled(true);
                DeviceControlUtil.switchControl2(SmartSocketActivity.this,gateDevices,1,1,"0");
            } else {
                switchOneChild = true;
                mSmartSocketChild.setSelected(true);
                mSmartSocketIv.setEnabled(false);
                DeviceControlUtil.switchControl2(SmartSocketActivity.this,gateDevices,1,1,"1");
            }
        }else if(id== R.id.top_btn){
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",gateDevices);
            startActivity(intent);
        }
    }
}