package com.example.smarthome.iot.impl.device.gateway.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.example.smarthome.iot.entry.GateWayBeanVo;
import com.example.smarthome.iot.impl.device.gateway.adapter.AppendGatewayAdapter;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.udpsocket.ReceiveUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class AppendGatewayActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    private RelativeLayout image_back;
    private TextView tv_title;
    private ImageView image_scan;
    private ListView list_item;
    AppendGatewayAdapter appendGatewayAdapter;
    private boolean getUdpdataFlag=false;
    private String proto;
    private String gw_model_id;
    private List<String> udpDatas=new ArrayList<>();
    private ArrayList<GateWayBeanVo.GateWayBeanVoData> datas;
    private List<DeviceListVo.ProductCollectionBean> devList = new ArrayList<>();
    private String gateWayId;
    private String modelId;
    private String msg1;
    private Timer timer;
    private int time=0;
    private ArrayList<String> msgList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_append_gateway);
        addDestoryActivity(this,"AppendGatewayActivity");
        msgList = new ArrayList<>();
        msgList.add("msg");
        initView();
        initdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUdp();


    }

    private String mIp;
    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 110:
                    timer.cancel();
                    ToastUtil.show("扫描不到设备");
                    finish();
                   /* Iterator<GateWayBean> iterator = gateWayList.iterator();
                    while (iterator.hasNext()){
                        GateWayBean next = iterator.next();
                        getGateWay(next);
                    }*/
                   /* Intent intent=new Intent(AppendGatewayActivity.this,AppendGatewayActivity_two.class);
                    intent.putExtra("datas",(Serializable) datas);
                    startActivity(intent);
                    finish();*/
                    break;
                case 111:
                    if (time<30){
                        JSONObject jsonObject=JSONObject.parseObject(msg1);
                        LogUtils.e("AppendGatewayActivity", "handleMessage: "+msg1 );
                        String proto_data = jsonObject.getString("proto");
                        gw_model_id = jsonObject.getString("gw_model_id");
                        String gwId = jsonObject.getString("gw_id");
                        mIp = jsonObject.getString("ip");
                        gateWayId=gwId;
                        if (!msgList.contains(gateWayId)){
                            msgList.add(gateWayId);
                            if (proto_data == null) {
                                proto = "zigbee";
                            } else {
                                proto = proto_data;
                            }

                            if (gw_model_id == null) {
                                modelId = "SMK_H1_GTW";
                            } else {
                                modelId = gw_model_id;
                            }

                            if (gateWayId != null&&gwId!=null&&proto!=null) {
                                SPUtils.put(AppendGatewayActivity.this, "gateWayId_sun", gateWayId);
                                getGateWay(modelId,gwId,proto,mIp);
                            }
                        }

                        //initUdp();
                    }else {
                        timer.cancel();

                        Intent intent=new Intent(AppendGatewayActivity.this,AppendGatewayActivity_two.class);
                        intent.putExtra("datas",(Serializable) datas);
                        startActivity(intent);
                        msgList.clear();
                        finish();
                        //handler.sendEmptyMessageDelayed(110,10*1000);
                    }
                    break;
            }
        }
    };

    private void initUdp() {
        ReceiveUtils.init();
        ReceiveUtils.receiveMessage(6767);
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                msg1=ReceiveUtils.getMsg();
                time++;
                if (msg1!=null&&!msg1.equalsIgnoreCase("")){
                    handler.sendEmptyMessage(111);
                }else {
                    if (time>30){
                        handler.sendEmptyMessage(110);
                    }
                }
            }
        };
        timer.schedule(timerTask,100,300);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while (!getUdpdataFlag) {
                    msg1=ReceiveUtils.getMsg();
                    if (msg1!=null&&!msg1.equalsIgnoreCase("")){
                        handler.sendEmptyMessage(111);
                    }
                }
            }
        }).start();*/

//        while (!getUdpdataFlag){
//            msg = ReceiveUtils.getMsg();
//            if (msg!=null&&!msg.equalsIgnoreCase("")){
//
//                getUdpdataFlag=true;
//            }
//        }

