package com.example.smarthome.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceManageAdapter;
import com.example.smarthome.iot.adapter.DeviceViewAdapter;
import com.example.smarthome.iot.entry.AddRoomVo;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceManageItemVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.CircleImageView;
import com.xhwl.commonlib.uiutils.ClearEditText;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.MaxTextLengthFilter;
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

import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author:
 * date:
 * description: 家庭管理
 * update:
 * version:
 */
public class FamilyManageActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private ClearEditText mFamilyManageName;
    private CircleImageView mRoomManageRoomIcon;
    private RecyclerView mRoomAppendDeviceRv;
    private List<DeviceManageItemVo> mDeviceManageItemVos = new ArrayList<>();
    private DeviceManageAdapter mManageAdapter;
    /**
     * 保存
     */
    private Button mFamilyManageSaveBtn;
    private BaseDialog mBaseDialog;
    private Intent mIntent = new Intent();
    /**
     * 管理设备
     */
    private TextView mRoomDeviceManage;
    /**
     * 添加设备
     */
    private TextView mRoomManageDeviceText;
    private final static int REQUESTCODE = 1; // 返回的结果码
    private String iconName, token, currentRoomName,familyId,roomId,userId;
    private SmartInfoVo.FamilysBean.RoomsBean roomDetail;
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> deviceInfos = new ArrayList<>();
    private DeviceViewAdapter deviceViewAdapter;
    private final static int DEVICE_REQCODE = 2;
    private List<String> devlist = new ArrayList<>();
    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_family_manage);
        initView();
        initDate();
    }

    private void initDate() {
        loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);
        mTopTitle.setText("房间详情");
        mTopBtn.setText("删除");
        mBaseDialog = new BaseDialog(this, 1)
                .setTitleText("提示")
                .setInfoText("确定删除该房间?")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mBaseDialog.setCancelable(false);
//        mRoomsBean = getIntent().getParcelableExtra("roomItem");
        roomId = getIntent().getStringExtra("roomItemId");
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this,"familyId"+userId,"");
        String rooms = SPUtils.get(this, "rooms"+userId, "");
        mRoomsBeanList = new ArrayList<>();
        if (!StringUtils.isEmpty(rooms)) {
            mRoomsBeanList = JSONObject.parseArray(rooms, SmartInfoVo.FamilysBean.RoomsBean.class);
        }
        //获取某房间完整树形结构
//        getDevs(roomId);
        loadingDialog.show();

        deviceViewAdapter = new DeviceViewAdapter(deviceInfos);
        mRoomAppendDeviceRv.setAdapter(deviceViewAdapter);
    }

