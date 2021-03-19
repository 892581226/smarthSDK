package com.xhwl.commonlib.uiutils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarthome.R;


public class SatisfactionDialog extends Dialog {
    ImageView mDialogClose;
    TextView mDialogTitle;
    TextView mDialogMessage;
    ImageView mDialogSuperPraise;
    ImageView mDialogPraise;
    ImageView mDialogGeneral;
    ImageView mDialogBad;
    ImageView mDialogVeryBad;

    private OnSuperPraiseClickListener mOnSuperPraiseClickListener;
    private OnPraiseClickListener mOnPraiseClickListener;
    private OnGeneralClickListener mOnGeneralClickListener;
    private OnBadClickListener mOnBadClickListener;
    private OnVeryBadClickListener mOnVeryBadClickListener;
    private OnCancelClickListener mOnCancelClickListener;

    public SatisfactionDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_satisfaction_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        mDialogClose = findViewById(R.id.dialog_close);
        mDialogSuperPraise =  findViewById(R.id.dialog_super_praise);
        mDialogPraise =  findViewById(R.id.dialog_praise);
        mDialogGeneral =  findViewById(R.id.dialog_general);
        mDialogBad =  findViewById(R.id.dialog_bad);
        mDialogVeryBad =  findViewById(R.id.dialog_very_bad);
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        mDialogClose.setOnClickListener(v -> {
            if (mOnCancelClickListener != null) {
                mOnCancelClickListener.onClick();
            }
        });
        mDialogSuperPraise.setOnClickListener(v -> {
            if (mOnSuperPraiseClickListener != null) {
                mOnSuperPraiseClickListener.onClick();
            }
        });
        mDialogPraise.setOnClickListener(v -> {
            if (mOnPraiseClickListener != null) {
                mOnPraiseClickListener.onClick();
            }
        });
        mDialogGeneral.setOnClickListener(v -> {
            if (mOnGeneralClickListener != null) {
                mOnGeneralClickListener.onClick();
            }
        });
        mDialogBad.setOnClickListener(v -> {
            if (mOnBadClickListener != null) {
                mOnBadClickListener.onClick();
            }
        });
        mDialogVeryBad.setOnClickListener(v -> {
            if (mOnVeryBadClickListener != null) {
                mOnVeryBadClickListener.onClick();
            }
        });
    }


    /**
     * 设置按钮的显示内容和监听
     *
     */
    public void setOnSuperPraiseClickListener(OnSuperPraiseClickListener onSuperPraiseClickListener) {
        this.mOnSuperPraiseClickListener = onSuperPraiseClickListener;
    }


    /**
     * 设置按钮的显示内容和监听
     *
     */
    public void setOnPraiseClickListener(OnPraiseClickListener onPraiseClickListener) {
        this.mOnPraiseClickListener = onPraiseClickListener;
    }

    /**
     * 设置按钮的显示内容和监听
     *
     */
    public void setOnGeneralClickListener(OnGeneralClickListener onGeneralClickListener) {
        this.mOnGeneralClickListener = onGeneralClickListener;
    }

    /**
     * 设置按钮的显示内容和监听
     *
     */
    public void setOnBadClickListener(OnBadClickListener onBadClickListener) {
        this.mOnBadClickListener = onBadClickListener;
    }

    /**
     * 设置按钮的显示内容和监听
     *
     */
    public void setOnVeryBadClickListener(OnVeryBadClickListener onVeryBadClickListener) {
        this.mOnVeryBadClickListener = onVeryBadClickListener;
    }

    /**
     * 设置按钮的显示内容和监听
     *
     */
    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.mOnCancelClickListener = onCancelClickListener;
    }

    /**
     * 设置按钮被点击的接口
     */
    public interface OnSuperPraiseClickListener {
        public void onClick();
    }

    public interface OnPraiseClickListener {
        public void onClick();
    }

    public interface OnGeneralClickListener {
        public void onClick();
    }

    public interface OnBadClickListener {
        public void onClick();
    }

    public interface OnVeryBadClickListener {
        public void onClick();
    }

    public interface OnCancelClickListener {
        public void onClick();
    }
}
