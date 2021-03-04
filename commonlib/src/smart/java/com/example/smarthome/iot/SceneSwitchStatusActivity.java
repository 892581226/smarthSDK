package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.SceneSwitchStatusAdapter;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.SwitchStatusBeanVo;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * author:
 * date: 2019/7/8
 * description: 开关状态选择
 * update:
 * version:
 */
public class SceneSwitchStatusActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private static final String TAG = "SceneSwitchStatusActivity";
    private ImageView mTopBackImg;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private String familyId,userId;
    private String iconName = "icon_iot_scene_more";

    private RecyclerView switchCheckRv;
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> deviceInfoBeans = new ArrayList<>();
    private SceneSwitchStatusAdapter statusAdapter;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private Button saveBtn;
    private int switchPos;
    private List<SwitchStatusBeanVo> statusBeanVos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_scene_switch);
        initView();
        initDate();
    }

    private void initDate() {
        deviceInfoBean = getIntent().getParcelableExtra("deviceInfoBean");
        if(deviceInfoBean!=null){
            statusBeanVos = dataConversion(deviceInfoBean.getDeviceType());
            mTopTitle.setText(deviceInfoBean.getDeviceName());
            Log.e("窗帘状态",deviceInfoBean.toString());
        }

        switchPos = getIntent().getIntExtra("switchPos",-1);
        statusAdapter = new SceneSwitchStatusAdapter(statusBeanVos);
        switchCheckRv.setAdapter(statusAdapter);
        statusAdapter.bindToRecyclerView(switchCheckRv);
        statusAdapter.setOnItemChildClickListener(this);
    }

    private void initView() {
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this,"familyId"+userId,"");
        mTopBackImg = (ImageView) findViewById(R.id.top_back_img);
        mTopBackImg.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);

        mTopBtn = (TextView) findViewById(R.id.top_btn);
