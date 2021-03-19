package com.example.smarthome.iot;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceAddressAdapter;
import com.example.smarthome.iot.entry.CommonResp;
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
 * author: glq
 * date: 2019/5/17 13:28
 * description: 添加设备成功
 * update: 2019/5/17
 * version:
 */
public class AddDeviceSuccessActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private View mTitleLine;
    /**
     * 客厅网关
     */
    private ClearEditText mDeviceName;
    /**
     * 客厅
     */
    private TextView mDeviceAddress;
    /**
     * 保存
     */
    private Button mDeviceSettingsSaveBtn;
    private RecyclerView mDeviceSettingsRoomRv;
    private DeviceAddressAdapter mAddressAdapter;
    private List<String> mStringList = new ArrayList<>();
    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList;
    private SmartInfoVo.FamilysBean.DeviceInfoBean mDeviceInfoBean;
    private String userId;
    private ZLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_success);
        loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);
        initView();
        initDate();
    }

    private void initDate() {
        mTopTitle.setText("结果");
        mTitleLine.setVisibility(View.GONE);
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        String rooms = SPUtils.get(this, "rooms"+userId, "");
        mRoomsBeanList = new ArrayList<>();
        if (!StringUtils.isEmpty(rooms)) {
            mRoomsBeanList = JSONObject.parseArray(rooms, SmartInfoVo.FamilysBean.RoomsBean.class);
        }
        mDeviceInfoBean = getIntent().getParcelableExtra("DeviceInfoBean");
        if(mDeviceInfoBean!=null){
            mDeviceName.setText(mDeviceInfoBean.getDeviceName());
        }
        mAddressAdapter = new DeviceAddressAdapter(mRoomsBeanList);
        mDeviceSettingsRoomRv.setAdapter(mAddressAdapter);
        mAddressAdapter.setOnItemChildClickListener(this);
    }

    private void initView() {
        mRoomsBeanList = new ArrayList<>();
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTitleLine = (View) findViewById(R.id.title_line);
        mDeviceName = (ClearEditText) findViewById(R.id.device_name);
        mDeviceAddress = (TextView) findViewById(R.id.device_address);
        mDeviceAddress.setOnClickListener(this);
        mDeviceSettingsSaveBtn = (Button) findViewById(R.id.device_settings_save_btn);
        mDeviceSettingsSaveBtn.setOnClickListener(this);
        mDeviceSettingsRoomRv = (RecyclerView) findViewById(R.id.device_settings_room_rv);
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
        if(v.getId() == R.id.top_back){
            finish();
        } else if(v.getId() ==R.id.device_address){

        } else if(v.getId() ==R.id.device_settings_save_btn){
            // 保存
            SmartInfoVo.FamilysBean.RoomsBean checkedRoom = mAddressAdapter.getClickedItem();
            if (mDeviceInfoBean != null && checkedRoom != null /*&& !StringUtils.isEmpty(mDeviceName.getText().toString().trim())*/) {
                loadingDialog.show();
                smartUpdateDevice(mDeviceInfoBean.getDeviceId(), mDeviceName.getText().toString().trim(), checkedRoom.getRoomName(), checkedRoom.getRoomId());
            } else {
                ToastUtil.show(AddDeviceSuccessActivity.this,"设备名称或设备位置为空");
            }
        }
    }

    //获取FlexboxLayout的子View
    private View getFlexboxLayoutItemView(int position) {
        View view = getLayoutInflater().inflate(R.layout.item_iot_flex_box_layout, null, false);
        TextView itemTv = (TextView) view.findViewById(R.id.item_tv);
        itemTv.setText(mRoomsBeanList.get(position).getRoomName());
        return view;
    }

    /**
     * 修改设备位置、名字
     *
     * @param location
     * @param deviceId
     * @param deviceName
     */
    private void smartUpdateDevice(String deviceId, String deviceName, String location,String roomId) {
        JSONObject obj = new JSONObject();
        obj.put("deviceId", deviceId);
        obj.put("deviceName", deviceName);
        obj.put("location", location);
        obj.put("roomId", roomId);
        OkGo.<String>post(Constant.HOST+Constant.Device_updateDev)
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
                        LogUtils.e("onSubscribe","   =---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        if(resp !=null){
                            if(resp.getErrorCode().equalsIgnoreCase("200")){
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
                        LogUtils.e("onComplete","   =====");
                    }
                });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            loadingDialog.dismiss();
            if (msg.what == 200) {
                ToastUtil.show(AddDeviceSuccessActivity.this,"操作成功");
                EventBus.getDefault().post(new UpdateFamilyEvent(true));
                finish();
            } else if (msg.what == 202) {
                ToastUtil.show(AddDeviceSuccessActivity.this,"操作失败");
            }
        }
    };

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if(view.getId() == R.id.item_ll){
            for(int i =0;i<mRoomsBeanList.size();i++){
                if(i !=position){
                    mRoomsBeanList.get(i).setChecked(false);
                }
            }
            mRoomsBeanList.get(position).setChecked(!mRoomsBeanList.get(position).isChecked());
            mAddressAdapter.notifyDataSetChanged();
            if(mRoomsBeanList.get(position).isChecked()){
                mDeviceAddress.setText(mRoomsBeanList.get(position).getRoomName());
            } else {
                mDeviceAddress.setText("");
            }
        }
    }
}
