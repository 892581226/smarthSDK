package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.SceneMsgAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceRealStateMsg;
import com.example.smarthome.iot.entry.DeviceRealStateVo;
import com.example.smarthome.iot.entry.SensorInfoVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

/**
 * author:
 * date:
 * description: 海令门磁传感器
 * update:
 * version:
 */
public class DoorMagnetActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private LinearLayout mDoorSensorLayout;
    /**
     * 3月
     */
    private TextView mDoorSensorMonth;
    /**
     * 2019
     */
    private TextView mDoorSensorYear;
    private LinearLayout mDoorSensorLinear;
    /**
     * 关闭记录
     */
    private Button mDoorSensorCloseHistory;
    private RelativeLayout mDoorSensorHistoryRelate;
    private RecyclerView mDoorSensorHistoryRv;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private List<SensorInfoVo.SensorMessages> sensorMsgs = new ArrayList<>();
    private SceneMsgAdapter sceneMsgAdapter;
    private boolean rvShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_magnet);
        addDestoryActivity(this,"DoorMagnetActivity");
        EventBus.getDefault().register(this);
        initView();
        initDate();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (messageEvent.isUpdate()) {
            mTopTitle.setText(messageEvent.getTitle());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    private void initDate() {
        mTopBtn.setText("管理");
        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        if(deviceInfoBean!=null){
            mTopTitle.setText(deviceInfoBean.getDeviceName());
            getSensorInfo();
        }
        sceneMsgAdapter = new SceneMsgAdapter(sensorMsgs);
        sceneMsgAdapter.setDevType(deviceInfoBean.getDeviceType());
        View footView = getLayoutInflater().inflate(R.layout.main_device_foot_view, (ViewGroup) mDoorSensorHistoryRv.getParent(),false);
        sceneMsgAdapter.addFooterView(footView);
        mDoorSensorHistoryRv.setAdapter(sceneMsgAdapter);
    }

    private void initView() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mDoorSensorLayout = (LinearLayout) findViewById(R.id.human_door_sensor_layout);
        mDoorSensorMonth = (TextView) findViewById(R.id.human_door_sensor_month);
        mDoorSensorYear = (TextView) findViewById(R.id.human_door_sensor_year);
        mDoorSensorLinear = (LinearLayout) findViewById(R.id.human_door_sensor_linear);
        mDoorSensorCloseHistory = (Button) findViewById(R.id.human_door_sensor_close_history);
        mDoorSensorCloseHistory.setOnClickListener(this);
        mDoorSensorHistoryRelate = (RelativeLayout) findViewById(R.id.human_door_sensor_history_relate);
        mDoorSensorHistoryRv = (RecyclerView) findViewById(R.id.human_door_sensor_history_rv);
        mDoorSensorHistoryRv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.top_back){
            finish();
        } else if(v.getId() == R.id.top_btn){
            // 管理
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",deviceInfoBean);
            startActivity(intent);
        } else if(v.getId() == R.id.human_door_sensor_close_history){
            rvShow = !rvShow;
            if(rvShow){
                mDoorSensorHistoryRv.setVisibility(View.VISIBLE);
                mDoorSensorCloseHistory.setText("关闭记录");
            } else {
                mDoorSensorHistoryRv.setVisibility(View.GONE);
                mDoorSensorCloseHistory.setText("打开记录");
            }
        }
    }

    private void getSensorInfo(){
        OkGo.<String>get(Constant.HOST+Constant.Device_getSensorInfo)
                .params("deviceId",deviceInfoBean.getDeviceId())
                .params("deviceType",deviceInfoBean.getDeviceType())
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
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
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
                    public void onNext(CommonResp sensorInfo) {
                        if(sensorInfo!=null){
                            SensorInfoVo object = new Gson().fromJson(sensorInfo.getResult(),SensorInfoVo.class);
                            if(object!=null){
                                sensorMsgs.clear();
                                sensorMsgs.addAll(object.getSensor().getMsgs());
                                sceneMsgAdapter.setNewData(sensorMsgs);
                            }
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
