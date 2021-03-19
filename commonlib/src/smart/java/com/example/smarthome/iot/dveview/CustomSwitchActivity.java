package com.example.smarthome.iot.dveview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceInfoActivity;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceRealStateMsg;
import com.example.smarthome.iot.entry.DeviceRealStateVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.DeviceBean;
import com.example.smarthome.iot.impl.device.negativecontrol.adapter.NegativeControlAdapter;
import com.example.smarthome.iot.impl.device.negativecontrol.bean.FkDatsBean;
import com.example.smarthome.iot.impl.device.negativecontrol.bean.NegativeControlBean;
import com.example.smarthome.iot.impl.device.negativecontrol.dialog.YesOrNoBindDialog;
import com.example.smarthome.iot.impl.device.negativecontrol.dialog.YesOrNoBindDialog2;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DeviceControlUtil;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class CustomSwitchActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private View mTitleLine;
    private ImageView mDoubleBitSwitchOne;
    private ImageView mDoubleBitSwitchTwo;
    private boolean switchOne; // 默认未开启 false
    private boolean switchTwo=false; // 默认未开启 false
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private TextView mCustomOne;
    private TextView mCustomTwo;
    private RecyclerView mCustomRecycler;
    NegativeControlAdapter negativeControlAdapter;
    private TextView mTvname;
    Integer position1=2;
    private DeviceBean deviceBean;
    private JSONObject jsonObject;
    private String suffix;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom__switch);
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

    private void initView() {
        mCustomRecycler = findViewById(R.id.custom_recycler);
        mCustomRecycler.setLayoutManager(new LinearLayoutManager(this));
        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        mTopBack = findViewById(R.id.top_back);
        mTvname = findViewById(R.id.tv_name);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTitleLine = (View) findViewById(R.id.title_line);
        mDoubleBitSwitchOne = (ImageView) findViewById(R.id.custom_socket_one);
        mDoubleBitSwitchTwo = (ImageView) findViewById(R.id.custom_socket_two);
        mCustomOne = findViewById(R.id.custom_tv);
        mCustomTwo = findViewById(R.id.custom_tv2);
        mDoubleBitSwitchTwo.setOnClickListener(this);
        mDoubleBitSwitchOne.setOnClickListener(this);
        mTopBack.setOnClickListener(this);
        mTopBtn.setOnClickListener(this);
        mCustomOne.setOnClickListener(this);
        mCustomTwo.setOnClickListener(this);
        getDeviceById(deviceInfoBean.getDeviceId());
        getFkBindDevice(deviceInfoBean.getGatewayId());
        setRes();
        mTopTitle.setText(deviceInfoBean.getDeviceName());
    }

    private void initDate() {
        mTitleLine.setVisibility(View.GONE);
        mTopBtn.setText("管理");
        getRealMsg(deviceInfoBean.getDeviceId(),deviceInfoBean.getDeviceType(),deviceInfoBean.getSupplierId());
    }

    private void getRealMsg(String deviceId, String deviceType, String supplierId) {
        OkGo.<String>get(Constant.HOST+ Constant.Device_getRealMsg)
                .params("deviceId",deviceId)
                .params("deviceType",deviceType)
                .params("supplierId",supplierId)
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
                        LogUtils.w("smartInit", "onSuccess: " + realStateVo.getRealState().getDev_type());
                        if(realStateVo!=null && realStateVo.getRealState().getStateInfos()!=null && realStateVo.getRealState().getStateInfos().size()>0){
                            List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans = realStateVo.getRealState().getStateInfos();
                            if(stateInfosBeans.size()>0){
                                for(DeviceRealStateVo.RealStateBean.StateInfosBean bean: stateInfosBeans){
                                    DeviceRealStateMsg stateMsg = new Gson().fromJson(bean.getState(), DeviceRealStateMsg.class);
                                    if(bean.getDev_ep_id() == 1){
                                        switchOne = stateMsg.getState().equalsIgnoreCase("1");
                                    }
                                }
                            }
                            initSwitch();
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


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.top_back){
            finish();
        } else if(v.getId() == R.id.top_btn){
            //管理
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",deviceInfoBean);
            startActivity(intent);
        } else if(v.getId() == R.id.custom_socket_one){
            // 开启/关闭 开关
            if (switchOne) {
                switchOne = false;
                mDoubleBitSwitchOne.setSelected(true);
                DeviceControlUtil.switchControl(this,deviceInfoBean,1,0);
            } else {
                switchOne = true;
                mDoubleBitSwitchOne.setSelected(false);
                DeviceControlUtil.switchControl(this,deviceInfoBean,1,1);
            }
        } else if(v.getId() == R.id.custom_socket_two){
            // 开启/关闭 开关
            if (switchTwo) {
                switchTwo = false;
                mDoubleBitSwitchTwo.setSelected(true);
                DeviceControlUtil.switchControl(this,deviceInfoBean1,Integer.parseInt(suffix),0);
            } else {
                switchTwo = true;
                mDoubleBitSwitchTwo.setSelected(false);
                DeviceControlUtil.switchControl(this,deviceInfoBean1,Integer.parseInt(suffix),1);
            }
        }else if (v.getId()== R.id.custom_tv2){
            //解绑
            YesOrNoBindDialog2 yesOrNoBindDialog2=new YesOrNoBindDialog2(this
                    , R.style.dialog
                    ,deviceBean.getDeviceInfo(),deviceBean.getDeviceInfo().getDeviceId(),position1);
            yesOrNoBindDialog2.show();
            yesOrNoBindDialog2.setYesOrNoBindCallBack2(new YesOrNoBindDialog2.YesOrNoBindCallBack2() {
                @Override
                public void CallBack() {
                    Toast.makeText(CustomSwitchActivity.this,"解绑成功",Toast.LENGTH_LONG).show();
                    mTvname.setText("");
                    if (mTvname.getText().toString().isEmpty()){
                        deviceInfoBean1=new SmartInfoVo.FamilysBean.DeviceInfoBean();
                        mCustomTwo.setText(getString(R.string.switch_custom));
                        mCustomOne.setText(getString(R.string.switch_custom));
                    }
                }
            });
        }
    }
    private void setRes(){
        if (negativeControlAdapter==null){
            negativeControlAdapter=new NegativeControlAdapter();
            mCustomRecycler.setAdapter(negativeControlAdapter);
        }
        negativeControlAdapter.setNeGativeCallBack(new NegativeControlAdapter.NeGativeCallBack() {
            @Override
            public void callBack(int position2, String deviceId, String name) {
                YesOrNoBindDialog yesOrNoBindDialog=new YesOrNoBindDialog(CustomSwitchActivity.this, R.style.dialog,deviceBean.getDeviceInfo(),deviceBean.getDeviceInfo().getDeviceId(),position1,deviceId,position2,mTvname.getText().toString());
                yesOrNoBindDialog.show();
                yesOrNoBindDialog.setYesOrNoBindCallBack(new YesOrNoBindDialog.YesOrNoBindCallBack() {
                    @Override
                    public void CallBack() {
                        //更新数据
                        mTvname.setText(name);
                        mCustomOne.setText(name);
                        getFkBindDevice(deviceInfoBean.getGatewayId());
                        mCustomTwo.setText(getString(R.string.unbind));
                    }
                });
            }
        });
    }
    private void getFkBindDevice(String gatewayId){
        OkGo.<String>get(Constant.HOST+ Constant.GetAuxiliary)
                .params("gatewayId",gatewayId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
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
                        Log.e("getFkBindDevice","onNext===="+commonResp.toString());
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
                            getDeviceById(deviceInfoBean.getDeviceId());
                            NegativeControlBean negativeControlBean=JSON.parseObject(commonResp.getResult(), NegativeControlBean.class);
                            deviceInfoBean1 = new SmartInfoVo.FamilysBean.DeviceInfoBean();
                            List<FkDatsBean> fkDatsBeans=new ArrayList<>();
                            fkDatsBeans.clear();
                            for (int i=0;i<negativeControlBean.getDevices().size();i++){
                                String field=negativeControlBean.getDevices().get(i).getField();
                                JSONObject jsonObject=JSON.parseObject(field);
                                Iterator<String> iterators=jsonObject.keySet().iterator();
                                for (Iterator<String> it = iterators; it.hasNext(); ){
                                    String key=it.next();
                                    FkDatsBean f=new FkDatsBean();
                                    f.setLocation(negativeControlBean.getDevices().get(i).getLocation());
                                    f.setDeviceId(negativeControlBean.getDevices().get(i).getDeviceId());
                                    if (key.contains("switch")){
                                        if (key.contains("1")){
                                            Log.e("第三页1",negativeControlBean.getDevices().get(i).getDeviceName()+" "+key);
                                            f.setName(negativeControlBean.getDevices().get(i).getDeviceName()+"键位一");
                                            f.setPosition(1);
                                            fkDatsBeans.add(f);
                                        }else if (key.contains("2")){
                                            Log.e("第三页2",negativeControlBean.getDevices().get(i).getDeviceName()+" "+key);
                                            f.setName(negativeControlBean.getDevices().get(i).getDeviceName()+"键位二");
                                            f.setPosition(2);
                                            fkDatsBeans.add(f);
                                        }else if (key.contains("3")){
                                            Log.e("第三页3",negativeControlBean.getDevices().get(i).getDeviceName()+" "+key);
                                            f.setName(negativeControlBean.getDevices().get(i).getDeviceName()+"键位三");
                                            f.setPosition(3);
                                            fkDatsBeans.add(f);
                                        }else if (key.contains("4")){
                                            Log.e("第三页4",negativeControlBean.getDevices().get(i).getDeviceName()+" "+key);
                                            f.setName(negativeControlBean.getDevices().get(i).getDeviceName()+"键位四");
                                            f.setPosition(4);
                                            fkDatsBeans.add(f);
                                        }
                                    }
                                }
                            }
                            Log.e("第三页",fkDatsBeans.toString());

                            negativeControlAdapter.setNewData(fkDatsBeans);
                        }else {

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
    private void getDeviceById(String devId){
        Log.e("devId",devId);
        OkGo.<String>get(Constant.HOST+ Constant.Get_Device_By_Id)
                .params("devId",devId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("getDevById","onSubscribe"+d.isDisposed()+"====");
                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        Log.e("getDevById","onNext====");
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
                            deviceBean = JSON.parseObject(commonResp.getResult(), DeviceBean.class);
                            Integer length= deviceBean.getDeviceInfo().getAuxiliaryDevStatusMap().length();
                            String auxiliaryDevStatusMap= deviceBean.getDeviceInfo().getAuxiliaryDevStatusMap();
                            jsonObject = JSON.parseObject(auxiliaryDevStatusMap);
                            if (jsonObject !=null){

                                Iterator<String> iterators= jsonObject.keySet().iterator();
                                for (Iterator<String> it = iterators; it.hasNext(); ) {
                                    String key = it.next();
                                    String values= jsonObject.getString(key);
                                    String prefix=values.substring(0,values.lastIndexOf("-"));
                                    suffix = values.substring(values.lastIndexOf("-")+1);
                                    Log.e("TAG",prefix+" "+ suffix +" "+key);
                                    getDeviceById2(prefix,key,suffix);
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getDevById","onError====");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("getDevById","onComplete====");
                    }
                });
    }
    private void getDeviceById2(String deviceId,String key,String suffix){
        OkGo.<String>get(Constant.HOST+ Constant.Get_Device_By_Id)
                .params("devId",deviceId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(CommonResp commonResp) {
                        DeviceBean d= JSON.parseObject(commonResp.getResult(), DeviceBean.class);
                        Log.e("辅控数据", d.toString());
                        String key_number="";
                        switch (suffix){
                            default:
                                break;
                            case "1":
                                key_number="键位一";
                                break;
                            case "2":
                                key_number="键位二";
                                break;
                            case "3":
                                key_number="键位三";
                                break;
                            case "4":
                                key_number="键位四";
                                break;
                        }
                        mCustomOne.setText(d.getDeviceInfo().getRoomName()+ d.getDeviceInfo().getDeviceName()+"-"+key_number);
                        mTvname.setText(d.getDeviceInfo().getRoomName()+ d.getDeviceInfo().getDeviceName()+"-"+key_number);
                        if (!mTvname.getText().toString().isEmpty()){
                            mCustomTwo.setText(getString(R.string.unbind));
                            deviceInfoBean1 = new SmartInfoVo.FamilysBean.DeviceInfoBean();
                            deviceInfoBean1.setDeviceId(d.getDeviceInfo().getDeviceId());
                            deviceInfoBean1.setDeviceType(d.getDeviceInfo().getDeviceType());
                            deviceInfoBean1.setGatewayId(d.getDeviceInfo().getGatewayId());
                            deviceInfoBean1.setSupplierId(d.getDeviceInfo().getSupplierId());
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
    private void initSwitch() {
        if (switchOne) {
            mDoubleBitSwitchOne.setSelected(false);
        } else {
            mDoubleBitSwitchOne.setSelected(true);
        }
    }



        /*if (switchTwo) {
            mDoubleBitSwitchTwo.setSelected(false);
        } else {
            mDoubleBitSwitchTwo.setSelected(true);
        }*/
}