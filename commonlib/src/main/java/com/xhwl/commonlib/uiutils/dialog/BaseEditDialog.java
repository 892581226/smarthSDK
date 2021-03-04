package com.xhwl.commonlib.uiutils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.smarthome.R;
import com.xhwl.commonlib.uiutils.ClearEditText;
import com.xhwl.commonlib.uiutils.UiTools;


public class BaseEditDialog extends Dialog {

    private View contentView;
    private Context mContext;
    private TextView titleTv;
    private TextView warningTv;
    private ClearEditText inputEt;
    private TextView cancelTv;
    private TextView confirmTv;
    private int maxLength = 10;
    private OnConfirmListener listener;
    private OnCancelListener mOnCancelListener;


    public BaseEditDialog(@NonNull Context context) {
        super(context, R.style.BaseDialogStyle);
        mContext = context;
        contentView = View.inflate(mContext, R.layout.base_edit_dialog, null);
        setContentView(contentView);
        titleTv = findViewById(R.id.base_dialog_title);
        warningTv = findViewById(R.id.base_dialog_warning_tv);
        inputEt = findViewById(R.id.base_dialog_et);
        cancelTv = findViewById(R.id.base_dialog_cancel_tv);
        confirmTv = findViewById(R.id.base_dialog_confirm_tv);

        confirmTv.setOnClickListener(v -> {
            if (listener != null){
                listener.onConfirmClick(inputEt.getText().toString());
            }
        });
        cancelTv.setOnClickListener(v -> {
            if (mOnCancelListener != null){
                mOnCancelListener.onCancelListener();
            }
        });
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

    private TextWatcher defaultWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > maxLength){
                setWarningVisity(true);
            }else {
                setWarningVisity(false);
            }
        }
    };

    public BaseEditDialog setMaxLength(int maxLength){

        this.maxLength = maxLength;
        inputEt.addTextChangedListener(defaultWatcher);
        return this;
    }

    public BaseEditDialog addEditTextWatcher(TextWatcher watcher){
        removeDefaultTextWatcher();
        inputEt.addTextChangedListener(watcher);
        return this;
    }

    public BaseEditDialog removeEditTextWatcher(TextWatcher watcher){
        inputEt.removeTextChangedListener(watcher);
        return this;
    }

    public BaseEditDialog removeDefaultTextWatcher(){
        if (defaultWatcher != null){
            inputEt.removeTextChangedListener(defaultWatcher);
        }
        return this;
    }

    public BaseEditDialog setTitleText(String text){
        titleTv.setText(text);
        return this;
    }

    public BaseEditDialog setEditTextHint(String text){
        inputEt.setHint(text);
        return this;
    }

    public BaseEditDialog setWarningText(String text){
        warningTv.setText(text);
        return this;
    }

    public BaseEditDialog setDefaultEditText(String text){
        inputEt.setText(text);
        return this;
    }

    public BaseEditDialog setWarningVisity(Boolean visible){
        warningTv.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseEditDialog setCancelListener(OnCancelListener listener){
        this.mOnCancelListener = listener;
        return this;
    }

    public BaseEditDialog setConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnConfirmListener{
        void onConfirmClick(String data);
    }

    public interface OnCancelListener{
        void onCancelListener();
    }

}
