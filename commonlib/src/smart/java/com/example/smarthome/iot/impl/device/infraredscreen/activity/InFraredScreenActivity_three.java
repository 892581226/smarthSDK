package com.example.smarthome.iot.impl.device.infraredscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.ClearEditText;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class InFraredScreenActivity_three extends BaseActivity implements View.OnClickListener {

    private String deviceId;
    private Integer position;
    private LinearLayout top_back;
    private TextView top_title;
    private TextView top_btn;
    private ClearEditText location_name;
    private TextView tv_save;
    private String location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_frared_screen_three);
        Intent intent=getIntent();
        if (intent!=null){
            deviceId = intent.getStringExtra("deviceId");
            position = intent.getIntExtra("position",-1);
            location = intent.getStringExtra("location");
        }
        initView();
        initDate();
    }

    private void initView(){
        top_back = findViewById(R.id.top_back);
        top_title = findViewById(R.id.top_title);
        top_btn = findViewById(R.id.top_btn);
        top_back.setOnClickListener(this);
        location_name = findViewById(R.id.location_name);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
    }
    private void initDate(){
        top_title.setText("红外幕帘位置管理");
        top_btn.setVisibility(View.GONE);
        location_name.setText(location);
    }

    private void saveInfo(){

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("deviceId",deviceId);
        jsonObject.put("location",location_name.getText().toString());
        jsonObject.put("number",position);
        Log.e("saveInfo","jsonOnject"+JSON.toJSONString(jsonObject));
        OkGo.<String>post(Constant.HOST+Constant.Save_Infrared_Curtain)
                .upJson(JSON.toJSONString(jsonObject))
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
                        Log.e("saveInfo","onNext====");
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
                            Toast.makeText(InFraredScreenActivity_three.this,"保存成功",Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toast.makeText(InFraredScreenActivity_three.this,commonResp.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("saveInfo","onError"+e.getMessage()+"====");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("saveInfo","onComplete====");
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.top_back){
            finish();
        }else if (v.getId()==R.id.tv_save){
            saveInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
