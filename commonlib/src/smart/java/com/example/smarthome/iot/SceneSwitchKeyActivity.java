package com.example.smarthome.iot;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.AttchSceneAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceInfoVo;
import com.example.smarthome.iot.entry.ScenePanelMsgVo;
import com.example.smarthome.iot.entry.SmartControlVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.net.Constant;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
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
import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author:
 * date:
 * description: 场景面板键位
 * update:
 * version:
 */
public class SceneSwitchKeyActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private View mTitleLine;
    /**
     * 客厅
     */
    private TextView attchSceneName, top_btn;
    private RecyclerView sceneRv;
    /**
     * 保存
     */
    private SmartInfoVo.FamilysBean.RoomsBean mRoomsBean;
    private SmartInfoVo.FamilysBean.DeviceInfoBean mDeviceInfoBean;
    private AttchSceneAdapter attchSceneAdapter;
    private List<SmartInfoVo.FamilysBean.ScenesBean> scenesBeans;
    private boolean isRvShow = true;
    private ImageView scene_switch_scene_icon;
    private String userId, familyId;
    private BaseDialog mBaseDialog;

    private String switchKey;
    private ScenePanelMsgVo.ScenePanelMsgBean.SwitchsBean switchsBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_switch_key);
        initView();
        initDate();
    }

    private void initDate() {
        mTopTitle.setText(switchKey);
        mTitleLine.setVisibility(View.GONE);

        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId" + userId, "");


        String scenes = SPUtils.get(this, "scenes" + userId, "");
        scenesBeans = new ArrayList<>();
        if (!StringUtils.isEmpty(scenes)) {
            scenesBeans = JSONObject.parseArray(scenes, SmartInfoVo.FamilysBean.ScenesBean.class);
        }
        SmartInfoVo.FamilysBean.ScenesBean noAttchScene = new SmartInfoVo.FamilysBean.ScenesBean();
        noAttchScene.setSceneName("无场景");
        noAttchScene.setSceneId("");
        scenesBeans.add(0, noAttchScene);

        attchSceneName.setText(StringUtils.isEmpty(switchsBean.getSceneId()) ? "无场景" : switchsBean.getSceneName());

        if(!StringUtils.isEmpty(switchsBean.getSceneId())){
            for(SmartInfoVo.FamilysBean.ScenesBean scenesBean :scenesBeans){
                if(scenesBean.getSceneId().equalsIgnoreCase(switchsBean.getSceneId())){
                    scenesBean.setChecked(true);
                }
            }
        } else {
            scenesBeans.get(0).setChecked(true);
        }
        attchSceneAdapter = new AttchSceneAdapter(scenesBeans);
        sceneRv.setAdapter(attchSceneAdapter);
        attchSceneAdapter.setOnItemChildClickListener(this);

        mBaseDialog = new BaseDialog(this, 1)
                .setTitleText("删除设备")
                .setInfoText("设备删除后会清除与设备相关的所有数据，若再次使用需重新添加设备")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mBaseDialog.setCancelable(false);

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
        mDeviceInfoBean = getIntent().getParcelableExtra("deviceInfoBean");
        switchKey = getIntent().getStringExtra("switchKey");
        switchsBean = getIntent().getParcelableExtra("switchBean");

        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        top_btn = findViewById(R.id.top_btn);
        top_btn.setText("保存");
        top_btn.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTitleLine = (View) findViewById(R.id.title_line);

        attchSceneName = (TextView) findViewById(R.id.scene_switch_scene_name);
        sceneRv = (RecyclerView) findViewById(R.id.scene_switch_scene_rv);

        scene_switch_scene_icon = (ImageView) findViewById(R.id.scene_switch_scene_icon);
        LinearLayout scene_switch_append_layout = (LinearLayout) findViewById(R.id.scene_switch_append_layout);
        scene_switch_append_layout.setOnClickListener(this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        //设置主轴排列方式
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setAlignItems(AlignItems.CENTER);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);//交叉轴的起点对齐。
        sceneRv.setLayoutManager(flexboxLayoutManager);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.top_back) {
            finish();
        } else if (v.getId() == R.id.top_btn) {
            SmartInfoVo.FamilysBean.ScenesBean checkedScene = attchSceneAdapter.getClickedItem();

            if (checkedScene != null) {
                LogUtils.e("SceneSwitchKeyActivity", checkedScene.getSceneId() + "====" + checkedScene.getSceneName());
                updateScenePanel(mDeviceInfoBean.getDeviceId(), checkedScene.getSceneId());
            }
//            else if (!StringUtils.isEmpty(switchsBean.getSceneId())) {
//                LogUtils.e("SceneSwitchKeyActivity", switchsBean.getSceneId() + "====" + mDeviceInfoBean.getDeviceId());
//                updateScenePanel(mDeviceInfoBean.getDeviceId(), switchsBean.getSceneId());
//            }
            else {
                updateScenePanel(mDeviceInfoBean.getDeviceId(), "");
//                    ToastUtil.show(SceneSwitchKeyActivity.this, "未关联场景");
            }
        } else if (v.getId() == R.id.scene_switch_append_layout) {
            isRvShow = !isRvShow;
            if (isRvShow) {
                sceneRv.setVisibility(View.VISIBLE);
                scene_switch_scene_icon.setImageDrawable(SceneSwitchKeyActivity.this.getResources().getDrawable(R.drawable.icon_blue_top));
            } else {
                sceneRv.setVisibility(View.GONE);
                scene_switch_scene_icon.setImageDrawable(SceneSwitchKeyActivity.this.getResources().getDrawable(R.drawable.icon_blue_bottom));
            }
        }

    }

    /**
     * 更新场景面板
     *
     * @param deviceId
     * @param sceneId
     */
    private void updateScenePanel(String deviceId, String sceneId) {
        JSONObject obj = new JSONObject();
        obj.put("deviceId", deviceId);
        obj.put("familyId", familyId);
        obj.put("sceneId", sceneId);
        obj.put("switchNum", switchsBean.getSwitchNum());
        obj.put("userId", userId);
        OkGo.<String>post(Constant.HOST + Constant.Device_updateScenePanel)
                .upJson(JSON.toJSONString(obj))
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
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
                        if (resp != null) {
                            if (resp.getErrorCode().equalsIgnoreCase("200")) {
                                handler.sendEmptyMessage(200);
                            } else {
                                handler.sendEmptyMessage(202);
                            }
                        } else {
                            handler.sendEmptyMessage(202);
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(202);
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.show();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadingDialog.dismiss();
            if (msg.what == 200) {
                ToastUtil.show(SceneSwitchKeyActivity.this, "操作成功");
                EventBus.getDefault().post("RefreshSceneSixSwitchActivity");
                finish();
//                destoryActivity("SingleBitSwitchActivity");
//                destoryActivity("DoubleBitSwitchActivity");
//                destoryActivity("ThreeBitSwitchActivity");
//                destoryActivity("FourBitSwitchActivity");
//                destoryActivity("CurtainSwitchActivity");
//                destoryActivity("DoorMagnetActivity");
//                destoryActivity("HumanSensorActivity");
//                destoryActivity("HumitureSensorActivity");

            } else if (msg.what == 202) {
                ToastUtil.show(SceneSwitchKeyActivity.this, "操作失败");
            }
        }
    };

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.item_ll) {
            for (int i = 0; i < scenesBeans.size(); i++) {
                if (i != position) {
                    scenesBeans.get(i).setChecked(false);
                }
            }
            scenesBeans.get(position).setChecked(!scenesBeans.get(position).isChecked());
            attchSceneAdapter.notifyDataSetChanged();
            if (scenesBeans.get(position).isChecked()) {
                attchSceneName.setText(scenesBeans.get(position).getSceneName());
            } else {
                attchSceneName.setText("无场景");
            }
        }
    }

    private void deleteDev() {
        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
        deviceInfoVo.setDeviceId(mDeviceInfoBean.getDeviceId());
        deviceInfoVo.setDeviceType("EEEEEE");//删除
        deviceInfoVo.setGatewayId(mDeviceInfoBean.getGatewayId());
        deviceInfoVo.setSupplierId(mDeviceInfoBean.getSupplierId());
        SmartControlVo smartControlVo = new SmartControlVo();
        smartControlVo.setUserId(userId);
        smartControlVo.setFamilyId(familyId);
        smartControlVo.setCmdType("del");
        smartControlVo.setCmd("");
        smartControlVo.setDeviceInfo(deviceInfoVo);

        OkGo.<String>post(Constant.HOST + Constant.Device_control)
                .upJson(JSON.toJSONString(smartControlVo))
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
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
                        if (resp != null) {
                            if (resp.getErrorCode().equalsIgnoreCase("200")) {
                                handler.sendEmptyMessage(200);
                            } else {
                                handler.sendEmptyMessage(202);
                            }
                        } else {
                            handler.sendEmptyMessage(202);
                        }
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(202);
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    @Override
    public void onConfirmClick() {
        mBaseDialog.dismiss();
        deleteDev();
        loadingDialog.show();
    }

    @Override
    public void onCancelListener() {
        if (mBaseDialog.isShowing()) {
            mBaseDialog.dismiss();
        }
    }
}