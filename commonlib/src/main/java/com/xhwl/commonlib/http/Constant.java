package com.xhwl.commonlib.http;


import android.util.Log;

import com.example.smarthome.BuildConfig;
import com.xhwl.commonlib.uiutils.ToastUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by longqun on 2017/3/13 0013.
 * 正式：http://202.105.104.105:8006
 * 测试：http://202.105.96.131:8006
 */

public class Constant {

    public static final String APPLICATION_ID = "com.xhwl.xhwlownerapp";
    public static final String APPLICATION_ID_PL = "com.xhwl.puluo";

    public static final String openDoorType = "6";//业主端远程开门type

    public static final String FM_STORY_ID = "6";// 喜马拉雅FM 故事分类id
    public static final String FM_CROSS_TALK = "12";//喜马拉雅FM 相声分类id
    public static final String FM_EMOTION = "10";// 喜马拉雅FM 情感分类id
    public static final String FM_HUMANITY = "39";// 喜马拉雅FM 人文分类id
    public static final String FM_SEARCH = "0";// 喜马拉雅FM 人文分类id

    //云瞳登录账密
    public static final String HKurl = "202.105.104.109";
    public static final String HKusername = "wlwpt";
    public static final String HKpassword = "xinghai123";

    public static boolean isForceVersion = true;//记录版本更新是否取消。取消则下次显示页面不提醒
    public static boolean isCheck = false;//常用门移除需要刷新列表

    public static final int PHOTO_PICKED_WITH_DATA = 0x0000015;// Gallery
    public static final int PHOTO_PICKED_WITH_CAMERA = 0x0000016;// Camera

    ///////////////////////////////////////////////////////////////////////////
    //
    //    TencentCloud APPID
    //    1400148064 31600  测试
    //    1400121679  33019  正式
    //
    ///////////////////////////////////////////////////////////////////////////
//    public static final int TENCENT_CLOUD_SDKAPPID = 1400148064;// 测试
    public static final int TENCENT_CLOUD_SDKAPPID = BuildConfig.TENCENT_CLOUD_APPID;


    public static final String HOST2 = BuildConfig.SERVER_URL;

    public static final String HOST = BuildConfig.SMART_SERVER_URL;

    public static final String TLAKHOST = BuildConfig.TLAK_SERVER_URL;

    //测试
//    public static final String HOST2 = "http://seven.xy-mind.com:8006/";//http://192.168.200.116:8011
    //正式
//    public static final String HOST2 = "http://seven.xhmind.com:8006/";
    //预发布
//    public static final String HOST2 = "xhseven.xhmind.com";


    //背景音乐正式服务器
//    public static final String MUSICHOST2 = "http://202.105.104.105:8007/";
    //背景音乐测试服务器
    public static final String MUSICHOST2 = "http://202.105.96.131:443/";


    public static final String WEB2 =  BuildConfig.WEB_SERVER_URL;
    //web测试服务器
//    public static final String WEB2 = "https://seven.xy-mind.com:8060/";
    //web正式服务器
//    public static final String WEB2 = "http://yq.xhmind.com:8060";
    //web预发布
//    public static final String  WEB2 = "https://pre.xhmind.com:8093/";



    //极光推送测试环境APPID
    public static final String JPUSH_APPKEY = "3a74312a795793257ac2ddbb";
    //极光推送预发布、正式APPID
//    public static final String JPUSH_APPKEY = "a38fe644ee98a5b8a36c021e";

    //服务器名
    public static final String SERVERNAME = "ssh/";
    //接口版本号
    public static final String INTERFACEVERSION = "v1/";

    /**
     * API接口
     */
    //获取云对讲token
    public static final String WILDDOGGETTOKEN = "wilddog/getToken?uid=";
    //获取最近新版
    public static final String NEWVERSION = "version/getNewestVersion";
    //获取验证码
    public static final String GETVERIFYCODEBYTYPE = "appBase/getVerificatCodeByType";
    //验证验证码
    public static final String TESTVERIFYCODE = "appBase/register/testVerificatCode";
    //修改密码---通过手机号获取验证码修改密码
    public static final String MODIFYPWD_FORGETPWD = "appBase/modifyPassword/forgetOldPsw";
    //登录
    public static final String LOGIN = "appBase/loginNew";
    //注册
    public static final String REGISTER = "appBase/register";
    //退出登录
    public static final String LOGINOUT = "appBase/appLogout";
    //短信验证码登录
    public static final String VERIFYCODELOGIN = "appBase/verifyCodeLogin";
    //第三方登录
    public static final String THREEPARTYLOGIN = "appBase/ThreeParty/login";
    //第三方绑定用户时获取验证码
    public static final String THREEPARTYGETVERIFYCODE = "appBase/ThreeParty/getVerifyCode";
    //绑定账号时验证码 验证
    public static final String THREEPARTYTESTCODE = "appBase/ThreeParty/testVerifyCode";
    //第三方登录时注册用户
    public static final String THREEPARTYREGISTER = "appBase/ThreeParty/register";
    //文件上传
    public static final String FILEUPLOAD = "appBase/filesUpload";
    //修改密码
    public static final String MODIFYPWD = "appBase/modifyPassword";

