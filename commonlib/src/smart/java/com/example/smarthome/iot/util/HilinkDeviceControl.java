package com.example.smarthome.iot.util;

/**
 * author:
 * date:
 * description: 海令设备控制指令
 * update:
 * version:
 */
public class HilinkDeviceControl {

    //开关
    public static final String CMD_LIGHT_OFF = "{\"State\":\"0\"}"; // 关
    public static final String CMD_LIGHT_ON = "{\"State\":\"1\"}"; // 开
    public static final String CMD_LIGHT_TOGGLE = "{\"State\":\"2\"}"; // 取反

    //窗帘电机
    public static final String CMD_CURTAIN_OFF = "{ \"dev_ep_id\" : 1,\"state\" : 0}"; // 正转
    public static final String CMD_CURTAIN_ON = "{ \"dev_ep_id\" : 1,\"state\" : 1}"; // 反转
    public static final String CMD_CURTAIN_STOP = "{ \"dev_ep_id\" : 1,\"state\" : 2}"; // 停止

    public static final String DOUBLE_CMD_CURTAIN_OFF = "{ \"dev_ep_id\" : 1,\"Operate\" : 0}"; // 正转
    public static final String DOUBLE_CMD_CURTAIN_ON = "{ \"dev_ep_id\" : 1,\"Operate\" : 1}"; // 反转
    public static final String DOUBLE_CMD_CURTAIN_STOP = "{ \"dev_ep_id\" : 1,\"Operate\" : 2}"; // 停止
    public static final String DOUBLE2_CMD_CURTAIN_OFF = "{ \"dev_ep_id\" : 2,\"Operate\" : 0}"; // 正转
    public static final String DOUBLE2_CMD_CURTAIN_ON = "{ \"dev_ep_id\" : 2,\"Operate\" : 1}"; // 反转
    public static final String DOUBLE2_CMD_CURTAIN_STOP = "{ \"dev_ep_id\" : 2,\"Operate\" : 2}"; // 停止

    //空调
    public static final int VRV_ORDER_ON = 0x31; //开关
    public static final int VRV_ORDER_TEM = 0x32; //温度
    public static final int VRV_ORDER_PATTERN = 0x33; //模式
    public static final int VRV_ORDER_WIND_NUM = 0x34; //风量
    public static final int VRV_ORDER_STATUS = 0x50; //所有状态

    public static final int EP_ID_VRV_ON = 0x01; //
    public static final int EP_ID_VRV_TEM = 0x02; //
    public static final int EP_ID_VRV_PATTERN = 0x03; //
    public static final int EP_ID_VRV_WIND_NUM = 0x04; //
    public static final int EP_ID_VRV_STATUS = 0xFF; //

    public static final int CMD_VRV_OFF = 2; //
    public static final int CMD_VRV_ON = 1; //

    public static final int PATTERN_COOL = 1; //
    public static final int PATTERN_HEAT = 8; //
}
