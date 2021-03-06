package com.yaokantv.yaokanui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarthome.R;
import com.jaeger.library.StatusBarUtil;
import com.yaokantv.yaokansdk.callback.YaokanSDKListener;
import com.yaokantv.yaokansdk.manager.Yaokan;
import com.yaokantv.yaokansdk.model.YkMessage;
import com.yaokantv.yaokansdk.model.e.MsgType;
import com.yaokantv.yaokanui.utils.ToastUtils;
import com.yaokantv.yaokanui.utils.YKAppManager;
import com.yaokantv.yaokanui.widget.TypeListDialog;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements YaokanSDKListener {
    protected static final int TITLE_LOCATION_LEFT = 0;
    protected static final int TITLE_LOCATION_CENTER = 1;
    ProgressDialog dialog;
    ProgressDialog progressDialog;
    static final String TAG = "YaokanSDK";
    protected String[] rcItem = {"删除"};
    protected String[] rcListItem = {"查询小苹果内的遥控器"};
    Activity activity;
    protected int rcTid;
    protected String uuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Yaokan.instance().addSdkListener(this);
        activity = this;
        dialog = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        progressDialog.setCanceledOnTouchOutside(false);
        YKAppManager.getAppManager().addActivity(activity);
    }


    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Yaokan.instance().removeSdkListener(this);
        YKAppManager.getAppManager().finishActivity(activity);
    }

    protected void setTopColor(int type) {
        View view = findViewById(R.id.top);
        if (view == null) {
            return;
        }
        if (type == 6 || type == 15 || type == 40 || type == 14 || type == 8 || type == 7 || type == 21
                || type == 22 || type == 24 || type == 25 || type == 23 || type == 38 || type == 41|| type == 44) {
            view.setBackgroundResource(R.color.top_gray_deep);
//            StatusBarUtil.setTranslucent(this);
            StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.top_gray_deep));
        }
    }

    OnDownloadTimerOutListener onDownloadTimerOutListener;

    public void setOnDownloadTimerOutListener(OnDownloadTimerOutListener onDownloadTimerOutListener) {
        this.onDownloadTimerOutListener = onDownloadTimerOutListener;
    }

    interface OnDownloadTimerOutListener {
        void onTimeOut();
    }

    protected void showSetting(boolean isShow) {
        View v = findViewById(R.id.setting);
        if (v != null) {
            v.setVisibility(isShow ? (Config.IS_SHOW_SETTING ? View.VISIBLE : View.GONE) : View.GONE);
        }
    }

    protected void showSetting(int res, View.OnClickListener listener) {
        showSetting(true);
        ImageView v = findViewById(R.id.setting);
        if (v != null) {
            if (res != 0) {
                v.setImageResource(res);
            }
            v.setOnClickListener(listener);
        }
    }

    protected void toast(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToastFree(activity, s);
            }
        });
    }

    protected void toast(int s) {
        toast(getString(s));
    }

    public abstract void onReceiveMsg(final MsgType msgType, final YkMessage ykMessage);

    protected void setBtnTypeface(Button view, String t) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        view.setTypeface(typeface);
        view.setText(t);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
        Button button = findViewById(R.id.btn_reload);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reload();
                }
            });
        }
        View back = findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        initView();
    }

    protected void hideBack() {
        View back = findViewById(R.id.back);
        if (back != null) {
            back.setVisibility(View.VISIBLE);
        }
    }

    protected void showContent(boolean b) {
        View content = findViewById(R.id.rl_content);
        View empty = findViewById(R.id.empty);
        if (content != null) {
            content.setVisibility(b ? View.VISIBLE : View.GONE);
        }
        if (empty != null) {
            empty.setVisibility(!b ? View.VISIBLE : View.GONE);
        }
    }

    protected abstract void reload();

    public void setMTitle(String name, int location) {
        if (findViewById(R.id.tv_top_left) != null) {
            findViewById(R.id.tv_top_left).setVisibility(View.GONE);
        }
        if (findViewById(R.id.tv_top_center) != null) {
            findViewById(R.id.tv_top_center).setVisibility(View.GONE);
        }
        switch (location) {
            case 0:
                if (findViewById(R.id.tv_top_left) != null) {
                    ((TextView) findViewById(R.id.tv_top_left)).setText(name);
                    findViewById(R.id.tv_top_left).setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if (findViewById(R.id.tv_top_center) != null) {
                    ((TextView) findViewById(R.id.tv_top_center)).setText(name);
                    findViewById(R.id.tv_top_center).setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    protected void setMTitle(int name, int location) {
        setMTitle(getString(name), location);
    }

    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
    }

    public void showDlg() {
        if (dialog != null && !dialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage(getString(R.string.loading));
                    dialog.show();
                }
            });
        }
    }

    protected void showForceDlg(final String s) {
        if (dialog != null && !dialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage(s);
                    dialog.setCancelable(false);
                    dialog.show();
                }
            });
        }
    }

    protected void showProDlg(final String s, final int p) {
        if (progressDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setCancelable(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setMax(100);
                    progressDialog.setProgress(p);
                    progressDialog.setMessage(s);
                    if (!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                }
            });
        }
    }

    protected void dismissPro() {
        if (progressDialog != null && progressDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }
    }

    protected void showDlg(final String s) {
        if (dialog != null && !dialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage(s);
                    dialog.setCancelable(true);
                    dialog.show();
                }
            });
        }
    }

    CountDownTimer timer;

    protected void timerCancel() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * @param time 超时时间
     * @param s
     */
    protected void showDlg(int time, String s, final OnDownloadTimerOutListener onDownloadTimerOutListener) {
        this.onDownloadTimerOutListener = onDownloadTimerOutListener;
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(time * 1000, 900) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (onDownloadTimerOutListener != null) {
                    onDownloadTimerOutListener.onTimeOut();
                }
                timer.cancel();
                timer = null;
                dismiss();
            }
        };
        timer.start();
        showForceDlg(s);
    }

    protected void showDlg(final String s, final DialogInterface.OnCancelListener listener) {
        if (dialog != null && !dialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage(s);
                    dialog.setOnCancelListener(listener);
                    dialog.setCancelable(true);
                    dialog.show();
                }
            });
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
    }

    public static Dialog showTypeList(View v, Context context, List<String> data, TypeListDialog.OnStringSelectedListener listener) {
        TypeListDialog shakeDlg = new TypeListDialog(context, data, listener);
        shakeDlg.setCanceledOnTouchOutside(true);
        Window dialogWindow = shakeDlg.getWindow();// 设置Dialog的位置
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);// 垂直居中和水平居中
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        lp.y = (int) (location[1] - v.getHeight() * 1.3);
        dialogWindow.setAttributes(lp);
        try {
            shakeDlg.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return shakeDlg;
    }
}
