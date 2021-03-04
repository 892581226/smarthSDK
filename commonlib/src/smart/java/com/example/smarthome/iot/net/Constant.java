package com.example.smarthome.iot.net;

import com.example.smarthome.BuildConfig;


/**
 * Created by longqun on 2017/3/13 0013.
 * 正式：http://202.105.104.105:8006
 * 测试：http://202.105.96.131:8006
 * http://139.9.76.9:8008/
 */

public class Constant {
    //public static final String HOST = "http://139.9.76.9:8008/";
    public static final String HOST = BuildConfig.SMART_SERVER_URL;
    public static final String SMART_HOME_HOST = "http://58.250.164.144:18080/";//BuildConfig.SMART_SERVER_URL

    public static final String SMART_HOST_REALEASE = "http://139.9.74.92:8080/";

    public static final String YS_GetToken_Host = "https://open.ys7.com/";
    public static final String YS_AccessToken = "api/lapp/token/get";

    public static final String YS_CameraList = "api/device/list";

    public static final String INTERFACE_VERSION = "dev1.0";

    public static final String INIT_SAMRTHOME = "user/"+ INTERFACE_VERSION +"/init";

    public static final String INIT_THOME_LIST = "user/"+ INTERFACE_VERSION +"/families";

    public static final String INIT_HOME_INIT = "user/"+ INTERFACE_VERSION +"/families/";

    public static final String smartExecuteScene = "scene/"+ INTERFACE_VERSION +"/execute";

    public static final String Device_getAll = "device/"+ INTERFACE_VERSION +"/getAll";

    public static final String Device_getDev = "device/"+ INTERFACE_VERSION +"/getDev";

    public static final String Room_create = "room/"+ INTERFACE_VERSION +"/create";

    public static final String Family_getRoomList = "family/"+ INTERFACE_VERSION +"/getRoomList";

    public static final String Room_delete = "room/"+ INTERFACE_VERSION +"/delete";

    public static final String Room_getDevs = "room/"+ INTERFACE_VERSION +"/getDevs";

    public static final String Room_update = "room/"+ INTERFACE_VERSION +"/update";

    public static final String Family_getSceneList = "family/"+ INTERFACE_VERSION +"/getSceneList";

    public static final String Scene_add = "scene/"+ INTERFACE_VERSION +"/add";

    public static final String Scene_get = "scene/"+ INTERFACE_VERSION +"/get";

    public static final String Scene_update = "scene/"+ INTERFACE_VERSION +"/update";

    public static final String Scene_delete = "scene/"+ INTERFACE_VERSION +"/delete";

    public static final String Gateway_regist = "gateway/"+ INTERFACE_VERSION +"/regist";

    public static final String Gateway_getActive = "gateway/"+ INTERFACE_VERSION +"/getActive";

    public static final String Gateway_getDevice = "gateway/"+ INTERFACE_VERSION +"/getDevice";

    public static final String Device_control = "device/"+ INTERFACE_VERSION +"/control";

    public static final String Device_updateDev = "device/"+ INTERFACE_VERSION +"/updateDev";
    public static final String Device_updateGateWay= "gateway/"+ INTERFACE_VERSION +"/updateGateway";

    public static final String Device_getRealMsg = "device/"+ INTERFACE_VERSION +"/getRealMsg";

    public static final String Device_getSensorInfo = "device/"+ INTERFACE_VERSION +"/getSensorInfo";

    public static final String Device_getScenePanelMsg = "device/"+ INTERFACE_VERSION +"/getScenePanelMsg";

    public static final String Device_updateScenePanel = "device/"+ INTERFACE_VERSION +"/updateScenePanel";

    public static final String User_getSubUserList = "user/"+ INTERFACE_VERSION +"/getSubUserList";

    public static final String User_delShareFamily = "user/"+ INTERFACE_VERSION +"/delShareFamily";

    public static final String User_shareFamily = "user/"+ INTERFACE_VERSION +"/shareFamily";

    public static final String User_getUserId = "user/"+ INTERFACE_VERSION +"/getUserId";

    public static final String Family_updateFamilyName = "family/"+ INTERFACE_VERSION +"/updateFamilyName";

    public static final String Ezviz_GetToken = "ezviz/"+ INTERFACE_VERSION +"/getToken";

    public static final String Ezviz_Create = "ezviz/"+ INTERFACE_VERSION +"/create";

    public static final String Ezviz_SetDev = "ezviz/"+ INTERFACE_VERSION +"/setDev";

    public static final String Ezviz_DelDev = "ezviz/"+ INTERFACE_VERSION +"/delDev";

    public static final String House_by_userId="user/"+INTERFACE_VERSION+"/getHouseBuUserId";

    public static final String Save_Family_Project="family/"+INTERFACE_VERSION+"/saveFamilyProjectCode";

    public static final String Get_Device_By_Id="device/"+INTERFACE_VERSION+"/getDeviceById";

    public static final String Save_Infrared_Curtain="device/"+INTERFACE_VERSION+"/saveInfraredCurtain";

    //获取辅控绑定设备
    public static final String GetAuxiliary="device/"+INTERFACE_VERSION+"/getAuxiliary";

    //开启/关闭一键布防
    public static final String Open_close_Defense="family/"+INTERFACE_VERSION+"/openFamilyDefense";

    //获取三合一温控器初始化数据
    public static final String THREE_ONE_INIT="device/"+INTERFACE_VERSION+"/getRealMsg";

    public static final String Get_Device_list="device/"+INTERFACE_VERSION+"/getProtoProduct";

    public static final String Family="family/dev1.0";

    public static final String CreateFamily="family/dev1.0/create";

    public static final String GetToken="ezviz/dev1.0/auth/getToken";

    public static final String HKAdd="ezviz/dev1.0/deveice/add";

    public static final String HKRemove="ezviz/dev1.0/deveice/remove";
}
