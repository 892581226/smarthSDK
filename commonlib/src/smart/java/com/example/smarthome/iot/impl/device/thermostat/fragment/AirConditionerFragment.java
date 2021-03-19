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
import android.widget.RelativeLayout;
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
import com.xhwl.commonlib.uiutils.SPUtils;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AirConditionerFragment extends Fragment implements View.OnClickListener {

    private  final String TAG=this.getClass().getSimpleName();
    private RelativeLayout relate_create_cold;
    private LinearLayout ll_create_cold;
    private RelativeLayout relate_create_hot;
    private LinearLayout ll_create_hot;
    private RelativeLayout relate_clean_wet;
    private LinearLayout ll_clean_wet;
    private RelativeLayout relate_give_wind;
    private LinearLayout ll_give_wind;
    private boolean relate_1=false;
    private boolean relate_2=false;
    private boolean relate_3=false;
    private boolean relate_4=false;
    private ImageView image_small_wind;
    private ImageView image_middle_wind;
    private ImageView image_big_wind;
    private boolean wind_1=false;
    private boolean wind_2=false;
    private boolean wind_3=false;
    private LinearLayout ll_open_or_close_switch;
    private boolean switch_open=false;
    private ImageView image_subtract;
    private ImageView image_add;
    private TextView tv_temperature;
    private double temperature;
    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;
    private String tmp;
    private TextView tv_tmp;
    private JSONObject jsonObject;
    private boolean userVisibleHint=false;
    private List<WkRealMsgBean.WkRealMagData.WkRealMsgData2> stateInfos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            gateDevices = getArguments().getParcelable("gateDevices");
            stateInfos = (List<WkRealMsgBean.WkRealMagData.WkRealMsgData2>) getArguments().getSerializable("stateInfos");
        }

        tmp = SPUtils.get(getActivity(),"Tmp_sun","");
//        temperature=Integer.parseInt(tmp);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_air_conditioner,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        relate_create_cold = view.findViewById(R.id.relate_create_cold);
        ll_create_cold = view.findViewById(R.id.ll_create_cold);
        relate_create_hot = view.findViewById(R.id.relate_create_hot);
        ll_create_hot = view.findViewById(R.id.ll_create_hot);
        relate_clean_wet = view.findViewById(R.id.relate_clean_wet);
        ll_clean_wet = view.findViewById(R.id.ll_clean_wet);
        relate_give_wind = view.findViewById(R.id.relate_give_wind);
        ll_give_wind = view.findViewById(R.id.ll_give_wind);
        relate_create_cold.setOnClickListener(this);
        relate_create_hot.setOnClickListener(this);
        relate_clean_wet.setOnClickListener(this);
        relate_give_wind.setOnClickListener(this);

        image_small_wind = view.findViewById(R.id.image_small_wind);
        image_middle_wind = view.findViewById(R.id.image_middle_wind);
        image_big_wind = view.findViewById(R.id.image_big_wind);
        image_small_wind.setOnClickListener(this);
        image_middle_wind.setOnClickListener(this);
        image_big_wind.setOnClickListener(this);

        ll_open_or_close_switch = view.findViewById(R.id.ll_open_or_close_switch);
        ll_open_or_close_switch.setOnClickListener(this);

        image_subtract = view.findViewById(R.id.image_subtract);
        image_add = view.findViewById(R.id.image_add);
        image_subtract.setOnClickListener(this);
        image_add.setOnClickListener(this);

        tv_temperature = view.findViewById(R.id.tv_temperature);
//        if (temperature>=30) {
//            tv_temperature.setText("30℃");
//        }else if (temperature<=16){
//            tv_temperature.setText("16℃");
//        }else
//            tv_temperature.setText(temperature+"℃");
        tv_tmp = view.findViewById(R.id.tv_tmp);
        tv_tmp.setText(tmp+"℃");

