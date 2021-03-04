package com.example.smarthome.iot.impl.device;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.CurtainSwitchActivity;
import com.example.smarthome.iot.DeviceInfoActivity;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.DoorMagnetActivity;
import com.example.smarthome.iot.DoubleBitSwitchActivity;
import com.example.smarthome.iot.DoubleCurtainSwitchActivity;
import com.example.smarthome.iot.FourBitSwitchActivity;
import com.example.smarthome.iot.HumanSensorActivity;
import com.example.smarthome.iot.HumitureSensorActivity;
import com.example.smarthome.iot.SceneSixSwitchActivity;
import com.example.smarthome.iot.SingleBitSwitchActivity;
import com.example.smarthome.iot.SmartSocketActivity;
import com.example.smarthome.iot.TeanaMusicActivity;
import com.example.smarthome.iot.ThreeBitSwitchActivity;
import com.example.smarthome.iot.dveview.CustomSwitchActivity;
import com.example.smarthome.iot.dveview.HlSmartSocket;
import com.example.smarthome.iot.dveview.WaterAlarmActivity;
import com.example.smarthome.iot.adapter.DeviceListAdapter;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.impl.DeviceAppendActivity;
import com.example.smarthome.iot.impl.device.infraredscreen.activity.InFraredScreenActivity;
import com.example.smarthome.iot.impl.device.negativecontrol.activity.NegativeControlActivity;
import com.example.smarthome.iot.impl.device.thermostat.activity.TherMostatMainActivity;
import com.example.smarthome.iot.util.DeviceControlUtil;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;


/**
 * author: glq
 * date: 2019/5/13 16:50
 * description: 海令网关详情
 * update: 2019/5/13
 * version:
 */
