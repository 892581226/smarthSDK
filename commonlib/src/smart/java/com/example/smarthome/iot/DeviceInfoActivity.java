package com.example.smarthome.iot;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceAddressAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceInfoVo;
import com.example.smarthome.iot.entry.SmartControlVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
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
import com.xhwl.commonlib.uiutils.ClearEditText;
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

import static com.xhwl.commonlib.application.MyAPP.destoryActivity;
import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author: glq
 * date: 2019/5/14 17:26
 * description: 设备详情
 * update: 2019/5/14
 * version:
 */
public class DeviceInfoActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private View mTitleLine;
    /**
     * 客厅
     */
    private TextView mDeviceInfoAddressName, top_btn;
    private RecyclerView mDeviceSettingsRoomRv;
    private ClearEditText mDeviceInfoDeviceName;
    /**
     * 保存
     */
    private Button mDeviceAddressSaveBtn;
    private SmartInfoVo.FamilysBean.RoomsBean mRoomsBean;
    private SmartInfoVo.FamilysBean.DeviceInfoBean mDeviceInfoBean;
    private DeviceAddressAdapter mAddressAdapter;
    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList;
    private boolean isRvShow = true;
    private ImageView device_info_address_icon;
    private String userId, familyId;
    private BaseDialog mBaseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        initView();
        initDate();
    }

    private void initDate() {
        mTopTitle.setText("设备详情");
        mTitleLine.setVisibility(View.GONE);
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId" + userId, "");
        String rooms = SPUtils.get(this, "rooms" + userId, "");
        mRoomsBeanList = new ArrayList<>();
        if (!StringUtils.isEmpty(rooms)) {
            mRoomsBeanList = JSONObject.parseArray(rooms, SmartInfoVo.FamilysBean.RoomsBean.class);
        }
        if (mRoomsBeanList!=null&&mRoomsBeanList.size()>0){
            LogUtils.e("改变设备名称页面数据",mRoomsBeanList.toString());
        }else {
            LogUtils.e("改变设备名称页面数据","");
        }
        mDeviceInfoDeviceName.setText(mDeviceInfoBean.getDeviceName());
        mDeviceInfoAddressName.setText(mDeviceInfoBean.getRoomName());
        mAddressAdapter = new DeviceAddressAdapter(mRoomsBeanList);
        mDeviceSettingsRoomRv.setAdapter(mAddressAdapter);
        mAddressAdapter.setOnItemChildClickListener(this);

        mBaseDialog = new BaseDialog(this, 1)
                .setTitleText("删除设备")
                .setInfoText("删除网关设备时，请确保网关设备在线，设备删除后会清除与设备相关的所有数据。")
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
        mTopBack =findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        top_btn = findViewById(R.id.top_btn);
        top_btn.setText("删除");
        top_btn.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTitleLine = (View) findViewById(R.id.title_line);
        mDeviceInfoAddressName = (TextView) findViewById(R.id.device_info_address_name);
        mDeviceSettingsRoomRv = (RecyclerView) findViewById(R.id.device_settings_room_rv);
        mDeviceInfoDeviceName = (ClearEditText) findViewById(R.id.device_info_device_name);
        mDeviceAddressSaveBtn = (Button) findViewById(R.id.device_address_save_btn);
        mDeviceAddressSaveBtn.setOnClickListener(this);
        device_info_address_icon = (ImageView) findViewById(R.id.device_info_address_icon);
        LinearLayout device_info_address_layout = (LinearLayout) findViewById(R.id.device_info_address_layout);
        device_info_address_layout.setOnClickListener(this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        //设置主轴排列方式
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setAlignItems(AlignItems.CENTER);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);//交叉轴的起点对齐。
        mDeviceSettingsRoomRv.setLayoutManager(flexboxLayoutManager);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.top_back) {
            finish();
        } else if (v.getId() == R.id.top_btn) {
            if (mDeviceInfoBean.getSceneFlag()!=null) {
                if (!mDeviceInfoBean.getSceneFlag().equals("0")) {
                    ToastUtil.show(DeviceInfoActivity.this, "设备绑定了场景，无法删除");
                } else {
                    mBaseDialog.show();
                }
            }else {
                mBaseDialog.show();
            }
        } else if (v.getId() == R.id.device_address_save_btn) {
            SmartInfoVo.FamilysBean.RoomsBean checkedRoom = mAddressAdapter.getClickedItem();
            if (!StringUtils.isEmpty(mDeviceInfoDeviceName.getText().toString().trim())) {
                if (checkedRoom != null) {
                    LogUtils.e("DeviceInfoActivity", checkedRoom.getRoomId() + "====" + checkedRoom.getRoomName());
                    smartUpdateDevice(mDeviceInfoBean.getDeviceId(), mDeviceInfoDeviceName.getText().toString().trim(), checkedRoom.getRoomName(), checkedRoom.getRoomId());
                    loadingDialog.show();
                } else if (!StringUtils.isEmpty(mDeviceInfoBean.getRoomName()) && !StringUtils.isEmpty(mDeviceInfoBean.getRoomId())) {
                    LogUtils.e("DeviceInfoActivity", mDeviceInfoBean.getRoomId() + "====" + mDeviceInfoBean.getRoomName());
                    smartUpdateDevice(mDeviceInfoBean.getDeviceId(), mDeviceInfoDeviceName.getText().toString().trim(), mDeviceInfoBean.getRoomName(), mDeviceInfoBean.getRoomId());
                    loadingDialog.show();
                } else {
                    ToastUtil.show(DeviceInfoActivity.this, "设备位置为空");
                }
            } else {
                ToastUtil.show(DeviceInfoActivity.this, "设备名称为空");
            }
        } else if (v.getId() == R.id.device_info_address_layout) {
            isRvShow = !isRvShow;
            if (isRvShow) {
                mDeviceSettingsRoomRv.setVisibility(View.VISIBLE);
                device_info_address_icon.setImageDrawable(DeviceInfoActivity.this.getResources().getDrawable(R.drawable.icon_blue_top));
            } else {
                mDeviceSettingsRoomRv.setVisibility(View.GONE);
                device_info_address_icon.setImageDrawable(DeviceInfoActivity.this.getResources().getDrawable(R.drawable.icon_blue_bottom));
            }
        }

