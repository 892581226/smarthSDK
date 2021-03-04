package com.example.smarthome.iot.impl.device.negativecontrol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.smarthome.R;

public class ZloadPasswordDialog extends Dialog {

    public ZloadPasswordDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_load);
    }
}
