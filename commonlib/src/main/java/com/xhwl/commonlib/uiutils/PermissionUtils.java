package com.xhwl.commonlib.uiutils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.xhwl.commonlib.base.BaseActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;


/**
 * <p> 权限检测</p>
 * create by zw at 2017/06/08
 */
public class PermissionUtils {
    private static final String TAG = "PermissionUtils";
    private static int mRequestCode;

    private static PermissionsResultListener mListener;

    private static BaseActivity fragment;
    /**
     * 其他 fragment 继承 BaseFragment 调用 performRequestPermissions 方法
     *
     * @param desc        首次申请权限被拒绝后再次申请给用户的描述提示
     * @param permissions 要申请的权限数组
     * @param requestCode 申请标记值
     * @param listener    实现的接口
     */
    public static void performRequestPermissions(BaseActivity mFragment, String desc, String[] permissions, int requestCode, PermissionsResultListener listener) {
        if (permissions == null || permissions.length == 0) return;
        mRequestCode = requestCode;
        mListener = listener;
        fragment = mFragment;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkEachSelfPermission(permissions)) {// 检查是否声明了权限
                requestEachPermissions(desc, permissions, requestCode);
            } else {// 已经申请权限
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }
        }
        PermissionUtils.fragment = null;
        PermissionUtils.mListener = null;
    }

    /**
     * 申请权限前判断是否需要声明
     *
     * @param desc
     * @param permissions
     * @param requestCode
     */
    private static void requestEachPermissions(String desc, String[] permissions, int requestCode) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
//            showRationaleDialog(desc, permissions, requestCode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(permissions, requestCode);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(permissions, requestCode);
            }
        }
    }

    /**
     * 弹出声明的 Dialog
     * @param desc
     * @param permissions
     * @param requestCode
     */
    private static void showRationaleDialog(String desc, final String[] permissions, final int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(fragment);
        builder.setTitle("提示")
                .setMessage(desc)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            fragment.requestPermissions(permissions, requestCode);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                })
                .setCancelable(false)
                .show();
    }


    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private static boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (fragment.shouldShowRequestPermissionRationale(permission)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return true 需要申请权限,false 已申请权限
     */
    public static boolean checkEachSelfPermission(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(fragment, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkEachSelfPermission(Context context,String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(fragment, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public static void check(final Activity activity, String... permissions){
        AndPermission.with(activity)
                .requestCode(100)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(activity, rationale).show();
                    }
                })
                .permission(permissions)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        LogUtils.e(TAG,"权限获取成功！");
                        for (String grantPermission : grantPermissions) {
                            LogUtils.e(TAG, grantPermission);
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        LogUtils.e(TAG,"权限获取失败！");
                        AndPermission.defaultSettingDialog(activity, 400).show();
                        for (String grantPermission : deniedPermissions) {
                            LogUtils.e(TAG, grantPermission);
                        }
                    }
                })
                .start();
    }

    public static void check(final Activity activity, PermissionsResultListener listener, String... permissions) {
        AndPermission.with(activity)
                .requestCode(100)
                .rationale((requestCode, rationale) -> AndPermission.rationaleDialog(activity, rationale).show())
                .permission(permissions)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        LogUtils.e(TAG, "权限获取成功！");
                        for (String grantPermission : grantPermissions) {
                            LogUtils.e(TAG, grantPermission);
                        }
                        if (listener != null) {
                            listener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        LogUtils.e(TAG, "权限获取失败！");
                        try {
                            AndPermission.defaultSettingDialog(activity, 400).show();
                        } catch (WindowManager.BadTokenException e) {
                            e.printStackTrace();
                        }
                        for (String grantPermission : deniedPermissions) {
                            LogUtils.e(TAG, grantPermission);
                        }
                        if (listener != null) {
                            listener.onPermissionDenied(deniedPermissions);
                        }
                    }
                })
                .start();
    }

    public interface PermissionsResultListener {

        void onPermissionGranted();

        void onPermissionDenied();

        void onPermissionDenied(List<String> deniedPermissions);


    }
}
