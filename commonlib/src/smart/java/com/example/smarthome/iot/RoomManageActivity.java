package com.example.smarthome.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceManageAdapter;
import com.example.smarthome.iot.adapter.DeviceViewAdapter;
import com.example.smarthome.iot.entry.AddRoomVo;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceManageItemVo;
import com.example.smarthome.iot.entry.RoomDevsVo;
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
import com.xhwl.commonlib.uiutils.UiTools;
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
 * author: glq
 * date: 2019/4/8 9:22
 * description: 房间详情
 * update: 2019/4/8
 * version: V1.4.1
 */
public class RoomManageActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    /**
     * 请输入房间名字
     */
    private ClearEditText mRoomManageName;
    private CircleImageView mRoomManageRoomIcon;
    private RecyclerView mRoomAppendDeviceRv;
    private List<DeviceManageItemVo> mDeviceManageItemVos = new ArrayList<>();
    private DeviceManageAdapter mManageAdapter;
    /**
     * 保存
     */
    private Button mRoomManageSaveBtn;
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
    //    private RoomListItemVo.Item mRoomItem;
//    private RoomListVo.RoomItem mRoomsBean;
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
        setContentView(R.layout.activity_iot_room_manage);
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
        //mTopBtn.setText("删除");
        mTopBtn.setVisibility(View.GONE);
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
        getDevs(roomId);
        loadingDialog.show();

        deviceViewAdapter = new DeviceViewAdapter(deviceInfos);
        mRoomAppendDeviceRv.setAdapter(deviceViewAdapter);
    }

    private void getDevs(String roomId) {
//        NetWorkWrapper.smartGetDevs(roomId,new HttpHandler<RoomDevsVo>(){
//
//            @Override
//            public void onSuccess(ServerTip serverTip, RoomDevsVo roomVo) {
//                loadingDialog.dismiss();
//                roomDetail = roomVo.getRoom();
//                if(roomDetail!=null){
//                    mRoomManageRoomIcon.setImageResource(MyAPP.getIns().getResource(roomDetail.getIcon(), "drawable", R.drawable.icon_iot_room_master));
//                    mRoomManageName.setText(roomDetail.getRoomName());
//                    currentRoomName = roomDetail.getRoomName();
//                    iconName = roomDetail.getIcon();
//
//                    deviceInfos.addAll(roomDetail.getDeviceInfo());
//                    if(deviceInfos!=null && deviceInfos.size()>0){
//                        for(int i=0;i<deviceInfos.size();i++){
//                            devlist.add(deviceInfos.get(i).getDeviceId());
//                        }
//                        mRoomAppendDeviceRv.setVisibility(View.VISIBLE);
//                        mRoomDeviceManage.setVisibility(View.VISIBLE);
//                        mRoomManageDeviceText.setVisibility(View.GONE);
//                        deviceViewAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(ServerTip serverTip) {
//                super.onFailure(serverTip);
//                loadingDialog.dismiss();
//            }
//        });

        OkGo.<String>get(Constant.HOST+Constant.Room_getDevs)
                .params("roomId", roomId)//
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })//
                .map(new Function<Response<String>, RoomDevsVo>() {
                    @Override
                    public RoomDevsVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        RoomDevsVo obj = JSON.parseObject(resp.getResult(),RoomDevsVo.class);

                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<RoomDevsVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(RoomDevsVo roomDevsVo) {
//                        loadingDialog.dismiss();
                        roomDetail = roomDevsVo.getRoom();
                        if(roomDetail!=null){
                            mRoomManageRoomIcon.setImageResource(UiTools.getResource(RoomManageActivity.this,roomDetail.getIcon(), "drawable", R.drawable.icon_iot_room_master));
                            mRoomManageName.setText(roomDetail.getRoomName());
                            currentRoomName = roomDetail.getRoomName();
                            iconName = roomDetail.getIcon();

                            deviceInfos.addAll(roomDetail.getDeviceInfo());
                            if(deviceInfos!=null && deviceInfos.size()>0){
                                mRoomAppendDeviceRv.setVisibility(View.VISIBLE);
                                mRoomDeviceManage.setVisibility(View.VISIBLE);
                                mRoomManageDeviceText.setVisibility(View.GONE);
                                deviceViewAdapter.notifyDataSetChanged();
                            }
                        }

                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(RoomManageActivity.this,"请求失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete","============");
                    }
                });
    }

    private void initView() {
        token = SPUtils.get(this, SpConstant.SP_USERTOKEN, "");
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mRoomManageName = findViewById(R.id.room_manage_name);
        mRoomManageName.setCursorVisible(false); //设置输入框不可编辑、光标不可见
        mRoomManageName.setClearIconVisible(false); //设置清楚icon是否显示
        mRoomManageName.clearFocus(); // 设置不弹出软件盘
        mRoomManageName.setFilters(new InputFilter[]{new MaxTextLengthFilter(10)});
        mRoomManageName.setOnClickListener(this);
        mRoomManageRoomIcon = (CircleImageView) findViewById(R.id.room_manage_room_icon);
        mRoomManageRoomIcon.setOnClickListener(this);
        mRoomAppendDeviceRv = (RecyclerView) findViewById(R.id.room_manage_device_rv);
        mRoomAppendDeviceRv.setLayoutManager(new GridLayoutManager(this, 2));
        mRoomManageSaveBtn = (Button) findViewById(R.id.room_manage_save_btn);
        mRoomManageSaveBtn.setOnClickListener(this);
        mRoomDeviceManage = (TextView) findViewById(R.id.room_device_manage);
        mRoomDeviceManage.setOnClickListener(this);
        mRoomManageDeviceText = (TextView) findViewById(R.id.room_manage_add_device_text);
        mRoomManageDeviceText.setOnClickListener(this);
        StringUtils.setCanNotEditNoClick(mRoomManageName); // 默认取消输入框的输入

    }

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.top_back) {
            finish();
        } else if (i1 == R.id.room_manage_name) {// 点击可修改名字
            StringUtils.setCanEdit(mRoomManageName); // 设置输入框可输入
            mRoomManageName.setCursorVisible(true);
            mRoomManageName.setClearIconVisible(true); //设置清除icon是否显示
        } else if (i1 == R.id.room_manage_room_icon) {// 修改房间图标
            mIntent.setClass(this, IconUpdateActivity.class);
            mIntent.putExtra("isRoomIcon", true);
            mIntent.putExtra("isUpdate", true);
            startActivityForResult(mIntent, REQUESTCODE);
        } else if (i1 == R.id.room_manage_save_btn) {// 保存修改
            if (!StringUtils.isEmpty(mRoomManageName.getText().toString().trim())) {
                if (RoomAppendActivity.checkRoomNameRepeat(roomId, mRoomManageName.getText().toString().trim(), mRoomsBeanList)) {
                    ToastUtil.show(RoomManageActivity.this,"与其他房间名称相同");
                    return;
                }
                AddRoomVo updataRoom = new AddRoomVo();
                updataRoom.setFamilyId(familyId);
                updataRoom.setRoomId(roomId);
                updataRoom.setRoomName(mRoomManageName.getText().toString().trim());
                updataRoom.setIcon(iconName);
                if (deviceInfos.size() > 0) {
                    for (int i = 0; i < deviceInfos.size(); i++) {
                        devlist.add(deviceInfos.get(i).getDeviceId());
                    }
                }
                updataRoom.setDeviceIds(devlist);
                loadingDialog.show();
                smartUpdateRoom(updataRoom);
            } else {
                ToastUtil.show(RoomManageActivity.this,"房间名称为空");
            }
        } else if (i1 == R.id.top_btn) {// 删除
            mBaseDialog.show();
        } else if (i1 == R.id.room_device_manage) {// 管理设备
            mIntent.setClass(this, DeviceAllActivity.class);
            mIntent.putParcelableArrayListExtra("deviceInfoBeans", (ArrayList<? extends Parcelable>) deviceInfos);
            mIntent.putExtra("from", "RoomManageActivity");
            startActivityForResult(mIntent, DEVICE_REQCODE);
        } else if (i1 == R.id.room_manage_add_device_text) {// 添加设备
            mIntent.setClass(this, DeviceAllActivity.class);
            mIntent.putParcelableArrayListExtra("deviceInfoBeans", (ArrayList<? extends Parcelable>) deviceInfos);
            mIntent.putExtra("from", "RoomManageActivity");
            startActivityForResult(mIntent, DEVICE_REQCODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActionConstant.UPDATE_ROOM_RESULT_CODE) {
            if (requestCode == REQUESTCODE) {
                //图片
                int icon = UiTools.getResource(RoomManageActivity.this,data.getStringExtra("iconName"), "drawable", R.drawable.icon_iot_room_master);
                mRoomManageRoomIcon.setImageResource(icon);
                iconName = getResources().getResourceEntryName(icon);
            }else if(requestCode == DEVICE_REQCODE) {
                //deviceInfos.clear();   //清空集合28
                devlist.clear();
                deviceInfos.addAll(data.getParcelableArrayListExtra("checkedDeviceList"));
                for (int i = 0; i < deviceInfos.size(); i++) {
                    for (int j = 0; j < i; j++) {
                        if(deviceInfos.get(i).getDeviceType().equals(deviceInfos.get(j).getDeviceType())){
                            deviceInfos.remove(i);
                            i=i-1;
                        }
                    }
                }
                if(deviceInfos.size()>0){
                    mRoomAppendDeviceRv.setVisibility(View.VISIBLE);
                    mRoomDeviceManage.setVisibility(View.VISIBLE);
                    mRoomManageDeviceText.setVisibility(View.GONE);
                }
                deviceViewAdapter.notifyDataSetChanged();
            }
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
            ToastUtil.show(RoomManageActivity.this,"房间或家庭不存在");
        }
    }

    // 删除家庭房间
    private void removeRoom(String familyId, String ids) {
//        NetWorkWrapper.smartDeleteRoom(familyId, ids, new HttpHandler<String>() {
//            @Override
//            public void onSuccess(ServerTip serverTip, String baseResult) {
//                loadingDialog.dismiss();
//                ToastUtil.showCenterToast("删除成功");
//                EventBus.getDefault().post(new UpdateFamilyEvent(true));
//                finish();
//            }
//
//            @Override
//            public void onFailure(ServerTip serverTip) {
//                super.onFailure(serverTip);
//                loadingDialog.dismiss();
//                ToastUtil.show("请求失败");
//            }
//        });

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
                            ToastUtil.show(RoomManageActivity.this,"删除成功");
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                            finish();
                        } else {
                            ToastUtil.show(RoomManageActivity.this,"请求失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(RoomManageActivity.this,"请求失败");
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
//        NetWorkWrapper.smartUpdateRoom(updataRoom,new HttpHandler<String>() {
//
//
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String httpResponse = response.body().string();
//
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
                            ToastUtil.show(RoomManageActivity.this,"更新成功");
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                            finish();
                        } else {
                            ToastUtil.show(RoomManageActivity.this,"更新失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(RoomManageActivity.this,"更新失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete","============");
                    }
                });
    }
}
