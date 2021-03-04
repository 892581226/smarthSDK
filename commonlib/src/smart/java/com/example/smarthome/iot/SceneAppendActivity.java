package com.example.smarthome.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.SceneDeviceAdapter;
import com.example.smarthome.iot.entry.AddSceneVo;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SceneListVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.CircleImageView;
import com.xhwl.commonlib.uiutils.ClearEditText;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.UiTools;
import com.xhwl.commonlib.uiutils.udpsocket.ReceiveUtils;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author: glq
 * date: 2019/4/8 13:39
 * description: 添加场景
 * update: 2019/4/8
 * version: V1.4.1
 */
public class SceneAppendActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private static final String TAG = "SceneAppendActivity";
    private ImageView mTopBackImg;
    private TextView mTopTitle;

    /**
     * 添加场景
     */
    private ClearEditText mSceneAppendName;
    private TextView mSceneAddTv;
    private TextView mTopBtn,sceneDeviceManage;
    private CircleImageView mSceneAppendIcon;
    private LinearLayout mSceneAppendIconLinear;
    private String familyId,userId;
    private String iconName = "icon_iot_scene_go_home";
    private final static int REQUESTCODE = 1; // icon请求码
    private final static int DEVICE_REQUESTCODE = 2; // dev请求码

    private RecyclerView mRoomAppendDeviceRv;
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> deviceInfoBeans = new ArrayList<>();
    private SceneDeviceAdapter sceneDeviceAdapter;
    private final static int REQUEST_SWITCH_CODE = 3;//开关请求码
    private int scenesNum;
    private List<SmartInfoVo.FamilysBean.ScenesBean> existScenes;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_scene_append);
        initView();
        initDate();
    }

    private void initDate() {
        scenesNum = SPUtils.get(this, "scenesNum"+userId, 0);
        String sceneList = SPUtils.get(this, "sceneList"+userId, "");
        if (!StringUtils.isEmpty(sceneList)) {
            existScenes = JSONObject.parseArray(sceneList, SmartInfoVo.FamilysBean.ScenesBean.class);
        }
        sceneDeviceAdapter = new SceneDeviceAdapter(deviceInfoBeans);
        sceneDeviceAdapter.setOnItemChildClickListener(this);
        sceneDeviceAdapter.bindToRecyclerView(mRoomAppendDeviceRv);
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
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId"+userId, "");
        mTopBackImg = (ImageView) findViewById(R.id.top_back_img);
        mTopBackImg.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mSceneAppendName = findViewById(R.id.scene_append_name);
        mSceneAddTv = findViewById(R.id.scene_add_tv);
        mSceneAddTv.setOnClickListener(this);
        mTopTitle.setText("新建场景");
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTopBtn.setText("保存");
        sceneDeviceManage = findViewById(R.id.scene_append_device_manage);
        sceneDeviceManage.setOnClickListener(this);
        mSceneAppendIcon = (CircleImageView) findViewById(R.id.scene_append_icon);
        mSceneAppendIconLinear = (LinearLayout) findViewById(R.id.scene_append_icon_linear);
        mSceneAppendIconLinear.setOnClickListener(this);
        mRoomAppendDeviceRv = (RecyclerView) findViewById(R.id.room_append_device_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRoomAppendDeviceRv.setLayoutManager(manager);
        //当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小。
        mRoomAppendDeviceRv.setHasFixedSize(true);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back_img) {
            finish();
        } else if (i == R.id.top_btn) {// 保存场景
            // 保存场景
            if (scenesNum < 20) {
                if (!StringUtils.isEmpty(familyId) && !StringUtils.isEmpty(mSceneAppendName.getText().toString().trim())) {
                    if (checkSceneNameRepeat("",mSceneAppendName.getText().toString().trim(),existScenes)) {
                        ToastUtil.show(SceneAppendActivity.this,"与其他场景名称相同");
                        return;
                    }

                    timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            addScene();
                        }
                    };
                    timer.schedule(timerTask,500,500);


                    loadingDialog.show();
                } else {
                    ToastUtil.show(SceneAppendActivity.this,"场景名称为空");
                }
            } else {
                ToastUtil.show(SceneAppendActivity.this,"小主，我还在学习创建更多场景哟");
            }
        } else if (i == R.id.scene_add_tv) {// 场景添加设备
//                startToActivity(DeviceAllActivity.class);
            mIntent.setClass(this, DeviceAllActivity.class);
            mIntent.putExtra("from", "SceneAppendActivity");
            mIntent.putParcelableArrayListExtra("deviceInfoBeans", (ArrayList<? extends Parcelable>) deviceInfoBeans);
            startActivityForResult(mIntent, DEVICE_REQUESTCODE);
        } else if (i == R.id.scene_append_icon_linear) {
            mIntent.setClass(this, IconUpdateActivity.class);
            mIntent.putExtra("isRoomIcon", false);
            mIntent.putExtra("isUpdate", false);
            startActivityForResult(mIntent, REQUESTCODE);
        } else if (i == R.id.scene_append_device_manage){
            mIntent.setClass(this, DeviceAllActivity.class);
            mIntent.putParcelableArrayListExtra("deviceInfoBeans", (ArrayList<? extends Parcelable>) deviceInfoBeans);
            mIntent.putExtra("from", "SceneAppendActivity");
            startActivityForResult(mIntent, DEVICE_REQUESTCODE);
        }
    }

    public static boolean checkSceneNameRepeat(String id,String name,List<SmartInfoVo.FamilysBean.ScenesBean> list) {
        if (list != null && list.size() > 0) {
            for (SmartInfoVo.FamilysBean.ScenesBean scenesBean : list) {
                if (scenesBean.getSceneName().equals(name) && !scenesBean.getSceneId().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addScene() {
        AddSceneVo addSceneVo = new AddSceneVo();
        addSceneVo.setFamilyId(familyId);
        addSceneVo.setSceneIcon(iconName);
        addSceneVo.setSceneName(mSceneAppendName.getText().toString().trim());
        addSceneVo.setTaskType("once");
        addSceneVo.setTasks(getTasks(deviceInfoBeans));
//        NetWorkWrapper.smartAddScene(addSceneVo, new HttpHandler<String>() {
//
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                loadingDialog.dismiss();
//                try {
//                    String httpResponse = response.body().string();
//                    LogUtils.e("onResponse----", httpResponse + "=====");
//                    EventBus.getDefault().post(new UpdateFamilyEvent(true));
//                    finish();
//                    Looper.prepare();
//                    ToastUtil.showCenterToast("新建成功");
//                    Looper.loop();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                super.onFailure(call, e);
//                mHandler.sendEmptyMessage(202);
//            }
//        });

        OkGo.<String>post(Constant.HOST+Constant.Scene_add)
                .upJson(JSON.toJSONString(addSceneVo))
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })//
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);

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
                        if(resp.getErrorCode().equalsIgnoreCase("200")){
                            ToastUtil.show(SceneAppendActivity.this,"新建成功");
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                            timer.cancel();
                            finish();
                        } else {
                            ToastUtil.show(SceneAppendActivity.this,"请求失败");
                        }
                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SceneAppendActivity.this,"请求失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete","============");
                    }
                });
    }

    public static List<String> getTasks(List<SmartInfoVo.FamilysBean.DeviceInfoBean> devBeans) {
        List<String> tasks = new ArrayList<>();
        if (devBeans != null && devBeans.size() > 0) {
            for (int i = 0; i < devBeans.size(); i++) {
                SmartInfoVo.FamilysBean.DeviceInfoBean bean = devBeans.get(i);
                String field="";
                Log.e("场景更新",bean.getField().toString());
//                if (devBeans.get(i).getDeviceType().equalsIgnoreCase(DeviceType.HILINK_CURTAIN)){
//                    JSONObject jsonState=new JSONObject();
//                    if (bean.getField().getState()!=null){
//                        if (bean.getField().getState().equalsIgnoreCase("1")){
//                            jsonState.put("state",1);
//                        }else {
//                            jsonState.put("state",0);
//                        }
//                    }
//
//                    field=JSON.toJSONString(jsonState);
//                }else
//                    {
                field = JSON.toJSONString(bean.getField());
//                }
                LogUtils.e("getTasks field", field + "-----------");
                String fieldBase64 = Base64.encodeToString(field.getBytes(), Base64.NO_WRAP);
                LogUtils.e("fieldBase64", fieldBase64 + "-------------------");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("taskId", "");
                if (bean.getDelayTime() > 0) {
                    jsonObject.put("isdelayed", true);
                } else {
                    jsonObject.put("isdelayed", false);
                }
                jsonObject.put("delayTime", bean.getDelayTime());
                jsonObject.put("deviceName", bean.getDeviceName());
                jsonObject.put("deviceDesc", "");
                jsonObject.put("deviceIcon", bean.getDeviceIcon());
                jsonObject.put("deviceId", bean.getDeviceId());
                jsonObject.put("deviceType", bean.getDeviceType());
                jsonObject.put("gatewayId", bean.getGatewayId());//网关id
                jsonObject.put("supplierId", bean.getSupplierId());//供应商id
                jsonObject.put("field", fieldBase64);
                //
                String taskBase64 = Base64.encodeToString(JSON.toJSONString(jsonObject).getBytes(), Base64.NO_WRAP);
                Log.e("taskBase64",jsonObject.toJSONString());
                LogUtils.e("taskBase64", taskBase64 + "----==============");
                tasks.add(taskBase64);
            }
        }
        return tasks;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActionConstant.ADD_SCENE_RESULT_CODE) {
            if (requestCode == REQUESTCODE) {
                //图片
                int icon = UiTools.getResource(SceneAppendActivity.this,data.getStringExtra("iconName"), "drawable", R.drawable.icon_iot_scene_more);
                mSceneAppendIcon.setImageResource(icon);
                iconName = getResources().getResourceEntryName(icon);
            } else if (requestCode == DEVICE_REQUESTCODE) {

                //deviceInfoBeans.clear();
                //设备
                deviceInfoBeans.addAll(data.getParcelableArrayListExtra("checkedDeviceList"));
                for (int i = 0; i < deviceInfoBeans.size(); i++) {
                    for (int j = 0; j < i; j++) {
                        if(deviceInfoBeans.get(i).getDeviceType().equals(deviceInfoBeans.get(j).getDeviceType())){
                            deviceInfoBeans.remove(i);
                            i=i-1;
                        }
                    }
                }
                sceneDeviceAdapter.notifyDataSetChanged();
                if(deviceInfoBeans.size()>0){
                    mSceneAddTv.setVisibility(View.GONE);
                    sceneDeviceManage.setVisibility(View.VISIBLE);
                } else {
                    mSceneAddTv.setVisibility(View.VISIBLE);
                    sceneDeviceManage.setVisibility(View.GONE);
                }
            }
        } else if (resultCode == ActionConstant.SCENE_SWITCH_RESULT_CODE) {
            if (requestCode == REQUEST_SWITCH_CODE) {
                int switchPosStatus = data.getIntExtra("switchPosStatus", -1);
                if (switchPosStatus != -1) {
                    deviceInfoBeans.set(switchPosStatus, data.getParcelableExtra("deviceInfoBeanStatus"));
                    sceneDeviceAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int i = view.getId();
        if (i == R.id.scene_item_time) {
            TimePickerView timePickerView = new TimePickerBuilder(SceneAppendActivity.this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    TextView timeView = (TextView) adapter.getViewByPosition(position, R.id.scene_item_time);
                    if (date.getSeconds() >= 0 && date.getSeconds() < 10) {
                        timeView.setText("延时0" + date.getSeconds() + "秒");
                    } else {
                        timeView.setText("延时" + date.getSeconds() + "秒");
                    }
                    int delayTime = date.getSeconds() * 1000;//毫秒
                    deviceInfoBeans.get(position).setDelayTime(delayTime);
                }
            }).setType(new boolean[]{false, false, false, false, false, true}).build();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 0);
            timePickerView.setDate(calendar);
            timePickerView.show();
        } else if (i == R.id.scene_item_device_delete_layout) {
            deviceInfoBeans.remove(position);
            sceneDeviceAdapter.notifyDataSetChanged();
        } else if (i == R.id.scene_item_ll) {
            if (deviceInfoBeans != null && deviceInfoBeans.size() > 0) {
                SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean = deviceInfoBeans.get(position);
                if (deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_2)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_3)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_4)

                        //plc
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_1)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_2)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_3)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_4)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_86_RECEPTACLE)

                        // 鸿雁开关控制
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_1)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_2)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_3)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_4)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_SMAET_SOCKET)

                        //控客开关
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.KONGKE_SWITCH_1)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.KONGKE_SWITCH_2)
                        || deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.KONGKE_SWITCH_3)
                )
                {
                    //开关
                    Intent intent = new Intent(this, SceneSwitchStatusActivity.class);
                    Log.e("TAG", "onItemChildClick: "+deviceInfoBean.toString() );
                    intent.putExtra("deviceInfoBean", deviceInfoBean);
                    intent.putExtra("switchPos", position);
                    startActivityForResult(intent, REQUEST_SWITCH_CODE);
                }else if (deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_CURTAIN)
                        ||deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_DOUBLE_CURTAIN)
                ){
                    Intent intent = new Intent(this, SceneSwitchStatusActivity_WindowCurtains.class);
                    intent.putExtra("deviceInfoBean", deviceInfoBean);
                    intent.putExtra("switchPos", position);
                    startActivityForResult(intent, REQUEST_SWITCH_CODE);
                }
                else {
                    //其他设备
                    ToastUtil.show(this,"暂不支持");
                }
            }
        }

//        return false;
    }
}
