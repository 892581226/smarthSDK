package com.example.smarthome.iot.dveview;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceInfoActivity;
import com.example.smarthome.iot.adapter.SceneMsgAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SensorInfoVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
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

public class WaterAlarmActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mWater;
    private ImageView mEllRed;
    private JSONObject jsonObject;
    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBt;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private List<SensorInfoVo.SensorMessages> sensorMsgs = new ArrayList<>();
    private SceneMsgAdapter sceneMsgAdapter;
    private RecyclerView mRecycler;
    private boolean rvShow = true;
    private Button mWaterAlarmCloseHistory;
    private SmartRefreshLayout mSmartRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_alarm);
        EventBus.getDefault().register(this);
        addDestoryActivity(this,"Activity");
        initview();
        initDate();
    }

    private void initview() {
        mWater = findViewById(R.id.water);
        mEllRed = findViewById(R.id.ell_red);
        mTopBack = findViewById(R.id.top_back);
        View lin = findViewById(R.id.title_line);
        lin.setVisibility(View.GONE);
        mSmartRefresh = findViewById(R.id.smart_refresh);
        mRecycler = (RecyclerView) findViewById(R.id.water_alarm_history_rv);
        mWaterAlarmCloseHistory = findViewById(R.id.water_alarm_close_history);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mTopTitle = findViewById(R.id.top_title);
        mTopBt = findViewById(R.id.top_btn);
        mWaterAlarmCloseHistory.setOnClickListener(this);
        mTopBack.setOnClickListener(this);
        mTopBt.setOnClickListener(this);
        mSmartRefresh.autoRefresh();
        mSmartRefresh.setRefreshHeader(new ClassicsHeader(this));
        mSmartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@androidx.annotation.NonNull RefreshLayout refreshLayout) {
                getSensorInfo();
            }
        });
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
        mTopTitle.setText(getString(R.string.water_alarm));
        mTopBt.setText("管理");
        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        if(deviceInfoBean !=null){
            mTopTitle.setText(deviceInfoBean.getDeviceName());
            getSensorInfo();
        }
        sceneMsgAdapter = new SceneMsgAdapter(sensorMsgs);
        sceneMsgAdapter.setDevType(deviceInfoBean.getDeviceType());
        View footView = getLayoutInflater().inflate(R.layout.main_device_foot_view, (ViewGroup) mRecycler.getParent(),false);
        sceneMsgAdapter.addFooterView(footView);
        mRecycler.setAdapter(sceneMsgAdapter);
    }

    private void getSensorInfo(){
        Log.e("TAG", "getSensorInfo: "+deviceInfoBean.getDeviceId()+"--"+deviceInfoBean.getDeviceType() );
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
                        LogUtils.e("onSubscribe","   =---====");
                    }

                    @Override
                    public void onNext(CommonResp sensorInfo) {
                        if(sensorInfo!=null){
                            SensorInfoVo object = new Gson().fromJson(sensorInfo.getResult(),SensorInfoVo.class);
                            if(object!=null){
                                sensorMsgs.clear();
                                List<SensorInfoVo.SensorMessages> msgs = object.getSensor().getMsgs();
                                for (int i = 0; i < msgs.size(); i++) {
                                    sensorMsgs.add(i, msgs.get(msgs.size()-1-i));
                                }
                                sceneMsgAdapter.setNewData(sensorMsgs);
                                mSmartRefresh.finishRefresh();
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
                        LogUtils.e("onComplete","   =====");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(v.getId() == R.id.top_back){
            finish();
        }else if(id== R.id.top_btn){
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",deviceInfoBean);
            startActivity(intent);
        }else if(v.getId() == R.id.water_alarm_close_history){
            rvShow = !rvShow;
            if(rvShow){
                mRecycler.setVisibility(View.VISIBLE);
                mWaterAlarmCloseHistory.setText("关闭记录");
            } else {
                mRecycler.setVisibility(View.GONE);
                mWaterAlarmCloseHistory.setText("打开记录");
            }
        }
    }
}