//        switch (v.getId()) {
//            default:
//                break;
//            case R.id.top_back:
//                finish();
//                break;
//            case R.id.top_btn:
//                if(!mDeviceInfoBean.getSceneFlag().equals("0")){
//                    ToastUtil.show(DeviceInfoActivity.this,"设备绑定了场景，无法删除");
//                }else {
//                    mBaseDialog.show();
//                }
//                break;
//            case R.id.device_address_save_btn:
//                SmartInfoVo.FamilysBean.RoomsBean checkedRoom = mAddressAdapter.getClickedItem();
//                if(!StringUtils.isEmpty(mDeviceInfoDeviceName.getText().toString().trim())){
//                    if(checkedRoom!=null){
//                        LogUtils.e("DeviceInfoActivity",checkedRoom.getRoomId()+"===="+checkedRoom.getRoomName());
//                        smartUpdateDevice(mDeviceInfoBean.getDeviceId(), mDeviceInfoDeviceName.getText().toString().trim(),checkedRoom.getRoomName(),checkedRoom.getRoomId());
//                        loadingDialog.show();
//                    } else if(!StringUtils.isEmpty(mDeviceInfoBean.getRoomName()) && !StringUtils.isEmpty(mDeviceInfoBean.getRoomId())){
//                        LogUtils.e("DeviceInfoActivity",mDeviceInfoBean.getRoomId()+"===="+mDeviceInfoBean.getRoomName());
//                        smartUpdateDevice(mDeviceInfoBean.getDeviceId(), mDeviceInfoDeviceName.getText().toString().trim(),mDeviceInfoBean.getRoomName(),mDeviceInfoBean.getRoomId());
//                        loadingDialog.show();
//                    } else {
//                        ToastUtil.show(DeviceInfoActivity.this,"设备位置为空");
//                    }
//                } else {
//                    ToastUtil.show(DeviceInfoActivity.this,"设备名称为空");
//                }
////                if(checkedRoom!=null && !StringUtils.isEmpty(mDeviceInfoDeviceName.getText().toString().trim())){
////                    LogUtils.e("DeviceInfoActivity",checkedRoom.getRoomId()+"===="+checkedRoom.getRoomName());
////                    smartUpdateDevice(mDeviceInfoBean.getDeviceId(), mDeviceInfoDeviceName.getText().toString().trim(),checkedRoom.getRoomName(),checkedRoom.getRoomId());
////                    loadingDialog.show();
////                } else {
////                    ToastUtil.show("设备名称或设备位置为空");
////                }
//                break;
//            case R.id.device_info_address_layout:
//                isRvShow = !isRvShow;
//                if(isRvShow){
//                    mDeviceSettingsRoomRv.setVisibility(View.VISIBLE);
//                    device_info_address_icon.setImageDrawable(DeviceInfoActivity.this.getResources().getDrawable(R.drawable.icon_blue_top));
//                } else {
//                    mDeviceSettingsRoomRv.setVisibility(View.GONE);
//                    device_info_address_icon.setImageDrawable(DeviceInfoActivity.this.getResources().getDrawable(R.drawable.icon_blue_bottom));
//                }
//                break;
//        }
    }

    /**
     * 修改设备位置、名字
     *
     * @param location
     * @param deviceId
     * @param deviceName
     */
    private void smartUpdateDevice(String deviceId, String deviceName, String location, String roomId) {
//        NetWorkWrapper.smartUpdateDevice(deviceId, deviceName, location, roomId, new HttpHandler<String>() {
//            @Override
//            public void onSuccess(ServerTip serverTip, String baseResult) {
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String httpResponse = response.body().string();
//                ResponseVo responseVo = JSONObject.parseObject(httpResponse,ResponseVo.class);
//                if(responseVo!=null){
//                    if(responseVo.getErrorCode().equalsIgnoreCase("200")){
//                        handler.sendEmptyMessage(200);
//                    } else {
//                        handler.sendEmptyMessage(202);
//                    }
//                } else {
//                    handler.sendEmptyMessage(202);
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                super.onFailure(call, e);
//                handler.sendEmptyMessage(202);
//            }
//        });
        JSONObject obj = new JSONObject();
        String httpId="";
        if(getIntent().getStringExtra("GateDeviceActivity")!=null){
            obj.put("gatewayId", mDeviceInfoBean.getId());
            obj.put("gatewayName", deviceName);
            obj.put("location", location);
            obj.put("roomId", roomId);
            httpId=Constant.Device_updateGateWay;
        }else {
            obj.put("deviceId", deviceId);
            obj.put("deviceName", deviceName);
            obj.put("location", location);
            obj.put("roomId", roomId);
            httpId=Constant.Device_updateDev;
        }

        OkGo.<String>post(Constant.HOST+httpId)
                .upJson(JSON.toJSONString(obj))
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        LogUtils.e("resp",resp.getState()+"=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        if(resp !=null){
                            if(resp.getErrorCode().equalsIgnoreCase("200")){
                                handler.sendEmptyMessage(2000);
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
                        LogUtils.e("onComplete","============");
                    }
                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadingDialog.dismiss();
            if (msg.what == 200) {
                ToastUtil.show(DeviceInfoActivity.this,"操作成功");
                EventBus.getDefault().post(new UpdateFamilyEvent(true, mDeviceInfoDeviceName.getText().toString().trim()));
                finish();
                destoryActivity("Activity");
                destoryActivity("GateDeviceActivity");
                destoryActivity("SingleBitSwitchActivity");
                destoryActivity("DoubleBitSwitchActivity");
                destoryActivity("ThreeBitSwitchActivity");
                destoryActivity("FourBitSwitchActivity");
                destoryActivity("CurtainSwitchActivity");
                destoryActivity("DoorMagnetActivity");
                destoryActivity("HumanSensorActivity");
                destoryActivity("HumitureSensorActivity");
                destoryActivity("SceneSixSwitchActivity");
            } else if (msg.what == 2000) {
                ToastUtil.show(DeviceInfoActivity.this,"操作成功");
                EventBus.getDefault().post(new UpdateFamilyEvent(true, mDeviceInfoDeviceName.getText().toString().trim()));
                finish();
            }else if (msg.what == 202) {
                ToastUtil.show(DeviceInfoActivity.this,"操作失败");
            }
        }
    };

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.item_ll) {
            for (int i = 0; i < mRoomsBeanList.size(); i++) {
                if (i != position) {
                    mRoomsBeanList.get(i).setChecked(false);
                }
            }
            mRoomsBeanList.get(position).setChecked(!mRoomsBeanList.get(position).isChecked());
            mAddressAdapter.notifyDataSetChanged();
            if (mRoomsBeanList.get(position).isChecked()) {
                mDeviceInfoAddressName.setText(mRoomsBeanList.get(position).getRoomName());
            } else {
                mDeviceInfoAddressName.setText("");
            }
        }
