package com.example.smarthome.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.SubUserListAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.SubUserListVo;
import com.example.smarthome.iot.impl.device.negativecontrol.dialog.ShowPasswordDialog;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
import com.xhwl.commonlib.uiutils.dialog.BaseEditDialog;
import com.zyao89.view.zloading.ZLoadingDialog;

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
 * description: 分享家庭
 * update:
 * version:
 */
public class ShareFamilyActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private ImageView mTopRecord;

    /**
     * 21设备
     */
    private ConstraintLayout share_family_title_cl;
    private RecyclerView share_family_all_rv;
    /**
     * 保存
     */
    private Button share_family_save_btn;

    private SmartInfoVo.FamilysBean mFamilysBean;
    //    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList;
    private SubUserListAdapter subUserListAdapter;

//    private List<RoomListItemVo.Item> mRoomListItemVos = new ArrayList<>();
//    private RoomManageAdapter mRoomManageAdapter;

    private boolean isManageMode;
    private Intent mIntent = new Intent();
    private BaseDialog mRemoveSubuserBaseDialog;
    private BaseEditDialog mBaseEditDialog;
    private String ids;
    public List<SubUserListVo.SubUserListBean> mUserList = new ArrayList<>();
    private String familyId, userId;
    private TextView tv_userAccount;
    private TextView tv_show_password;
    private ShowPasswordDialog showPasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_share_family);
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId" + userId, "");
        initView();
        initDate();
    }

    private void initDate() {
        mTopTitle.setText("权限共享");
        mTopBtn.setText("记录管理");


        mRemoveSubuserBaseDialog = new BaseDialog(this, 2)
                .setInfoText("删除后对方将无法使用是否删除")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mRemoveSubuserBaseDialog.setCancelable(false);

        subUserListAdapter.notifyDataSetChanged();

    }

    private void initView() {
//        mFamilysBean = getIntent().getParcelableExtra("familyItem"); //获取当前家庭下的信息
//        mRoomsBeanList = (List<SmartInfoVo.FamilysBean.RoomsBean>) getIntent().getSerializableExtra("roomItem");

        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTopRecord = (ImageView) findViewById(R.id.top_record);

        share_family_title_cl = (ConstraintLayout) findViewById(R.id.share_family_title_cl);
        share_family_title_cl.setOnClickListener(this);

        share_family_all_rv = (RecyclerView) findViewById(R.id.share_family_all_rv);
        share_family_all_rv.setLayoutManager(new LinearLayoutManager(this));

        share_family_save_btn = (Button) findViewById(R.id.share_family_save_btn);
        share_family_save_btn.setOnClickListener(this);

        subUserListAdapter = new SubUserListAdapter(mUserList);
        subUserListAdapter.setOnItemChildClickListener(this);

//        mRoomManageAdapter = new RoomManageAdapter(mRoomListItemVos);
//        mRoomManageAdapter.setOnItemChildClickListener(this);

        share_family_all_rv.setAdapter(subUserListAdapter);

        loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);

        tv_userAccount = findViewById(R.id.tv_userAccount);
        tv_show_password = findViewById(R.id.tv_show_password);
        tv_userAccount.setText(userId);
        tv_show_password.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSubUserList(userId); //获取家庭下的房间列表
//        loadingDialog.show();
        getUserInfo(userId);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {
            finish();
        } else if (i == R.id.top_btn) {
//            if (isManageMode) {
//
////                mDeleteFamilyBaseDialog.show();
//
//            } else {
//                isManageMode = true;
////                mTopBtn.setText("删除");
//                share_family_save_btn.setVisibility(View.VISIBLE);
//            }
            isManageMode = true;
            share_family_save_btn.setVisibility(View.VISIBLE);
            subUserListAdapter.setManageMode(isManageMode);
            subUserListAdapter.notifyDataSetChanged();
        } else if (i == R.id.family_room_all_device) {// 全部设备
//                mIntent.setClass(this, DeviceAllActivity.class);
//                startActivity(mIntent);
        } else if (i == R.id.share_family_save_btn) {// 点击保存
            isManageMode = false;

            share_family_save_btn.setVisibility(View.GONE);
            subUserListAdapter.setManageMode(isManageMode);
            subUserListAdapter.notifyDataSetChanged();
        } else if (i == R.id.share_family_title_cl) {
            mIntent.setClass(this, SharePhoneActivity.class);
            startActivity(mIntent);
        }else if (i==R.id.tv_show_password){
            showPasswordDialog.show();
        }
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int i = view.getId();
        if (i == R.id.item_sub_user_delete_iv) {// 点击删除
//                ids = String.valueOf(mRoomsBeanList.get(position).getRoomId());
            ids = mUserList.get(position).getSubUserId();
            mRemoveSubuserBaseDialog.show();// 弹框显示
        } else if (i == R.id.item_sub_user_manage) {
            if (!isManageMode) {

            }
        }
    }

    @Override
    public void onConfirmClick() {
        if (mRemoveSubuserBaseDialog.isShowing()) {
            // 删除房间
            mRemoveSubuserBaseDialog.dismiss();
//            removeRoom(token, mFamilyListItemVo.getId(), ids);
            //loadingDialog.show();
            delShareFamily(userId, familyId, ids);
        }

    }

    @Override
    public void onCancelListener() {
        // 执行了dialog 取消操作
        if (mRemoveSubuserBaseDialog.isShowing()) {
            mRemoveSubuserBaseDialog.dismiss();
        }
    }

    // 获取房间列表
    private void getSubUserList(String userId) {
        OkGo.<String>get(Constant.HOST + Constant.User_getSubUserList)
                .params("userId", userId)
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        //loadingDialog.show();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
                .map(new Function<Response<String>, SubUserListVo>() {
                    @Override
                    public SubUserListVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        SubUserListVo deviceListVo = JSON.parseObject(resp.getResult(), SubUserListVo.class);

                        if (deviceListVo != null) {
                            LogUtils.e("apply", "====");
                        }
                        return deviceListVo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<SubUserListVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(SubUserListVo subUserListVo) {
                        mUserList.clear();
                        mUserList.addAll(subUserListVo.getSubUserList());
                        subUserListAdapter.notifyDataSetChanged();
                        LogUtils.e("onNext:", "------------------");
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(ShareFamilyActivity.this, "请求失败", Toast.LENGTH_LONG).show();
//                        showToast("请求失败");
//                        handleError(null);
                    }

                    @Override
                    public void onComplete() {
                       // loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    // 删除分享
    private void delShareFamily(String userId, String familyId, String subUserId) {
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
                                ToastUtil.showCenter(ShareFamilyActivity.this, "删除成功");
                                getSubUserList(userId);
                            } else {
                                ToastUtil.showCenter(ShareFamilyActivity.this, resp.getMessage());
                            }
                        } else {
                            ToastUtil.showCenter(ShareFamilyActivity.this, "操作失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.showCenter(ShareFamilyActivity.this, "操作失败");
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    private void getUserInfo(String userId){
        OkGo.<String>get(Constant.HOST + Constant.User_getUserId)
                .params("userId", userId)
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
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
                        CommonResp resp=JSON.parseObject(stringResponse.body(),CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
                            JSONObject result=JSON.parseObject(commonResp.getResult());
                            String user=result.getString("user");
                            JSONObject userJson=JSONObject.parseObject(user);
                            String password=userJson.getString("password");
                            String userId=userJson.getString("userId");
                            Log.e("用户信息",password+" "+userId);
                            showPasswordDialog = new ShowPasswordDialog(ShareFamilyActivity.this,R.style.dialog,userId,password);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
