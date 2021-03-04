package com.example.smarthome.iot.impl.device.infraredscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceInfoActivity;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.impl.device.infraredscreen.adapter.InFraredScreenAdapter;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.InFraredScreenBean;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class InFraredScreenActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG=getClass().getSimpleName();
    private LinearLayout back;
    private TextView top_title;
    private TextView top_btn;
    private ClearEditText device_name;
    private ListView list_item;
    private TextView tv_next;
    InFraredScreenAdapter inFraredScreenAdapter;
    private String userId;
    private List<InFraredScreenBean.InFraredScreenData> datas=new ArrayList<>();
    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;
    private int pso=-1;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_frared_screen);
        addDestoryActivity(this,"InFraredScreenActivity");
        Intent intent=getIntent();
        if (intent!=null){
            gateDevices = intent.getParcelableExtra("gateDevices");
            Log.e("红外幕帘页面",gateDevices.toString());
        }
        initView();
        initDate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (messageEvent.isUpdate()) {
            top_title.setText(messageEvent.getTitle());
        }
    }
    private void initView(){
        back = findViewById(R.id.top_back);
        top_title = findViewById(R.id.top_title);
        top_btn = findViewById(R.id.top_btn);
        device_name = findViewById(R.id.device_name);
        list_item = findViewById(R.id.list_item);
        tv_next = findViewById(R.id.tv_next);
        back.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        top_btn.setOnClickListener(this);
        progress_bar = findViewById(R.id.progress_bar);
    }
    private void initDate(){
        top_title.setText("房产选择");
        userId = SPUtils.get(InFraredScreenActivity.this, SpConstant.SP_USER_MASTER_TELEPHONE,"");
        inFraredScreenAdapter=new InFraredScreenAdapter(InFraredScreenActivity.this,null);
        list_item.setAdapter(inFraredScreenAdapter);
        inFraredScreenAdapter.setInFraredCallBack(new InFraredScreenAdapter.InFraredCallBack() {
            @Override
            public void CallBack(String name,int postion) {
                pso = postion;
                if(datas!=null&&datas.size()>0){
                    device_name.setText(name);
                }else
                    Toast.makeText(InFraredScreenActivity.this,"列表无数据",Toast.LENGTH_LONG).show();
            }
        });
        getHouse(userId);

    }

    private void getHouse(String userId){
        progress_bar.setVisibility(View.VISIBLE);
        OkGo.<String>get(Constant.HOST+Constant.House_by_userId)
                .params("userId",userId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .map(new Function<Response<String>, InFraredScreenBean>() {
                    @Override
                    public InFraredScreenBean apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        if (!resp.getErrorCode().equalsIgnoreCase("200")){
                            Toast.makeText(InFraredScreenActivity.this,resp.getMessage(),Toast.LENGTH_LONG).show();
                        }
                        InFraredScreenBean obj=JSON.parseObject(resp.getResult(),InFraredScreenBean.class);
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InFraredScreenBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG,"onSubscribe==="+d.isDisposed());
                    }

                    @Override
                    public void onNext(InFraredScreenBean inFraredScreenBean) {
                        progress_bar.setVisibility(View.GONE);
                        Log.e(TAG,"onNext==="+inFraredScreenBean.toString());
                        if(inFraredScreenBean.getList()!=null&&inFraredScreenBean.getList().size()>0){
                            datas = inFraredScreenBean.getList();
                            inFraredScreenAdapter.refreshData(datas);
                        }else{

                        }
//                            Toast.makeText(InFraredScreenActivity.this,inFraredScreenBean,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG,"onError==="+e.getMessage());
                        progress_bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        progress_bar.setVisibility(View.GONE);
                        Log.e(TAG,"onComplete===");
                    }
                });
    }





    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.top_back){
            finish();
        }else if (v.getId()==R.id.tv_next){
            //下一步
            if (!TextUtils.isEmpty(device_name.getText().toString())&&datas.size()>0){
                Intent intent=new Intent(InFraredScreenActivity.this,InFraredScreenActivity_two.class);
                intent.putExtra("deviceInfoBean",gateDevices);
                intent.putExtra("InFraredScreenData",datas.get(pso));
                startActivity(intent);
            }else {
                Toast.makeText(InFraredScreenActivity.this,"房屋列表为空或者未选择房屋编号",Toast.LENGTH_LONG).show();
            }
        }else if (v.getId()==R.id.top_btn){
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",gateDevices);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
