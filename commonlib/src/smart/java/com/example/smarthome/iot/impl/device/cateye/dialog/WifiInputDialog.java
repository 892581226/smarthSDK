package com.example.smarthome.iot.impl.device.cateye.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.R;

public class WifiInputDialog extends Dialog implements View.OnClickListener {

    private TextView turnout_no;
    private TextView turnout_yes;

    private CatEyeCallback catEyeCallback;
    private TextView tv_wifiAccount;
    private TextView edit_wifi;
    private String wifiName;
    private Context context;

    public void setCatEyeCallback(CatEyeCallback catEyeCallback) {
        this.catEyeCallback = catEyeCallback;
    }

    public interface CatEyeCallback{
        void callBack(String wifiPassword);
    }

    public WifiInputDialog(@NonNull Context context, int themeResId,String wifiName) {
        super(context, themeResId);
        this.wifiName=wifiName;
        this.context=context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifi_input);
        getWindow().setWindowAnimations(R.style.DialogOutAndInStyle);
        turnout_no = findViewById(R.id.turnout_no);
        turnout_yes = findViewById(R.id.turnout_yes);
        turnout_no.setOnClickListener(this);
        turnout_yes.setOnClickListener(this);
        tv_wifiAccount = findViewById(R.id.tv_wifiAccount);
        edit_wifi = findViewById(R.id.edit_wifi);
        tv_wifiAccount.setText(wifiName);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.turnout_no){
            dismiss();
        }else if (v.getId()==R.id.turnout_yes){
            String wifiPassword=edit_wifi.getText().toString();
            if (TextUtils.isEmpty(wifiPassword)){
                Toast.makeText(context,"wifi密码不能为空",Toast.LENGTH_LONG).show();
            }else {
                dismiss();
                catEyeCallback.callBack(wifiPassword);
            }
        }
    }
}
