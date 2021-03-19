package com.xhwl.commonlib.base;

//import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.smarthome.R;
import com.xhwl.commonlib.application.MyAPP;
import com.xhwl.commonlib.uiutils.ClickUtil;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.ManagerActivity;
import com.xhwl.commonlib.uiutils.ProcessUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.statusbar.StatusBarColorUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zw on 2018/8/08.
 */
public abstract class BaseOtherActivity extends AppCompatActivity implements IBaseView {

    private static final String TAG = "BaseOtherActivity";
    private InputMethodManager mInputMethodManager = null;
    public boolean isActive;  //判断是否从后台切换到前台
    private Unbinder mBind;

    protected <T extends View> T findView(int id) {
        return findViewById(id);
    }

//    private ProgressDialog progressDialog;
    private boolean isSearchEditTextCenter = false;
    private boolean isJudgeDoubleClick = false;
    private Context mApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = MyAPP.getIns();
        ManagerActivity.getInstance().addActivity(this);//添加Activity到容器
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(getLayoutId());
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (showStatusBar()) {
            statusBarTransparent();
            setStatusBar();
        }
        StatusBarColorUtils.setStatusBarDarkIcon(this,true);  //参数 false 白色 true 黑色
        MyAPP.baseActivity = this;
        mBind = ButterKnife.bind(this);
        getKeyData();
        initView();
        setListener();
        initData();
    }

    protected boolean showStatusBar() {
        return true;
    }

    protected void getKeyData() {

    }

    protected abstract int getLayoutId();

    protected void initView() {

    }

    protected void setListener() {

    }

    protected void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyAPP.baseActivity = this;
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
            LogUtils.d(TAG, "程序从后台唤醒");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!ProcessUtils.isAppOnForeground()) {
            //app 进入后台
            isActive = false;//记录当前已经进入后台
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_form_left, R.anim.slide_out_to_right);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_form_right, R.anim.slide_out_to_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_form_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //mApplication.setLastTouchTime();
        if (!isJudgeDoubleClick) {
            return super.dispatchTouchEvent(ev);
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isSearchEditTextCenter) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    hideKeyboard(v.getWindowToken());
                }
            }
            if (!ClickUtil.isFastDoubleClick(500)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                v.clearFocus();
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    public void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void showError(String error) {
        ToastUtil.show(this, error);
    }

    /**
     * 浸入式状态栏实现同时取消5.0以上的阴影
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        //修改字体颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 设置状态栏透明
     */
    public void statusBarTransparent() {
        final int sdk = Build.VERSION.SDK_INT;
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        if (sdk >= Build.VERSION_CODES.KITKAT) {
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            // 设置透明状态栏
            if ((params.flags & bits) == 0) {
                params.flags |= bits;
                window.setAttributes(params);
            }
        }
    }

    /**
     * 弹出加载等待窗口
     *
     * @param
     */
    public void showProgressDialog(String msg, boolean cancelable) {
//        try {
//            if (null != progressDialog) {
//                progressDialog.dismiss();
//            }
//            if (!isFinishing())
//                progressDialog = ProgressDialog.show(this, "", msg, true, cancelable);
//        } catch (WindowManager.BadTokenException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 取消ProgressDialog
     */
    public void dismissDialog() {
//        try {
//            if (null != progressDialog) {
//                progressDialog.dismiss();
//            }
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
    }

    public void setJudgeDoubleClick(boolean judgeDoubleClick) {
        isJudgeDoubleClick = judgeDoubleClick;
    }

    public void setIsSearchEditTextCeanter(boolean isSearchEditTextCeanter) {
        this.isSearchEditTextCenter = isSearchEditTextCeanter;
    }

    @Override
    protected void onDestroy() {
        mBind.unbind();
        super.onDestroy();
    }
}
