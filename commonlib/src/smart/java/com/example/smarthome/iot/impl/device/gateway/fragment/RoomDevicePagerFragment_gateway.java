package com.example.smarthome.iot.impl.device.gateway.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.CurtainSwitchActivity;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.DoorMagnetActivity;
import com.example.smarthome.iot.DoubleBitSwitchActivity;
import com.example.smarthome.iot.FourBitSwitchActivity;
import com.example.smarthome.iot.HumanSensorActivity;
import com.example.smarthome.iot.HumitureSensorActivity;
import com.example.smarthome.iot.SceneSixSwitchActivity;
import com.example.smarthome.iot.SingleBitSwitchActivity;
import com.example.smarthome.iot.TeanaMusicActivity;
import com.example.smarthome.iot.ThreeBitSwitchActivity;
import com.example.smarthome.iot.dveview.CustomSwitchActivity;
import com.example.smarthome.iot.dveview.HlSmartSocket;
import com.example.smarthome.iot.dveview.WaterAlarmActivity;
import com.example.smarthome.iot.adapter.DeviceListAdapter2;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.impl.DeviceAppendActivity;
import com.example.smarthome.iot.impl.device.GateDeviceActivity;
import com.example.smarthome.iot.impl.device.infraredscreen.activity.InFraredScreenActivity;
import com.example.smarthome.iot.impl.device.negativecontrol.activity.NegativeControlActivity;
import com.example.smarthome.iot.impl.device.thermostat.activity.TherMostatMainActivity;
import com.example.smarthome.iot.util.DeviceControlUtil;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

//import com.example.smarthome.iot.yscamera.YsCameraListActivity;
//import com.example.smarthome.iot.yscamera.YsCameraActivity;
//import com.example.smarthome.iot.yscamera.YsCameraUikitActivity;

/**
 * author: glq
 * date: 2019/5/5 9:56
 * description: 首页房间设备列表页面
 * update: 2019/5/5
 * version:
 */
public class RoomDevicePagerFragment_gateway extends Fragment implements BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {

    private int mFragmentIndex;
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> mDeviceManageItemVos = new ArrayList<>();
    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList = new ArrayList<>();

    private DeviceListAdapter2 mAdapter;
    private LinearLayout mParentView;
    private RecyclerView mRecyclerView;
    private Intent intent;
    private RelativeLayout iot_room_device_default;
    private String deviceType;


//    public static RoomDevicePagerFragment create(int index, List<SmartInfoVo.FamilysBean.DeviceInfoBean> mDeviceManageItemVos,
//                                                 List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList) {
//        RoomDevicePagerFragment roomDevicePagerFragment = new RoomDevicePagerFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("key_of_index", index);
//        bundle.putParcelableArrayList("deviceList", (ArrayList<? extends Parcelable>) mDeviceManageItemVos);
//        LogUtils.e("RoomDevicePagerFragment create",mDeviceManageItemVos.size()+"===============");
//        bundle.putParcelableArrayList("roomList", (ArrayList<? extends Parcelable>) mRoomsBeanList);
//        roomDevicePagerFragment.setArguments(bundle);
//        return roomDevicePagerFragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentIndex = getArguments().getInt("key_of_index");
            mDeviceManageItemVos = getArguments().getParcelableArrayList("deviceList");
//            LogUtils.e("mDeviceManageItemVos  onCreate",mDeviceManageItemVos.size()+mDeviceManageItemVos.get(0).getDeviceType()+"-----------");
            mRoomsBeanList = getArguments().getParcelableArrayList("roomList");


        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentView = view.findViewById(R.id.parent_view);
        mRecyclerView = view.findViewById(R.id.room_device_pager_fragment);
        iot_room_device_default = view.findViewById(R.id.iot_room_device_default);
        ImageView iot_room_device_default_add = view.findViewById(R.id.iot_room_device_default_add);
        iot_room_device_default_add.setVisibility(View.GONE);
        iot_room_device_default_add.setOnClickListener(this);

