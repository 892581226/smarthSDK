package com.xhwl.commonlib.uiutils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.smarthome.R;
import com.xhwl.commonlib.uiutils.UiTools;


public class BaseDialog extends Dialog {

    private View contentView;
    private Context mContext;
    private TextView titleTv;
    private TextView infoTv;
    private TextView confirmTv;
    private TextView cancelTv;
    public final static int NOMAL_DIALOG = 1;
    public final static int NO_TITLE_DIALOG = 2;
    private int type = -1; //1 为普通 2为无标题dialog
    private OnConfirmListener listener;
    private OnCancelListener mOnCancelListener;

    public BaseDialog(@NonNull Context context,int type) {
        super(context, R.style.BaseDialogStyle);
        this.type = type;
        mContext = context;
        contentView = View.inflate(mContext, R.layout.base_dialog, null);
        setContentView(contentView);

        titleTv = findViewById(R.id.base_dialog_title);
        infoTv = findViewById(R.id.base_dialog_info_tv);
        confirmTv = findViewById(R.id.base_dialog_confirm_tv);
        cancelTv = findViewById(R.id.base_dialog_cancel_tv);
        confirmTv.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmClick();
            }
        });
        cancelTv.setOnClickListener(v -> {
            if (mOnCancelListener != null) {
                mOnCancelListener.onCancelListener();
            }
        });//默认监听器
        if (NOMAL_DIALOG == type){
            cancelTv.setVisibility(View.GONE); //无取消按钮
            infoTv.setVisibility(View.VISIBLE);
        }else if (NO_TITLE_DIALOG == type){ //无标题dialog
            cancelTv.setVisibility(View.VISIBLE);
            infoTv.setVisibility(View.GONE); //无详细信息 标题作为显示信息
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    public void initView(){
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = UiTools.dip2px(getContext(),280);
        // lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        // lp.width  = 400;
        lp.height = UiTools.dip2px(getContext(),168);
        window.setAttributes(lp);
    }

    public BaseDialog setTitleText(String text){
        if (NOMAL_DIALOG == type)
        titleTv.setText(text);
        return this;
    }

    public BaseDialog setCancelVisity(Boolean visible){
        cancelTv.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseDialog setInfoText(String info){
        if (NOMAL_DIALOG == type){
            infoTv.setText(info);
        }else if (NO_TITLE_DIALOG == type){
            titleTv.setText(info);
        }

        return this;
    }

    public BaseDialog setConfirmListener(OnConfirmListener confirmListener){
        this.listener = confirmListener;
        return this;
    }

    public BaseDialog setCancelListener(OnCancelListener cancelListener){
        this.mOnCancelListener = cancelListener;
        return this;
    }

    public interface OnConfirmListener{
        void onConfirmClick();
    }

    public interface OnCancelListener{
        void onCancelListener();
    }

}