    //根据手机号用户授权门禁（有工作站）
    public static final String GETDOORBYPHONENEW = "openDoor/v2/getDoorByPhone";
    //根据人员获取项目下门禁（无工作站）
    public static final String GETDOORBYUSER= "openDoor/getDoorByUser";
    //常用门设置---获取常用门
    public static final String GETCOMMONDOOR = "appBusiness/getCommonDoor";
    //常用门设置---新增/编辑常用门信息
    public static final String OPERATECOMMONDOOR = "appBusiness/operateCommonDoor";

    //远程开门
    public static final String REMOTEOPENDOOR = "openDoor/v2/openDoor";


    //云对讲保存历史记录
    public static final String TALKHISTORYSAVE = "wyBusiness/talkingBack/history/add";
    //门口机---通话时，远程开门
    public static final String OPENDOORBYCALL = "doorMachine/openDoorByCall";
    //获取对讲记录
    public static final String GETHISTORY = "wyBusiness/talkingBack/history/list";
    //云对讲获取客服列表
    public static final String GETONLINELIST = "wyBusiness/talkback/getUids";
    //云对讲获取门口机列表
    public static final String GETDOORLIST = "wyBusiness/getDoorMachineUidByRoomId";

    //云瞳上传图片
    public static final String HKUPLOADIMG = "wyBusiness/iot/video/upload";

    //获取该网段下的所有设备
    public static final String MUSICGETALLDEVICE = "backgroundMusic/mediaDevice/";
    //背景音乐 获取点歌记录
    public static final String MUSICALREDYLIST = "backgroundMusic/mediaplaylist/listPage";
    //背景音乐 删除歌曲
    public static final String MUSICREMOVE = "backgroundMusic/mediaplaylist/remove";
    //背景音乐 当前播放的音乐
    public static final String MUSICCURRENTMEDIA = "backgroundMusic/current/media/";
    //背景音乐 点击歌曲添加到点歌记录
    public static final String MUSICADDLIST = "backgroundMusic/mediaplaylist";
    //背景音乐 获取歌曲资源列表（20首）
    public static final String MUSICLIST = "backgroundMusic/mediaSongs/";
    //背景音乐 只能切换当前播放时自己的歌曲
    public static final String MUSICSWITCH = "backgroundMusic/media/switch";
    //个人信息---绑定/解绑 第三方账号；（微信、QQ、微博）
    public static final String THREEPARTYBIND = "appBase/ThreeParty/bind";

    //心情天气
    public static final String MOODWEATHER = "appBusiness/weather";
    //个人信息---获取个人信息
    public static final String GETUSERINFO = "appBase/getUserInfoByToken";
    //个人信息---最新修改账户个人信息接口
    public static final String UPDATEUSERINFO = "appBase/updateUserInfo";
    public static final String AIFEATURE = "AI/feature";
    public static final String FEATURE = "HWAI/user/face/add";

    //获取智能家居家庭
    public static final String GETSAMRTHOME = "smartHome/listFamily/";
    //获取可接入的设备
    public static final String GETSAMARTHOMEDEVICE = "smartHome/device";
    //绑定网关
    public static final String BINDGATEWAY = "smartHome/family/bindGateWay";
    //解绑网关
    public static final String REMOVEGATEWAY = "smartHome/family/removeGateWay";
    //智能家居--获取某个家庭下所有的场景信息
    public static final String SCENEINFOLIST = "smartHome/sceneInfo/list";
    //智能家居--保存/编辑场景对应图片信息
    public static final String SCENEINFO = "smartHome/sceneInfo";
    //智能家居---删除场景信息
    public static final String SCENEINFODEL = "smartHome/sceneInfo/del";
    //智能家居 智能家居---家庭新增/家庭重命名
    public static final String ADDFAMILY = "smartHome/family";
    //智能家居---删除家庭
    public static final String REMOVEFAMILY = "smartHome/removeFamily";
    //智能家居---获取全部房间
    public static final String ROOMLIST = "smartHome/roomInfo/list";
    //智能家居---删除房间
    public static final String DELETEROOMINFO = "smartHome/roomInfo/del";
    //智能家居---保存/编辑房间对应图片信息
    public static final String ROOMINFO = "smartHome/roomInfo";
    public static final String TYPE = "image/*";
    private static String serverHeadImg = "";


    //将头像上传到服务器获取路径
    public static void imgUpload(String imgUrl, String serverUrl, OkHttpClient client,Callback callback) {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Log.e("file", imgUrl);
        File file = new File(imgUrl);
        if (!file.exists()) {
            ToastUtil.showSingleToast("文件不存在");
        } else {
            //MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE), file);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("files", file.getName(), fileBody)
                    .build();

            Request requestPostFile = new Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build();
            client.newCall(requestPostFile).enqueue(callback);
        }
    }
//new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//            Log.e("lfq", "onFailure");
//        }
//
//        @Override
//        public void onResponse(Call call, final Response response) throws IOException {
//
//        }
//    }
}
