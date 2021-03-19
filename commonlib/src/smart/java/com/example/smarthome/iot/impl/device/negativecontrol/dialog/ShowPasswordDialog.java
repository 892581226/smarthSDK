package com.example.smarthome.iot.impl.device.negativecontrol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.smarthome.R;

public class ShowPasswordDialog extends Dialog implements View.OnClickListener {


    private TextView tv_userAccount;
    private TextView tv_show_password;
    String userName;
    String password;

    public ShowPasswordDialog(@NonNull Context context, int themeResId,String userName,String password) {
        super(context, themeResId);
        this.userName=userName;
        this.password=password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_password);
        tv_userAccount = findViewById(R.id.tv_userAccount);
        tv_show_password = findViewById(R.id.tv_show_password);
        tv_userAccount.setText(userName);
        tv_show_password.setText(password);
    }

    @Override
    public void onClick(View v) {

    }
}