        LogUtils.e("mDeviceManageItemVos  onViewCreated",mDeviceManageItemVos.size()+"============");
        if (mDeviceManageItemVos != null && mDeviceManageItemVos.size() > 0) {
            iot_room_device_default.setVisibility(View.GONE);

            mAdapter = new DeviceListAdapter2(mDeviceManageItemVos);
            if(mFragmentIndex==0){
                mAdapter.setMydevice(true);
            } else {
                mAdapter.setMydevice(false);
            }
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemChildClickListener(this);
            View footView = getLayoutInflater().inflate(R.layout.main_device_foot_view, (ViewGroup) mRecyclerView.getParent(),false);
            mAdapter.addFooterView(footView);
            mAdapter.notifyDataSetChanged();
        } else {
            mRecyclerView.setVisibility(View.GONE);
            iot_room_device_default.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.item_iot_device_linear) {
            deviceType = mDeviceManageItemVos.get(position).getDeviceType();
            Log.e("点击获取设备类型",deviceType+"");
            intent = new Intent();
            intent.putExtra("deviceItem", mDeviceManageItemVos.get(position));
            if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_3)) {
                intent.setClass(getActivity(), ThreeBitSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)) {
                intent.setClass(getActivity(), SingleBitSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_2)) {
                intent.setClass(getActivity(), DoubleBitSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_4)) {
                intent.setClass(getActivity(), FourBitSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_CURTAIN)) {
                intent.setClass(getActivity(), CurtainSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_DOOR_MAGNETIC)) {
                intent.setClass(getActivity(), DoorMagnetActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_HUMAN_INFRARED)) {
                intent.setClass(getActivity(), HumanSensorActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SMOKE_ALARM)) {
                ToastUtil.show(getActivity(), "暂不支持");
                return;
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_GAS_ALARM)) {
                ToastUtil.show(getActivity(), "暂不支持");
                return;
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_TEMP_HUN)) {
                intent.setClass(getActivity(), HumitureSensorActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SCENE_SWITCH_6)) {
                intent.setClass(getActivity(), SceneSixSwitchActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_GATEWAY)) {
                intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.setClass(getActivity(), GateDeviceActivity.class);
            } else if (deviceType.equalsIgnoreCase(DeviceType.YS_CAMERA)) {
//                intent.setClass(getActivity(), YsCameraListActivity.class);
                ToastUtil.show(getActivity(), "暂不支持");
                return;
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_THERMOSTAT)){
                //三合一温控
                intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.setClass(getActivity(), TherMostatMainActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_INFRARED_SCREEN)){
                //红外幕帘
                intent.putExtra("gateDevices", (Parcelable) mDeviceManageItemVos.get(position));
                intent.setClass(getActivity(), InFraredScreenActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_FK)){
                //负控开关
                intent.putExtra("gateDevices", (Parcelable) mDeviceManageItemVos.get(position));
                intent.setClass(getActivity(), NegativeControlActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_1)&&mDeviceManageItemVos.get(position).getProto().isEmpty()){
                //plc一路开关
                intent.setClass(getActivity(), SingleBitSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_2)){
                //plc二路开关
                intent.setClass(getActivity(), DoubleBitSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_3)){
                //plc三路开关
                intent.setClass(getActivity(), ThreeBitSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_4)){
                //plc四路开关
                intent.setClass(getActivity(), FourBitSwitchActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.HILINK_PLC_SWITCH_1)&&mDeviceManageItemVos.get(position).getProto().equals("zigbee")){
                // 定制开关
                intent.setClass(getActivity(), CustomSwitchActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_1)){
                // 控课一路开关
                intent.setClass(getActivity(), SingleBitSwitchActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_2)){
                // 控课二路开关
                intent.setClass(getActivity(), DoubleBitSwitchActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.KONGKE_SWITCH_3)){
                // 控课三路开关
                intent.setClass(getActivity(), ThreeBitSwitchActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_CURTAIN_2)) {
                //平开窗帘
                intent.setClass(getActivity(), CurtainSwitchActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.HILINK_WATER_ALARM)){
                //溢水报警
                intent.setClass(getActivity(), WaterAlarmActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.HILINK_86_RECEPTACLE)){
                //86智能插座
                intent.setClass(getActivity(), HlSmartSocket.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_WIRED)) {
                //有线网关
                intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.setClass(getActivity(), GateDeviceActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_WIRLESS)) {
                //无线网关
                intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.setClass(getActivity(), GateDeviceActivity.class);
            }else if (deviceType.equalsIgnoreCase(DeviceType.HONGYAN_WIRLESS)) {
                // 鸿雁网关
                intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
                intent.setClass(getActivity(), GateDeviceActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.TL_MUSIC)){
                // 天籁网关
                intent.setClass(getActivity(), GateDeviceActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.TL_MUSIC_TYPE)){
                // 天籁
                intent.setClass(getActivity(), TeanaMusicActivity.class);
            }else if(deviceType.equalsIgnoreCase(DeviceType.KONGKE_GATEWAY)){
                // 控课网关
                intent.setClass(getActivity(), GateDeviceActivity.class);
            }
            else {
                return;
            }
            startActivity(intent);
        } else if(view.getId() == R.id.item_iot_device_switch) {
            if (mDeviceManageItemVos.get(position).getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)) {
                DeviceControlUtil.switchControl(getActivity(), mDeviceManageItemVos.get(position), 1, 2);
            }
        }