public class GateDeviceActivity extends BaseActivity implements View.OnClickListener , BaseQuickAdapter.OnItemChildClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private View mTitleLine;
    private ImageView mSingleBitSwitch;
    private boolean switchOne; // 默认为开启 false
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;

    private RecyclerView mRecyclerView;
    private DeviceListAdapter mAdapter;
    private List<SmartInfoVo.FamilysBean.GatewaysBean> mGatewaysBeanList = new ArrayList<>();
    private String telephone;
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> mDeviceManageItemVos = new ArrayList<>();
    private TextView tv_add_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_device);
        addDestoryActivity(this,"GateDeviceActivity");
        initView();
        initDate();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (!StringUtils.isEmpty(messageEvent.getTitle())){
            mTopTitle.setText(messageEvent.getTitle());
        }else {
            mTopTitle.setText(deviceInfoBean.getDeviceName());
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
        //网关删除无接口
        mTopBtn.setText("管理");
        EventBus.getDefault().post("GateDeviceActivity");
        String gates = SPUtils.get(this, "GatewaysBeanList"+telephone, "");

        if (!StringUtils.isEmpty(gates)) {
            mGatewaysBeanList = JSONObject.parseArray(gates,SmartInfoVo.FamilysBean.GatewaysBean.class);
        }
        if(mGatewaysBeanList!=null && mGatewaysBeanList.size()>0){
            for(SmartInfoVo.FamilysBean.GatewaysBean gatewaysBean : mGatewaysBeanList){
                if(gatewaysBean.getGatewayId().equalsIgnoreCase(deviceInfoBean.getId())){
                    mDeviceManageItemVos.addAll(gatewaysBean.getDeviceInfo());
                }
            }
        }
        mAdapter = new DeviceListAdapter(mDeviceManageItemVos);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left=10;
            }
        });
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        telephone = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTitleLine = (View) findViewById(R.id.title_line);
        mRecyclerView = findViewById(R.id.gateway_device_actrv);

        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        LogUtils.e("辅控开关",deviceInfoBean.toString());
        mTopTitle.setText(deviceInfoBean.getDeviceName());

        tv_add_device = findViewById(R.id.tv_add_device);
        tv_add_device.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.top_back){
            finish();
        } else if(viewId == R.id.top_btn){
            // 管理
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",deviceInfoBean);
            intent.putExtra("GateDeviceActivity","GateDeviceActivity");
            startActivity(intent);
        }else if (viewId==R.id.tv_add_device){
            Intent intent=new Intent(GateDeviceActivity.this, DeviceAppendActivity.class);
            intent.putExtra("deviceItem",deviceInfoBean);
            startActivity(intent);
        }
    }


    private Intent intent;
    private String deviceType;
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.item_iot_device_linear) {
            deviceType = mDeviceManageItemVos.get(position).getDeviceType();
            intent = new Intent();
            intent.putExtra("deviceItem", mDeviceManageItemVos.get(position));
            Log.e("网关内部设备类型",deviceType);
            if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_3) || deviceType.equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_3)) {
                intent.setClass(GateDeviceActivity.this, ThreeBitSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_1) || deviceType.equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_1)) {
                intent.setClass(GateDeviceActivity.this, SingleBitSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_2) || deviceType.equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_2)) {
                intent.setClass(GateDeviceActivity.this, DoubleBitSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_4) || deviceType.equalsIgnoreCase(DeviceType.HONGYAN_SWITCH_4)) {
                intent.setClass(GateDeviceActivity.this, FourBitSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_CURTAIN)) {
                intent.setClass(GateDeviceActivity.this, CurtainSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_DOOR_MAGNETIC)) {
                intent.setClass(GateDeviceActivity.this, DoorMagnetActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_HUMAN_INFRARED)) {
                intent.setClass(GateDeviceActivity.this, HumanSensorActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SMOKE_ALARM)) {
                ToastUtil.show(GateDeviceActivity.this, "暂不支持");
                return;
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_GAS_ALARM)) {
                ToastUtil.show(GateDeviceActivity.this, "暂不支持");
                return;
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_TEMP_HUN)) {
                intent.setClass(GateDeviceActivity.this, HumitureSensorActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SCENE_SWITCH_6)) {
                intent.setClass(GateDeviceActivity.this, SceneSixSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_GATEWAY)) {
                intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.setClass(GateDeviceActivity.this, GateDeviceActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.YS_CAMERA)) {
//                intent.setClass(getActivity(), YsCameraListActivity.class);
                ToastUtil.show(GateDeviceActivity.this, "暂不支持");
                return;
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_THERMOSTAT)){
                //三合一温控
                intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.putExtra("type",3);
                intent.setClass(GateDeviceActivity.this, TherMostatMainActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_THERMOSTAT_2)){
                //二合一温控
                intent.putExtra("gateDevices", (Parcelable) mDeviceManageItemVos.get(position));
                intent.putExtra("type",2);
                intent.setClass(GateDeviceActivity.this, TherMostatMainActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_THERMOSTAT_1)){
                //单一温控
                intent.putExtra("gateDevices", (Parcelable) mDeviceManageItemVos.get(position));
                intent.putExtra("type",1);
                intent.setClass(GateDeviceActivity.this, TherMostatMainActivity.class);
            }
            else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_INFRARED_SCREEN)){
                //红外幕帘
                intent.putExtra("gateDevices", (Parcelable) mDeviceManageItemVos.get(position));
                intent.setClass(GateDeviceActivity.this, InFraredScreenActivity.class);
            }
            else if (deviceType.equalsIgnoreCase(DeviceType.HONGYAN_SMAET_SOCKET)) {
                //智能插座
                intent.setClass(this, SmartSocketActivity.class);
            }
            else if (deviceType.equalsIgnoreCase(DeviceType.HONGYAN_DOUBLE_CURTAIN)) {
                //双规窗帘开关
                intent.setClass(this, DoubleCurtainSwitchActivity.class);
            }
            else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_FK)){
                //负控开关
                intent.putExtra("gateDevices", (Parcelable) mDeviceManageItemVos.get(position));
                intent.setClass(GateDeviceActivity.this, NegativeControlActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_1)&&mDeviceManageItemVos.get(position).getProto().equals("zigbee")){
                // 定制开关
                intent.setClass(this, CustomSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_1)&&mDeviceManageItemVos.get(position).getProto().isEmpty()){
                //plc一路开关
                intent.setClass(GateDeviceActivity.this, SingleBitSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_2)){
                //plc二路开关
                intent.setClass(GateDeviceActivity.this, DoubleBitSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_3)){
                //plc三路开关
                intent.setClass(GateDeviceActivity.this, ThreeBitSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_4)){
                //plc四路开关
                intent.setClass(GateDeviceActivity.this, FourBitSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_CURTAIN_2)) {
                //平开窗帘
                intent.setClass(GateDeviceActivity.this, CurtainSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_KT)){
                //VRV空调
                intent.putExtra("gateDevices", (Parcelable) mDeviceManageItemVos.get(position));
                intent.putExtra("type",1);
                intent.setClass(GateDeviceActivity.this, TherMostatMainActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.HONGYAN_WIRLESS)){
                // 鸿雁网关
                //intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                Toast.makeText(getApplicationContext(), "点击了一下鸿雁网关", Toast.LENGTH_SHORT).show();

                intent.setClass(getApplicationContext(), GateDeviceActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.HILINK_WATER_ALARM)){
                //溢水报警
                intent.setClass(getApplicationContext(), WaterAlarmActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.HILINK_86_RECEPTACLE)){
                //86智能插座
                intent.setClass(getApplicationContext(), HlSmartSocket.class);
            } else if(deviceType.equalsIgnoreCase(DeviceType.TL_MUSIC)){
                // 天籁网关
                //intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.setClass(getApplicationContext(), GateDeviceActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.TL_MUSIC_TYPE)){
                // 天籁
                //intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.setClass(getApplicationContext(), TeanaMusicActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_1)){
                // 控课一路开关
                intent.setClass(getApplicationContext(), SingleBitSwitchActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_2)){
                // 控课二路开关
                intent.setClass(getApplicationContext(), DoubleBitSwitchActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_3)){
                // 控课三路开关
                intent.setClass(getApplicationContext(), ThreeBitSwitchActivity.class);
            }
            else {
                return;
            }
            startActivity(intent);
        } else if(view.getId() == R.id.item_iot_device_switch) {
            if (mDeviceManageItemVos.get(position).getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)) {
                DeviceControlUtil.switchControl(GateDeviceActivity.this, mDeviceManageItemVos.get(position), 1, 2);
            }
        }
    }
}