//        mTopBtn.setOnClickListener(this);
        mTopBtn.setVisibility(View.GONE);
        saveBtn = findViewById(R.id.scene_switch_save_btn);
        saveBtn.setOnClickListener(this);


        switchCheckRv = (RecyclerView) findViewById(R.id.scene_switch_device_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        switchCheckRv.setLayoutManager(manager);
        //当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小。
        switchCheckRv.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.top_back_img) {
            finish();
        } else if (i1 == R.id.top_btn) {
        } else if (i1 == R.id.scene_switch_save_btn) {
            if(deviceInfoBean.getDeviceType().equals("233000")){
                for (int i = 0; i < statusBeanVos.size(); i++) {//bug标记
                    if (i == 0) {
                        deviceInfoBean.getField().setState((statusBeanVos.get(0).isSwitchStatus() ? "1" : "0"));
                    }
                }
            }else{
                for (int i = 0; i < statusBeanVos.size(); i++) {//bug标记
                    if (i == 0) {
                        deviceInfoBean.getField().setSwitch1((statusBeanVos.get(0).isSwitchStatus() ? "1" : "0"));
                    } else if (i == 1) {
                        deviceInfoBean.getField().setSwitch2((statusBeanVos.get(1).isSwitchStatus() ? "1" : "0"));
                    } else if (i == 2) {
                        deviceInfoBean.getField().setSwitch3((statusBeanVos.get(2).isSwitchStatus() ? "1" : "0"));
                    } else if (i == 3) {
                        deviceInfoBean.getField().setSwitch4((statusBeanVos.get(3).isSwitchStatus() ? "1" : "0"));
                    }
                }
            }

            Intent intent = new Intent();
            intent.putExtra("deviceInfoBeanStatus", deviceInfoBean);
            intent.putExtra("switchPosStatus", switchPos);
            setResult(ActionConstant.SCENE_SWITCH_RESULT_CODE, intent);
            finish();
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.scene_switch_status_cl) {
            statusBeanVos.get(position).setSwitchStatus(!statusBeanVos.get(position).isSwitchStatus());
            adapter.notifyDataSetChanged();
        }
    }

    private List<SwitchStatusBeanVo> dataConversion(String switchType){
        List<SwitchStatusBeanVo> tempList = new ArrayList<>();
        int switchNum=-1;
        List<Boolean> switchStatus = new ArrayList<>();
        // 判断的后半段是新添加鸿雁开关控制
        if(switchType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)
                || switchType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_1)
                || switchType.equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_1)
                || switchType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_1)
                || switchType.equalsIgnoreCase(DeviceType.HILINK_86_RECEPTACLE)){
            switchNum = 1;
            if (StringUtils.isEmpty(deviceInfoBean.getField().getSwitch1())){
                deviceInfoBean.getField().setSwitch1("0");
            }
            switchStatus.add(0, deviceInfoBean.getField().getSwitch1().equals("1"));
        } else if(switchType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_2)
                || switchType.equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_2)
                || switchType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_2)
                || switchType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_2)){
            switchNum = 2;
            if (StringUtils.isEmpty(deviceInfoBean.getField().getSwitch1())||
                    StringUtils.isEmpty(deviceInfoBean.getField().getSwitch2())){
                deviceInfoBean.getField().setSwitch1("0");
                deviceInfoBean.getField().setSwitch2("0");
            }
            switchStatus.add(0, deviceInfoBean.getField().getSwitch1().equals("1"));
            switchStatus.add(1, deviceInfoBean.getField().getSwitch2().equals("1"));
        } else if(switchType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_3)
                || switchType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_3)
                || switchType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_3)
                || switchType.equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_3)){
            switchNum = 3;
            if (StringUtils.isEmpty(deviceInfoBean.getField().getSwitch1())||
                    StringUtils.isEmpty(deviceInfoBean.getField().getSwitch2())||
                    StringUtils.isEmpty(deviceInfoBean.getField().getSwitch3())){
                deviceInfoBean.getField().setSwitch1("0");
                deviceInfoBean.getField().setSwitch2("0");
                deviceInfoBean.getField().setSwitch3("0");
            }
            switchStatus.add(0, deviceInfoBean.getField().getSwitch1().equals("1"));
            switchStatus.add(1, deviceInfoBean.getField().getSwitch2().equals("1"));
            switchStatus.add(2, deviceInfoBean.getField().getSwitch3().equals("1"));
        } else if(switchType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_4)
                || switchType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_4)
                || switchType.equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_4)){
            switchNum = 4;
            if (StringUtils.isEmpty(deviceInfoBean.getField().getSwitch1())||
                    StringUtils.isEmpty(deviceInfoBean.getField().getSwitch2())||
                    StringUtils.isEmpty(deviceInfoBean.getField().getSwitch3())||
                    StringUtils.isEmpty(deviceInfoBean.getField().getSwitch4())){
                deviceInfoBean.getField().setSwitch1("0");
                deviceInfoBean.getField().setSwitch2("0");
                deviceInfoBean.getField().setSwitch3("0");
                deviceInfoBean.getField().setSwitch4("0");
            }
            switchStatus.add(0, deviceInfoBean.getField().getSwitch1().equals("1"));
            switchStatus.add(1, deviceInfoBean.getField().getSwitch2().equals("1"));
            switchStatus.add(2, deviceInfoBean.getField().getSwitch3().equals("1"));
            switchStatus.add(3, deviceInfoBean.getField().getSwitch4().equals("1"));
        }
        else if (switchType.equalsIgnoreCase(DeviceType.HONGYAN_SMAET_SOCKET)){
            switchNum = 1;
            if (deviceInfoBean.getField().getState()!=null){
                if (deviceInfoBean.getField().getState().equalsIgnoreCase("1")){
                    switchStatus.add(0,true);
                }else {
                    switchStatus.add(0,false);
                }
            }else {
                switchStatus.add(0,false);
            }
        }
        else if (switchType.equalsIgnoreCase(DeviceType.HILINK_CURTAIN)){
            //电动窗帘
            switchNum = 3;
            if (deviceInfoBean.getField().getSwitch1()==null||
                    !deviceInfoBean.getField().getSwitch1().equals("1")){
                switchStatus.add(0,false);
            }else {
                switchStatus.add(0,true);
            }

            if (deviceInfoBean.getField().getSwitch2()==null||
                    !deviceInfoBean.getField().getSwitch2().equals("1")){
                switchStatus.add(1,false);
            }else {
                switchStatus.add(1,true);
            }

            if (deviceInfoBean.getField().getSwitch3()==null||
                    !deviceInfoBean.getField().getSwitch3().equals("1")){
                switchStatus.add(2,false);
            }else {
                switchStatus.add(2,true);
            }
//            switchStatus.add(0,deviceInfoBean.getField().getSwitch1()!=null||
//                    deviceInfoBean.getField().getSwitch1().equals("1")?true:false);
//            switchStatus.add(1,deviceInfoBean.getField().getSwitch1()!=null||
//                    deviceInfoBean.getField().getSwitch2().equals("1")?true:false);
//            switchStatus.add(2,deviceInfoBean.getField().getSwitch1()!=null||
//                    deviceInfoBean.getField().getSwitch3().equals("1")?true:false);
        }

        if (deviceInfoBean.getDeviceType().equals("233000")
                ||deviceInfoBean.getDeviceType().equals("100100")){
            for (int i =0;i<switchNum;i++){
                SwitchStatusBeanVo tempStatusBean  = new SwitchStatusBeanVo();
                if (i==0){
                    tempStatusBean.setSwitchName("插座开关");
                }
                tempStatusBean.setSwitchStatus(switchStatus.get(i));
                tempList.add(tempStatusBean);
            }
        }else {
            for (int i =0;i<switchNum;i++){
                SwitchStatusBeanVo tempStatusBean = new SwitchStatusBeanVo();
                tempStatusBean.setSwitchName("第"+ (i+1) +"位开关");
                tempStatusBean.setSwitchStatus(switchStatus.get(i));
                tempList.add(tempStatusBean);
            }
        }

        LogUtils.e(TAG,tempList.size()+"-----------------");
        return tempList;
    }


}