//        switch (view.getId()) {
//            case R.id.item_ll:
//                for(int i =0;i<mRoomsBeanList.size();i++){
//                    if(i !=position){
//                        mRoomsBeanList.get(i).setChecked(false);
//                    }
//                }
//                mRoomsBeanList.get(position).setChecked(!mRoomsBeanList.get(position).isChecked());
//                mAddressAdapter.notifyDataSetChanged();
//                if(mRoomsBeanList.get(position).isChecked()){
//                    mDeviceInfoAddressName.setText(mRoomsBeanList.get(position).getRoomName());
//                } else {
//                    mDeviceInfoAddressName.setText("");
//                }
//                break;
//        }
    }

    private void deleteDev() {
        SmartControlVo smartControlVo = new SmartControlVo();
        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
        deviceInfoVo.setDeviceId(mDeviceInfoBean.getDeviceId());
        if(getIntent().getStringExtra("GateDeviceActivity")!=null){
            deviceInfoVo.setDeviceType("GGGGGG");//删除
            smartControlVo.setCmdType("control");
            deviceInfoVo.setGatewayId(mDeviceInfoBean.getId());
        }else {
            deviceInfoVo.setDeviceType("EEEEEE");//删除
            smartControlVo.setCmdType("del");
            deviceInfoVo.setGatewayId(mDeviceInfoBean.getGatewayId());
        }
        deviceInfoVo.setSupplierId(mDeviceInfoBean.getSupplierId());
        smartControlVo.setUserId(userId);
        smartControlVo.setFamilyId(familyId);
        smartControlVo.setCmd("");
        smartControlVo.setDeviceInfo(deviceInfoVo);

//        NetWorkWrapper.smartDeviceControl(smartControlVo, new HttpHandler<String>() {
//
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String json = response.body().string();
//                JSONObject resObj = JSONObject.parseObject(json);
//
//                handler.sendEmptyMessage(resObj.getInteger("errorCode"));
//
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                super.onFailure(call, e);
//                handler.sendEmptyMessage(202);
//            }
//        });
        LogUtils.e("删除设备",smartControlVo.toString());

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