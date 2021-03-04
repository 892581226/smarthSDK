package com.example.smarthome.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.SceneMsgAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.ScenePanelMsgVo;
import com.example.smarthome.iot.entry.SensorInfoVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.zyao89.view.zloading.ZLoadingDialog;

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
import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author:
 * date:
 * description: 海令六路场景开关
 * update:
 * version:
 */
public class SceneSixSwitchActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private View mTitleLine;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private TextView scene_switch_1, scene_switch_2, scene_switch_3, scene_switch_4, scene_switch_5, scene_switch_6;
    private ImageView six_bit_switch_1, six_bit_switch_2, six_bit_switch_3, six_bit_switch_4, six_bit_switch_5, six_bit_switch_6;
    private String userId, familyId;
    private SceneMsgAdapter sceneMsgAdapter;
    private List<SensorInfoVo.SensorMessages> sensorMsgs = new ArrayList<>();
    private List<ScenePanelMsgVo.ScenePanelMsgBean.SwitchsBean> switchsBeans = new ArrayList<>();
    private RecyclerView mSceneRecycle;
    private ZLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six_bit_scene_switch);
        EventBus.getDefault().register(this);
        addDestoryActivity(this, "SceneSixSwitchActivity");
        initView();
        initDate();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (messageEvent.isUpdate()) {
            if (messageEvent.getTitle().isEmpty()){
                mTopTitle.setText(deviceInfoBean.getDeviceName());
            }else
                mTopTitle.setText(messageEvent.getTitle());
        }
    }

    private void initDate() {
        mTitleLine.setVisibility(View.GONE);
        mTopBtn.setText("管理");
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE,"");
        familyId = SPUtils.get(this, "familyId" + userId,"");
        getScenePanelMsg(deviceInfoBean.getDeviceId(), deviceInfoBean.getDeviceType(), familyId, userId);
        loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);
    }

    private void initView() {
        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        mTopBack = findViewById(R.id.top_back);
        mSceneRecycle = findViewById(R.id.scene_history_rv);
        mSceneRecycle.setLayoutManager(new LinearLayoutManager(this));
        sceneMsgAdapter = new SceneMsgAdapter(sensorMsgs);
        View footView = getLayoutInflater().inflate(R.layout.main_device_foot_view, (ViewGroup) mSceneRecycle.getParent(),false);
        sceneMsgAdapter.addFooterView(footView);
        sceneMsgAdapter.setDevType(deviceInfoBean.getDeviceType());
        mSceneRecycle.setAdapter(sceneMsgAdapter);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTitleLine = (View) findViewById(R.id.title_line);
        mTopTitle.setText(deviceInfoBean.getDeviceName());

        scene_switch_1 = findViewById(R.id.six_scene_switch_one);
        scene_switch_2 = findViewById(R.id.six_scene_switch_two);
        scene_switch_3 = findViewById(R.id.six_scene_switch_three);
        scene_switch_4 = findViewById(R.id.six_scene_switch_four);
//        scene_switch_5 = findViewById(R.id.six_scene_switch_five);
//        scene_switch_6 = findViewById(R.id.six_scene_switch_six);
        scene_switch_1.setOnClickListener(this);
        scene_switch_2.setOnClickListener(this);
        scene_switch_3.setOnClickListener(this);
        scene_switch_4.setOnClickListener(this);
//        scene_switch_5.setOnClickListener(this);
//        scene_switch_6.setOnClickListener(this);

        six_bit_switch_1 = findViewById(R.id.six_bit_switch_one);
        six_bit_switch_2 = findViewById(R.id.six_bit_switch_two);
        six_bit_switch_3 = findViewById(R.id.six_bit_switch_three);
        six_bit_switch_4 = findViewById(R.id.six_bit_switch_four);
//        six_bit_switch_5 = findViewById(R.id.six_bit_switch_five);
//        six_bit_switch_6 = findViewById(R.id.six_bit_switch_six);
        six_bit_switch_1.setOnClickListener(this);
        six_bit_switch_2.setOnClickListener(this);
        six_bit_switch_3.setOnClickListener(this);
        six_bit_switch_4.setOnClickListener(this);
//        six_bit_switch_5.setOnClickListener(this);
//        six_bit_switch_6.setOnClickListener(this);
//        initSwitch();
    }

    private void initSwitch() {
        if (StringUtils.isEmpty(switchsBeans.get(0).getSceneId())) {
            scene_switch_1.setText("未关联");
            scene_switch_1.setTextColor(getResources().getColor(R.color.color_9E9E9E));
            six_bit_switch_1.setImageResource(R.drawable.icon_iot_scene_bit_switch_close);
            scene_switch_1.setBackgroundResource(R.drawable.trans_bg);
        } else {
            if(switchsBeans.get(0).getSceneName().length()>2){
                scene_switch_1.setText(switchsBeans.get(0).getSceneName().substring(0,2)+"...");
            } else {
                scene_switch_1.setText(switchsBeans.get(0).getSceneName());
            }
//            scene_switch_1.setText(switchsBeans.get(0).getSceneName());
//            scene_switch_1.setTextColor(switchsBeans.get(0).isSceneState()?(getResources().getColor(R.color.color_598DF3)):(getResources().getColor(R.color.color_1F8E8E93)));
            scene_switch_1.setTextColor(getResources().getColor(R.color.color_598DF3));
            six_bit_switch_1.setImageResource(switchsBeans.get(0).isSceneState() ? R.drawable.icon_iot_scene_bit_switch_open : R.drawable.icon_iot_scene_bit_switch_close);
            scene_switch_1.setBackgroundResource(switchsBeans.get(0).isSceneState() ? R.drawable.icon_iot_scene_open_bg : R.drawable.trans_bg);
        }
        if (StringUtils.isEmpty(switchsBeans.get(1).getSceneId())) {
            scene_switch_2.setText("未关联");
            scene_switch_2.setTextColor(getResources().getColor(R.color.color_9E9E9E));
            six_bit_switch_2.setImageResource(R.drawable.icon_iot_scene_bit_switch_close);
            scene_switch_2.setBackgroundResource(R.drawable.trans_bg);
        } else {
            if(switchsBeans.get(1).getSceneName().length()>2){
                scene_switch_2.setText(switchsBeans.get(1).getSceneName().substring(0,2)+"...");
            } else {
                scene_switch_2.setText(switchsBeans.get(1).getSceneName());
            }
//            scene_switch_2.setText(switchsBeans.get(1).getSceneName());
//            scene_switch_2.setTextColor(switchsBeans.get(1).isSceneState()?(getResources().getColor(R.color.color_598DF3)):(getResources().getColor(R.color.color_1F8E8E93)));
            scene_switch_2.setTextColor(getResources().getColor(R.color.color_598DF3));
            six_bit_switch_2.setImageResource(switchsBeans.get(1).isSceneState() ? R.drawable.icon_iot_scene_bit_switch_open : R.drawable.icon_iot_scene_bit_switch_close);
            scene_switch_2.setBackgroundResource(switchsBeans.get(1).isSceneState() ? R.drawable.icon_iot_scene_open_bg : R.drawable.trans_bg);
        }
        if (StringUtils.isEmpty(switchsBeans.get(2).getSceneId())) {
            scene_switch_3.setText("未关联");
            scene_switch_3.setTextColor(getResources().getColor(R.color.color_9E9E9E));
            six_bit_switch_3.setImageResource(R.drawable.icon_iot_scene_bit_switch_close);
            scene_switch_3.setBackgroundResource(R.drawable.trans_bg);
        } else {
            if(switchsBeans.get(2).getSceneName().length()>2){
                scene_switch_3.setText(switchsBeans.get(2).getSceneName().substring(0,2)+"...");
            } else {
                scene_switch_3.setText(switchsBeans.get(2).getSceneName());
            }
//            scene_switch_3.setText(switchsBeans.get(2).getSceneName());
//            scene_switch_3.setTextColor(switchsBeans.get(2).isSceneState()?(getResources().getColor(R.color.color_598DF3)):(getResources().getColor(R.color.color_1F8E8E93)));
            scene_switch_3.setTextColor(getResources().getColor(R.color.color_598DF3));
            six_bit_switch_3.setImageResource(switchsBeans.get(2).isSceneState() ? R.drawable.icon_iot_scene_bit_switch_open : R.drawable.icon_iot_scene_bit_switch_close);
            scene_switch_3.setBackgroundResource(switchsBeans.get(2).isSceneState() ? R.drawable.icon_iot_scene_open_bg : R.drawable.trans_bg);
        }
        if (StringUtils.isEmpty(switchsBeans.get(3).getSceneId())) {
            scene_switch_4.setText("未关联");
            scene_switch_4.setTextColor(getResources().getColor(R.color.color_9E9E9E));
            six_bit_switch_4.setImageResource(R.drawable.icon_iot_scene_bit_switch_close);
            scene_switch_4.setBackgroundResource(R.drawable.trans_bg);
        } else {
            if(switchsBeans.get(3).getSceneName().length()>2){
                scene_switch_4.setText(switchsBeans.get(3).getSceneName().substring(0,2)+"...");
            } else {
                scene_switch_4.setText(switchsBeans.get(3).getSceneName());
            }
//            scene_switch_4.setText(switchsBeans.get(3).getSceneName());
//            scene_switch_4.setTextColor(switchsBeans.get(3).isSceneState()?(getResources().getColor(R.color.color_598DF3)):(getResources().getColor(R.color.color_1F8E8E93)));
            scene_switch_4.setTextColor(getResources().getColor(R.color.color_598DF3));
            six_bit_switch_4.setImageResource(switchsBeans.get(3).isSceneState() ? R.drawable.icon_iot_scene_bit_switch_open : R.drawable.icon_iot_scene_bit_switch_close);
            scene_switch_4.setBackgroundResource(switchsBeans.get(3).isSceneState() ? R.drawable.icon_iot_scene_open_bg : R.drawable.trans_bg);
        }
//        if (StringUtils.isEmpty(switchsBeans.get(4).getSceneId())) {
//            scene_switch_5.setText("未关联");
//            scene_switch_5.setTextColor(getResources().getColor(R.color.color_1F8E8E93));
//            six_bit_switch_5.setImageResource(R.drawable.icon_iot_single_bit_switch_close);
//        } else {
//            if(switchsBeans.get(4).getSceneName().length()>2){
//                scene_switch_5.setText(switchsBeans.get(4).getSceneName().substring(0,2)+"...");
//            } else {
//                scene_switch_5.setText(switchsBeans.get(4).getSceneName());
//            }
////            scene_switch_5.setText(switchsBeans.get(4).getSceneName());
//            scene_switch_5.setTextColor(switchsBeans.get(4).isSceneState()?(getResources().getColor(R.color.color_598DF3)):(getResources().getColor(R.color.color_1F8E8E93)));
//            six_bit_switch_5.setImageResource(switchsBeans.get(4).isSceneState() ? R.drawable.icon_iot_single_bit_switch_open : R.drawable.icon_iot_single_bit_switch_close);
//        }
//        if (StringUtils.isEmpty(switchsBeans.get(5).getSceneId())) {
//            scene_switch_6.setText("未关联");
//            scene_switch_6.setTextColor(getResources().getColor(R.color.color_1F8E8E93));
//            six_bit_switch_6.setImageResource(R.drawable.icon_iot_single_bit_switch_close);
//        } else {
//            if(switchsBeans.get(5).getSceneName().length()>2){
//                scene_switch_6.setText(switchsBeans.get(5).getSceneName().substring(0,2)+"...");
//            } else {
//                scene_switch_6.setText(switchsBeans.get(5).getSceneName());
//            }
////            scene_switch_6.setText(switchsBeans.get(5).getSceneName());
//            scene_switch_6.setTextColor(switchsBeans.get(5).isSceneState()?(getResources().getColor(R.color.color_598DF3)):(getResources().getColor(R.color.color_1F8E8E93)));
//            six_bit_switch_6.setImageResource(switchsBeans.get(5).isSceneState() ? R.drawable.icon_iot_single_bit_switch_open : R.drawable.icon_iot_single_bit_switch_close);
//        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.top_back) {
            finish();
        } else if (v.getId() == R.id.top_btn) {
            // 管理
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean", deviceInfoBean);
            startActivity(intent);
        } else if (v.getId() == R.id.six_scene_switch_one) {
            Intent intent = new Intent(this, SceneSwitchKeyActivity.class);
            intent.putExtra("deviceInfoBean", deviceInfoBean);
            intent.putExtra("switchKey","键位一");
            intent.putExtra("switchBean",switchsBeans.get(0));
            startActivity(intent);
        } else if (v.getId() == R.id.six_scene_switch_two) {
            Intent intent = new Intent(this, SceneSwitchKeyActivity.class);
            intent.putExtra("deviceInfoBean", deviceInfoBean);
            intent.putExtra("switchKey","键位二");
            intent.putExtra("switchBean",switchsBeans.get(1));
            startActivity(intent);
        } else if (v.getId() == R.id.six_scene_switch_three) {
            Intent intent = new Intent(this, SceneSwitchKeyActivity.class);
            intent.putExtra("deviceInfoBean", deviceInfoBean);
            intent.putExtra("switchKey","键位三");
            intent.putExtra("switchBean",switchsBeans.get(2));
            startActivity(intent);
        } else if (v.getId() == R.id.six_scene_switch_four) {
            Intent intent = new Intent(this, SceneSwitchKeyActivity.class);
            intent.putExtra("deviceInfoBean", deviceInfoBean);
            intent.putExtra("switchKey","键位四");
            intent.putExtra("switchBean",switchsBeans.get(3));
            startActivity(intent);
        }
