package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceInfoVo;
import com.example.smarthome.iot.entry.DeviceRealStateVo;
import com.example.smarthome.iot.entry.SmartControlVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.impl.device.thermostat.bean.WkRealMsgBean;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DeviceControlUtil;
import com.example.smarthome.iot.util.HilinkDeviceControl;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class TeanaMusicActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mCircuit;
    private ImageView mMusicLeft;
    private ImageView mMusicOpen;
    private ImageView mMusicRight;

    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private String familyId, userId;
    private TextView mTopBtn;
    private List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans;
    private boolean switchOne;
    private SeekBar mSeekBar;
    private TextView mSeekTv;
    public static final String TL_MUSIC_OFF = "{ \"dev_ep_id\" : 1,\"Music\" : 0}"; // 正转
    public static final String TL_MUSIC_NO = "{ \"dev_ep_id\" : 1,\"Music\" : 1}"; // 反转
    public static final String TL_MUSIC_START = "{ \"dev_ep_id\" : 1,\"Music\" : 2}"; // 播放/停止
    public static final String TL_MUSIC_SOCKET_OFF = "{ \"dev_ep_id\" : 3,\"State\" : 1}"; // 播放
    public static final String TL_MUSIC_SOCKET_NO = "{ \"dev_ep_id\" :3 ,\"State\" : 0}"; //停止

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teana_music);
        addDestoryActivity(this,"Activity");
        EventBus.getDefault().register(this);
        initView();
        initDate();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (messageEvent.isUpdate()) {
            mTopTitle.setText(messageEvent.getTitle());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    private void initDate() {
        mTopBtn.setText("管理");
        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        mTopTitle.setText(deviceInfoBean.getDeviceName());
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId" + userId, "");
        init(deviceInfoBean.getId(), deviceInfoBean.getDeviceType(), deviceInfoBean.getSupplierId());
    }

    private void initView() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
     /*   mCircuit = (ImageView) findViewById(R.id.circuit);
        mCircuit.setOnClickListener(this);*/
        mMusicLeft = (ImageView) findViewById(R.id.music_left);
        mMusicLeft.setOnClickListener(this);
        mMusicOpen = (ImageView) findViewById(R.id.music_open);
        mMusicOpen.setOnClickListener(this);
        mMusicRight = (ImageView) findViewById(R.id.music_right);
        mMusicRight.setOnClickListener(this);
        mSeekBar = findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekTv = findViewById(R.id.seekBartv2);
    }

    private void init(String deviceId, String deviceType, String supplierId) {
        OkGo.<String>get(Constant.HOST+ Constant.Device_getRealMsg)
                .params("deviceId",deviceId)
                .params("deviceType","300100")
                .params("supplierId",deviceType)
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })
                .map(new Function<Response<String>, DeviceRealStateVo>() {
                    @Override
                    public DeviceRealStateVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        DeviceRealStateVo obj = null;
                        if(resp!=null && resp.getErrorCode().equalsIgnoreCase("200")){
                            obj = JSON.parseObject(resp.getResult(), DeviceRealStateVo.class);
                        }

                        LogUtils.e("resp",resp.getState()+"=====");
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<DeviceRealStateVo>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(DeviceRealStateVo realStateVo) {
                        List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosHongYan = realStateVo.getRealState().getStateInfos();
                        LogUtils.e("天籁音乐", "ssss="+stateInfosHongYan);
                        if(realStateVo!=null &&stateInfosHongYan!=null && stateInfosHongYan.size()>0){
                            stateInfosBeans =stateInfosHongYan;
                            initSwitch(stateInfosBeans);
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete","============");
                    }
                });

    }

    private void initSwitch(List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans) {
        String play;
        String volume;
        for (DeviceRealStateVo.RealStateBean.StateInfosBean datas:stateInfosBeans){
            switch (datas.getDev_ep_id()){
                default:
                    break;
                case 1:
                    if(!datas.getState().equalsIgnoreCase("")){
                        JSONObject parseObject =JSON.parseObject(datas.getState());
                        if (!parseObject.getString("Play").equals("")){
                            play = parseObject.getString("Play");
                            if (play.equals("0")){
                                switchOne=false;
                                mMusicOpen.setSelected(switchOne);
                            }else {
                                switchOne=true;
                                mMusicOpen.setSelected(switchOne);
                            }
                        }
                    }


                    break;
                case 2:
                    if(!datas.getState().equalsIgnoreCase("")){
                        JSONObject parse =JSON.parseObject(datas.getState());
                        if (!parse.getString("Volume").equals("")){
                            volume = parse.getString("Volume");
                            mSeekTv.setText(volume);
                            mSeekBar.setProgress(Integer.parseInt(volume));
                        }
                        //datas.setState("{\"Volume\":\"0\"}");
                    }
                    break;

            }

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.top_back) {
            finish();
        } else if (v.getId() == R.id.top_btn) {
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("GateDeviceActivity","GateDeviceActivity");
            intent.putExtra("deviceInfoBean", deviceInfoBean);
            startActivity(intent);
        } /*else if (v.getId() == R.id.circuit) {
            // 开关
            if (switchOne) {
                switchOne = false;
                mCircuit.setSelected(false);
                controlCurtain(TL_MUSIC_SOCKET_OFF);
            } else {
                switchOne = true;
                mCircuit.setSelected(true);
                controlCurtain(TL_MUSIC_SOCKET_NO);
            }
        } */else if (v.getId() == R.id.music_left) {
            // 上一曲
            switchOne = true;
            mMusicOpen.setSelected(true);
            controlCurtain(TL_MUSIC_OFF);
        } else if (v.getId() == R.id.music_open) {
            // 暂停播放
            if (switchOne) {
                switchOne = false;
                mMusicOpen.setSelected(false);
                controlCurtain(TL_MUSIC_START);
            } else {
                switchOne = true;
                mMusicOpen.setSelected(true);
                controlCurtain(TL_MUSIC_START);
            }
        } else if (v.getId() == R.id.music_right) {
            // 下一曲
            switchOne = true;
            mMusicOpen.setSelected(true);
            controlCurtain(TL_MUSIC_NO);
        }

    }

    private void controlCurtain(String order) {
        String encodeCmd = Base64.encodeToString(order.getBytes(), Base64.NO_WRAP);
        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
        deviceInfoVo.setDeviceId(deviceInfoBean.getId());//deviceInfoBean.getDeviceId()
        deviceInfoVo.setDeviceType("300100");
        deviceInfoVo.setGatewayId(deviceInfoBean.getId());
        deviceInfoVo.setSupplierId(deviceInfoBean.getDeviceType());
        SmartControlVo smartControlVo = new SmartControlVo();
        smartControlVo.setUserId(userId);
        smartControlVo.setFamilyId(familyId);
        smartControlVo.setCmdType("control");
        smartControlVo.setCmd(encodeCmd);
        smartControlVo.setDeviceInfo(deviceInfoVo);

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

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String proId=progress+"";
        String TL_MUSIC = "{ \"dev_ep_id\" :2 ,\"Volume\" : "+proId+"}"; // 音量
        mSeekTv.setText(proId);
        controlCurtain(TL_MUSIC);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}