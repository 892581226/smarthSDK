package com.example.smarthome.iot.impl.device.cateye.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.example.smarthome.R;

public class DialogUtil {
    /**
     * 创建一个选择对话框
     *
     * @param context
     * @param pContent            提示消息
     * @param dialogClickListener 点击监听
     * @return
     */
    public static Dialog showSelectDialog(Context context, String title, String pContent, String pLeftBtnStr,
                                          String pRightBtnStr,
                                          final DialogClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(pContent)
                .setPositiveButton(pRightBtnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickListener.confirm();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(pLeftBtnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickListener.cancel();
                        dialog.dismiss();
                    }
                }).create();
        return dialog;
    }

    /**
     * 创建一个选择对话框
     *
     * @param context
     * @param title      说明
     * @param layout     自定义布局
     * @return
     */
    public static Dialog showCustomDialog(Context context, String title, View layout, final DialogClickListener dialogClickListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        AlertDialog dialog = alertDialog.setTitle(title)
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialogClickListener.confirm();
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickListener.cancel();
                        dialog.dismiss();
                    }
                }).create();
        return dialog;
    }


    public interface DialogClickListener {

        public abstract void confirm();

        public abstract void cancel();

    }
}
