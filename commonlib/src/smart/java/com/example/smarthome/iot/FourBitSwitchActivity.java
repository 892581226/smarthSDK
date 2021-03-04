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
import com.example.smarthome.iot.entry.DeviceRealStateMsg;
import com.example.smarthome.iot.entry.DeviceRealStateVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DeviceControlUtil;
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
 * date: 2019/5/14 9:25
 * description: 海令四位开关
 * update: 2019/5/14
 * version:
 */
public class FourBitSwitchActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private View mTitleLine;
    private ImageView mFourBitSwitchOne;
    private ImageView mFourBitSwitchTwo;
    private ImageView mFourBitSwitchThree;
    private ImageView mFourBitSwitchFour;
    private boolean switchOne; // 默认未开启 false
    private boolean switchTwo; // 默认未开启 false
    private boolean switchThree; // 默认未开启 false
    private boolean switchFour; // 默认未开启 false
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_bit_switch);
        addDestoryActivity(this,"FourBitSwitchActivity");
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
        mTitleLine.setVisibility(View.GONE);
        mTopBtn.setText("管理");
        getRealMsg(deviceInfoBean.getDeviceId(),deviceInfoBean.getDeviceType(),deviceInfoBean.getSupplierId());
    }

    private void initView() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn =  findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTitleLine = (View) findViewById(R.id.title_line);
        mFourBitSwitchOne = (ImageView) findViewById(R.id.four_bit_switch_one);
        mFourBitSwitchOne.setOnClickListener(this);
        mFourBitSwitchTwo = (ImageView) findViewById(R.id.four_bit_switch_two);
        mFourBitSwitchTwo.setOnClickListener(this);
        mFourBitSwitchThree = (ImageView) findViewById(R.id.four_bit_switch_three);
        mFourBitSwitchThree.setOnClickListener(this);
        mFourBitSwitchFour = (ImageView) findViewById(R.id.four_bit_switch_four);
        mFourBitSwitchFour.setOnClickListener(this);
        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        mTopTitle.setText(deviceInfoBean.getDeviceName());

    }

    private void initSwitch() {

        if (switchOne) {
            mFourBitSwitchOne.setImageResource(R.drawable.icon_iot_four_bit_switch_open);
        } else {
            mFourBitSwitchOne.setImageResource(R.drawable.icon_iot_four_bit_switch_close);
        }

        if (switchTwo) {
            mFourBitSwitchTwo.setImageResource(R.drawable.icon_iot_four_bit_switch_open);
        } else {
            mFourBitSwitchTwo.setImageResource(R.drawable.icon_iot_four_bit_switch_close);
        }

        if (switchThree) {
            mFourBitSwitchThree.setImageResource(R.drawable.icon_iot_four_bit_switch_open);
        } else {
            mFourBitSwitchThree.setImageResource(R.drawable.icon_iot_four_bit_switch_close);
        }

        if (switchFour) {
            mFourBitSwitchFour.setImageResource(R.drawable.icon_iot_four_bit_switch_open);
        } else {
            mFourBitSwitchFour.setImageResource(R.drawable.icon_iot_four_bit_switch_close);
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
        }else if(v.getId() == R.id.four_bit_switch_one){
            // 开启/关闭 开关
            if (switchOne) {
                switchOne = false;
                mFourBitSwitchOne.setImageResource(R.drawable.icon_iot_four_bit_switch_close);
                DeviceControlUtil.switchControl(this,deviceInfoBean,1,0);
            } else {
                switchOne = true;
                mFourBitSwitchOne.setImageResource(R.drawable.icon_iot_four_bit_switch_open);
                DeviceControlUtil.switchControl(this,deviceInfoBean,1,1);
            }
        } else if(v.getId() == R.id.four_bit_switch_two){
            // 开启/关闭 开关
            if (switchTwo) {
                switchTwo = false;
                mFourBitSwitchTwo.setImageResource(R.drawable.icon_iot_four_bit_switch_close);
                DeviceControlUtil.switchControl(this,deviceInfoBean,2,0);
            } else {
                switchTwo = true;
                mFourBitSwitchTwo.setImageResource(R.drawable.icon_iot_four_bit_switch_open);
                DeviceControlUtil.switchControl(this,deviceInfoBean,2,1);
            }
        } else if(v.getId() == R.id.four_bit_switch_three){
            // 开启/关闭 开关
            if (switchThree) {
                switchThree = false;
                mFourBitSwitchThree.setImageResource(R.drawable.icon_iot_four_bit_switch_close);
                DeviceControlUtil.switchControl(this,deviceInfoBean,3,0);
            } else {
                switchThree = true;
                mFourBitSwitchThree.setImageResource(R.drawable.icon_iot_four_bit_switch_open);
                DeviceControlUtil.switchControl(this,deviceInfoBean,3,1);
            }
        } else if(v.getId() == R.id.four_bit_switch_four){
            // 开启/关闭 开关
            if (switchFour) {
                switchFour = false;
                mFourBitSwitchFour.setImageResource(R.drawable.icon_iot_four_bit_switch_close);
                DeviceControlUtil.switchControl(this,deviceInfoBean,4,0);
            } else {
                switchFour = true;
                mFourBitSwitchFour.setImageResource(R.drawable.icon_iot_four_bit_switch_open);
                DeviceControlUtil.switchControl(this,deviceInfoBean,4,1);
            }
        }
    }

    private void getRealMsg(String deviceId,String deviceType,String supplierId){
        OkGo.<String>get(Constant.HOST+Constant.Device_getRealMsg)
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
                            obj = JSON.parseObject(resp.getResult(),DeviceRealStateVo.class);
                        }

                        LogUtils.e("resp",resp.getState()+"=====");
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<DeviceRealStateVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(DeviceRealStateVo realStateVo) {
                        LogUtils.w("smartInit", "onSuccess: " + realStateVo.getRealState().getDev_type());
                        if(realStateVo!=null && realStateVo.getRealState().getStateInfos()!=null && realStateVo.getRealState().getStateInfos().size()>0){
                            List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans = realStateVo.getRealState().getStateInfos();
                            if(stateInfosBeans.size()>0){
                                for(DeviceRealStateVo.RealStateBean.StateInfosBean bean: stateInfosBeans){
                                    DeviceRealStateMsg stateMsg = new Gson().fromJson(bean.getState(), DeviceRealStateMsg.class);
                                    if(bean.getDev_ep_id() == 1){
                                        switchOne = stateMsg.getState().equalsIgnoreCase("1");
                                    } else if(bean.getDev_ep_id() ==2){
                                        switchTwo = stateMsg.getState().equalsIgnoreCase("1");
                                    } else if(bean.getDev_ep_id() ==3){
                                        switchThree = stateMsg.getState().equalsIgnoreCase("1");
                                    } else if(bean.getDev_ep_id()==4){
                                        switchFour = stateMsg.getState().equalsIgnoreCase("1");
                                    }
                                }
                            }
                            initSwitch();
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
