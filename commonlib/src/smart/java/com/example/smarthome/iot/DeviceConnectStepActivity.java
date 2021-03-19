package com.example.smarthome.iot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceConnectStepAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceBaseInfoVo;
import com.example.smarthome.iot.entry.DeviceConnectStepItemVo;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.yscamera.YsCaptureActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;
import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author: glq
 * date: 2019/5/9 13:23
 * description: 设备接入流程
 * update: 2019/5/9
 * version:
 */
public class DeviceConnectStepActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private RecyclerView mDeviceConnectStepRv;
    /**
     * 下一步
     */
    private Button mDeviceConnectStepNextBtn;

    private DeviceConnectStepAdapter mDeviceConnectStepAdapter;
    private List<DeviceConnectStepItemVo> mDeviceConnectStepItemVos = new ArrayList<>();
    private DeviceListVo.ProductCollectionBean.DevListBean devListBean;
    private String supplierId, userId;
    private int getJurisdiction = 0;
    private String gatewayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connet_step);
        addDestoryActivity(this, "DeviceConnectStepActivity");
        userId = SPUtils.get(DeviceConnectStepActivity.this, SpConstant.SP_USER_TELEPHONE, "");
        LogUtils.e("DeviceConnectStepActivity", userId + "------");
        initView();
        initDate();
    }

    private void initDate() {
        getJurisdiction = SPUtils.get(DeviceConnectStepActivity.this, "getJurisdiction" + userId, 0);
        //萤石摄像头暂时注释
        //        if (getJurisdiction == 1) {
//            createEzviz(userId);
//        }
        devListBean = getIntent().getParcelableExtra("DeviceConnectStepActivity");
        LogUtils.e("DeviceConnectStepActivity", devListBean.getDeviceType() + devListBean.toString()+"------");
        /*loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);
        loadingDialog.show();*/
        getDevInfo(devListBean.getDevNo());

        mTopTitle.setText(devListBean.getDeviceName());

        mDeviceConnectStepAdapter = new DeviceConnectStepAdapter(mDeviceConnectStepItemVos);
//        mDeviceConnectStepItemVos.add(new DeviceConnectStepItemVo(1, devListBean.getDeviceType()));
//        mDeviceConnectStepItemVos.add(new DeviceConnectStepItemVo(2, devListBean.getDeviceType()));
        mDeviceConnectStepRv.setAdapter(mDeviceConnectStepAdapter);

    }

    private void initView() {
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mDeviceConnectStepRv = (RecyclerView) findViewById(R.id.device_connect_step_rv);
        mDeviceConnectStepNextBtn = (Button) findViewById(R.id.device_connect_step_next_btn);
        mDeviceConnectStepNextBtn.setOnClickListener(this);
        mDeviceConnectStepRv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {
            finish();
        } else if (i == R.id.device_connect_step_next_btn) {// 下一步
            if (devListBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_GATEWAY)) {
                Intent intent = new Intent(DeviceConnectStepActivity.this, GatewayNetSettingActivity.class);
                intent.putExtra("DeviceBean", devListBean);
                intent.putExtra("supplierId", supplierId);
                startActivity(intent);
            } else if (devListBean.getDeviceType().equalsIgnoreCase(DeviceType.YS_CAMERA)) {
                if (getJurisdiction == 0) {
                    ToastUtil.show(DeviceConnectStepActivity.this, "非自己家庭无法增加摄像头");
                    return;
                }
                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(DeviceConnectStepActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    String[] mPermissionList = new String[]{
                            Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(this, mPermissionList, 123);
                } else {
                    mIntent.setClass(DeviceConnectStepActivity.this, YsCaptureActivity.class);//萤石测试
                    startActivity(mIntent);
                }
            } else {
                Intent intent = new Intent(DeviceConnectStepActivity.this, ScanAddDeviceActivity.class);
                intent.putExtra("DeviceBean", devListBean);
                intent.putExtra("supplierId", supplierId);
                startActivity(intent);
            }
        }
    }

    void getDevInfo(String devNo) {
        OkGo.<String>get(Constant.HOST + Constant.Device_getDev)
                .params("devNo", devNo)
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
                .map(new Function<Response<String>, DeviceBaseInfoVo>() {
                    @Override
                    public DeviceBaseInfoVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        DeviceBaseInfoVo obj = null;
                        if (resp.getErrorCode().equalsIgnoreCase("200")) {
                            obj = JSON.parseObject(resp.getResult(), DeviceBaseInfoVo.class);
                        }
                        if (obj != null) {
                            LogUtils.e("apply", "====");
                        }
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<DeviceBaseInfoVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(DeviceBaseInfoVo deviceBaseInfoVo) {
                        DeviceBaseInfoVo.DeviceBaseInfo deviceBaseInfo = deviceBaseInfoVo.getDeviceBaseInfo();
                        if (deviceBaseInfo != null) {
                            supplierId = deviceBaseInfo.getSupplierId();
                            //界面
                            DeviceConnectStepItemVo itemVo1 = new DeviceConnectStepItemVo();
                            itemVo1.setIcon(deviceBaseInfo.getIcon1());
                            itemVo1.setTips(deviceBaseInfo.getTips1());
                            itemVo1.setStep(getString(R.string.step_1));
                            DeviceConnectStepItemVo itemVo2 = new DeviceConnectStepItemVo();
                            itemVo2.setIcon(deviceBaseInfo.getIcon2());
                            itemVo2.setTips(deviceBaseInfo.getTips2());
                            itemVo2.setStep(getString(R.string.step_2));
                            mDeviceConnectStepItemVos.add(itemVo1);
                            mDeviceConnectStepItemVos.add(itemVo2);
                            mDeviceConnectStepAdapter.notifyDataSetChanged();
                        }
                        LogUtils.e("onNext:", "------------------");
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(DeviceConnectStepActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

//    private void createEzviz(String telephone) {
//        OkGo.<String>get(Constant.HOST + Constant.Ezviz_Create)
//                .params("userId", telephone)//
//                .converter(new StringConvert())//
//                .adapt(new ObservableResponse<String>())//
//                .subscribeOn(Schedulers.io())//
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
////                        showLoading();
//                        LogUtils.e("accept", disposable.isDisposed() + "===");
//                    }
//                })//
//                .map(new Function<Response<String>, CommonResp>() {
//                    @Override
//                    public CommonResp apply(Response<String> stringResponse) throws Exception {
//                        LogUtils.e("apply", "====" + stringResponse.body());
////                        JSON
//                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
//                        return resp;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())//
//                .subscribe(new Observer<CommonResp>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
////                        addDisposable(d);
//                        LogUtils.e("onSubscribe", "========---====");
//                    }
//
//                    @Override
//                    public void onNext(CommonResp commonResp) {
//                        if (commonResp.getErrorCode().equalsIgnoreCase("200")) {
//                            JSONObject object = JSON.parseObject(commonResp.getResult());
//                            String accessToken = object.getString("accessToken");
//                            LogUtils.e("ezvizGetToken", accessToken + "--------------");
//                            if (!StringUtils.isEmpty(accessToken)) {
//                                SPUtils.put(DeviceConnectStepActivity.this, "createEzvizToken" + telephone, accessToken);
//                                EZOpenSDK.getInstance().setAccessToken(accessToken);
//                            } else {
//
//                            }
//                        } else {
//
//                        }
//                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
//                    }
//
//
//                    @Override
//                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        LogUtils.e("onComplete", "============");
//                    }
//                });
//    }

    /**
     * 6.0权限回调处理
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                mIntent.setClass(DeviceConnectStepActivity.this, YsCaptureActivity.class);//萤石测试
                startActivity(mIntent);
            } else {
                LogUtils.e("DeviceConnectStepActivity", "Permission Denied======");
                // Permission Denied
                ToastUtil.show(DeviceConnectStepActivity.this, "无摄像头权限");
//                return;
            }
        }
    }
}
