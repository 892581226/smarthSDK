package com.example.smarthome.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.RoomListAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.example.smarthome.iot.entry.RoomListVo;
import com.example.smarthome.iot.entry.SmartInfoVo;

import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.impl.DeviceAppendActivity;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.ClearEditText;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.MaxTextLengthFilter;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
import com.xhwl.commonlib.uiutils.dialog.BaseEditDialog;
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
 * date: 2019/4/4 17:44
 * description: 家庭房间详情
 * update: 2019/4/4
 * version: V1.4.1
 */
public class FamilyRoomActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private ImageView mTopRecord;
    /**
     * 我的家
     */
    private ClearEditText mFamilyRoomName;
    /**
     * 21设备
     */
    private TextView mFamilyRoomDeviceTotal;
    private ImageView mFamilyRoomAddDeviceIcon;
    private ConstraintLayout mFamilyRoomAllDevice;
    private RecyclerView mFamilyRoomAllRoomRv;
    /**
     * 新增其他房间
     */
    private TextView mFamilyManageAdd;
    private RelativeLayout mFamilyRoomAddRelate;
    /**
     * 保存
     */
    private Button mFamilyRoomSaveBtn;

    private SmartInfoVo.FamilysBean mFamilysBean;
    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList;
    private RoomListAdapter mRoomListAdapter;

