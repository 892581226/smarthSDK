package com.example.smarthome.iot.impl.device.thermostat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class NewWindFragment extends Fragment implements View.OnClickListener {

    private  final String TAG=this.getClass().getSimpleName();
    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;
    private LinearLayout ll_open_or_close_switch;
    private TextView tv_tmp;
    private TextView tv_wind_state;
    private ImageView image_small_wind;
    private ImageView image_middle_wind;
    private ImageView image_big_wind;
    private boolean switch_open=false;
    private String tmp;
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
        tmp = SPUtils.get(getActivity(),"Tmp","");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_wind,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ll_open_or_close_switch = view.findViewById(R.id.ll_open_or_close_switch);
        tv_tmp = view.findViewById(R.id.tv_tmp);
        tv_wind_state = view.findViewById(R.id.tv_wind_state);
        image_small_wind = view.findViewById(R.id.image_small_wind);
        image_middle_wind = view.findViewById(R.id.image_middle_wind);
        image_big_wind = view.findViewById(R.id.image_big_wind);
        ll_open_or_close_switch.setOnClickListener(this);
        image_small_wind.setOnClickListener(this);
        image_middle_wind.setOnClickListener(this);
        image_big_wind.setOnClickListener(this);

//        tv_tmp.setText(tmp);
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
                    if (type==3){
                        String fanMode="";
                        String state="";
                        jsonObject=JSON.parseObject(datas.getState());
                        if (datas.getState().isEmpty()){
                            datas.setState("{\"FanMode\":\"1\",\"State\":\"0\"}");
                            jsonObject=JSON.parseObject(datas.getState());
                        }
                        if (datas.getState().contains("无")){
                            datas.setState("{\"FanMode\":\"1\",\"State\":\"0\"}");
                            jsonObject=JSON.parseObject(datas.getState());
                        }
                        fanMode= jsonObject.getString("FanMode");
                        state= jsonObject.getString("State");

                        if (state.equalsIgnoreCase("0")){
                            ll_open_or_close_switch.setSelected(false);
                            switch_open=false;
                        }else {
                            switch_open=true;
                            ll_open_or_close_switch.setSelected(true);
                        }
                        if (fanMode.equalsIgnoreCase("1")){
                            image_small_wind.setImageResource(R.drawable.small_wind_icon_select);
                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                            image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                            tv_wind_state.setText("低档");
                        }else if (fanMode.equalsIgnoreCase("2")){
                            image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_select);
                            image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                        }else if (fanMode.equalsIgnoreCase("3")){
                            image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                            image_big_wind.setImageResource(R.drawable.big_wind_icon_select);
                            tv_wind_state.setText("高档");
                        }else {
                            image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                            image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                        }
                    }


                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
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
                                        jsonObject=JSON.parseObject(datas.getState());
                                        String state= jsonObject.getString("State");
                                        String fanMode= jsonObject.getString("FanMode");
                                        if (state.equalsIgnoreCase("0")){
                                            ll_open_or_close_switch.setSelected(false);
                                            switch_open=false;
                                        }else {
                                            switch_open=true;
                                            ll_open_or_close_switch.setSelected(true);
                                        }
                                        if (fanMode.equalsIgnoreCase("1")){
                                            image_small_wind.setImageResource(R.drawable.small_wind_icon_select);
                                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                                            image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                                            tv_wind_state.setText("低档");
                                        }else if (fanMode.equalsIgnoreCase("2")){
                                            image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_select);
                                            image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                                        }else if (fanMode.equalsIgnoreCase("3")){
                                            image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                                            image_big_wind.setImageResource(R.drawable.big_wind_icon_select);
                                            tv_wind_state.setText("高档");
                                        }else {
                                            image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                                            image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                                        }

                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
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
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,1,1,1+"");
            }else {
                ll_open_or_close_switch.setSelected(false);
                switch_open=false;
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,1,1,0+"");
            }
        }else if(v.getId()==R.id.image_small_wind){
            if (switch_open){
                image_small_wind.setImageResource(R.drawable.small_wind_icon_select);
                image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,1,4,1+"");
                tv_wind_state.setText("低档");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.image_middle_wind){
            if (switch_open){
                image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                image_middle_wind.setImageResource(R.drawable.middle_wind_icon_select);
                image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,1,4,2+"");
                tv_wind_state.setText("中档");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.image_big_wind){
            if (switch_open){
                image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                image_big_wind.setImageResource(R.drawable.big_wind_icon_select);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,1,4,3+"");
                tv_wind_state.setText("高档");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
