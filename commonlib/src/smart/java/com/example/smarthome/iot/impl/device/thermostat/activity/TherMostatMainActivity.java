package com.example.smarthome.iot.impl.device.thermostat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceInfoActivity;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.RoomDevicePagerFragment;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.impl.device.thermostat.adapter.TherMastatMainAdapter;
import com.example.smarthome.iot.impl.device.thermostat.bean.WkRealMsgBean;
import com.example.smarthome.iot.impl.device.thermostat.fragment.AirConditionerFragment;
import com.example.smarthome.iot.impl.device.thermostat.fragment.GroundWarmFragment;
import com.example.smarthome.iot.impl.device.thermostat.fragment.NewWindFragment;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.viewpager.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TherMostatMainActivity extends BaseActivity implements View.OnClickListener {

    private  final String TAG=this.getClass().getSimpleName();
    private LinearLayout back;
    private TextView title;
    private TextView title1,title2,title3;
    private NoScrollViewPager mostatViewPager;
    TherMastatMainAdapter therMastatMainAdapter;
    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;
    private TextView top_btn;
    private Integer type;
    private List<WkRealMsgBean.WkRealMagData.WkRealMsgData2> stateInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ther_mostat_main);
        Intent intent=getIntent();
        EventBus.getDefault().register(this);
        if (intent!=null){
            gateDevices = (SmartInfoVo.FamilysBean.DeviceInfoBean) intent.getParcelableExtra("gateDevices");
            type = intent.getIntExtra("type",-1);
            Log.e("三合一温控",gateDevices.getDeviceName()+gateDevices.getDeviceId()+" "+gateDevices.getDeviceType()+" "+gateDevices.getSupplierId());
        }
        initView();
        initDate();
        init(gateDevices.getDeviceId(),gateDevices.getDeviceType(),gateDevices.getSupplierId());
    }

    private void initView() {
        back = findViewById(R.id.top_back);
        back.setOnClickListener(this);
        title = findViewById(R.id.top_title);
        title1 = findViewById(R.id.tv_title_1);
        title2 = findViewById(R.id.tv_title_2);
        title3 = findViewById(R.id.tv_title_3);
        title1.setOnClickListener(this);
        title2.setOnClickListener(this);
        title3.setOnClickListener(this);
        mostatViewPager = findViewById(R.id.Ther_Mostat_viewPager);

        top_btn = findViewById(R.id.top_btn);
        top_btn.setOnClickListener(this);
        switch (type){
            default:
                break;
            case 1:
                title2.setVisibility(View.GONE);
                title3.setVisibility(View.GONE);
                break;
            case 2:
                title2.setVisibility(View.GONE);
                break;
            case 3:
                break;
        }
    }
    private void initDate() {
        mostatViewPager.setScanScroll(false);
        title1.setSelected(true);
        title.setText(gateDevices.getDeviceName());
        top_btn.setText("管理");
        mostatViewPager.setCurrentItem(0);
    }

    private List<Fragment> createFragment(int index){
        List<Fragment> fragments=new ArrayList<>();
        fragments.clear();
        for (int i=0;i<index;i++){
            if (i==0){
                //空调
                AirConditionerFragment fragment = new AirConditionerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("key_of_index", i);
                bundle.putParcelable("gateDevices",gateDevices);
                bundle.putSerializable("stateInfos",(Serializable) stateInfos);
//                bundle.putParcelableArrayList("deviceList", (ArrayList<? extends Parcelable>) mRoomsBeanList.get(i).getDeviceInfo());
//                bundle.putParcelableArrayList("roomList", (ArrayList<? extends Parcelable>) mRoomsBeanList);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }else if (i==1){
                //新风
                NewWindFragment fragment = new NewWindFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("key_of_index", i);
                bundle.putInt("type",type);
                bundle.putParcelable("gateDevices",gateDevices);
                bundle.putSerializable("stateInfos",(Serializable) stateInfos);
//                bundle.putParcelableArrayList("deviceList", (ArrayList<? extends Parcelable>) mRoomsBeanList.get(i).getDeviceInfo());
//                bundle.putParcelableArrayList("roomList", (ArrayList<? extends Parcelable>) mRoomsBeanList);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }else if (i==2){
                //地暖
                GroundWarmFragment fragment = new GroundWarmFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("key_of_index", i);
                bundle.putInt("type",type);
                bundle.putParcelable("gateDevices",gateDevices);
                bundle.putSerializable("stateInfos",(Serializable) stateInfos);
//                bundle.putParcelableArrayList("deviceList", (ArrayList<? extends Parcelable>) mRoomsBeanList.get(i).getDeviceInfo());
//                bundle.putParcelableArrayList("roomList", (ArrayList<? extends Parcelable>) mRoomsBeanList);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }
        }

        return fragments;

    }


    private void init(String deviceId, String deviceType, String supplierId) {
        OkGo.<String>get(Constant.HOST + Constant.THREE_ONE_INIT)
                .params("deviceId", deviceId)
                .params("deviceType", deviceType)
                .params("supplierId", supplierId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        Log.e("TAG","init onNext"+commonResp.getErrorCode());

                        if (commonResp.getErrorCode().equalsIgnoreCase("200")) {
                            WkRealMsgBean wkRealMsgBean = JSON.parseObject(commonResp.getResult(), WkRealMsgBean.class);
                            stateInfos = wkRealMsgBean.getRealState().getStateInfos();
                            therMastatMainAdapter=new TherMastatMainAdapter(getSupportFragmentManager(),createFragment(3));
                            mostatViewPager.setAdapter(therMastatMainAdapter);
                            mostatViewPager.setOffscreenPageLimit(2);

                        }else {
                            Toast.makeText(TherMostatMainActivity.this,commonResp.getErrorCode(),Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (messageEvent.isUpdate()) {
            title.setText(messageEvent.getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.top_back){
            finish();
        }else if (v.getId()==R.id.tv_title_1){
            title1.setSelected(true);
            title2.setSelected(false);
            title3.setSelected(false);
            mostatViewPager.setCurrentItem(0);
        }else if (v.getId()==R.id.tv_title_2){
            title1.setSelected(false);
            title2.setSelected(true);
            title3.setSelected(false);
            mostatViewPager.setCurrentItem(1);
        }else if (v.getId()==R.id.tv_title_3){
            title1.setSelected(false);
            title2.setSelected(false);
            title3.setSelected(true);
            mostatViewPager.setCurrentItem(2);
        }else if (v.getId()==R.id.top_btn){
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",gateDevices);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