//    private void getDevs(String roomId) {
//        OkGo.<String>get(Constant.HOST+Constant.Room_getDevs)
//                .params("roomId", roomId)//
//                .converter(new StringConvert())//
//                .adapt(new ObservableResponse<String>())//
//                .subscribeOn(Schedulers.io())//
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
////                        showLoading();
//                        LogUtils.e("accept",disposable.isDisposed()+"===");
//                    }
//                })//
//                .map(new Function<Response<String>, RoomDevsVo>() {
//                    @Override
//                    public RoomDevsVo apply(Response<String> stringResponse) throws Exception {
//                        LogUtils.e("apply","===="+stringResponse.body());
////                        JSON
//                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
//                        RoomDevsVo obj = JSON.parseObject(resp.getResult(),RoomDevsVo.class);
//
//                        return obj;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())//
//                .subscribe(new Observer<RoomDevsVo>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
////                        addDisposable(d);
//                        LogUtils.e("onSubscribe","========---====");
//                    }
//
//                    @Override
//                    public void onNext(RoomDevsVo roomDevsVo) {
////                        loadingDialog.dismiss();
//                        roomDetail = roomDevsVo.getRoom();
//                        if(roomDetail!=null){
//                            mRoomManageRoomIcon.setImageResource(UiTools.getResource(FamilyManageActivity.this,roomDetail.getIcon(), "drawable", R.drawable.icon_iot_room_master));
//                            mRoomManageName.setText(roomDetail.getRoomName());
//                            currentRoomName = roomDetail.getRoomName();
//                            iconName = roomDetail.getIcon();
//
//                            deviceInfos.addAll(roomDetail.getDeviceInfo());
//                            if(deviceInfos!=null && deviceInfos.size()>0){
//                                mRoomAppendDeviceRv.setVisibility(View.VISIBLE);
//                                mRoomDeviceManage.setVisibility(View.VISIBLE);
//                                mRoomManageDeviceText.setVisibility(View.GONE);
//                                deviceViewAdapter.notifyDataSetChanged();
//                            }
//                        }
//
//                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
//                    }
//
//
//                    @Override
//                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        e.printStackTrace();
//                        ToastUtil.show(FamilyManageActivity.this,"请求失败");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        loadingDialog.dismiss();
//                        LogUtils.e("onComplete","============");
//                    }
//                });
//    }

    private void initView() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);

        mFamilyManageName = findViewById(R.id.family_manage_name);
        mFamilyManageName.setCursorVisible(false); //设置输入框不可编辑、光标不可见
        mFamilyManageName.setClearIconVisible(false); //设置清楚icon是否显示
        mFamilyManageName.clearFocus(); // 设置不弹出软件盘
        mFamilyManageName.setFilters(new InputFilter[]{new MaxTextLengthFilter(10)});
        mFamilyManageName.setOnClickListener(this);

        mFamilyManageSaveBtn = (Button) findViewById(R.id.family_manage_save_btn);
        mFamilyManageSaveBtn.setOnClickListener(this);
        StringUtils.setCanNotEditNoClick(mFamilyManageName); // 默认取消输入框的输入

    }

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.top_back) {
            finish();
        } else if (i1 == R.id.family_manage_name) {// 点击可修改名字
            StringUtils.setCanEdit(mFamilyManageName); // 设置输入框可输入
            mFamilyManageName.setCursorVisible(true);
            mFamilyManageName.setClearIconVisible(true); //设置清除icon是否显示
        } else if (i1 == R.id.family_manage_save_btn) {// 保存修改
//            if (!StringUtils.isEmpty(mFamilyManageName.getText().toString().trim())) {
//                    if (RoomAppendActivity.checkRoomNameRepeat(roomId, mFamilyManageName.getText().toString().trim(), mRoomsBeanList)) {
//                        ToastUtil.show(FamilyManageActivity.this,"与其他房间名称相同");
//                        return;
//                    }
//                AddRoomVo updataRoom = new AddRoomVo();
//                updataRoom.setFamilyId(familyId);
//                updataRoom.setRoomId(roomId);
//                updataRoom.setRoomName(mRoomManageName.getText().toString().trim());
//                updataRoom.setIcon(iconName);
//                if (deviceInfos.size() > 0) {
//                    for (int i = 0; i < deviceInfos.size(); i++) {
//                        devlist.add(deviceInfos.get(i).getDeviceId());
//                    }
//                }
//                updataRoom.setDeviceIds(devlist);
//                loadingDialog.show();
//                smartUpdateRoom(updataRoom);
//            } else {
//                ToastUtil.show(FamilyManageActivity.this,"房间名称为空");
//            }
        } else if (i1 == R.id.top_btn) {// 删除
            mBaseDialog.show();
        }
    }


    @Override
    public void onCancelListener() {
        if (mBaseDialog.isShowing()) {
            mBaseDialog.dismiss();
        }
    }

    @Override
    public void onConfirmClick() {
        mBaseDialog.dismiss();
        //执行删除操作
        if(!StringUtils.isEmpty(familyId) && !StringUtils.isEmpty(roomId)){
            removeRoom(familyId, roomId);
            loadingDialog.show();
        } else {
            ToastUtil.show(FamilyManageActivity.this,"房间或家庭不存在");
        }
    }

    // 删除家庭房间
    private void removeRoom(String familyId, String ids) {

        OkGo.<String>get(Constant.HOST+Constant.Room_delete)
                .params("familyId",familyId)
                .params("roomId",roomId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
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
//                        JSON
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
                        if(resp.getErrorCode().equalsIgnoreCase("200")){
                            ToastUtil.show(FamilyManageActivity.this,"删除成功");
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                            finish();
                        } else {
                            ToastUtil.show(FamilyManageActivity.this,"请求失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(FamilyManageActivity.this,"请求失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete","============");
                    }
                });
    }

    //更新房间
    private void smartUpdateRoom(AddRoomVo updataRoom){
        OkGo.<String>post(Constant.HOST+Constant.Room_update)
                .upJson(JSON.toJSONString(updataRoom))
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
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
                    public CommonResp apply(Response<String> stringResponse) {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
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
                        if(resp.getErrorCode().equalsIgnoreCase("200")){
                            ToastUtil.show(FamilyManageActivity.this,"更新成功");
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                            finish();
                        } else {
                            ToastUtil.show(FamilyManageActivity.this,"更新失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(FamilyManageActivity.this,"更新失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete","============");
                    }
                });
    }
}