//        init(gateDevices.getDeviceId(),gateDevices.getDeviceType(),gateDevices.getSupplierId());
        init();
    }

    private void init(){
        for (WkRealMsgBean.WkRealMagData.WkRealMsgData2 datas:stateInfos){
            switch (datas.getDev_ep_id()){
                default:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    //空调开关
                    if (datas.getState().isEmpty()){
                        datas.setState("{\"State\":\"0\"}");
                    }
                    jsonObject=JSON.parseObject(datas.getState());
                    String state= jsonObject.getString("State");
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
                    break;
                case 6:
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
                    break;
                case 7:
                    String fanMode="";
                    jsonObject = JSON.parseObject(datas.getState());
                    if (datas.getState().isEmpty()){
                        datas.setState("{\"FanMode\":\"2\"}");
                        jsonObject = JSON.parseObject(datas.getState());
                        fanMode = jsonObject.getString("FanMode");
                    }else if (jsonObject.containsKey("State")){
                        datas.setState("{\"State\":\"2\"}");
                        jsonObject = JSON.parseObject(datas.getState());
                        fanMode = jsonObject.getString("State");
                    }else {
                        jsonObject = JSON.parseObject(datas.getState());
                        fanMode = jsonObject.getString("FanMode");
                    }
                    if (fanMode.equalsIgnoreCase("1")){
                        image_small_wind.setImageResource(R.drawable.small_wind_icon_select);
                        image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                        image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                    }else if (fanMode.equalsIgnoreCase("2")){
                        image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                        image_middle_wind.setImageResource(R.drawable.middle_wind_icon_select);
                        image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                    }else if (fanMode.equalsIgnoreCase("3")){
                        image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                        image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                        image_big_wind.setImageResource(R.drawable.big_wind_icon_select);
                    }
                    break;
                case 8:
                    String workMode="";
                    jsonObject = JSON.parseObject(datas.getState());
                    if (datas.getState().isEmpty()){
                        datas.setState("{\"WorkMode\":\"3\"}");
                        jsonObject = JSON.parseObject(datas.getState());
                        workMode = jsonObject.getString("WorkMode");
                    }else if (jsonObject.containsKey("State")){
                        datas.setState("{\"State\":\"3\"}");
                        jsonObject = JSON.parseObject(datas.getState());
                        workMode = jsonObject.getString("State");
                    }else {
                        jsonObject = JSON.parseObject(datas.getState());
                        workMode = jsonObject.getString("WorkMode");
                    }
                    if (workMode.equalsIgnoreCase("3")){
                        ll_create_cold.setSelected(true);
                        ll_create_hot.setSelected(false);
                        ll_clean_wet.setSelected(false);
                        ll_give_wind.setSelected(false);
                    }else if (workMode.equalsIgnoreCase("4")){
                        ll_create_cold.setSelected(false);
                        ll_create_hot.setSelected(true);
                        ll_clean_wet.setSelected(false);
                        ll_give_wind.setSelected(false);
                    }else if (workMode.equalsIgnoreCase("7")){
                        ll_create_cold.setSelected(false);
                        ll_create_hot.setSelected(false);
                        ll_clean_wet.setSelected(false);
                        ll_give_wind.setSelected(true);
                    }else if (workMode.equalsIgnoreCase("8")){
                        ll_create_cold.setSelected(false);
                        ll_create_hot.setSelected(false);
                        ll_clean_wet.setSelected(true);
                        ll_give_wind.setSelected(false);
                    }
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
                case 255:
                    if (datas.getState().isEmpty()){
                        datas.setState("{\"On\":\"0\",\"Temperature\":\"25\",\"Pattern\":\"1\",\"Wind_num\":\"1\"}");
                    }else if (datas.getState().equalsIgnoreCase("暂无状态")){
                        datas.setState("{\"On\":\"3\",\"Temperature\":\"25\",\"Pattern\":\"5\",\"Wind_num\":\"5\"}");
                    }
                    jsonObject=JSON.parseObject(datas.getState());
                    String no= jsonObject.getString("On");
                    if (no.equalsIgnoreCase("0")){
                        ll_open_or_close_switch.setSelected(false);
                        switch_open=false;
                    }else if (no.equalsIgnoreCase("1")){
                        switch_open=true;
                        ll_open_or_close_switch.setSelected(true);
                    }

                    String temperatureVrv= jsonObject.getString("Temperature");
                    tv_temperature.setText(temperatureVrv+"℃");
                    temperature=Double.parseDouble(temperatureVrv);

                    String Wind_num= jsonObject.getString("Wind_num");
                    if (Wind_num.equalsIgnoreCase("1")){
                        image_small_wind.setImageResource(R.drawable.small_wind_icon_select);
                        image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                        image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                    }else if (Wind_num.equalsIgnoreCase("2")){
                        image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                        image_middle_wind.setImageResource(R.drawable.middle_wind_icon_select);
                        image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                    }else if (Wind_num.equalsIgnoreCase("3")){
                        image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                        image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                        image_big_wind.setImageResource(R.drawable.big_wind_icon_select);
                    }

                    String Pattern= jsonObject.getString("Pattern");
                    if (Pattern.equalsIgnoreCase("1")){
                        ll_create_cold.setSelected(true);
                        ll_create_hot.setSelected(false);
                        ll_clean_wet.setSelected(false);
                        ll_give_wind.setSelected(false);
                    }else if (Pattern.equalsIgnoreCase("2")){
                        ll_create_cold.setSelected(false);
                        ll_create_hot.setSelected(true);
                        ll_clean_wet.setSelected(false);
                        ll_give_wind.setSelected(false);
                    }else if (Pattern.equalsIgnoreCase("3")){
                        ll_create_cold.setSelected(false);
                        ll_create_hot.setSelected(false);
                        ll_clean_wet.setSelected(false);
                        ll_give_wind.setSelected(true);
                    }else if (Pattern.equalsIgnoreCase("4")){
                        ll_create_cold.setSelected(false);
                        ll_create_hot.setSelected(false);
                        ll_clean_wet.setSelected(true);
                        ll_give_wind.setSelected(false);
                    }


                    break;
            }
        }
    }

    private void init(String deviceId, String deviceType, String supplierId){
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
                                switch (datas.getDev_ep_id()){
                                    default:
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                    case 5:
                                        //空调开关
                                        jsonObject=JSON.parseObject(datas.getState());
                                        String state= jsonObject.getString("State");
                                        if (state.equalsIgnoreCase("0")){
                                            ll_open_or_close_switch.setSelected(false);
                                            switch_open=false;
                                        }else {
                                            switch_open=true;
                                            ll_open_or_close_switch.setSelected(true);
                                        }
                                        break;
                                    case 6:
                                        jsonObject=JSON.parseObject(datas.getState());
                                        String temperature1= jsonObject.getString("Temperature");
                                        tv_temperature.setText(temperature1+"℃");
                                        temperature=Double.parseDouble(temperature1);
                                        break;
                                    case 7:
                                        jsonObject=JSON.parseObject(datas.getState());
                                        String fanMode= jsonObject.getString("FanMode");
                                        if (fanMode.equalsIgnoreCase("1")){
                                            image_small_wind.setImageResource(R.drawable.small_wind_icon_select);
                                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                                            image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                                        }else if (fanMode.equalsIgnoreCase("2")){
                                            image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_select);
                                            image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                                        }else if (fanMode.equalsIgnoreCase("3")){
                                            image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                                            image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                                            image_big_wind.setImageResource(R.drawable.big_wind_icon_select);
                                        }
                                        break;
                                    case 8:
                                        jsonObject=JSON.parseObject(datas.getState());
                                        String workMode= jsonObject.getString("WorkMode");
                                        if (workMode.equalsIgnoreCase("3")){
                                            ll_create_cold.setSelected(true);
                                            ll_create_hot.setSelected(false);
                                            ll_clean_wet.setSelected(false);
                                            ll_give_wind.setSelected(false);
                                        }else if (workMode.equalsIgnoreCase("4")){
                                            ll_create_cold.setSelected(false);
                                            ll_create_hot.setSelected(true);
                                            ll_clean_wet.setSelected(false);
                                            ll_give_wind.setSelected(false);
                                        }else if (workMode.equalsIgnoreCase("7")){
                                            ll_create_cold.setSelected(false);
                                            ll_create_hot.setSelected(false);
                                            ll_clean_wet.setSelected(false);
                                            ll_give_wind.setSelected(true);
                                        }else if (workMode.equalsIgnoreCase("8")){
                                            ll_create_cold.setSelected(false);
                                            ll_create_hot.setSelected(false);
                                            ll_clean_wet.setSelected(true);
                                            ll_give_wind.setSelected(false);
                                        }
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
        if (v.getId()==R.id.relate_create_cold){
            if (switch_open){
                ll_create_cold.setSelected(true);
                ll_create_hot.setSelected(false);
                ll_clean_wet.setSelected(false);
                ll_give_wind.setSelected(false);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,3,3+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.relate_create_hot){
            if (switch_open){
                ll_create_cold.setSelected(false);
                ll_create_hot.setSelected(true);
                ll_clean_wet.setSelected(false);
                ll_give_wind.setSelected(false);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,3,4+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId()==R.id.relate_clean_wet){
            if (switch_open){
                ll_create_cold.setSelected(false);
                ll_create_hot.setSelected(false);
                ll_clean_wet.setSelected(true);
                ll_give_wind.setSelected(false);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,3,8+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.relate_give_wind){
            if (switch_open){
                ll_create_cold.setSelected(false);
                ll_create_hot.setSelected(false);
                ll_clean_wet.setSelected(false);
                ll_give_wind.setSelected(true);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,3,7+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.image_small_wind){
            if (switch_open){
                image_small_wind.setImageResource(R.drawable.small_wind_icon_select);
                image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,4,1+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.image_middle_wind){
            if (switch_open){
                image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                image_middle_wind.setImageResource(R.drawable.middle_wind_icon_select);
                image_big_wind.setImageResource(R.drawable.big_wind_icon_normal);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,4,2+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.image_big_wind){
            if (switch_open){
                image_small_wind.setImageResource(R.drawable.small_wind_icon_normal);
                image_middle_wind.setImageResource(R.drawable.middle_wind_icon_normal);
                image_big_wind.setImageResource(R.drawable.big_wind_icon_select);
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,4,3+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.ll_open_or_close_switch){
            if (!switch_open){
                ll_open_or_close_switch.setSelected(true);
                switch_open=true;
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,1,1+"");
            }else {
                ll_open_or_close_switch.setSelected(false);
                switch_open=false;
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,1,0+"");
            }
        }else if (v.getId()==R.id.image_subtract){

            if (switch_open){
                //减温度
                if (temperature<=16){
                    return;
                }
                temperature--;
                tv_temperature.setText(temperature+"℃");
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,2,temperature+"");
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
                DeviceControlUtil.switchControl2(getActivity(),gateDevices,3,2,temperature+"");
            }else {
                Toast.makeText(getActivity(),"请打开开关",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
