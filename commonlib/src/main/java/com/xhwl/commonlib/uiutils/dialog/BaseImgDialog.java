package com.xhwl.commonlib.uiutils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarthome.R;
import com.xhwl.commonlib.uiutils.UiTools;


public class BaseImgDialog extends Dialog {

    private View contentView;
    private Context mContext;
    private TextView confirmTv;
    private TextView titleTv;
    private TextView infoTv;
    private ImageView imgView;
    private ImageView closeIv;

    public BaseImgDialog(@NonNull Context context) {
        super(context, R.style.BaseDialogStyle);
        mContext = context;
        contentView = View.inflate(mContext, R.layout.base_img_dialog, null);
        setContentView(contentView);

        confirmTv = findViewById(R.id.base_dialog_confirm_tv);
        closeIv = findViewById(R.id.base_dialog_close_iv);
        titleTv = findViewById(R.id.base_dialog_title);
        infoTv = findViewById(R.id.base_dialog_info_tv);
        imgView = findViewById(R.id.base_dialog_img_iv);
        imgView.setOnClickListener(v -> dismiss());
        confirmTv.setOnClickListener(v -> dismiss());
        closeIv.setOnClickListener(v -> dismiss());

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
        lp.width = UiTools.dip2px(getContext(),270);
        // lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        // lp.width  = 400;
        lp.height = UiTools.dip2px(getContext(),300);
        window.setAttributes(lp);
    }

    public BaseImgDialog setTitleText(String text){
        titleTv.setText(text);
        return this;
    }

    public BaseImgDialog setInfoText(String info){
        infoTv.setText(info);
        return this;
    }

    public BaseImgDialog setCloseListener(View.OnClickListener listener){
        closeIv.setOnClickListener(listener);
        return this;
    }

    public BaseImgDialog setImageListener(View.OnClickListener listener){
        imgView.setOnClickListener(listener);
        return this;
    }

    public BaseImgDialog setConfirmListener(View.OnClickListener listener) {
        confirmTv.setOnClickListener(listener);
        return this;
    }
    public BaseImgDialog setImageResouce(int resId){
        imgView.setImageResource(resId);
        return this;
    }
    public BaseImgDialog setImageResouce(Drawable drawable){
        imgView.setImageDrawable(drawable);
        return this;
    }
    public BaseImgDialog setImageBitmap(Bitmap bitmap){
        imgView.setImageBitmap(bitmap);
        return this;
    }
    public BaseImgDialog setImageURI(Uri uri){
        imgView.setImageURI(uri);
        return this;
    }

}