//        else if (v.getId() == R.id.six_scene_switch_five) {
//            Intent intent = new Intent(this, SceneSwitchKeyActivity.class);
//            intent.putExtra("deviceInfoBean", deviceInfoBean);
//            intent.putExtra("switchKey","键位五");
//            intent.putExtra("switchBean",switchsBeans.get(4));
//            startActivity(intent);
//        } else if (v.getId() == R.id.six_scene_switch_six) {
//            Intent intent = new Intent(this, SceneSwitchKeyActivity.class);
//            intent.putExtra("deviceInfoBean", deviceInfoBean);
//            intent.putExtra("switchKey","键位六");
//            intent.putExtra("switchBean",switchsBeans.get(5));
//            startActivity(intent);
//        }
        else if(v.getId() == R.id.six_bit_switch_one){
            if(StringUtils.isEmpty(switchsBeans.get(0).getSceneId())){

            } else {
                executeScene(familyId,switchsBeans.get(0).getSceneId(),switchsBeans.get(0).getTaskType());
            }
        } else if(v.getId() == R.id.six_bit_switch_two){
            if(StringUtils.isEmpty(switchsBeans.get(1).getSceneId())){

            } else {
                executeScene(familyId,switchsBeans.get(1).getSceneId(),switchsBeans.get(1).getTaskType());
            }
        } else if(v.getId() == R.id.six_bit_switch_three){
            if(StringUtils.isEmpty(switchsBeans.get(2).getSceneId())){

            } else {
                executeScene(familyId,switchsBeans.get(2).getSceneId(),switchsBeans.get(2).getTaskType());
            }
        } else if(v.getId() == R.id.six_bit_switch_four){
            if(StringUtils.isEmpty(switchsBeans.get(3).getSceneId())){

            } else {
                executeScene(familyId,switchsBeans.get(3).getSceneId(),switchsBeans.get(3).getTaskType());
            }
        }
