package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceInfoVo;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.example.smarthome.iot.entry.RegistGatewayVo;
import com.example.smarthome.iot.entry.SmartControlVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.udpsocket.ReceiveUtils;
import com.example.smarthome.iot.util.EasySocket;
import com.example.smarthome.iot.util.SocketCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.progressbar.RoundProgressBar;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;
import static com.xhwl.commonlib.application.MyAPP.destoryActivity;

/**
 * author: glq
 * date: 2019/5/14 15:34
 * description: 添加设备扫描页
 * update: 2019/5/14
 * version:
 *
 * 海令网关绑定流程：
 *  1.接收网关UDP消息  取ip gw_id
 *  2.调用后台注册网关接口  clientId  deviceName
 *  3.与网关socket通信 发送指定json字符串  并接收返回msg  判断result字段
 */
public class ScanAddDeviceActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG=this.getClass().getSimpleName();
    private DatagramPacket packet;
    private DatagramSocket mSocket;
    private volatile boolean stopReceiver;
    private RoundProgressBar mIdProgressBar;

    private final long DURATION_TIME = 80 * 1000;
    private final int TOTAL_PROGRESS = 100;
    /**
     * 0%
     */
    private TextView mScanAddDeviceConnectProgress;
    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private String familyId, supplierId, telephone, devType, getewayId;
    private DeviceListVo.ProductCollectionBean.DevListBean devBean;
    private EasySocket socket = null;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private String gatewayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_add_device);
        addDestoryActivity(this, "ScanAddDeviceActivity");
        initView();
        initDate();
    }

    private void initView() {
        mIdProgressBar = (RoundProgressBar) findViewById(R.id.id_progress_bar);
        mScanAddDeviceConnectProgress = (TextView) findViewById(R.id.scan_add_device_connect_progress);
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
    }

    private void initDate() {
        telephone = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId"+telephone, "");
        devBean = getIntent().getParcelableExtra("DeviceBean");
//        gatewayId = getIntent().getStringExtra("gatewayId");
//        Log.e("设备扫描界面",gatewayId);
        gatewayId = SPUtils.get(ScanAddDeviceActivity.this,"scan_gateway","");
        Log.e("设备扫描界面",gatewayId);
        devType = devBean.getDeviceType();
        Log.e("扫描添加网关设备数据11",devBean.toString());
        supplierId = getIntent().getStringExtra("supplierId");
        mTopTitle.setText("添加设备");

        if (devType.equalsIgnoreCase(DeviceType.HILINK_GATEWAY)) {
            ReceiveUtils.init();
            ReceiveUtils.receiveMessage(6767);
        } else {
//            getewayId = SPUtils.get(this, "gatewayId"+telephone+familyId, "");
//            LogUtils.e("getewayId",getewayId);
            if(!StringUtils.isEmpty(gatewayId)){
                addDevice();
            } else {
                ToastUtil.show(ScanAddDeviceActivity.this,"请先添加网关");
                return;
            }

        }

        mTimer.start();
        if (devType.equalsIgnoreCase(DeviceType.HILINK_GATEWAY)) {
            mHandler.sendEmptyMessageDelayed(66,10*1000);
        }
    }

    /**
     * 每隔一秒钟执行一次
     */
    private CountDownTimer mTimer = new CountDownTimer(DURATION_TIME, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // 每隔一秒调用一次，剩余多少时间
            int progress = (int) (TOTAL_PROGRESS * (DURATION_TIME - millisUntilFinished) / DURATION_TIME);
            mIdProgressBar.setProgress(progress);
            mScanAddDeviceConnectProgress.setText(progress + "%");
        }

        @Override
        public void onFinish() {
            // 执行完毕
            mIdProgressBar.setProgress(TOTAL_PROGRESS);
            mScanAddDeviceConnectProgress.setText(TOTAL_PROGRESS + "%");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiveUtils.closeSoket();
        mTimer.cancel();
        mTimer = null;
        if (socket != null) {
            socket.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.top_back){
            finish();
        }
    }

    void easySocketCon(String ip, String gwId, String clientId, String devName) {
        EasySocket.Builder builder = new EasySocket.Builder();
        socket = builder.setIp(ip)
                .setPort(6768)
                .setCallback(new SocketCallback() {
                    @Override
                    public void onConnected() {
                        if (socket.isConnected) {
                            JSONObject sendObj = new JSONObject();
                            //
                            sendObj.put("method", "bind");
                            sendObj.put("gw_id", gwId);
                            sendObj.put("client_id", clientId);
                            sendObj.put("device_name", devName);
                            LogUtils.e("socket.isConnected",socket.isConnected+"---------------");
                            socket.send(sendObj.toString().getBytes());
                        }
                    }

                    @Override
                    public void onDisconnected() {

                    }

                    @Override
                    public void onReconnected() {

                    }

                    @Override
                    public void onSend() {

                    }

                    @Override
                    public void onReceived(byte[] msg) {
                        String text = new String(msg);
                        LogUtils.e("onReceived text",text+"=====");
                        JSONObject recMsg = JSONObject.parseObject(text);
                        if (recMsg != null) {
                            int result = recMsg.getInteger("result");
                            LogUtils.e("onReceived",result+"=====");
                            mHandler.sendEmptyMessage(result);
                        }
                    }

                    @Override
                    public void onError(String msg) {
//                        mHandler.sendEmptyMessage(4);
                    }

                    @Override
                    public void onSendHeart() {

                    }
                }).build();


        socket.connect();
        LogUtils.e("socket",socket.isConnected+"=========");
//        if (socket.isConnected) {
//            JSONObject sendObj = new JSONObject();
//            //
//            sendObj.put("method", "bind");
//            sendObj.put("gw_id", gwId);
//            sendObj.put("client_id", clientId);
//            sendObj.put("device_name", devName);
//            LogUtils.e("socket.isConnected",socket.isConnected+"---------------");
//            socket.send(sendObj.toString().getBytes());
//        }

    }

    void registGateWay() {
        String udpData = ReceiveUtils.getMsg();
        LogUtils.e("udpData",udpData+"--------------");
        ReceiveUtils.closeSoket();
        JSONObject obj = JSON.parseObject(udpData);
        String gwIp, gw_id;
        if (obj != null) {
            gwIp = obj.getString("ip");
            gw_id = obj.getString("gw_id");
            getewayId = gw_id;
            RegistGatewayVo registGatewayVo = new RegistGatewayVo();
            registGatewayVo.setFamilyId(familyId);
            registGatewayVo.setGatewayId(obj.getString("gw_id"));
            registGatewayVo.setGatewayName(devBean.getDeviceName());
            registGatewayVo.setProductName("");
            registGatewayVo.setSupplierId(supplierId);
            registGatewayVo.setUserId(telephone);

            OkGo.<String>post(Constant.HOST+Constant.Gateway_regist)
                    .upJson(JSON.toJSONString(registGatewayVo))
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
                    .map(new Function<com.lzy.okgo.model.Response<String>, CommonResp>() {
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
                            if(resp.getErrorCode().equalsIgnoreCase("200")){
                                JSONObject registInfo = JSON.parseObject(resp.getResult()).getJSONObject("registInfo");
                                easySocketCon(gwIp, gw_id, registInfo.getString("clientId"), registInfo.getString("deviceName"));
                            } else {
                                //注册失败
                                if (mTimer != null) {
                                    mTimer.cancel();
                                }
                                mHandler.sendEmptyMessage(4);
                            }

                        }


                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            e.printStackTrace();
                            ToastUtil.show(ScanAddDeviceActivity.this,"请求失败");
                        }

                        @Override
                        public void onComplete() {
//                        dismissLoading();
                            LogUtils.e("onComplete","============");
                        }
                    });

        } else {
            if (mTimer != null) {
                mTimer.cancel();
            }
//            ToastUtil.show("未收到网关广播");
            mHandler.sendEmptyMessage(4);
        }
    }

    private void addDevice() {
        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
        deviceInfoVo.setDeviceId("");
        deviceInfoVo.setDeviceType("FFFFFF");
        deviceInfoVo.setGatewayId(gatewayId);
        deviceInfoVo.setSupplierId(supplierId);
        SmartControlVo smartControlVo = new SmartControlVo();
        smartControlVo.setUserId(telephone);
        smartControlVo.setFamilyId(familyId);
        smartControlVo.setCmdType("add");
        smartControlVo.setCmd("");
        smartControlVo.setDeviceInfo(deviceInfoVo);
        Log.e("扫描添加设备",smartControlVo.toString());
        OkGo.<String>post(Constant.HOST+Constant.Device_control)
                .upJson(JSON.toJSONString(smartControlVo))
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
                .map(new Function<com.lzy.okgo.model.Response<String>, CommonResp>() {
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
                        Log.e(TAG,"addDevice inNext"+resp.getErrorCode()+" "+resp.getMessage());
                        if(resp.getErrorCode().equalsIgnoreCase("200")){
                            deviceInfoBean = JSON.parseObject(resp.getResult()).getObject("device", SmartInfoVo.FamilysBean.DeviceInfoBean.class);

                        }
                        mHandler.sendEmptyMessage(Integer.parseInt(resp.getErrorCode()));
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG,"addDevice onError"+e.getMessage());
                        e.printStackTrace();
//                        ToastUtil.show(ScanAddDeviceActivity.this,"请求失败");
                        mHandler.sendEmptyMessage(201);
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
//                        LogUtils.e("onComplete","============");
                        Log.e(TAG,"addDevice onComplete");
                    }
                });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Intent intent = new Intent();
            intent.putExtra("DeviceBean", devBean);
            intent.putExtra("supplierId", supplierId);
            switch (msg.what) {
                case 0:
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                    socket.disconnect();
                    closeActivity();
                    getActive(getewayId);
//                    ToastUtil.show("绑定成功");
//                    intent.setClass(ScanAddDeviceActivity.this, AddDeviceSuccessActivity.class);
//                    startActivity(intent);
                    break;
                case 1:
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                    socket.disconnect();
                    closeActivity();
                    getActive(getewayId);
//                    ToastUtil.show("网关已被绑定（如需绑定，请先解除绑定");
//                    intent.setClass(ScanAddDeviceActivity.this, AddDeviceFailureActivity.class);
//                    startActivity(intent);
                    break;
                case 2:
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                    socket.disconnect();
                    closeActivity();
                    ToastUtil.show(ScanAddDeviceActivity.this,"网关未联网");
                    intent.setClass(ScanAddDeviceActivity.this, AddDeviceFailureActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 3:
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                    socket.disconnect();
                    closeActivity();
                    ToastUtil.show(ScanAddDeviceActivity.this,"网关ID不符");
                    intent.setClass(ScanAddDeviceActivity.this, AddDeviceFailureActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 4:
                    closeActivity();
                    ToastUtil.show(ScanAddDeviceActivity.this,"网关绑定失败");
                    intent.setClass(ScanAddDeviceActivity.this, AddDeviceFailureActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 201:
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                    //closeActivity();
                    EventBus.getDefault().post(new UpdateFamilyEvent(true));
                    ToastUtil.show(ScanAddDeviceActivity.this,"用户添加设备失败，网络超时或不存在设备加网");
                    intent.setClass(ScanAddDeviceActivity.this, AddDeviceFailureActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 200:
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                    closeActivity();
//                    MyAPP.destoryActivity("DeviceAppendActivity");
                    ToastUtil.show(ScanAddDeviceActivity.this,"添加设备成功");
                    EventBus.getDefault().post(new UpdateFamilyEvent(true));
                    intent.putExtra("DeviceInfoBean", deviceInfoBean);
                    intent.setClass(ScanAddDeviceActivity.this, AddDeviceSuccessActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 66:
                    registGateWay();
                    break;
                default:
                    break;
            }
        }
    };

    int i = 0;//查询网关roma状态次数

    void getActive(String gatewayId) {
//        NetWorkWrapper.smartGetActive(gatewayId, new HttpHandler<String>() {
//            @Override
//            public void onSuccess(ServerTip serverTip, String resp) {
//                i++;
//                LogUtils.e("getActive onSuccess","----"+resp);
//                JSONObject result = JSONObject.parseObject(resp);
//                if (result != null) {
//                    if (!StringUtils.isEmpty(result.getString("active")) && result.getString("active").equalsIgnoreCase("true")) {
//                        //网关roma在线
//                        ToastUtil.show("网关添加成功，请前往添加设备");
//                        LogUtils.e("getActive  save  getewayId",getewayId);
//                        SPUtils.put(ScanAddDeviceActivity.this, "gatewayId"+telephone, gatewayId);
//                        finish();
//                        return;
//                    } else {
//                        reGetActive(gatewayId);
//                    }
//                } else {
//                    reGetActive(gatewayId);
//                }
//            }
//
//            @Override
//            public void onFailure(ServerTip serverTip) {
//                LogUtils.e("onFailure","----");
//                reGetActive(gatewayId);
//            }
//        });

        OkGo.<String>get(Constant.HOST+Constant.Gateway_getActive)
                .params("gatewayId",gatewayId)
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
                .map(new Function<com.lzy.okgo.model.Response<String>, CommonResp>() {
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
                        Log.e(TAG,"getActive inNext"+resp.getErrorCode());
                        i++;
                        if(resp.getErrorCode().equalsIgnoreCase("200")){
                            LogUtils.e("getActive onSuccess","----"+resp.getResult());
                            JSONObject result = JSONObject.parseObject(resp.getResult());
                            if (result != null) {
                                if (!StringUtils.isEmpty(result.getString("active")) && result.getString("active").equalsIgnoreCase("true")) {
                                    //网关roma在线
                                    ToastUtil.show(ScanAddDeviceActivity.this,"网关添加成功，请前往添加设备");
                                    LogUtils.e("getActive  save  getewayId",getewayId);
                                    SPUtils.put(ScanAddDeviceActivity.this, "gatewayId"+telephone+familyId, gatewayId);
//                        Intent intent = new Intent();
//                        intent.putExtra("DeviceBean", devBean);
//                        intent.putExtra("supplierId", supplierId);
//                        intent.setClass(ScanAddDeviceActivity.this, AddDeviceSuccessActivity.class);
//                        EventBus.getDefault().post(new UpdateFamilyEvent(true));
//                        startActivity(intent);
                                    finish();
                                    return;
                                } else {
                                    reGetActive(gatewayId);
                                }
                            } else {
                                reGetActive(gatewayId);
                            }
                        } else {
                            reGetActive(gatewayId);
                        }
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG,"getActive onError"+e.getMessage());
                        e.printStackTrace();
//                        ToastUtil.show(ScanAddDeviceActivity.this,"请求失败");
                        mHandler.sendEmptyMessage(201);
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
//                        LogUtils.e("onComplete","============");
                        Log.e(TAG,"getActive onComplete");
                    }
                });
    }

    void reGetActive(String gatewayId){
        LogUtils.e("reGetActive","----"+i);
        if(i<3){
            getActive(gatewayId);
        } else {
            Intent intent = new Intent();
            intent.putExtra("DeviceBean",devBean);
            intent.putExtra("supplierId",supplierId);
            intent.setClass(ScanAddDeviceActivity.this, AddDeviceFailureActivity.class);
            startActivity(intent);
            finish();
        }
    }

    void closeActivity() {
        destoryActivity("DeviceConnectStepActivity");
        destoryActivity("GatewayNetSettingActivity");
        destoryActivity("DeviceAppendActivity");
        destoryActivity("GateDeviceActivity");
    }
}
