package com.example.smarthome.iot;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceViewAdapter;
import com.example.smarthome.iot.entry.AddRoomVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.YsAccessTokenResp;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
//import com.videogo.openapi.EZOpenSDK;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.CircleImageView;
import com.xhwl.commonlib.uiutils.ClearEditText;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.UiTools;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
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
 * author: glq
 * date: 2019/4/4 17:46
 * description: 新增房间
 * update: 2019/4/4
 * version: V1.4.1
 */
public class RoomAppendActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "RoomAppendActivity";
    private LinearLayout mTopBack;
    private TextView mTopTitle;
    /**
     * 房间名称
     */
    private TextView mRoomNameTitle;
    /**
     * 请输入房间名字
     */
    private ClearEditText mRoomName;
    private CircleImageView mRoomAppendDeviceIcon;
    /**
     * 添加设备
     */
    private TextView mRoomAppendDeviceText;
    /**
     * 管理设备
     */
    private TextView mRoomDeviceManage;
    private ConstraintLayout mRoomIconLayout;
    private RecyclerView mRoomAppendDeviceRv;
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> deviceInfoBeans = new ArrayList<>();
    private DeviceViewAdapter deviceViewAdapter;
    private Intent mIntent = new Intent();

    private final static int REQUESTCODE = 1; // 返回的结果码
    private String familyId, userId;
    private String iconName = "icon_iot_room_master";
    private TextView mTopBtn;
    private final static int DEVICE_REQUESTCODE = 2;
    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_room_append);
        initView();
        initDate();

    }

    private void initDate() {
        mTopTitle.setText("新建房间");
        mTopBtn.setText("保存");
        loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);

        deviceViewAdapter = new DeviceViewAdapter(deviceInfoBeans);
        mRoomAppendDeviceRv.setAdapter(deviceViewAdapter);
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        String rooms = SPUtils.get(this, "rooms" + userId, "");
        mRoomsBeanList = new ArrayList<>();
        if (!StringUtils.isEmpty(rooms)) {
            mRoomsBeanList = JSONObject.parseArray(rooms, SmartInfoVo.FamilysBean.RoomsBean.class);
        }
        LogUtils.e("roomappend mRoomsBeanList", mRoomsBeanList.size() + "   ");

    }

    private void initView() {
        familyId = getIntent().getStringExtra("familyId");

        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mRoomNameTitle = (TextView) findViewById(R.id.room_name_title);
        mRoomName = (ClearEditText) findViewById(R.id.room_name);
        mRoomAppendDeviceIcon = (CircleImageView) findViewById(R.id.room_append_device_icon);
        mRoomIconLayout = (ConstraintLayout) findViewById(R.id.room_icon_layout);
        mRoomIconLayout.setOnClickListener(this);
        mRoomAppendDeviceRv = (RecyclerView) findViewById(R.id.room_append_device_rv);
        mRoomAppendDeviceRv.setLayoutManager(new GridLayoutManager(this, 2));
        mRoomAppendDeviceText = (TextView) findViewById(R.id.room_append_device_text);
        mRoomAppendDeviceText.setOnClickListener(this);
        mRoomDeviceManage = (TextView) findViewById(R.id.room_device_manage);
        mRoomDeviceManage.setOnClickListener(this);

        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.top_back) {
            finish();
        } else if (i1 == R.id.room_icon_layout) {
            mIntent.setClass(this, IconUpdateActivity.class);
            mIntent.putExtra("isRoomIcon", true);
//                startActivity(mIntent);
            // 这种启动方式：startActivity(intent);并不能返回结果
            startActivityForResult(mIntent, REQUESTCODE); //REQUESTCODE--->1
        } else if (i1 == R.id.room_append_device_text) {// 添加设备
            mIntent.setClass(this, DeviceAllActivity.class);
            mIntent.putParcelableArrayListExtra("deviceInfoBeans", (ArrayList<? extends Parcelable>) deviceInfoBeans);
            mIntent.putExtra("from", TAG);
            startActivityForResult(mIntent, DEVICE_REQUESTCODE);
        } else if (i1 == R.id.room_device_manage) {// 管理设备
            mIntent.setClass(this, DeviceAllActivity.class);
            mIntent.putParcelableArrayListExtra("deviceInfoBeans", (ArrayList<? extends Parcelable>) deviceInfoBeans);
            mIntent.putExtra("from", TAG);
            startActivity(mIntent);
        } else if (i1 == R.id.top_btn) {
            if (mRoomsBeanList.size() < 20) {
                if (!StringUtils.isEmpty(familyId) && !StringUtils.isEmpty(mRoomName.getText().toString().trim())) {
                    if (checkRoomNameRepeat("", mRoomName.getText().toString().trim(), mRoomsBeanList)) {
                        ToastUtil.show(RoomAppendActivity.this, "与其他房间名称相同");
                        return;
                    }
//                    saveRoom(mRoomName.getText().toString().trim(), iconName, familyId);
                    AddRoomVo addRoomVo = new AddRoomVo();
                    addRoomVo.setFamilyId(familyId);
                    addRoomVo.setIcon(iconName);
                    addRoomVo.setRoomName(mRoomName.getText().toString().trim());
                    List<String> devlist = new ArrayList<>();
                    if (deviceInfoBeans.size() > 0) {
                        for (int i = 0; i < deviceInfoBeans.size(); i++) {
                            devlist.add(deviceInfoBeans.get(i).getDeviceId());
                        }
                    }
                    addRoomVo.setDeviceIds(devlist);
                    saveRoom(addRoomVo);
                    loadingDialog.show();
                } else {
                    ToastUtil.show(RoomAppendActivity.this, "房间名称为空");
                }
            } else {
                ToastUtil.show(RoomAppendActivity.this, "小七还在学习如何创建更多房间哟");
            }
        }
    }

    public static boolean checkRoomNameRepeat(String id, String name, List<SmartInfoVo.FamilysBean.RoomsBean> list) {
        if (list != null && list.size() > 0) {
            for (SmartInfoVo.FamilysBean.RoomsBean roomsBean : list) {
                if (roomsBean.getRoomName().equals(name) && !roomsBean.getRoomId().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActionConstant.ADD_ROOM_RESULT_CODE) {
            if (requestCode == REQUESTCODE) {
                //图片
                int icon = UiTools.getResource(RoomAppendActivity.this, data.getStringExtra("iconName"), "drawable", R.drawable.icon_iot_room_master);
                mRoomAppendDeviceIcon.setImageResource(icon);
                iconName = getResources().getResourceEntryName(icon);
            } else if (requestCode == DEVICE_REQUESTCODE) {
                //deviceInfoBeans.clear();
                deviceInfoBeans.addAll(data.getParcelableArrayListExtra("checkedDeviceList"));
                for (int i = 0; i < deviceInfoBeans.size(); i++) {
                    for (int j = 0; j < i; j++) {
                        if(deviceInfoBeans.get(i).getDeviceType().equals(deviceInfoBeans.get(j).getDeviceType())){
                            deviceInfoBeans.remove(i);
                            i=i-1;
                        }
                    }
                }
                if (deviceInfoBeans.size() > 0) {
                    mRoomAppendDeviceRv.setVisibility(View.VISIBLE);
                }
                deviceViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void saveRoom(AddRoomVo addRoomVo) {
//        NetWorkWrapper.smartCreateRoom(addRoomVo, new HttpHandler<String>() {
//
//
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                loadingDialog.dismiss();
//                EventBus.getDefault().post(new UpdateFamilyEvent(true));
//                finish();
//                Looper.prepare();
//                ToastUtil.showCenterToast("新建成功");
//                Looper.loop();
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                super.onFailure(call, e);
//                loadingDialog.dismiss();
//            }
//        });
        OkGo.<String>post(Constant.HOST + Constant.Room_create)
                .upJson(JSON.toJSONString(addRoomVo))//
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
//                .map(new Function<Response<String>, YsAccessTokenResp>() {
//                    @Override
//                    public YsAccessTokenResp apply(Response<String> stringResponse) throws Exception {
//                        LogUtils.e("apply","===="+stringResponse.body());
//                        YsAccessTokenResp resp = JSON.parseObject(stringResponse.body(), YsAccessTokenResp.class);
//                        LogUtils.e("apply",resp.getData().getAccessToken()+"   ==");
//                        return resp;
//                    }
//                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "   =---====");
                    }

                    @Override
                    public void onNext(Response<String> resp) {
                        LogUtils.e("onNext:", "------------------");//

                        EventBus.getDefault().post(new UpdateFamilyEvent(true));
                        finish();
                        ToastUtil.show(RoomAppendActivity.this, "新建成功");
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(RoomAppendActivity.this, "请求失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "   =====");
                    }
                });

    }

}
