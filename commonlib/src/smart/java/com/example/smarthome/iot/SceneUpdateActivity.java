package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.smarthome.iot.entry.SceneDetailVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.impl.device.negativecontrol.dialog.ZloadPasswordDialog;
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
import com.xhwl.commonlib.uiutils.MaxTextLengthFilter;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.UiTools;
import com.xhwl.commonlib.uiutils.dialog.BaseDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author: glq
 * date: 2019/4/8 13:52
 * description: 场景编辑
 * update: 2019/4/8
 * version: V1.4.1
 */
public class SceneUpdateActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener, BaseQuickAdapter.OnItemChildClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    /**
     * 出门上班
     */
    private ClearEditText mSceneUpdateName;
    private ConstraintLayout mSceneUpdateNameInfo;
    private CircleImageView mSceneIcon;
    private RecyclerView mSceneUpdateDeviceRv;
    //    private DeviceManageAdapter mManageAdapter;
    private String sceneModel;
    /**
     * 添加设备
     */
    private TextView mSceneUpdateAddDevice;
    /**
     * 保存
     */
    private Button mSceneUpdateSaveBtn;
//    private SceneManageItemVo.Item mSceneManageItemVo;
    /**
     * 管理设备
     */
    private TextView mSceneUpdateDeviceManage;
    private final static int REQUESTCODE = 1; // 返回的结果码
    private String iconName = "icon_iot_scene_go_home";
    private BaseDialog mBaseDialog;
    private boolean defaultScene = true;
    private String sceneId,familyId,userId;
    private SmartInfoVo.FamilysBean.ScenesBean scenesBean;
    private SceneDeviceAdapter deviceAdapter;
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> deviceInfoBeans = new ArrayList<>();
    private final static int DEVICE_REQUESTCODE = 2; // dev请求码
    private final static int REQUEST_SWITCH_CODE = 3;//开关请求码
    private List<SmartInfoVo.FamilysBean.ScenesBean> existScenes;
    private ZloadPasswordDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_scene_update);
        initView();
        initDate();
    }

    private void initDate() {
        mTopTitle.setText("场景编辑");
        //mTopBtn.setText("删除");
        mTopBtn.setVisibility(View.GONE);
        mBaseDialog = new BaseDialog(this, 1)
                .setTitleText("提示")
                .setInfoText("确定删除该场景模式?")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mBaseDialog.setCancelable(false);

        deviceAdapter = new SceneDeviceAdapter(deviceInfoBeans);
        deviceAdapter.setOnItemChildClickListener(this);
        mSceneUpdateDeviceRv.setAdapter(deviceAdapter);
        deviceAdapter.bindToRecyclerView(mSceneUpdateDeviceRv);

        String sceneList = SPUtils.get(this, "sceneList"+userId, "");
        if (!StringUtils.isEmpty(sceneList)) {
            existScenes = JSONObject.parseArray(sceneList, SmartInfoVo.FamilysBean.ScenesBean.class);
        }
        getSceneDetail();
    }

    private void getSceneDetail() {
//        NetWorkWrapper.smartGetSceneInfo(familyId, sceneId, new HttpHandler<SceneDetailVo>() {
//            @Override
//            public void onSuccess(ServerTip serverTip, SceneDetailVo bean) {
//                scenesBean = bean.getScene();
//                LogUtils.e("getSceneDetail",scenesBean+"================");
//                if(scenesBean!=null){
//                    mSceneUpdateName.setText(scenesBean.getSceneName());
//                    mSceneIcon.setImageResource(MyAPP.getIns().getResource(scenesBean.getIcon(), "drawable", R.drawable.icon_iot_scene_more));
//                    if(scenesBean.getTasks()!=null&&scenesBean.getTasks().size()>0){
//                        for(int i=0;i<scenesBean.getTasks().size();i++){
//                            SmartInfoVo.FamilysBean.DeviceInfoBean deviceBean = scenesBean.getTasks().get(i).getDeviceInfo();
//                            deviceBean.setDelayTime(scenesBean.getTasks().get(i).getTaskInfo().getDelayTime());
//                            deviceInfoBeans.add(deviceBean);
//                        }
//                    }
//                    deviceAdapter.notifyDataSetChanged();
//                    if(deviceInfoBeans.size()>0){
//                        mSceneUpdateDeviceRv.setVisibility(View.VISIBLE);
//                        mSceneUpdateDeviceManage.setVisibility(View.VISIBLE);
//                        mSceneUpdateAddDevice.setVisibility(View.GONE);
//                    } else {
//                        mSceneUpdateDeviceRv.setVisibility(View.GONE);
//                        mSceneUpdateDeviceManage.setVisibility(View.GONE);
//                        mSceneUpdateAddDevice.setVisibility(View.VISIBLE);
//                    }
//
////                    deviceInfoBeans
//                }
//            }
//        });

        Log.e("场景选择","familyId:"+familyId+"sceneId:"+sceneId);
        OkGo.<String>get(Constant.HOST+Constant.Scene_get)
                .params("familyId",familyId)
                .params("sceneId",sceneId)
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })//
                .map(new Function<Response<String>, SceneDetailVo>() {
                    @Override
                    public SceneDetailVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        SceneDetailVo obj = null;
                        if(resp.getErrorCode().equalsIgnoreCase("200")){
                            obj = JSON.parseObject(resp.getResult(),SceneDetailVo.class);
                        }
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<SceneDetailVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                    }

                    @Override
                    public void onNext(SceneDetailVo sceneDetailVo) {
                        if(sceneDetailVo == null){
                            return;
                        }
                        scenesBean = sceneDetailVo.getScene();


                        if(scenesBean!=null){
                            mSceneUpdateName.setText(scenesBean.getSceneName());
                            iconName = scenesBean.getIcon();
                            mSceneIcon.setImageResource(UiTools.getResource(SceneUpdateActivity.this,scenesBean.getIcon(), "drawable", R.drawable.icon_iot_scene_go_home));
                            if(scenesBean.getTasks()!=null&&scenesBean.getTasks().size()>0){
                                for(int i=0;i<scenesBean.getTasks().size();i++){
                                    SmartInfoVo.FamilysBean.DeviceInfoBean deviceBean = scenesBean.getTasks().get(i).getDeviceInfo();
                                    deviceBean.setDelayTime(scenesBean.getTasks().get(i).getTaskInfo().getDelayTime());
                                    Log.e("场景选择",deviceBean.toString());
                                    deviceInfoBeans.add(deviceBean);
                                }
                            }
                            deviceAdapter.notifyDataSetChanged();
                            if(deviceInfoBeans.size()>0){
                                mSceneUpdateDeviceRv.setVisibility(View.VISIBLE);
                                mSceneUpdateDeviceManage.setVisibility(View.VISIBLE);
                                mSceneUpdateAddDevice.setVisibility(View.GONE);
                            } else {
                                mSceneUpdateDeviceRv.setVisibility(View.GONE);
                                mSceneUpdateDeviceManage.setVisibility(View.GONE);
                                mSceneUpdateAddDevice.setVisibility(View.VISIBLE);
                            }
                        }
                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SceneUpdateActivity.this,"请求失败");
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();


                    }
                });
    }

    private void initView() {
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this,"familyId"+userId,"");
        sceneId = getIntent().getStringExtra("sceneId");
        defaultScene = getIntent().getBooleanExtra("defaultScene",true);

        mTopBack =findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mSceneUpdateName = findViewById(R.id.scene_update_name);
        mSceneUpdateName.setCursorVisible(false); //设置输入框不可编辑、光标不可见
        mSceneUpdateName.setClearIconVisible(false); //设置清楚icon是否显示
        mSceneUpdateName.clearFocus(); // 设置不弹出软件盘
        mSceneUpdateName.setFilters(new InputFilter[]{new MaxTextLengthFilter(10)});
        mSceneUpdateName.setOnClickListener(this);
        mSceneUpdateNameInfo = (ConstraintLayout) findViewById(R.id.scene_update_name_info);
        mSceneIcon = (CircleImageView) findViewById(R.id.scene_icon);
        mSceneIcon.setOnClickListener(this);
        mSceneUpdateDeviceRv = (RecyclerView) findViewById(R.id.scene_update_device_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mSceneUpdateDeviceRv.setLayoutManager(manager);
        mSceneUpdateAddDevice = (TextView) findViewById(R.id.scene_update_device_text);
        mSceneUpdateAddDevice.setOnClickListener(this);
        mSceneUpdateSaveBtn = (Button) findViewById(R.id.scene_update_save_btn);
        mSceneUpdateSaveBtn.setOnClickListener(this);
        mSceneUpdateDeviceManage = (TextView) findViewById(R.id.scene_update_device_manage);
        mSceneUpdateDeviceManage.setOnClickListener(this);
        StringUtils.setCanNotEditNoClick(mSceneUpdateName); // 默认取消输入框的输入

        if (defaultScene) {
            mTopBtn.setVisibility(View.GONE);
        } else {
            mTopBtn.setVisibility(View.VISIBLE);
        }

//        loadingDialog = new ZLoadingDialog(this);
//        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
//                .setLoadingColor(Color.BLUE)//颜色
//                .setHintText("Loading...")
//                .setHintTextSize(16) // 设置字体大小 dp
//                .setHintTextColor(Color.GRAY)  // 设置字体颜色
//                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
//                .setCanceledOnTouchOutside(false);
        //loadingDialog = new ZloadPasswordDialog(this,R.style.dialog);/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (loadingDialog==null){
//            loadingDialog = new ZLoadingDialog(this);
//            loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
//                    .setLoadingColor(Color.BLUE)//颜色
//                    .setHintText("Loading...")
//                    .setHintTextSize(16) // 设置字体大小 dp
//                    .setHintTextColor(Color.GRAY)  // 设置字体颜色
//                    .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
//                    .setCanceledOnTouchOutside(false);
//        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {
            finish();
        } else if (i == R.id.scene_update_name) {// 更新场景名称
            if (defaultScene) {
                return;
            } else {
                StringUtils.setCanEdit(mSceneUpdateName); // 设置输入框可输入
                mSceneUpdateName.setCursorVisible(true);
                mSceneUpdateName.setClearIconVisible(true); //设置清除icon是否显示
            }
        } else if (i == R.id.scene_icon) {// 跳转页面更新icon
            if (defaultScene) {
                //默认场景不让修改图标和名字
                return;
            } else {
                mIntent.setClass(this, IconUpdateActivity.class);
                mIntent.putExtra("isRoomIcon", false);
                mIntent.putExtra("isUpdate", true);
                startActivityForResult(mIntent, REQUESTCODE);

            }
        } else if (i == R.id.scene_update_device_text) {// 添加设备
            mIntent.setClass(this, DeviceAllActivity.class);
            mIntent.putParcelableArrayListExtra("deviceInfoBeans", (ArrayList<? extends Parcelable>) deviceInfoBeans);
            mIntent.putExtra("from", "SceneUpdateActivity");
            startActivityForResult(mIntent, DEVICE_REQUESTCODE);

        } else if (i == R.id.scene_update_save_btn) {// 保存
            // 保存
            if (!StringUtils.isEmpty(mSceneUpdateName.getText().toString().trim())) {
                if(SceneAppendActivity.checkSceneNameRepeat(sceneId,mSceneUpdateName.getText().toString().trim(),existScenes)){
                    ToastUtil.show(SceneUpdateActivity.this,"与其他场景名称相同");
                    return;
                }
                updateScene();
                if (!isFinishing()){
                    //loadingDialog.show();
                }
            } else {
                ToastUtil.show(SceneUpdateActivity.this,"场景名称为空");
            }
        } else if (i == R.id.scene_update_device_manage) {//                startToActivity(DeviceAllActivity.class);
            mIntent.setClass(this, DeviceAllActivity.class);
            mIntent.putParcelableArrayListExtra("deviceInfoBeans", (ArrayList<? extends Parcelable>) deviceInfoBeans);
            mIntent.putExtra("from", "SceneUpdateActivity");

            startActivityForResult(mIntent, DEVICE_REQUESTCODE);
        } else if (i == R.id.top_btn) {
            mBaseDialog.show();
        }
    }

    private void updateScene() {
        AddSceneVo addSceneVo = new AddSceneVo();
        addSceneVo.setSceneId(sceneId);
        addSceneVo.setFamilyId(familyId);
        addSceneVo.setSceneIcon(iconName);
        addSceneVo.setSceneName(mSceneUpdateName.getText().toString().trim());
        addSceneVo.setTaskType("once");
        addSceneVo.setTasks(SceneAppendActivity.getTasks(deviceInfoBeans));
        Log.e("保存场景",deviceInfoBeans.toString()+" "+addSceneVo.toString());
//        NetWorkWrapper.smartUpdateScene(addSceneVo, new HttpHandler<String>() {
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
//                    ToastUtil.showCenterToast("更新成功");
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
        OkGo.<String>post(Constant.HOST+Constant.Scene_update)
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


                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        if(resp.getErrorCode().equalsIgnoreCase("200")){
                            ToastUtil.show(SceneUpdateActivity.this,"更新成功");
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                            finish();
                        } else {
                            ToastUtil.show(SceneUpdateActivity.this,"请求失败");
                        }
                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SceneUpdateActivity.this,"请求失败");
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();


                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActionConstant.UPDATE_SCENE_RESULT_CODE) {
            if (requestCode == REQUESTCODE) {
                //图片
                int icon = UiTools.getResource(SceneUpdateActivity.this,data.getStringExtra("iconName"), "drawable", R.drawable.icon_iot_room_master);
                mSceneIcon.setImageResource(icon);
                iconName = getResources().getResourceEntryName(icon);
            }else if (requestCode == DEVICE_REQUESTCODE) {
                //deviceInfoBeans.clear();   //清空集合28

                //设备
                deviceInfoBeans.addAll(data.getParcelableArrayListExtra("checkedDeviceList"));
                // if (deviceInfoBeans.size()>0){
                for (int i = 0; i < deviceInfoBeans.size(); i++) {
                    for (int j = 0; j < i; j++) {
                        if(deviceInfoBeans.get(i).getDeviceId().equals(deviceInfoBeans.get(j).getDeviceId())){
                            deviceInfoBeans.remove(i);
                            i=i-1;
                        }
                    }
                }
                mSceneUpdateDeviceRv.setVisibility(View.VISIBLE);
                mSceneUpdateDeviceManage.setVisibility(View.VISIBLE);
                mSceneUpdateAddDevice.setVisibility(View.GONE);
                // }
                deviceAdapter.setNewData(deviceInfoBeans);
                deviceAdapter.notifyDataSetChanged();
            }
        } else if (resultCode == ActionConstant.SCENE_SWITCH_RESULT_CODE) {
            if (requestCode == REQUEST_SWITCH_CODE) {
                int switchPosStatus = data.getIntExtra("switchPosStatus", -1);
                if (switchPosStatus != -1) {
                    Log.e("回调页面返回的数据",switchPosStatus+" "+data.getParcelableExtra("deviceInfoBeanStatus").toString());
                    deviceInfoBeans.set(switchPosStatus, data.getParcelableExtra("deviceInfoBeanStatus"));
                    deviceAdapter.setNewData(deviceInfoBeans);
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        }
    }




    @Override
    public void onConfirmClick() {
        mBaseDialog.dismiss();
        //执行删除操作
        deleteScene(familyId,sceneId);
        if (!isFinishing()){
            //loadingDialog.show();
        }
    }

    @Override
    public void onCancelListener() {
        if (mBaseDialog.isShowing()) {
            mBaseDialog.dismiss();
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int i = view.getId();
        if (i == R.id.scene_item_time) {
            TimePickerView timePickerView = new TimePickerBuilder(SceneUpdateActivity.this, new OnTimeSelectListener() {
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
            deviceAdapter.setNewData(deviceInfoBeans);
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
                    Intent intent = new Intent(SceneUpdateActivity.this, SceneSwitchStatusActivity.class);
                    Log.e("TAG", "onItemChildClick: "+deviceInfoBean.toString() );
                    intent.putExtra("deviceInfoBean", deviceInfoBean);
                    intent.putExtra("switchPos", position);
                    startActivityForResult(intent, REQUEST_SWITCH_CODE);
                }else if (deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_CURTAIN)
                        ||deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_DOUBLE_CURTAIN)
                ){
                    Intent intent = new Intent(SceneUpdateActivity.this, SceneSwitchStatusActivity_WindowCurtains.class);
                    intent.putExtra("deviceInfoBean", deviceInfoBean);
                    intent.putExtra("switchPos", position);
                    startActivityForResult(intent, REQUEST_SWITCH_CODE);
                }
                else {
                    //其他设备
                    ToastUtil.show(SceneUpdateActivity.this,"暂不支持");
                }
            }
        }

//        return false;
    }

    private void deleteScene(String familyId, String sceneId){
//        NetWorkWrapper.smartDeleteScene(familyId, sceneId, new HttpHandler<String>() {
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//                loadingDialog.dismiss();
//                ToastUtil.show("删除成功");
//                EventBus.getDefault().post(new UpdateFamilyEvent(true));
//                finish();
//            }
//
//            @Override
//            public void onFailure(ServerTip serverTip) {
//                super.onFailure(serverTip);
//                loadingDialog.dismiss();
//                ToastUtil.show("请求失败");
//            }
//        });

        OkGo.<String>get(Constant.HOST+Constant.Scene_delete)
                .params("familyId",familyId)
                .params("sceneId",sceneId)
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



                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        if(resp.getErrorCode().equalsIgnoreCase("200")){
                            ToastUtil.show(SceneUpdateActivity.this,"删除成功");
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                            finish();
                        } else {
                            ToastUtil.show(SceneUpdateActivity.this,"请求失败");
                        }
                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SceneUpdateActivity.this,"请求失败");
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();

                    }
                });
    }

}