//    private List<RoomListItemVo.Item> mRoomListItemVos = new ArrayList<>();
//    private RoomManageAdapter mRoomManageAdapter;

    private boolean isManageMode;
    private Intent mIntent = new Intent();
    private BaseDialog mDeleteFamilyBaseDialog;
    private BaseDialog mRemoveRoomBaseDialog;
    private BaseEditDialog mBaseEditDialog;
    private String token, ids, currentFamilyName;  // 获取当前家庭的名字
    private int index;
    public List<RoomListVo.RoomItem> mRoomList = new ArrayList<>();
    private String familyId,userId;
    private int mDevNum;
    private ZLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_family_room);
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this,"familyId"+userId,"");
        initView();
        initDate();
    }

    private void initDate() {
        mTopTitle.setText("家庭房间详情");
        mTopBtn.setText("管理");
        token = SPUtils.get(this, SpConstant.SP_USERTOKEN, "");
        mDeleteFamilyBaseDialog = new BaseDialog(this, 1)
                .setTitleText("提示")
                .setInfoText("确定删除该家庭?")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mDeleteFamilyBaseDialog.setCancelable(false);

        mRemoveRoomBaseDialog = new BaseDialog(this, 1)
                .setTitleText("提示")
                .setInfoText("确定删除该房间?")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mRemoveRoomBaseDialog.setCancelable(false);

        mRoomListAdapter.notifyDataSetChanged();
        List<SmartInfoVo.FamilysBean.RoomsBean> rooms = mFamilysBean.getRooms();
        for (int i = 0; i <rooms.size() ; i++) {
            mDevNum+= rooms.get(i).getDevNum();
        }
        mFamilyRoomDeviceTotal.setText(mDevNum+"个设备");
        mFamilyRoomName.setText(mFamilysBean.getFamilyName());
        currentFamilyName = mFamilysBean.getFamilyName();
    }

    private void initView() {
        mFamilysBean = getIntent().getParcelableExtra("familyItem"); //获取当前家庭下的信息
        mRoomsBeanList = (List<SmartInfoVo.FamilysBean.RoomsBean>) getIntent().getSerializableExtra("roomItem");

        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTopRecord = (ImageView) findViewById(R.id.top_record);
        mFamilyRoomName = findViewById(R.id.family_room_name);
        mFamilyRoomName.clearFocus(); // 设置不弹出软件盘
        mFamilyRoomName.setCursorVisible(false); //设置输入框不可编辑、光标不可见
        mFamilyRoomName.setClearIconVisible(false); //设置清除icon是否显示
        mFamilyRoomName.setFilters(new InputFilter[]{new MaxTextLengthFilter(15)});
        mFamilyRoomDeviceTotal = (TextView) findViewById(R.id.family_room_device_total);
        mFamilyRoomAddDeviceIcon = (ImageView) findViewById(R.id.family_room_add_device_icon);
        mFamilyRoomAllDevice = (ConstraintLayout) findViewById(R.id.family_room_all_device);
        mFamilyRoomAllDevice.setOnClickListener(this);
        mFamilyRoomAllRoomRv = (RecyclerView) findViewById(R.id.family_room_all_room_rv);
        mFamilyManageAdd = (TextView) findViewById(R.id.family_room_add);
        mFamilyRoomAllRoomRv.setLayoutManager(new LinearLayoutManager(this));
        mFamilyRoomAddRelate = (RelativeLayout) findViewById(R.id.family_room_add_relate);
        mFamilyRoomAddRelate.setOnClickListener(this);
        mFamilyRoomSaveBtn = (Button) findViewById(R.id.family_room_save_btn);
        mFamilyRoomSaveBtn.setOnClickListener(this);

        mRoomListAdapter = new RoomListAdapter(mRoomList);
        mRoomListAdapter.setOnItemChildClickListener(this);

//        mRoomManageAdapter = new RoomManageAdapter(mRoomListItemVos);
//        mRoomManageAdapter.setOnItemChildClickListener(this);

        mFamilyRoomAllRoomRv.setAdapter(mRoomListAdapter);
        StringUtils.setCanNotEditNoClick(mFamilyRoomName); // 默认取消输入框的输入
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

    @Override
    protected void onResume() {
        super.onResume();

        getRoomList(familyId); //获取家庭下的房间列表

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {
            finish();
        } else if (i == R.id.top_btn) {
            if (isManageMode) {
                if(mFamilysBean.getJurisdiction()==0){
                    mDeleteFamilyBaseDialog.show();
                }
            } else {
                isManageMode = true;
                if(mFamilysBean.getJurisdiction()==1){
                    mTopBtn.setVisibility(View.GONE);
                } else {
                    mTopBtn.setVisibility(View.VISIBLE);
                    mTopBtn.setText("删除");
                }
                mFamilyRoomSaveBtn.setVisibility(View.VISIBLE);
                mFamilyRoomAddRelate.setVisibility(View.GONE);
                mFamilyRoomName.setCursorVisible(true);
                mFamilyRoomName.setClearIconVisible(true); //设置清除icon是否显示
                StringUtils.setCanEdit(mFamilyRoomName);
            }

            mRoomListAdapter.setManageMode(isManageMode);
            mRoomListAdapter.notifyDataSetChanged();
        } else if (i == R.id.family_room_all_device) {// 全部设备
//                mIntent.setClass(this, DeviceAllActivity.class);
//                startActivity(mIntent);
        } else if (i == R.id.family_room_add_relate) {// 新建房间
            mIntent.setClass(this, RoomAppendActivity.class);
            mIntent.putExtra("familyId", familyId);
            startActivity(mIntent);
        } else if (i == R.id.family_room_save_btn) {// 点击保存
            mTopBtn.setText("管理");
            mTopBtn.setVisibility(View.VISIBLE);
            isManageMode = false;
            StringUtils.setCanNotEditNoClick(mFamilyRoomName);
            mFamilyRoomName.setCursorVisible(false); //设置输入框不可编辑、光标不可见
            mFamilyRoomName.setClearIconVisible(false); //设置清除icon是否显示
            mFamilyRoomSaveBtn.setVisibility(View.GONE);
            if (!currentFamilyName.equals(mFamilyRoomName.getText().toString().trim())
                    && !StringUtils.isEmpty(mFamilyRoomName.getText().toString().trim())) {
//                    updateFamily(mFamilyRoomName.getText().toString().trim(),token,mFamilyListItemVo.getId());
                updateFamilyName(mFamilyRoomName.getText().toString().trim(),userId,mFamilysBean.getFamilyId());
            }
            mFamilyRoomAddRelate.setVisibility(View.VISIBLE);
            mRoomListAdapter.setManageMode(isManageMode);
            mRoomListAdapter.notifyDataSetChanged();
        }
    }

    private void updateFamilyName(String name,String userId,String familyId) {
        JSONObject obj = new JSONObject();
        obj.put("userId", userId);
        obj.put("familyName", name);
        obj.put("familyId", familyId);
        OkGo.<String>post(Constant.HOST + Constant.Family_updateFamilyName)
                .upJson(JSON.toJSONString(obj))
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        loadingDialog.show();
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
                                ToastUtil.show(FamilyRoomActivity.this,"修改成功");
                                EventBus.getDefault().post("FamilyRoomActivity");
                            } else {
                                ToastUtil.show(FamilyRoomActivity.this,resp.getMessage());
                            }
                        } else {
                            ToastUtil.show(FamilyRoomActivity.this,"修改失败");
                        }
                        loadingDialog.dismiss();
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(FamilyRoomActivity.this,"修改失败");
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int i = view.getId();
        if (i == R.id.item_family_delete_iv) {// 点击删除
//                ids = String.valueOf(mRoomsBeanList.get(position).getRoomId());
            ids = mRoomList.get(position).getRoomId();
            index = position;
            mRemoveRoomBaseDialog.show();// 弹框显示
        } else if (i == R.id.item_family_manage) {
            if (!isManageMode) {
                // 跳转至房间详情
                mIntent.setClass(this, RoomManageActivity.class);
//                    mIntent.putExtra("roomItem",mRoomList.get(position));
                mIntent.putExtra("roomItemId", mRoomList.get(position).getRoomId());
                startActivity(mIntent);
            }
        }
