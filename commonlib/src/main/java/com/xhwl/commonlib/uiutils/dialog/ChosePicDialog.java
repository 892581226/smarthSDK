package com.xhwl.commonlib.uiutils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.smarthome.R;


/**
 * Created by "huangasys" on 2019/8/23.17:15
 */
public class ChosePicDialog extends Dialog {

    public static final int SELECT_PHOTOS = 0;//相册
    public static final int CAMERA_PHOTO = 1;//拍照

    private TextView mAlbumText, mGraphText;


    public ChosePicDialog(@NonNull Context context) {
        super(context, R.style.bottom_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_photo);
        initView();
    }

    public void setTitleText(String titleText) {
//        if (mTitleText != null) {
//            mTitleText.setText(titleText);
//        }
    }


    public void goneGraph(boolean isGone){
        if (isGone) {
            if (mGraphText!=null) {
                mGraphText.setVisibility(View.GONE);
            }
        }
    }

    public void goneAlbum(boolean isGone){
        if (isGone) {
            if (mAlbumText!=null) {
                mAlbumText.setVisibility(View.GONE);
            }
        }
    }
    private void initView() {

        mAlbumText = findViewById(R.id.tv_select_gallery);
        mGraphText = findViewById(R.id.tv_select_camera);

//        mCancelText.setOnClickListener(v -> dismiss());
        mAlbumText.setOnClickListener(v -> {
            if (mChosePicListener != null) {
                //相册
                mChosePicListener.goToChosePic(SELECT_PHOTOS);
                dismiss();
            }
        });

        mGraphText.setOnClickListener(v -> {
            if (mChosePicListener != null) {
                //拍照
                mChosePicListener.goToChosePic(CAMERA_PHOTO);
                dismiss();
            }
        });

    }

    private ChosePicListener mChosePicListener;

    public void setChosePicListener(ChosePicListener chosePicListener) {
        mChosePicListener = chosePicListener;
    }

    public interface ChosePicListener {

        void goToChosePic(int type);
    }
}