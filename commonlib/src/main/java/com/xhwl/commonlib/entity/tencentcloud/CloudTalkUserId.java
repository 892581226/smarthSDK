package com.xhwl.commonlib.entity.tencentcloud;

import com.example.smarthome.BuildConfig;
import com.xhwl.commonlib.application.MyAPP;
import com.xhwl.commonlib.constant.ActionConstant;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.SPUtils;

/**
 * <p>获取腾讯云视频用户id   </p>
 * create by zw at 2018/11/13 0013
 */
public class CloudTalkUserId {

    public static String getLoginQCloudId() {
        String prefix;
        String telephone = SPUtils.get(MyAPP.getIns(), SpConstant.SP_USER_TELEPHONE, "");
        String projectCode = SPUtils.get(MyAPP.getIns(), SpConstant.SP_PROCODE, "");
//        if (BuildConfig.DEBUG) {
//            prefix = "dev";
//        } else {
//            prefix = "prod";
//        }
        prefix = BuildConfig.ENV;
        return prefix + ActionConstant.TENCENT_USER_NAME_REFERENCE + telephone;
//        return projectCode + ActionConstant.TENCENT_USER_NAME_REFERENCE + telephone;
    }

    public static boolean isWebUser(String uid) {
        return uid.contains("staffweb");
    }

    public static boolean isOuterDoor(String uid) {
        return uid.contains("door");
    }
}