//        return false;
    }

    @Override
    public void onConfirmClick() {
        if (mRemoveRoomBaseDialog.isShowing()) {
            // 删除房间
            mRemoveRoomBaseDialog.dismiss();
//            removeRoom(token, mFamilyListItemVo.getId(), ids);
            //loadingDialog.show();
            removeRoom(familyId, ids);
        }
        if (mDeleteFamilyBaseDialog.isShowing()) {
            // 删除家庭
            mDeleteFamilyBaseDialog.dismiss();
//            removeFamily(token,mFamilyListItemVo.getId());
            delShareFamily(userId,userId,familyId);
        }
    }

    private void delShareFamily(String userId,String subUserId,String familyId) {
        JSONObject obj = new JSONObject();
        obj.put("userId", userId);
        obj.put("subUserId", subUserId);
        obj.put("familyId", familyId);
        OkGo.<String>post(Constant.HOST + Constant.User_delShareFamily)
                .upJson(JSON.toJSONString(obj))
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        loadingDialog.show();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
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
                                ToastUtil.showCenter(FamilyRoomActivity.this,"删除成功");
                                EventBus.getDefault().post("delShareFamily");
                                finish();
                            } else {
                                ToastUtil.showCenter(FamilyRoomActivity.this,resp.getMessage());
                            }
                        } else {
                            ToastUtil.showCenter(FamilyRoomActivity.this,"操作失败");
                        }
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.showCenter(FamilyRoomActivity.this,"操作失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete","============");
                    }
                });
    }

    @Override
    public void onCancelListener() {
        // 执行了dialog 取消操作
        if (mRemoveRoomBaseDialog.isShowing()) {
            mRemoveRoomBaseDialog.dismiss();
        }
        if (mDeleteFamilyBaseDialog.isShowing()) {
            mDeleteFamilyBaseDialog.dismiss();
        }
    }

    // 获取房间列表
    private void getRoomList(String familyId) {
        Log.e("TAG", "onError: "+familyId);
        OkGo.<String>get(Constant.HOST + Constant.Family_getRoomList)
                .params("familyId",familyId)
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        loadingDialog.show();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
                .map(new Function<Response<String>, RoomListVo>() {
                    @Override
                    public RoomListVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        RoomListVo deviceListVo = JSON.parseObject(resp.getResult(), RoomListVo.class);

                        if (deviceListVo != null) {
                            LogUtils.e("apply", "====");
                        }
                        return deviceListVo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<RoomListVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(RoomListVo roomListVo) {
                        loadingDialog.dismiss();
                        mRoomList.clear();
                        List<RoomListVo.RoomItem> rooms = roomListVo.getRooms();
                        mDevNum=0;
                        for (int i = 0; i <rooms.size() ; i++) {
                            String devNum = rooms.get(i).getDevNum();
                            mDevNum+= Integer.parseInt(devNum);
                        }
                        mFamilyRoomDeviceTotal.setText(mDevNum+"个设备");

                        mRoomList.addAll(roomListVo.getRooms());
//                mFamilyRoomDeviceTotal.setText(mRoomList.size()+"");
                        mRoomListAdapter.notifyDataSetChanged();
                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(FamilyRoomActivity.this, "加载失败", Toast.LENGTH_LONG).show();
//                        showToast("请求失败");
//                        handleError(null);
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    // 删除家庭房间
    private void removeRoom(String familyId, String ids) {
        OkGo.<String>get(Constant.HOST + Constant.Room_delete)
                .params("familyId",familyId)
                .params("roomId",ids)
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        loadingDialog.show();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        loadingDialog.dismiss();
                        ToastUtil.showCenter(FamilyRoomActivity.this,"删除成功");
                        getRoomList(familyId);
                        EventBus.getDefault().post(new UpdateFamilyEvent(true));
                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                        ToastUtil.show(FamilyRoomActivity.this,"请求失败");
//                        showToast("请求失败");
//                        handleError(null);
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

}