//        else if(v.getId() == R.id.six_bit_switch_five){
//            if(StringUtils.isEmpty(switchsBeans.get(4).getSceneId())){
//
//            } else {
//                executeScene(familyId,switchsBeans.get(4).getSceneId(),switchsBeans.get(4).getTaskType());
//            }
//        } else if(v.getId() == R.id.six_bit_switch_six){
//            if(StringUtils.isEmpty(switchsBeans.get(5).getSceneId())){
//
//            } else {
//                executeScene(familyId,switchsBeans.get(5).getSceneId(),switchsBeans.get(5).getTaskType());
//            }
//        }
    }

    private void getScenePanelMsg(String deviceId, String deviceType, String familyId, String userId) {
        OkGo.<String>get(Constant.HOST + Constant.Device_getScenePanelMsg)
                .params("deviceId", deviceId)
                .params("deviceType", deviceType)
                .params("familyId", familyId)
                .params("userId", userId)
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        loadingDialog.show();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })
                .map(new Function<Response<String>, ScenePanelMsgVo>() {
                    @Override
                    public ScenePanelMsgVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        ScenePanelMsgVo obj = null;
                        if (resp != null && resp.getErrorCode().equalsIgnoreCase("200")) {
                            obj = JSON.parseObject(resp.getResult(), ScenePanelMsgVo.class);
                        }
                        LogUtils.e("resp", resp.getState() + "=====");
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<ScenePanelMsgVo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(ScenePanelMsgVo resp) {
                        if (resp != null) {
                            switchsBeans.clear();
                            switchsBeans.addAll(resp.getScenePanelMsg().getSwitchs());
                            initSwitch();

                            sensorMsgs.clear();
                            List<String> records = resp.getScenePanelMsg().getRecords();
                            if (records.size()>0){
                                for (int i = 0; i < records.size(); i++) {
                                    sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),records.get(i).split("_")[1]));
                                }
                                sceneMsgAdapter.setNewData(sensorMsgs);
                            }
                            loadingDialog.dismiss();
                            /*for (int i = 0; i < records.size(); i++) {
                                if (records.get(i).endsWith("1")){
                                    if (switchsBeans.get(1).getSceneName().isEmpty()){
                                        sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),"未关联"));
                                    }else
                                    sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),switchsBeans.get(1).getSceneName()));
                                }else if (records.get(i).endsWith("2")){
                                    if (switchsBeans.get(2).getSceneName().isEmpty()){
                                        sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),"未关联"));
                                    }else
                                    sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),switchsBeans.get(2).getSceneName()));
                                } else if (records.get(i).endsWith("3")){
                                    if (switchsBeans.get(3).getSceneName().isEmpty()){
                                        sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),"未关联"));
                                    }else {
                                        sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),switchsBeans.get(3).getSceneName()));
                                    }
                                } else if (records.get(i).endsWith("4")){
                                    if (switchsBeans.get(4).getSceneName().isEmpty()){
                                        sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),"未关联"));
                                    }else {
                                        sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),switchsBeans.get(4).getSceneName()));
                                    }
                                }else if (records.get(i).endsWith("0")){
                                    if (switchsBeans.get(1).getSceneName().isEmpty()){
                                        sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),"未关联"));
                                    }else {
                                        sensorMsgs.add(new SensorInfoVo.SensorMessages(records.get(i),switchsBeans.get(0).getSceneName()));
                                    }
                                }
                            }
                            sceneMsgAdapter.setNewData(sensorMsgs);*/
                        }

                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    private void executeScene(String familyId,String sceneId,String taskType) {
        LogUtils.e("海令六路场景开关executeScene", "executeScene: "+familyId+"---"+sceneId+"---"+taskType );
        OkGo.<String>get(Constant.HOST + Constant.smartExecuteScene)
                .params("familyId", familyId)
                .params("sceneId", sceneId)
                .params("taskType", taskType)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        loadingDialog.show();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        LogUtils.e("resp", resp.getState() + "=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        if (resp.getErrorCode().equalsIgnoreCase("200")) {
                            ToastUtil.show(SceneSixSwitchActivity.this, "执行场景成功");
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                            EventBus.getDefault().post("RefreshSceneSixSwitchActivity");
                        } else {
                            ToastUtil.show(SceneSixSwitchActivity.this, "请求失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SceneSixSwitchActivity.this, "请求失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(String msg) {
        if (msg.equalsIgnoreCase("RefreshSceneSixSwitchActivity")) {
            getScenePanelMsg(deviceInfoBean.getDeviceId(), deviceInfoBean.getDeviceType(), familyId, userId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
