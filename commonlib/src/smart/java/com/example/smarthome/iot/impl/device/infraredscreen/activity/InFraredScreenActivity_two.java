package com.example.smarthome.iot.impl.device.infraredscreen.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceInfoActivity;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceBaseInfoVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.impl.device.infraredscreen.adapter.InFraredScreenListAdapter;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.DeviceBean;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.FamilyProjectCodeDto;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.InFraredScreenBean;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class InFraredScreenActivity_two extends BaseActivity implements View.OnClickListener {

    private TextView top_title;
    private LinearLayout top_back;
    private TextView top_btn;
    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;
    InFraredScreenBean.InFraredScreenData inFraredScreenData;
    private RecyclerView recycler_view;
    private InFraredScreenListAdapter inFraredScreenListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_frared_screen_two);
        Intent intent=getIntent();
        if (intent!=null){
            gateDevices=intent.getParcelableExtra("deviceInfoBean");
            inFraredScreenData=(InFraredScreenBean.InFraredScreenData)intent.getSerializableExtra("InFraredScreenData");
        }
        initView();
        initDate();
    }
    private void initView(){
        top_back = findViewById(R.id.top_back);
        top_title = findViewById(R.id.top_title);
        top_btn = findViewById(R.id.top_btn);
        top_back.setOnClickListener(this);
        top_btn.setOnClickListener(this);
        recycler_view = findViewById(R.id.recycler_view);
    }
    private void initDate(){
        top_btn.setVisibility(View.GONE);
        top_btn.setText("管理");
        setRes();
        saveFamilyProject();

    }

    private void setRes(){
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom=10;
                outRect.left=10;
            }
        });
        if (inFraredScreenListAdapter==null){
            inFraredScreenListAdapter = new InFraredScreenListAdapter(R.layout.adapter_in_frared_scereen);
            recycler_view.setAdapter(inFraredScreenListAdapter);
        }
        inFraredScreenListAdapter.setInFraredScreenCallBack(new InFraredScreenListAdapter.InFraredScreenCallBack() {
            @Override
            public void callBack(int position,String location) {
                Intent intent=new Intent(InFraredScreenActivity_two.this,InFraredScreenActivity_three.class);
                intent.putExtra("deviceId",gateDevices.getDeviceId());
                intent.putExtra("position",position+1);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        top_title.setText(gateDevices.getDeviceName());
        getDevById(gateDevices.getDeviceId());
    }

    /**
     * 保存家庭和项目的关系
     */
    private void saveFamilyProject(){
        String userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        String familyId = SPUtils.get(this,"familyId"+userId,"");
        FamilyProjectCodeDto familyProjectCodeDto=new FamilyProjectCodeDto();
        familyProjectCodeDto.setFamilyId(familyId);
        familyProjectCodeDto.setProject(inFraredScreenData.getProjectCode()+","+inFraredScreenData.getName());
        OkGo.<String>post(Constant.HOST+Constant.Save_Family_Project)
                .upJson(JSON.toJSONString(familyProjectCodeDto))
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
                        Log.e("saveFamilyProject","onSubscribe"+d.isDisposed()+"====");
                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        Log.e("saveFamilyProject","onNext====");
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
//                            Toast.makeText(InFraredScreenActivity_two.this,"保存成功",Toast.LENGTH_LONG).show();
                        }else {
//                            Toast.makeText(InFraredScreenActivity_two.this,commonResp.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("saveFamilyProject","onError====");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("saveFamilyProject","onComplete====");
                    }
                });
    }

    /**
     * 通过ID获取设备
     * @param devId
     */
    private void getDevById(String devId){
        OkGo.<String>get(Constant.HOST+Constant.Get_Device_By_Id)
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
                        Log.e("getDevById","onNext===="+commonResp.getResult().toString());
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
                            DeviceBean deviceBean=JSON.parseObject(commonResp.getResult(), DeviceBean.class);
                            Integer length=deviceBean.getDeviceInfo().getField().getState().length();
                            String properField=deviceBean.getDeviceInfo().getProperField();
                            JSONObject jsonObject=JSON.parseObject(properField);
                            List<String> strings=new ArrayList<>();
                            strings.clear();
                            for (int i=0;i<length;i++){
                                strings.add(jsonObject.getString(""+(i+1)));
                            }
                            if (strings!=null&&strings.size()>0){
                                inFraredScreenListAdapter.setNewData(strings);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getDevById","onError===="+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("getDevById","onComplete====");
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.top_back){
            finish();
        }else if (v.getId()==R.id.top_btn){
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",gateDevices);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
