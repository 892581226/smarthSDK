package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceOperateAdapter;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: glq
 * date: 2019/4/8 14:51
 * description: 全部设备列表
 * update: 2019/4/8
 * version: V1.4.1
 */
public class DeviceAllActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private RecyclerView mDeviceAllRv;
    private DeviceOperateAdapter operateAdapter;
    private TextView mTopBtn;
    private String familyId, from, userId;
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> allDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_device_all);
        initView();
        initDate();
    }

    private void initDate() {
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId"+userId, "");
        from = getIntent().getStringExtra("from");
        ArrayList<SmartInfoVo.FamilysBean.DeviceInfoBean> deviceInfoBeans = getIntent().getParcelableArrayListExtra("deviceInfoBeans");
        String gateWayDevs = SPUtils.get(this, "gateWayDevs"+userId, "");
        if (!StringUtils.isEmpty(gateWayDevs)) {
            //后台需要APP过滤已经绑定过房间的设备  显示全部设备，adapter差异化显示
            allDevices = JSONObject.parseArray(gateWayDevs,SmartInfoVo.FamilysBean.DeviceInfoBean.class);
            Log.e("场景点击添加设备",allDevices.toString());

        }
        mTopTitle.setText("全部设备");
        operateAdapter = new DeviceOperateAdapter(allDevices);
        operateAdapter.setFrom(from);
        operateAdapter.setArray(deviceInfoBeans);
        mDeviceAllRv.setAdapter(operateAdapter);
        operateAdapter.setOnItemChildClickListener(this);
    }

//    private void getFamilyDevice() {
//        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
//        deviceInfoVo.setDeviceId("");
//        deviceInfoVo.setDeviceType("DDDDDD");
//        deviceInfoVo.setGatewayId("F88479057B4A");
//        deviceInfoVo.setSupplierId("DYxjSE290221");
//        SmartControlVo smartControlVo = new SmartControlVo();
//        smartControlVo.setUserId(userId);
//        smartControlVo.setFamilyId(familyId);
//        smartControlVo.setCmdType("get");
//        smartControlVo.setCmd("");
//        smartControlVo.setDeviceInfo(deviceInfoVo);
//
//        NetWorkWrapper.smartDeviceControl(smartControlVo, new HttpHandler<String>() {
//
//
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String httpUrl = response.request().url().toString();
//                String json = response.body().string();
//                LogUtils.e("onResponse----", json + "===");
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                super.onFailure(call, e);
//
//            }
//        });
//    }

    private void initView() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mDeviceAllRv = (RecyclerView) findViewById(R.id.device_all_rv);
        mDeviceAllRv.setLayoutManager(new GridLayoutManager(this, 2));
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTopBtn.setText("保存");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {
            finish();
        } else if (i == R.id.top_btn) {// 保存
            Intent intent = new Intent();
            Log.e("场景点击添加设备",operateAdapter.getCheckedList().toString());
            intent.putParcelableArrayListExtra("checkedDeviceList", (ArrayList<? extends Parcelable>) operateAdapter.getCheckedList());
            if (!StringUtils.isEmpty(from) && from.equalsIgnoreCase(RoomAppendActivity.TAG)) {
                //新建房间进入
                setResult(ActionConstant.ADD_ROOM_RESULT_CODE, intent);
            } else if (!StringUtils.isEmpty(from) && from.equalsIgnoreCase("RoomManageActivity")) {
                //从房间详情进入
                setResult(ActionConstant.UPDATE_ROOM_RESULT_CODE, intent);
            } else if (!StringUtils.isEmpty(from) && from.equalsIgnoreCase("SceneAppendActivity")) {
                //新建场景进入
                setResult(ActionConstant.ADD_SCENE_RESULT_CODE, intent);
            } else if (!StringUtils.isEmpty(from) && from.equalsIgnoreCase("SceneUpdateActivity")) {
                //更新场景
                setResult(ActionConstant.UPDATE_SCENE_RESULT_CODE, intent);
            }
            finish();
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.item_iot_device_linear) {
            Log.e("點擊添加",from);
			if (!StringUtils.isEmpty(from) && !from.equalsIgnoreCase("SceneAppendActivity") && !from.equalsIgnoreCase("SceneUpdateActivity")) {
                    if (StringUtils.isEmpty(allDevices.get(position).getRoomId())) {
                        allDevices.get(position).setCheck(!allDevices.get(position).isCheck());
                        operateAdapter.notifyDataSetChanged();
                    }
                }/*else if (from.equalsIgnoreCase("SceneUpdateActivity")){
                    allDevices.get(position).setCheck(!allDevices.get(position).isCheck());
                    operateAdapter.notifyDataSetChanged();
			    } */else {
                    if (!DeviceType.HILINK_SWITCH_1.equals(allDevices.get(position).getDeviceType()) ||
                            !DeviceType.HILINK_SWITCH_2.equals(allDevices.get(position).getDeviceType()) ||
                            !DeviceType.HILINK_SWITCH_3.equals(allDevices.get(position).getDeviceType()) ||
                           ! DeviceType.HILINK_SWITCH_4.equals(allDevices.get(position).getDeviceType())||
                    !DeviceType.HILINK_CURTAIN.equals(allDevices.get(position).getDeviceType())||
                // 鸿雁开关控制
                !DeviceType.HONGYAN_SWITCH_1.equals(allDevices.get(position).getDeviceType()) ||
                        !DeviceType.HONGYAN_SWITCH_2.equals(allDevices.get(position).getDeviceType()) ||
                        !DeviceType.HONGYAN_SWITCH_3.equals(allDevices.get(position).getDeviceType()) ||
                        !DeviceType.HONGYAN_SWITCH_4.equals(allDevices.get(position).getDeviceType())) {
                        allDevices.get(position).setCheck(!allDevices.get(position).isCheck());
                        operateAdapter.notifyDataSetChanged();
                    }
                }
        }
    }
}
