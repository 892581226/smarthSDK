package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import com.example.smarthome.R;
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
 * author: glq
 * date: 2019/5/28 15:11
 * description: 海令人体传感器
 * update: 2019/5/28
 * version:
 */
public class HumanSensorActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private LinearLayout mHumanSensorLayout;
    /**
     * 3月
     */
    private TextView mHumanSensorMonth;
    /**
     * 2019
     */
    private TextView mHumanSensorYear;
    private LinearLayout mHumanSensorLinear;
    /**
     * 关闭记录
     */
    private Button mHumanSensorCloseHistory;
    private RelativeLayout mHumanSensorHistoryRelate;
    private RecyclerView mHumanSensorHistoryRv;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private List<SensorInfoVo.SensorMessages> sensorMsgs = new ArrayList<>();
    private SceneMsgAdapter sceneMsgAdapter;
    private boolean rvShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_sensor);
        addDestoryActivity(this,"HumanSensorActivity");
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
    @Override
    protected void onResume() {
        super.onResume();
//        solveNavigationBar(getWindow());
    }

    /**
     * <P>shang</P>
     * <P>解决虚拟按键问题</P>
     * @param window
     */
    public void solveNavigationBar(Window window) {

        //保持布局状态
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions |= 0x00001000;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
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
        View footView = getLayoutInflater().inflate(R.layout.main_device_foot_view, (ViewGroup) mHumanSensorHistoryRv.getParent(),false);
        sceneMsgAdapter.addFooterView(footView);
        mHumanSensorHistoryRv.setAdapter(sceneMsgAdapter);
    }

    private void initView() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mHumanSensorLayout = (LinearLayout) findViewById(R.id.human_sensor_layout);
        mHumanSensorMonth = (TextView) findViewById(R.id.human_sensor_month);
        mHumanSensorYear = (TextView) findViewById(R.id.human_sensor_year);
        mHumanSensorLinear = (LinearLayout) findViewById(R.id.human_sensor_linear);
        mHumanSensorCloseHistory = (Button) findViewById(R.id.human_sensor_close_history);
        mHumanSensorCloseHistory.setOnClickListener(this);
        mHumanSensorHistoryRelate = (RelativeLayout) findViewById(R.id.human_sensor_history_relate);
        mHumanSensorHistoryRv = (RecyclerView) findViewById(R.id.human_sensor_history_rv);
        mHumanSensorHistoryRv.setLayoutManager(new LinearLayoutManager(this));
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
        }else if(v.getId() == R.id.human_sensor_close_history){
            rvShow = !rvShow;
            if(rvShow){
                mHumanSensorHistoryRv.setVisibility(View.VISIBLE);
                mHumanSensorCloseHistory.setText("关闭记录");
            } else {
                mHumanSensorHistoryRv.setVisibility(View.GONE);
                mHumanSensorCloseHistory.setText("打开记录");
            }
        }
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
                        LogUtils.e("onComplete","   =====");
                    }
                });
    }
}
