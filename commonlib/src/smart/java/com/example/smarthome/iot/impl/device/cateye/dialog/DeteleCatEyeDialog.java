package com.example.smarthome.iot.impl.device.cateye.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.smarthome.R;

public class DeteleCatEyeDialog extends Dialog implements View.OnClickListener {

    private TextView turnout_no;
    private TextView turnout_yes;

    private DeteleCatEyeCallback catEyeCallback;

    public void setCatEyeCallback(DeteleCatEyeCallback catEyeCallback) {
        this.catEyeCallback = catEyeCallback;
    }

    public interface DeteleCatEyeCallback{
        void callBack();
    }

    public DeteleCatEyeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detele_cat_eye);
        getWindow().setWindowAnimations(R.style.DialogOutAndInStyle);
        turnout_no = findViewById(R.id.turnout_no);
        turnout_yes = findViewById(R.id.turnout_yes);
        turnout_no.setOnClickListener(this);
        turnout_yes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.turnout_no){
            dismiss();
        }else if (v.getId()==R.id.turnout_yes){
            dismiss();
            catEyeCallback.callBack();
        }
    }
}
