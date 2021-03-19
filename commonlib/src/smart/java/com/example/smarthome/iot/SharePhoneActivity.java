package com.example.smarthome.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.ShareGetUserId;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.SubUserListVo;
import com.example.smarthome.iot.net.Constant;
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
 * description: 分享家庭 账号
 * update:
 * version:
 */
public class SharePhoneActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private ImageView mTopRecord;

    /**
     * 21设备
     */
    private ClearEditText share_phone_cedt;
    private RecyclerView share_family_all_rv;

    private SmartInfoVo.FamilysBean mFamilysBean;
    //    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList;


//    private List<RoomListItemVo.Item> mRoomListItemVos = new ArrayList<>();
//    private RoomManageAdapter mRoomManageAdapter;

    private boolean isManageMode;
    private Intent mIntent = new Intent();
    private BaseDialog mRemoveSubuserBaseDialog;
    private BaseEditDialog mBaseEditDialog;
    private String ids;
    public List<SubUserListVo.SubUserListBean> mUserList = new ArrayList<>();
    private String familyId, userId;
    private RelativeLayout share_phone_search_result,share_phone_share_rl,share_phone_search_rl;
    private ImageView share_phone_select;
    private TextView share_phone_result_tv;
    private boolean resultSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_phone_share);
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId" + userId, "");
        initView();
        initDate();
    }

    private void initDate() {
        mTopTitle.setText("共享成员");

    }

    private void initView() {

        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTopBtn.setVisibility(View.GONE);
        mTopRecord = (ImageView) findViewById(R.id.top_record);

        share_phone_search_rl = findViewById(R.id.share_phone_search_rl);
        share_phone_search_rl.setOnClickListener(this);
        share_phone_search_result = findViewById(R.id.share_phone_search_result);
        share_phone_search_result.setOnClickListener(this);
        share_phone_select = findViewById(R.id.share_phone_select);
        share_phone_result_tv = findViewById(R.id.share_phone_result_tv);
        share_phone_share_rl = findViewById(R.id.share_phone_share_rl);
        share_phone_share_rl.setOnClickListener(this);
        share_phone_cedt = (ClearEditText) findViewById(R.id.share_phone_cedt);

        share_phone_cedt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                share_phone_cedt.onTextChanged(s,start,before,count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()<11){
                    resultSelected = false;
                    share_phone_search_rl.setVisibility(View.VISIBLE);
                    share_phone_search_result.setVisibility(View.GONE);
                    share_phone_share_rl.setVisibility(View.GONE);
                }
            }
        });

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

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {
            finish();
        } else if (i == R.id.top_btn) {

        } else if (i == R.id.share_phone_search_rl) {//搜索
            LogUtils.e("SharePhoneActivity","familyId="+familyId);
            if(StringUtils.validatePhoneNumber(share_phone_cedt.getText().toString().trim())){
                getUserId(share_phone_cedt.getText().toString().trim());

//                shareFamily(userId,share_phone_cedt.getText().toString().trim(),familyId);
            } else {
                ToastUtil.show(SharePhoneActivity.this,"请输入正确手机号");
            }
        } else if(i == R.id.share_phone_share_rl){
            if(resultSelected){
                shareFamily(userId,share_phone_result_tv.getText().toString(),familyId);
            } else {
                ToastUtil.show(SharePhoneActivity.this,"请选择分享手机号");
            }

        } else if(i == R.id.share_phone_search_result){
            resultSelected = !resultSelected;
            if(resultSelected){
                share_phone_select.setImageResource(R.drawable.icon_iot_choose);
            } else {
                share_phone_select.setImageResource(R.drawable.icon_iot_unselected);
            }
        }
    }

    private void getUserId(String userId) {
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
                })//
                .map(new Function<Response<String>, ShareGetUserId>() {
                    @Override
                    public ShareGetUserId apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        ShareGetUserId getUserId = JSON.parseObject(resp.getResult(), ShareGetUserId.class);
                        return getUserId;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShareGetUserId>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(ShareGetUserId getUserId) {
                        if(getUserId!=null && !StringUtils.isEmpty(getUserId.getUserId())){
                            share_phone_search_rl.setVisibility(View.GONE);
                            share_phone_search_result.setVisibility(View.VISIBLE);
                            share_phone_share_rl.setVisibility(View.VISIBLE);
                            share_phone_result_tv.setText(getUserId.getUserId());
                        } else {
                            share_phone_search_rl.setVisibility(View.VISIBLE);
                            share_phone_search_result.setVisibility(View.GONE);
                            share_phone_share_rl.setVisibility(View.GONE);
                            ToastUtil.show(SharePhoneActivity.this,"该手机号不可被分享");
                        }
                        LogUtils.e("onNext:", "------------------");
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SharePhoneActivity.this,"请求失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }


    @Override
    public void onConfirmClick() {

    }

    @Override
    public void onCancelListener() {
        // 执行了dialog 取消操作
        if (mRemoveSubuserBaseDialog.isShowing()) {
            mRemoveSubuserBaseDialog.dismiss();
        }
    }

    // 分享
    private void shareFamily(String masterUserId, String subUserId, String familyId) {
        JSONObject obj = new JSONObject();
        obj.put("masterUserId", masterUserId);
        obj.put("subUserId", subUserId);
        obj.put("familyId", familyId);
        OkGo.<String>post(Constant.HOST + Constant.User_shareFamily)
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
                                ToastUtil.show(SharePhoneActivity.this, "分享成功");
                            } else {
                                ToastUtil.show(SharePhoneActivity.this, resp.getMessage());
                            }
                        } else {
                            ToastUtil.show(SharePhoneActivity.this, "操作失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SharePhoneActivity.this, "操作失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

}