//        switch (view.getId()) {
//            case R.id.item_iot_device_linear:
//                deviceType = mDeviceManageItemVos.get(position).getDeviceType();
//                intent = new Intent();
//                intent.putExtra("deviceItem", mDeviceManageItemVos.get(position));
//                if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_3)) {
//                    intent.setClass(getActivity(), ThreeBitSwitchActivity.class);
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)) {
//                    intent.setClass(getActivity(), GateDeviceActivity.class);
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_2)) {
//                    intent.setClass(getActivity(), DoubleBitSwitchActivity.class);
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SWITCH_4)) {
//                    intent.setClass(getActivity(), FourBitSwitchActivity.class);
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_CURTAIN)) {
//                    intent.setClass(getActivity(), CurtainSwitchActivity.class);
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_DOOR_MAGNETIC)) {
//                    intent.setClass(getActivity(), DoorMagnetActivity.class);
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_HUMAN_INFRARED)) {
//                    intent.setClass(getActivity(), HumanSensorActivity.class);
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_SMOKE_ALARM)) {
//                    ToastUtil.show("暂不支持");
//                    break;
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_GAS_ALARM)) {
//                    ToastUtil.show("暂不支持");
//                    break;
//                } else if (deviceType.equalsIgnoreCase(DeviceType.HILINK_TEMP_HUN)) {
//                    intent.setClass(getActivity(), HumitureSensorActivity.class);
//                } else if(deviceType.equalsIgnoreCase(DeviceType.HILINK_SCENE_SWITCH_6)){
//                    ToastUtil.show("暂不支持");
//                    break;
//                } else {
//                    break;
//                }
//                if (deviceType.equalsIgnoreCase(DeviceType.HILINK_GATEWAY)) {
//                    intent.putExtra("gateDevices", mDeviceManageItemVos.get(position));
//                    intent.setClass(getActivity(), GateDeviceActivity.class);
//                }
//                startActivity(intent);
//                break;
//            case R.id.item_iot_device_switch:
//                if (mDeviceManageItemVos.get(position).getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)) {
//                    DeviceControlUtil.switchControl(getActivity(), mDeviceManageItemVos.get(position), 1, 2);
//                }
//                break;
//        }
//        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iot_room_device_default_add) {// 点击banner
//            Intent mIntent = new Intent();
//            mIntent.setClass(getActivity(), YsCameraActivity.class);
//            startActivity(mIntent);
            Intent mIntent = new Intent();
            mIntent.setClass(getActivity(), DeviceAppendActivity.class);
            startActivity(mIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
