package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
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
 * description: 海令场景开关
 * update:
 * version:
 */
public class SceneSwitchActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private View mTitleLine;
    private ImageView mSingleBitSwitch;
    private boolean switchOne; // 默认为开启 false
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private TextView scene_switch_one,scene_switch_two;
    private String userId,familyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_bit_scene_switch);
        addDestoryActivity(this,"SceneSwitchActivity");
        initView();
        initDate();
    }

    private void initDate() {//device/dev1.0/getScenePanelMsg
        mTitleLine.setVisibility(View.GONE);
        mTopBtn.setText("管理");
        userId = SPUtils.getString(this, SpConstant.SP_USER_TELEPHONE);
        familyId = SPUtils.getString(this, "familyId"+userId);
        getScenePanelMsg(deviceInfoBean.getDeviceId(),deviceInfoBean.getDeviceType(),familyId,userId);
    }

    private void initView() {
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTitleLine = (View) findViewById(R.id.title_line);

        scene_switch_one = findViewById(R.id.scene_switch_one);
        scene_switch_one.setOnClickListener(this);
        scene_switch_two = findViewById(R.id.scene_switch_two);


//        if (switchOne) {
//            mSingleBitSwitch.setImageResource(R.drawable.icon_iot_single_bit_switch_close);
//        } else {
//            mSingleBitSwitch.setImageResource(R.drawable.icon_iot_single_bit_switch_open);
//        }
        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        mTopTitle.setText(deviceInfoBean.getDeviceName());
//        initSwitch();
    }

    private void initSwitch() {
//        switchOne = deviceInfoBean.getField().getSwitch1().equalsIgnoreCase("1");
        if (switchOne) {
            mSingleBitSwitch.setImageResource(R.drawable.icon_iot_single_bit_switch_open);
        } else {
            mSingleBitSwitch.setImageResource(R.drawable.icon_iot_single_bit_switch_close);
        }

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
        } else if(v.getId() == R.id.scene_switch_one){
            // 开启/关闭 开关   state: 0关，1开，2 toggle
//            if (switchOne) {
//                switchOne = false;
//                mSingleBitSwitch.setImageResource(R.drawable.icon_iot_single_bit_switch_close);
//                DeviceControlUtil.switchControl(SceneSwitchActivity.this,deviceInfoBean,1,0);
//            } else {
//                switchOne = true;
//                mSingleBitSwitch.setImageResource(R.drawable.icon_iot_single_bit_switch_open);
//                DeviceControlUtil.switchControl(SceneSwitchActivity.this,deviceInfoBean,1,1);
//            }

        }
    }

    private void getScenePanelMsg(String deviceId,String deviceType,String familyId,String userId){
        OkGo.<String>get(Constant.HOST+Constant.Device_getScenePanelMsg)
                .params("deviceId",deviceId)
                .params("deviceType",deviceType)
                .params("familyId",familyId)
                .params("userId",userId)
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
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
//                        DeviceRealStateVo obj = null;
//                        if(resp!=null && resp.getErrorCode().equalsIgnoreCase("200")){
//                            obj = JSON.parseObject(resp.getResult(),DeviceRealStateVo.class);
//                        }
//                        LogUtils.e("resp",resp.getState()+"=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        LogUtils.w("smartInit", "onSuccess: ---" + commonResp);
//                        if(realStateVo!=null && realStateVo.getRealState().getStateInfos()!=null && realStateVo.getRealState().getStateInfos().size()>0){
//                            List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans = realStateVo.getRealState().getStateInfos();
//                            if(stateInfosBeans.size()>0){
//                                for(DeviceRealStateVo.RealStateBean.StateInfosBean bean: stateInfosBeans){
//                                    DeviceRealStateMsg stateMsg = new Gson().fromJson(bean.getState(), DeviceRealStateMsg.class);
//                                    if(bean.getDev_ep_id() == 1){
//                                        switchOne = stateMsg.getState().equalsIgnoreCase("1");
//                                    }
//                                }
//                            }
//                            initSwitch();
//                        }
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
}
