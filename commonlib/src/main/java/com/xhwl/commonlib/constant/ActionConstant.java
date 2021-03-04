
package com.xhwl.commonlib.constant;

/**
 * <p>存储action的常量类 </p>
 * create by zw at 2018/8/10 0010
 */
public class ActionConstant {
    //intent key
    public static final String INTENTKEY01 = "send_intent_key01";
    public static final String INTENTKEY02 = "send_intent_key02";
    public static final String INTENTKEY03 = "send_intent_key03";
    public static final String INTENTKEY04 = "send_intent_key04";
    public static final String INTENTKEY05 = "send_intent_key05";

    public static final long OPEN_PERIOD = 10000;// 刷卡时间5s
    public static final int scanningTime = 5000;// 扫描时间20s

    //startActivity return code
    public static final int REQUEST_CODE = 0x1008;

    //数字动力云对讲类型
    public static int DP_TALK_TYPE = 2;

    //新增家庭限制长度
    public static int FAMILY_INPUT_LENGTH = 9;

    //新增房间、场景限制长度
    public static int ROOM_INPUT_LENGTH = 6;

    public static final int LOAD_PAGE_SIZE = 10;//load page default

    public static final String TENCENT_CLOUD_VIDEO_SERVICE_ACTION = "com.xhwl.xhwlownerapp.service.action.QCloudService";

    //Tencent Handler status
    public static final int CALL_TIMEOUT = 0x1011;   //通话超时
    public static final int CALL_CONNECT = 0x1012;  // 通话连接
    public static final int CALL_USER_NEXT = 0x1013;//  轮呼next
    public static final int CALL_USER_OFFLINE = 0x1014;//节点为空，五秒关闭
    public static final int CALL_PLAY_SOUND = 0x1015;// 开启一秒后播放铃声
    public static final int CALL_TIME_PLUS = 0x1016;// 通话时间记录
    public static final int CALL_TIME_INCREASE = 0x1017;   //通话计时
    public static final int SWITCHCAMERA = 0x1018; //  切换摄像头
    public static final int REMOVEOPENDOOR = 0x1019; //  远程开门
    public static final int REJECTVOICE = 0x1020; //  远程开门


    public static boolean TENCENT_ISCALLING;//用户是否正在通话中
    public static boolean TENCENT_ISROOM;//用户是否在房间中
    public static final String TENCENT_USER_NAME_REFERENCE = "-user-";

    //游客登录 -- 不使用账号，直接进入APP
    public static final String INTENT_GUEST = "guest";

    //我的小区管理房屋状态
    public static boolean managerHousr = false;
    //我的小区--我的管理--业主审核单状态
    public static int INT_STATUS_ONE = 1; // 同意
    public static int INT_STATUS_TWO = 2; // 重新授权
    public static int INT_STATUS_THREE = 3; // 拒绝授权
    public static int INT_STATUS_FOUR = 4; // 取消授权

    public static final String STRING_GUEST_LOGIN= "你当前为游客，请注册账号";

    public static final String USER_TYPE_FAMILY = "family";
    public static final String USER_TYPE_OWNER = "owner";
    public static final String USER_TYPE_TENANT = "tenant";
    public static final String USER_TYPE_OTHER = "other";

    //人脸列表删除状态
    public static boolean isDeleteFace = false;

    public static boolean SHOWMESSAGE = false;

    public static String inputPhone;

    public static int ADD_ROOM_RESULT_CODE = 2; //新增房间选择图片的result
    public static int UPDATE_ROOM_RESULT_CODE = 3; //更新房间选择图片的result
    public static int ADD_SCENE_RESULT_CODE = 4; //新增场景选择图片result
    public static int UPDATE_SCENE_RESULT_CODE = 5; //更新图片选择图片result
    public static int SCENE_SWITCH_RESULT_CODE = 6; //场景开关状态result

}
