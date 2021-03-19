package com.example.smarthome.iot.impl.device.negativecontrol.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.DeviceBean;
import com.example.smarthome.iot.impl.device.negativecontrol.adapter.NegativeControlAdapter;
import com.example.smarthome.iot.impl.device.negativecontrol.bean.FkDatsBean;
import com.example.smarthome.iot.impl.device.negativecontrol.bean.NegativeControlBean;
import com.example.smarthome.iot.impl.device.negativecontrol.dialog.YesOrNoBindDialog;
import com.example.smarthome.iot.impl.device.negativecontrol.dialog.YesOrNoBindDialog2;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NegativeControlActivity_Three extends BaseActivity implements View.OnClickListener {

    private final String TAG=this.getClass().getSimpleName();
    private DeviceBean.DeviceData deviceInfo;
    NegativeControlAdapter negativeControlAdapter;
    private RecyclerView recycler_view;
    private String gatewayId;
    private Integer position1;
    private String roomName;
    private TextView tv_name;
    private TextView tv_release_bind;
    private LinearLayout top_back;
    private TextView top_title;
    private TextView top_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negative_control_three);
        Intent intent=getIntent();
        if (intent!=null){
            deviceInfo = (DeviceBean.DeviceData) intent.getSerializableExtra("deviceInfo");
            position1 = intent.getIntExtra("position",-1);
            roomName = intent.getStringExtra("roomName");
        }
//        String userId = SPUtils.get(NegativeControlActivity_Three.this, SpConstant.SP_USER_TELEPHONE, "");
//        String familyId = SPUtils.get(NegativeControlActivity_Three.this, "familyId"+userId, "");
//        gatewayId = SPUtils.get(NegativeControlActivity_Three.this,"gatewayId"+userId+familyId,"");
        Log.e(TAG,deviceInfo.getGatewayId());
        initView();
        initDate();
    }

    private void initView(){
        recycler_view = findViewById(R.id.recycler_view);
        tv_name = findViewById(R.id.tv_name);
        tv_release_bind = findViewById(R.id.tv_release_bind);
        tv_release_bind.setOnClickListener(this);

        top_back = findViewById(R.id.top_back);
        top_title = findViewById(R.id.top_title);
        top_btn = findViewById(R.id.top_btn);
        top_back.setOnClickListener(this);
    }
    private void initDate(){
        if (roomName.equalsIgnoreCase("未绑定")){
            tv_name.setText("");
        }else {
            tv_name.setText(roomName);
        }
        switch (position1){
            default:
                break;
            case 1:
                top_title.setText("键位一");
                break;
            case 2:
                top_title.setText("键位二");
                break;
            case 3:
                top_title.setText("键位三");
                break;
            case 4:
                top_title.setText("键位四");
                break;
        }
        setRes();
        getFkBindDevice(deviceInfo.getGatewayId());

    }

    private void setRes(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        if (negativeControlAdapter==null){
            negativeControlAdapter=new NegativeControlAdapter();
            recycler_view.setAdapter(negativeControlAdapter);
        }
        negativeControlAdapter.setNeGativeCallBack(new NegativeControlAdapter.NeGativeCallBack() {
            @Override
            public void callBack(int position2, String deviceId, String name) {
                YesOrNoBindDialog yesOrNoBindDialog=new YesOrNoBindDialog(NegativeControlActivity_Three.this,R.style.dialog,deviceInfo,deviceInfo.getDeviceId(),position1,deviceId,position2,tv_name.getText().toString());
                yesOrNoBindDialog.show();
                yesOrNoBindDialog.setYesOrNoBindCallBack(new YesOrNoBindDialog.YesOrNoBindCallBack() {
                    @Override
                    public void CallBack() {
                        //更新数据
                        tv_name.setText(name);
                        getFkBindDevice(deviceInfo.getGatewayId());
                    }
                });
            }
        });
    }
    private void getFkBindDevice(String gatewayId){
        OkGo.<String>get(Constant.HOST+Constant.GetAuxiliary)
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
                            NegativeControlBean negativeControlBean=JSON.parseObject(commonResp.getResult(),NegativeControlBean.class);
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

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.tv_release_bind){
            //解绑
            YesOrNoBindDialog2 yesOrNoBindDialog2=new YesOrNoBindDialog2(NegativeControlActivity_Three.this
                    ,R.style.dialog
                    ,deviceInfo,deviceInfo.getDeviceId(),position1);
            yesOrNoBindDialog2.show();
            yesOrNoBindDialog2.setYesOrNoBindCallBack2(new YesOrNoBindDialog2.YesOrNoBindCallBack2() {
                @Override
                public void CallBack() {
                    Toast.makeText(NegativeControlActivity_Three.this,"解绑成功",Toast.LENGTH_LONG).show();
                    tv_name.setText("");
                }
            });
        }else if (v.getId()==R.id.top_back){
            finish();
        }
    }
}