//        if (msg==null){
//            Toast.makeText(AppendGatewayActivity.this,"数据为空",Toast.LENGTH_SHORT).show();
//        }else {
//            while (!getUdpdataFlag){
//                if (msg!=null){
//                    Log.e("添加网关udp数据",msg);
//                    JSONObject jsonObject=JSONObject.parseObject(msg);
//                    String proto_data=jsonObject.getString("proto");
//                    if (proto_data==null){
//                        proto = "zigbee";
//                    }else {
//                        proto=proto_data;
//                    }
//                    gw_model_id = jsonObject.getString("gw_model_id");
//
//                    if (gw_model_id==null){
//                        modelId = "SMK_H1_GTW";
//                    }else {
//                        modelId=gw_model_id;
//                    }
//
//                    Log.e("udp获取的数据",proto+" "+modelId);
//
//                    gateWayId = jsonObject.getString("gw_id");
//                    if (gateWayId!=null){
//                        SPUtils.put(AppendGatewayActivity.this,"gateWayId_sun",gateWayId);
//                    }
//
//                    getGateWay(proto,modelId);
//                    getUdpdataFlag=true;
//                }
//            }
//        }


    }

    private void getGateWay(String deviceType,String gateWayId,String proto,String ip){
        OkGo.<String>get(Constant.HOST+"gatewayType/selcet")
                .params("deviceType",deviceType)
                .params("gatewayId",gateWayId)
                .params("proto",proto)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        LogUtils.e("accept",disposable.isDisposed()+"");
                    }
                })
                .map(new Function<Response<String>, GateWayBeanVo>() {

                    @Override
                    public GateWayBeanVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        GateWayBeanVo obj=JSON.parseObject(resp.getResult(),GateWayBeanVo.class);
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GateWayBeanVo>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(GateWayBeanVo gateWayBeanVo) {
                        if (gateWayBeanVo.getGatewayTypeList().size()!=0){
                            List<GateWayBeanVo.GateWayBeanVoData> gatewayTypeList = gateWayBeanVo.getGatewayTypeList();
                            gatewayTypeList.get(0).setId(gateWayId);
                            gatewayTypeList.get(0).setIp(ip);
                            if (gateWayBeanVo.getGatewayTypeList()!=null&&gateWayBeanVo.getGatewayTypeList().size()>0
                            ){
                                datas.addAll(gatewayTypeList);
                                appendGatewayAdapter.refreshDatas(datas);
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        LogUtils.e("onError",e);
                        Log.e("onError",e.getMessage(),e);
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.e("onComplete","========---====");
                    }
                });
    }

    private void initView() {
        image_back = findViewById(R.id.back);
        image_back.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        image_scan = findViewById(R.id.image_scaning);
        list_item = findViewById(R.id.list_item);
        datas=new ArrayList<>();

    }

    private void initdate() {
        tv_title.setText("扫描设备");
        Glide.with(this)
                .load(R.drawable.gateway_scan_icon)
                .asGif()
                .into(image_scan);

        appendGatewayAdapter=new AppendGatewayAdapter(this,null);
        list_item.setAdapter(appendGatewayAdapter);
        appendGatewayAdapter.setCallBack(new AppendGatewayAdapter.CallBack() {
            @Override
            public void back(GateWayBeanVo.GateWayBeanVoData datas,int postion) {
                Intent intent=new Intent(AppendGatewayActivity.this,GatewayNetSettingActivity_gateway.class);
                intent.putExtra("datas",(Serializable) datas);
                timer.cancel();
                startActivity(intent);

                // devList.get(postion).getDev_list();//
            }
        });

        // getAllGateWay();//
    }

    private void getAllGateWay() {
        OkGo.<String>get(Constant.HOST + Constant.Device_getAll)
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
                .map(new Function<Response<String>, DeviceListVo>() {
                    @Override
                    public DeviceListVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        DeviceListVo deviceListVo = JSON.parseObject(resp.getResult(), DeviceListVo.class);

                        if (deviceListVo != null) {
                            LogUtils.e("apply", "====");
                        }
                        return deviceListVo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<DeviceListVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(DeviceListVo deviceListVo) {
                        devList=deviceListVo.getProduct_collection();
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(AppendGatewayActivity.this, "请求失败", Toast.LENGTH_LONG).show();
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
    protected void onStop() {
        super.onStop();
        ReceiveUtils.closeSoket();
        handler.removeMessages(110);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiveUtils.closeSoket();
        handler.removeMessages(110);
        handler.removeMessages(111);
        timer.cancel();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }else{

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
