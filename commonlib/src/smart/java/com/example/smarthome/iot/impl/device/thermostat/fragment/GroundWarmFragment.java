package com.example.smarthome.iot.impl.device.thermostat.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.impl.device.thermostat.bean.WkRealMsgBean;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DeviceControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GroundWarmFragment extends Fragment implements View.OnClickListener {

    private  final String TAG=this.getClass().getSimpleName();
    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;
    private String tmp;
    private LinearLayout ll_open_or_close_switch;
    private TextView tv_tmp;
    private ImageView image_subtract;
    private TextView tv_temperature;
    private ImageView image_add;
    private boolean switch_open=false;
    private double temperature;
    private JSONObject jsonObject;
    private List<WkRealMsgBean.WkRealMagData.WkRealMsgData2> stateInfos;
    private Integer type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            gateDevices = getArguments().getParcelable("gateDevices");
            stateInfos = (List<WkRealMsgBean.WkRealMagData.WkRealMsgData2>) getArguments().getSerializable("stateInfos");
            type = getArguments().getInt("type",-1);
        }
        tmp = SPUtils.get(getActivity(),"Tmp_sun","");
//        temperature=Integer.parseInt(tmp);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ground_warm,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ll_open_or_close_switch = view.findViewById(R.id.ll_open_or_close_switch);
        tv_tmp = view.findViewById(R.id.tv_tmp);
        image_subtract = view.findViewById(R.id.image_subtract);
        tv_temperature = view.findViewById(R.id.tv_temperature);
        image_add = view.findViewById(R.id.image_add);
        ll_open_or_close_switch.setOnClickListener(this);
        image_subtract.setOnClickListener(this);
        image_add.setOnClickListener(this);
//        if (temperature>=30){
//            tv_temperature.setText("30℃");
//        }else if (temperature<=16){
//            tv_temperature.setText("16℃");
//        }else {
//            tv_temperature.setText(temperature+"℃");
//        }

        tv_tmp = view.findViewById(R.id.tv_tmp);
//        tv_tmp.setText(tmp+"℃");
//        init(gateDevices.getDeviceId(),gateDevices.getDeviceType(),gateDevices.getSupplierId());
        init();
    }

    private void init(){
        for (WkRealMsgBean.WkRealMagData.WkRealMsgData2 datas:stateInfos){
            LogUtils.e(TAG,"init onNext"+datas.getDev_ep_id()+" "+datas.getState());
            switch (datas.getDev_ep_id()){
                default:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    if (type==2||type==3){
                        String state="";
                        if (datas.getState().isEmpty()){
                            datas.setState("{\"State\":\"0\"}");
                        }
                        jsonObject=JSON.parseObject(datas.getState());
                        state= jsonObject.getString("State");
                        if (state.equals("无")){
                            state="0";
                        }
                        if (state.equalsIgnoreCase("0")){
                            ll_open_or_close_switch.setSelected(false);
                            switch_open=false;
                        }else {
                            switch_open=true;
                            ll_open_or_close_switch.setSelected(true);
                        }
                    }

                    break;
                case 4:
                    if (type==3||type==2){
                        String temperature1="";
                        jsonObject=JSON.parseObject(datas.getState());
                        if (datas.getState().isEmpty()){
                            datas.setState("{\"Temperature\":\"20.0\"}");
                            jsonObject=JSON.parseObject(datas.getState());
                            temperature1= jsonObject.getString("Temperature");
                        }else if (jsonObject.containsKey("State")){
                            datas.setState("{\"State\":\"20.0\"}");
                            jsonObject = JSON.parseObject(datas.getState());
                            temperature1 = jsonObject.getString("State");
                        }else {
                            jsonObject = JSON.parseObject(datas.getState());
                            temperature1 = jsonObject.getString("Temperature");
                        }
                        tv_temperature.setText(temperature1+"℃");
                        temperature=Double.parseDouble(temperature1);
                    }
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    String current_temperture="";
                    jsonObject = JSON.parseObject(datas.getState());
                    if(datas.getState().isEmpty()){
                        datas.setState("{\"Current_temperature\":\"18\"}");
                        jsonObject = JSON.parseObject(datas.getState());
                    } else if (jsonObject.containsKey("State")){
                        datas.setState("{\"State\":\"18\"}");
                        jsonObject = JSON.parseObject(datas.getState());
                        current_temperture = jsonObject.getString("State");
                    }else
                        current_temperture= jsonObject.getString("Current_temperature");
                    tv_tmp.setText(current_temperture+"℃");
                    break;
            }
        }
    }


    private void init(String deviceId,String deviceType,String supplierId){
        OkGo.<String>get(Constant.HOST+Constant.THREE_ONE_INIT)
                .params("deviceId",deviceId)
                .params("deviceType",deviceType)
                .params("supplierId",supplierId)
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
                        CommonResp resp= JSON.parseObject(stringResponse.body(),CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG,"init onSubscribe"+d.isDisposed()+"===");
                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        Log.e(TAG,"init onNext"+commonResp.getErrorCode());
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
                            WkRealMsgBean wkRealMsgBean=JSON.parseObject(commonResp.getResult(),WkRealMsgBean.class);
                            List<WkRealMsgBean.WkRealMagData.WkRealMsgData2> stateInfos=wkRealMsgBean.getRealState().getStateInfos();
                            for (WkRealMsgBean.WkRealMagData.WkRealMsgData2 datas:stateInfos){
                                Log.e(TAG,"init onNext"+datas.getDev_ep_id()+" "+datas.getState());
                                switch (datas.getDev_ep_id()){
                                    default:
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        jsonObject = JSON.parseObject(datas.getState());
                                        String state= jsonObject.getString("State");
                                        if (state.equalsIgnoreCase("0")){
                                            ll_open_or_close_switch.setSelected(false);
                                            switch_open=false;
                                        }else {
                                            switch_open=true;
                                            ll_open_or_close_switch.setSelected(true);
                                        }
                                        break;
                                    case 4:
                                        jsonObject=JSON.parseObject(datas.getState());
                                        String temperature1= jsonObject.getString("Temperature");
                                        tv_temperature.setText(temperature1 + "℃");
                                        temperature = Double.parseDouble(temperature1);

                                        break;
                                    case 5:
                                        break;
                                    case 6:
                                        break;
                                    case 7:
                                        break;
                                    case 8:
                                        break;
                                    case 9:
                                        jsonObject = JSON.parseObject(datas.getState());
                                        String current_temperture= jsonObject.getString("Current_temperature");
                                        tv_tmp.setText(current_temperture+"℃");
                                        break;
                                }
                            }
                        }else {

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"init onError"+e.getMessage()+"===");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG,"init onComplete===");
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.ll_open_or_close_switch){
            if (!switch_open){
                ll_open_or_close_switch.setSelected(true);
                switch_open=true;
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,2,1,1+"");
            }else {
                ll_open_or_close_switch.setSelected(false);
                switch_open=false;
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,2,1,0+"");
            }
        }else if (v.getId()==R.id.image_subtract){
            if (switch_open){
                //减温度
                if (temperature<=16){
                    return;
                }
                temperature--;
                tv_temperature.setText(temperature+"℃");
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,2,2,temperature+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.image_add){
            if (switch_open){
                //增加温度
                if (temperature>=30){
                    return;
                }
                temperature++;
                tv_temperature.setText(temperature+"℃");
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,2,2,temperature+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
