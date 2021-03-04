package com.example.smarthome.iot.impl.device.cateye.utils;


/**
 * 权限请求接口
 * @author hgz
 */
public interface PermissionInterface {

    /**
     * 可得到请求权限请求码
     */
    int getPermissionsRequestCode();

    /**
     * 请求权限成功回调
     */
    void requestPermissionsSuccess();

    /**
     * 请求权限失败回调
     */
    void requestPermissionsFail();

}
