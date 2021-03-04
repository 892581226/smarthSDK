package com.example.smarthome.iot.impl.device.cateye.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.ELog;
import com.eques.icvss.utils.Method;
import com.eques.icvss.utils.ResultCode;
import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.cateye.entity.DevDetailsEntity;
import com.example.smarthome.iot.impl.device.cateye.entity.EventBusEntity;
import com.example.smarthome.iot.impl.device.cateye.utils.Constants;
import com.example.smarthome.iot.impl.device.cateye.utils.DialogUtil;
import com.example.smarthome.iot.impl.device.cateye.utils.ICVSSUserModule;
import com.example.smarthome.iot.impl.device.cateye.utils.NetworkUtils;
import com.example.smarthome.iot.impl.device.cateye.utils.PermissionHelper;
import com.example.smarthome.iot.impl.device.cateye.utils.PermissionInterface;
import com.xhwl.commonlib.base.BaseActivity;


import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;

public class ElectronicCatEyeActivity extends BaseActivity implements ICVSSListener {

    //设备类型 用于生成二维码
    int devType= BuddyType.TYPE_WIFI_DOOR_T21;
    public static ICVSSUserInstance icvss;
    private WifiManager wifiManager;
    // 当前手机连接wifi名称
    private String wifiSSID;

    // 存储的用户名
    private String preferfsUserName;
    // 存储的登录的appkey
    private String preferfsAppkey;
    // 存储的登录的keyid
    private String preferfsKeyId;
    // 权限帮助类
    private PermissionHelper mPermissionHelper;

    private String[] sdkApiList;
    private RelativeLayout rlHeadParent;
    private TextView tvMessageResponse;
    private ImageView ivQrCodeImage;
    private ListView lvEquesSdkAPI;
    private ImageView ivAddDev;

    // 支持操作条目适配器
    private EquesSdkAdapter adapter;

    // 选择要添加设备的类型
    private int selAddDevType = Constants.SELECT_ADD_DEV_TYPE_DEF;
    // 权限申请码
    private int permissionRequestCode = Constants.PERMISSION_APPLY_TOTLE;

    private int addBdyCode = ResultCode.FAILED;

    private String reqId;

    // 猫眼设备ID
    private String bid;
    // 用户ID
    private String uid;
    // 门锁绑定的猫眼设备ID,与设备bid一致，Demo中便于使用,将它们分别定义
    private String lockBid;
    // 数据请求类型
    private int requestType;

    // 预览类型（视频、语音）
    private int reviewType;

    private List<String> fids = null, pvids = null, aids = null;

    private List<String> ringFids = null;

    // 门锁ID
    private String lid;

    /**
     * 设备报警消息时间
     */

    private long alarmTime;

    private List<String> lockMsgIds = null;
    private List<String> lockAlarmIds = null;
    private EditText etWifiPwd;


    /**
     * 提示框类型-输入WiFi密码
     */

    private final int DIALOG_TYPE_WIFIPWD = 1000;

    /**
     * 提示框类型-输入开锁密码
     */

    private final int DIALOG_TYPE_OPENLOCK = 1001;

    private int dialogType;

    public static final int HANDLER_WAHT_QRCODE = 3;
    public static final int HANDLER_WAHT_OPENLOCK = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electronic_cat_eye);

        EventBus.getDefault().register(ElectronicCatEyeActivity.this);
        sdkApiList=getResources().getStringArray(R.array.t21_dev_support_operation);
        initUI();
        initData();
        permissionApply();
    }



    private final MyHandler myHandler = new MyHandler(ElectronicCatEyeActivity.this);

    private static class MyHandler extends Handler {
        private WeakReference<ElectronicCatEyeActivity> activityWeakReference;

        public MyHandler(ElectronicCatEyeActivity activity) {
            activityWeakReference = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ElectronicCatEyeActivity activity=activityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case HANDLER_WAHT_QRCODE:
                        // 生成二维码
                        activity.tvMessageResponse.append("\n\n" + "请扫描上面二维码");
                        activity.createQRCode();
                        break;
                }
            }
        }
    }


    /**
     * 重要权限申请（录音、相机、定位、读写）
     */

    private void permissionApply() {
        // 发起权限组申请
        mPermissionHelper.requestPermissions(Constants.PERMISSION_GROUP);
    }

    /**
     * 初始化控件
     */

    private void initUI() {
        rlHeadParent = findViewById(R.id.rl_head_parent);
        tvMessageResponse = findViewById(R.id.tv_messageResponse);
        ivQrCodeImage = findViewById(R.id.iv_qrCodeImage);
        lvEquesSdkAPI = findViewById(R.id.lv_equesSdkAPI);
        ivAddDev = findViewById(R.id.iv_add_dev);
//        ivAddDev.setOnClickListener(new SelectAddDevTypeListener());
    }


    /**
     * 初始化数据（权限的申请、数据的获取、操作条目的展示）
     */

    private void initData() {
        icvss = ICVSSUserModule.getInstance(this).getIcvss();
        if (wifiManager == null) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
        wifiSSID = NetworkUtils.getCurrentWifiSSID(wifiManager);
//        if (preference == null) {
//            preference = new UserInfoPreference(this);
//        }
//        preferfsUserName = preference.getString(Constants.PREFERFS_USERNAME, "");
//        preferfsAppkey = preference.getString(Constants.PREFERFS_APPKEY, "");
//        preferfsKeyId = preference.getString(Constants.PREFERFS_KEYID, "");

        if (mPermissionHelper == null) {
            mPermissionHelper = new PermissionHelper(ElectronicCatEyeActivity.this, new PermissionListener());
        }

        if (TextUtils.isEmpty(preferfsUserName)) {
            preferfsUserName = "13760186737";
        }

        if (TextUtils.isEmpty(preferfsAppkey)) {
            preferfsAppkey = Constants.APP_KEY;
        }

        if (TextUtils.isEmpty(preferfsKeyId)) {
            preferfsKeyId = Constants.DEF_KEYID;
        }

//        popView = getLayoutInflater().inflate(R.layout.pop_sel_dev_type, null);

        setLeftListAdapter();

    }


    /**
     * 开锁操作
     */

    public void openLock() {
//        String lockPwd = etLockPwd.getText().toString();

        /*
         * uid:设备ID
         * lid：门锁ID
         * pwd: 开锁密码
         */

        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
            icvss.equesOpenEDevLock(uid, "lockPwd");
        } else {
            icvss.equesOpenLock(uid, lid, "lockPwd");
        }
    }

    /**
     * 创建生成绑定二维码
     */
    public void createQRCode() {

        String wifiPwd = etWifiPwd.getText().toString().trim();
        Bitmap bitmap = icvss.equesCreateQrcode(
                wifiSSID,
                wifiPwd,
                preferfsKeyId,
                preferfsUserName,
                devType,
                210);
        ivQrCodeImage.setVisibility(View.VISIBLE);
        ivQrCodeImage.setImageBitmap(bitmap);
    }

    public class PermissionListener implements PermissionInterface{
        @Override
        public int getPermissionsRequestCode() {
            Log.e(TAG, " permissionRequestCode: " + permissionRequestCode);
            return permissionRequestCode;
        }

        @Override
        public void requestPermissionsSuccess() {
// 请求权限成功回调
            switch (permissionRequestCode) {
                case Constants.PERMISSION_APPLY_TOTLE:
                    // 权限组申请，成功后不做处理
                    Log.e(TAG, " 所有权限已被允许 ");
                    break;
                case Constants.PERMISSIONS_APPLY_RECORD_WRITE:
                    // 读写权限申请，禁止操作数据
                    Log.e(TAG, " 允许读写权限 ");
                    break;
                case Constants.PERMISSIONS_APPLY_LOCATION:
                    // 定位权限申请，获取wifi列表
                    Log.e(TAG, " 允许定位权限 ");
                    wifiSSID = NetworkUtils.getCurrentWifiSSID(wifiManager);
                    addDevOperation();
                    break;
                case Constants.PERMISSIONS_APPLY_CAMERA:
                    // 相机权限申请，扫码绑定设备
                    Log.e(TAG, " 允许相机权限 ");
                    tvMessageResponse.append("\n\n进入扫码界面...");
                    break;
                case Constants.PERMISSIONS_APPLY_AUDIO:
                    // 录音权限申请，远程视频
                    Log.e(TAG, " 允许录音权限 ");
                    Intent intent;
//                    if (reviewType == Constants.REVIEW_TYPE_VIDEO) {
//                        intent = new Intent(ElectronicCatEyeActivity.this, VideoCallActivity.class);
//                        intent.putExtra(Method.ATTR_ROLE, devType);
//                        intent.putExtra(Method.ATTR_BUDDY_UID, uid);
//                        intent.putExtra(Method.ATTR_CALL_HASVIDEO, true); //是否显示视频， true：显示  false： 不显示
//                        intent.putExtra("devDetailsEntity", detailsEntity);
//                    } else {
//                        // 临时测试，后面添加单语音入口
//                        intent = new Intent(ElectronicCatEyeActivity.this, VideoCallActivity.class);
//                        intent.putExtra(Method.ATTR_ROLE, devType);
//                        intent.putExtra(Method.ATTR_BUDDY_UID, uid);
//                        intent.putExtra(Method.ATTR_CALL_HASVIDEO, false); //是否显示视频， true：显示  false： 不显示
//                    }
//                    ElectronicCatEyeActivity.this.startActivity(intent);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void requestPermissionsFail() {
// 请求权限失败回调
            switch (permissionRequestCode) {
                case Constants.PERMISSION_APPLY_TOTLE:
                    // 权限组申请
                    Log.e(TAG, " 总权限被拒绝 ");
                    break;
                case Constants.PERMISSIONS_APPLY_RECORD_WRITE:
                    // 读写权限申请
                    Log.e(TAG, " 读写权限申请失败 ");
//                    showAlertDialog(
//                            Constants.PERMISSIONS_SD_RECORD_WRITE,
//                            getString(R.string.permission_apply_state_unusable),
//                            getString(R.string.permission_apply_sd_statement));
                    break;
                case Constants.PERMISSIONS_APPLY_LOCATION:
                    // 定位权限申请
                    Log.e(TAG, " 定位权限申请失败 ");
//                    showAlertDialog(
//                            Constants.PERMISSIONS_LOCATION,
//                            getString(R.string.permission_apply_state_unusable),
//                            getString(R.string.permission_apply_location_statement));
                    break;
                case Constants.PERMISSIONS_APPLY_CAMERA:
                    // 相机权限申请
                    Log.e(TAG, " 相机权限申请失败 ");
//                    showAlertDialog(
//                            Constants.PERMISSIONS_CAMERA,
//                            getString(R.string.permission_apply_state_unusable),
//                            getString(R.string.permission_apply_camera_statement));
                    break;
                case Constants.PERMISSIONS_APPLY_AUDIO:
                    // 录音权限申请
                    Log.e(TAG, " 录音权限申请失败 ");
//                    showAlertDialog(
//                            Constants.PERMISSIONS_AUDIO,
//                            getString(R.string.permission_apply_state_unusable),
//                            getString(R.string.permission_apply_record_statement));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 添加设备操作
     */

    public void addDevOperation() {
        if (preferfsKeyId == null || preferfsKeyId.equals("")) {
            tvMessageResponse.append("\n\n" + "二维码缺失KeyId，请前往“配置参数界面”设置参数");
            return;
        }
        LinearLayout layout_input_wifipwd = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_input_wifipwd, null);
        TextView tvWifiAccount = layout_input_wifipwd.findViewById(R.id.tv_wifiAccount);

        etWifiPwd = layout_input_wifipwd.findViewById(R.id.et_wifiPwd);
        tvWifiAccount.setText("当前WiFi：" + wifiSSID);

        dialogType = DIALOG_TYPE_WIFIPWD;
        createAlertDialog(layout_input_wifipwd, getString(R.string.add_dev_statement));
    }

    /**
     * 添加设备、开锁提示框
     *
     * @param layout 自定义布局文件
     * @param title  提示框说明
     */

    private void createAlertDialog(View layout, String title) {
        DialogUtil.showCustomDialog(ElectronicCatEyeActivity.this, title, layout, new DialogUtil.DialogClickListener() {
            @Override
            public void confirm() {
                int msgWhat = 0;
                switch (dialogType) {
                    case DIALOG_TYPE_WIFIPWD:

                        /*
                         * 需判断wifi类型（2.4G\5G）,绑定设备不支持5G网络
                         * 此处不做处理
                         */

                        msgWhat = HANDLER_WAHT_QRCODE;
                        break;
                    case DIALOG_TYPE_OPENLOCK:
                        msgWhat = HANDLER_WAHT_OPENLOCK;
                        break;
                    default:
                        break;
                }
                Message msg = new Message();
                msg.what = msgWhat;
                myHandler.sendMessage(msg);
            }

            @Override
            public void cancel() {
            }
        }).show();
    }

    /**
     * 展示列表数据（支持的操作，接口实例）
     */
    public void setLeftListAdapter() {
        if (adapter == null) {
            adapter = new EquesSdkAdapter();
            lvEquesSdkAPI.setAdapter(adapter);
            lvEquesSdkAPI.setOnItemClickListener(new ItemClickEvent());
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    public class EquesSdkAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sdkApiList.length;
        }

        @Override
        public String getItem(int position) {
            return sdkApiList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView tvSdkApiItem;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ElectronicCatEyeActivity.this).inflate(R.layout.adapter_eques_api_item, null);

                holder.tvSdkApiItem = convertView.findViewById(R.id.tv_sdkApiItem);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvSdkApiItem.setText(getItem(position));
            return convertView;
        }
    }


    /**
     * 操作列表相应事件
     * 注：1. 所有E、T系列设备操作之前（删除设备、获取设备列表、报警、访客等列表数据操作除外），必须调用唤醒指令（1-2s发送一次），
     * 设备回复唤醒指令后，停止发送，此时进行设备操作指令下发
     * 2. 视频或语音过程中，需要5s发送一次唤醒指令，防止设备休眠（设备唤醒时长：8s）
     * 3. 唤醒指令接口：equesWakeUp(uid);
     */


    public class ItemClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean bo = NetworkUtils.isNetworkAvailable(ElectronicCatEyeActivity.this);
            if (!bo) {
                Toast.makeText(ElectronicCatEyeActivity.this, " 无网络连接，请尝试连接网络！ ", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean bidError = false;
            boolean uidError = false;
            boolean ipError = false;
            boolean lidError = false;

            /**
             * 用户掉线，拦截除登录以外的所有操作
             */
            if (position > 0 && !judgeUserIsOnline() && position != (sdkApiList.length - 1)) {
                Toast.makeText(ElectronicCatEyeActivity.this, " 设备掉线状态，请重新登录！ ", Toast.LENGTH_SHORT).show();
                return;
            }

//            boolean interceptRemind = interceptRemind(selAddDevType, position);
//            if (interceptRemind) {
//                ELog.showToastShort(TestActivity.this, " 不支持此功能 ");
//                return;
//            }


            switch (position){
                case 0:
                    // 登录操作
                    tvMessageResponse.append("\n\n登录中...");
                    tvMessageResponse.append("\n\n用户名：" + preferfsUserName);
                    icvss.equesLogin(ElectronicCatEyeActivity.this, Constants.DISTRIBUTE_URL, preferfsUserName, preferfsAppkey);
                    break;
                case 1:
                    // 添加设备
                    if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_D) {
                        // 绑定智能门，相机权限判断，扫描设备二维码
                        permissionRequestCode = Constants.PERMISSIONS_APPLY_CAMERA;
                        mPermissionHelper.requestPermissions(Constants.PERMISSIONS_CAMERA);
                    } else {
                        // 定位权限判断，获取wifi列表
                        permissionRequestCode = Constants.PERMISSIONS_APPLY_LOCATION;
                        mPermissionHelper.requestPermissions(Constants.PERMISSIONS_LOCATION);
                    }
                    break;
                case 2:
                    // 添加设备请求确认
                    if (addBdyCode == ResultCode.FAILED) {
                        tvMessageResponse.append("\n\n未收到绑定请求...");
                    } else if (addBdyCode == ResultCode.DUPLICATE_OPERA) {
                        tvMessageResponse.append("\n\n设备已存在...");
                    } else {
                        icvss.equesAckAddResponse(reqId, 1);
                        tvMessageResponse.append("\n\n确认绑定请求...");
                    }
                    break;
                case 3:
                    // 获取设备列表
                    bid = null;
                    uid = null;

                    icvss.equesGetDeviceList();
                    tvMessageResponse.append("\n\n获取设备列表...");
                    break;
                case 4:
                    // 获取设备详情
                    if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E || selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                        tvMessageResponse.append("\n\n获取设备设置详情...");
                        requestType = Constants.NETWORK_REQUEST_SETTINGS_DETAILS;
                        String url = icvss.equesGetDevSettingDetailsUrl(bid, 1567360000000L);
                        Log.e("获取设备详情","url数据"+url);
//                        startRequestData(url);
                        return;
                    }
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            icvss.getDeviceInfo(uid);
                            tvMessageResponse.append("\n\n获取设备详情...");
                        } else {
                            icvss.equesGetDeviceInfo(uid);
                            tvMessageResponse.append("\n\n获取设备详情...");
                        }
                    }
                    break;

                case 5:
                    if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E || selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                        tvMessageResponse.append("\n\n获取E系列设备状态详情...");
                        requestType = Constants.NETWORK_REQUEST_STATE_DETAILS;
                        String url = icvss.equesGetDevStateDetailsUrl(bid);
//                        startRequestData(url);
                        Log.e("修改设备昵称"," "+url);
                        return;
                    }
                    // 修改设备昵称，通用接口
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        icvss.equesSetDeviceNick(bid, "change_dev_nick");
                        tvMessageResponse.append("\n\n修改设备昵称...");
                    }
                    break;

                case 6:
                    // 远程视频
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        // 远程视频，判断录音权限是否开启
                        reviewType = Constants.REVIEW_TYPE_VIDEO;
                        permissionRequestCode = Constants.PERMISSIONS_APPLY_AUDIO;
                        mPermissionHelper.requestPermissions(Constants.PERMISSIONS_AUDIO);
                    }
                    break;

                case 7:
                    // 语音通话
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        // 远程视频，判断录音权限是否开启
                        reviewType = Constants.REVIEW_TYPE_VOICE;
                        permissionRequestCode = Constants.PERMISSIONS_APPLY_AUDIO;
                        mPermissionHelper.requestPermissions(Constants.PERMISSIONS_AUDIO);
                    }
                    break;

                case 8:
                    // 开启人体侦测
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            // 0：关闭  1：开启
                            requestType = Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS;
                            setShadowData("pir_stat", "1", null, null);
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0：关闭  1：开启
                            icvss.setPirEnable(uid, 0);
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            // 0：关闭  1：开启
                            requestType = Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS;
                            setShadowData("alarm_enable", "1", null, null);
                        } else {
                            icvss.equesSetPirEnable(uid, 1);
                        }
                        tvMessageResponse.append("\n\n开启人体侦测...");
                    }
                    break;

                case 9:
                    // 开启门铃灯
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n请查看人体侦测示例...");
                            //updateSettingsParameterData("ring_led", "1");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0：关闭  1：开启
                            icvss.setBellLight(uid, 0);
                            tvMessageResponse.append("\n\n开启门铃灯...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            // 3、5、10、15、20s
                            requestType = Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS;
                            setShadowData("sense_time", "3", null, null);
                            tvMessageResponse.append("\n\n自动抓拍时间设置...");
                        } else {
                            icvss.equesSetDoorbellLight(uid, 1);
                            tvMessageResponse.append("\n\n开启门铃灯...");
                        }
                    }
                    break;

                case 10:
                    // 门铃声设置
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n请查看人体侦测示例...");
                            //updateSettingsParameterData("ring_tone", "1");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 2、3、5、10、15s
                            icvss.setAutoAlarmTime(uid, 3);
                            tvMessageResponse.append("\n\n自动抓拍时间设置...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            // format 0：抓拍  1：录像
                            // capture_num 抓拍模式选填：1、3、5张  录像模式随机值
                            requestType = Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS;
                            setShadowData("format", "0", "capture_num", "1");
                            tvMessageResponse.append("\n\n报警模式设置...");
                        } else {
                            icvss.equesSetDoorbellRingtone(uid, 1);
                            tvMessageResponse.append("\n\n门铃声设置...");
                        }
                    }
                    break;
                case 11:
                    // 获取人体侦测详情
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n生成临时密码，默认密码123456...");
                            icvss.equesCreateTemporaryPwd(uid, "123456");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0：低  1：高
                            icvss.setAlarmSensitivity(uid, 1);
                            tvMessageResponse.append("\n\n侦测灵敏度设置...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            // 0-6取值范围
                            requestType = Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS;
                            setShadowData("ringtone_vol", "2", null, null);
                            tvMessageResponse.append("\n\n通话音量设置...");
                        } else {
                            icvss.equesGetDevicePirInfo(uid);
                            tvMessageResponse.append("\n\n获取人体侦测详情...");
                        }
                    }
                    break;

                case 12:
                    // 设置人体侦测所有数据
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n设置未关门提醒，默认设置不提醒...");
                            String url = icvss.equesSetUnLockRemindUrl(uid, -1);
                            requestType = Constants.NETWORK_REQUEST_CREATE_TEMPORARY_PWD;
//                            startRequestData(url);
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0：1张 1：录像 2：3张 3：5张
                            icvss.setAlarmMode(uid, 0);
                            tvMessageResponse.append("\n\n报警模式设置...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            // 0：自动  1：始终开启  2：始终关闭
                            requestType = Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS;
                            setShadowData("daynight_switch", "0", null, null);
                            tvMessageResponse.append("\n\n红外模式设置...");
                        } else {
                            /**
                             * 设置PIR参数
                             *
                             * @param uid:                   与设备通信时使用的ID
                             * @param senseTime:             侦测时间
                             * @param senseSensitivity：     灵敏度
                             * @param ringtone：             报警铃声
                             * @param volume：               报警音量
                             * @param captureNum：           抓拍张数
                             * @param video_time：           录像时长
                             * @param format：               报警格式(抓拍/录像)
                             */
                            icvss.equesSetDevicePirInfo(uid, 5, 1, 3, 5, 3, 10, 1);
                            tvMessageResponse.append("\n\n设置人体侦测所有数据...");
                        }
                    }
                    break;

                case 13:
                    // 获取报警消息列表
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        // 获取从当前时间到24小时之间的报警记录
                        long start = System.currentTimeMillis() - 1000 * 60 * 60 * 24;
                        long end = System.currentTimeMillis();
                        // 需要注意的是，与服务器的通信，是使用bid；而仅通过服务器转发，与设备端的通信，使用的是uid
                        icvss.equesGetAlarmMessageList(bid, start, end, 10);

                        /*Intent alarmList = new Intent(TestActivity.this, AlarmListActivity.class);
                        alarmList.putExtra("bid", bid);
                        startActivity(alarmList);*/

                        tvMessageResponse.append("\n\n获取报警消息列表...");
                    }
                    break;

                case 14:
                    // 报警缩略图url
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        if (null == pvids || pvids.size() <= 0) {
                            tvMessageResponse.append("\n\n报警数据为空，请触发报警");
                            return;
                        }

                        URL thimbUrl = icvss.equesGetThumbUrl(pvids.get(0), bid);
                        tvMessageResponse.append("\n\n报警第一条缩略图url：" + thimbUrl.toString());
                    }
                    break;

                case 15:
                    // 报警文件url
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        if (null == fids || fids.size() <= 0) {
                            tvMessageResponse.append("\n\n报警数据为空，请触发报警");
                            return;
                        }
                        URL alarmUrl = icvss.equesGetThumbUrl(fids.get(0), bid);
                        tvMessageResponse.append("\n\n报警第一条文件url：" + alarmUrl.toString());
                    }
                    break;

                case 16:
                    // 报警文件下载
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            tvMessageResponse.append("\n\n需要开发者自行处理切片文件");
                            return;
                        }
                        if (null == fids || fids.size() <= 0) {
                            tvMessageResponse.append("\n\n请先获取报警消息列表，如果报警消息为空，请触发报警");
                            return;
                        }

                        if (null == pvids || pvids.size() <= 0) {
                            tvMessageResponse.append("\n\n请先获取报警消息列表，如果报警消息为空，请触发报警");
                            return;
                        }

                        String alarmTimeTemp = getLocalAlarmPicTime(String.valueOf(alarmTime));

//                        Intent alarmDownLoadIntent = new Intent(ElectronicCatEyeActivity.this, AlarmFileDownLoadActivity.class);
//                        alarmDownLoadIntent.putExtra(AlarmFileDownLoadActivity.ALARM_CONTANT_FID, alarmFid);
//                        alarmDownLoadIntent.putExtra(AlarmFileDownLoadActivity.ALARM_CONTANT_PVID, alarmPvid);
//                        alarmDownLoadIntent.putExtra(AlarmFileDownLoadActivity.ALARM_CONTANT_TYPE, alarmType);
//                        alarmDownLoadIntent.putExtra(AlarmFileDownLoadActivity.ALARM_CONTANT_BUDDY_BID, bid);
//                        alarmDownLoadIntent.putExtra(AlarmFileDownLoadActivity.ALARM_CONTANT_ALARMTIME, alarmTimeTemp);
//                        startActivity(alarmDownLoadIntent);
                    }
                    break;


                case 17:
                    // 删除报警消息
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        if (null == aids || aids.size() <= 0) {
                            tvMessageResponse.append("\n\n请先获取报警消息列表，如果报警消息为空，请触发报警");
                            return;
                        }
                        String aidStr = aids.get(0);
                        aids.remove(aidStr);
                        icvss.equesDelAlarmMessage(bid, new String[]{aidStr}, 0);
                        tvMessageResponse.append("\n\n删除第一条报警消息");
                    }
                    break;

                case 18:
                    // 门铃图片url
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        if (null == ringFids || ringFids.size() <= 0) {
                            tvMessageResponse.append("\n\n请先获取门铃记录列表，如果门铃记录消息为空，请触发门铃操作");
                            return;
                        }
                        URL url = icvss.equesGetRingPictureUrl(ringFids.get(0), bid);
                        tvMessageResponse.append("\n\n门铃第一条记录图片url：" + url.toString());
                    }
                    break;

                case 19:
                    // 获取门铃记录
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        long startTime = System.currentTimeMillis() - 1000 * 60 * 60
                                * 24;
                        long endTime = System.currentTimeMillis();
                        icvss.equesGetRingRecordList(bid, startTime, endTime, 10);
                        tvMessageResponse.append("\n\n获取门铃记录");
                    }
                    break;

                case 20:
                    // 删除门铃记录
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        if (null == ringFids || ringFids.size() <= 0) {
                            tvMessageResponse.append("\n\n请先获取门铃记录列表，如果门铃记录消息为空，请触发门铃操作");
                            return;
                        }
                        String fid = ringFids.get(0);
                        ringFids.remove(fid);
                        icvss.equesDelRingRecord(bid, new String[]{fid}, 0);
                        tvMessageResponse.append("\n\n删除门铃记录");
                    }
                    break;

                case 21:
                    // 获取门锁列表
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n猫眼和锁一体设备，不需要单独获取锁列表...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0-4取值范围
                            icvss.setStayWarningTone(uid, 0);
                            tvMessageResponse.append("\n\n逗留警示音设置...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            // 0：关闭  1：开启
                            requestType = Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS;
                            setShadowData("debug", "0", null, null);
                            tvMessageResponse.append("\n\n日志记录开关设置...");
                        } else {
                            // 需要注意的是,与服务器的通信,是使用bid, 而仅通过服务器转发;  与设备端的通信,使用的是uid
                            icvss.equesGetLockList(bid);
                            tvMessageResponse.append("\n\n获取门锁列表...");
                        }
                    }
                    break;

                case 22:
                    // 开锁
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        boolean isExecute;
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            isExecute = true;
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0-6取值范围
                            icvss.setStayWarningToneVol(uid, 0);
                            tvMessageResponse.append("\n\n逗留警示音音量设置...");
                            isExecute = false;
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            icvss.equesUpgradeDevice(uid);
                            tvMessageResponse.append("\n\n通知设备端远程升级...");
                            isExecute = false;
                        } else {
                            if (StringUtils.isBlank(lid)) {
                                lidError = true;
                                isExecute = false;
                            } else {
                                isExecute = true;
                            }
                        }
//                        if (isExecute) {
//                            LinearLayout dialog_input_lockpwd = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_input_lockpwd, null);
//                            TextView tvLockLid = dialog_input_lockpwd.findViewById(R.id.tv_lockLid);
//                            tvLockLid.setText("当前门锁ID：" + lid);
//
//                            etLockPwd = dialog_input_lockpwd.findViewById(R.id.et_lockPwd);
//
//                            dialogType = DIALOG_TYPE_OPENLOCK;
//                            createAlertDialog(dialog_input_lockpwd, getString(R.string.unlocking_statement));
//                            tvMessageResponse.append("\n\n开锁中...");
//                        }
                    }
                    break;

                case 23:
                    // 获取门锁消息
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        // 获取从当前时间到24小时之间的报警记录
                        long startTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24;
                        long endTime = System.currentTimeMillis();
                        int limit = 10;//加载条数,最大100
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n获取门锁消息...");
                            String url = icvss.equesGetLockMsgListUrl(bid, 1567094400000L, 1567180799000L, 1, 10);
                            requestType = Constants.NETWORK_REQUEST_LOCK_MSG_LIST;
//                            startRequestData(url);
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 5、10、15、20s
                            icvss.setStayWarningTime(uid, 5);
                            tvMessageResponse.append("\n\n逗留报警时间设置...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {
                            // 删除设备
//                            removeDev();
                        } else {
                            if (StringUtils.isBlank(lid)) {
                                lidError = true;
                            } else {
                                // 需要注意的是，与服务器的通信，是使用bid；而仅通过服务器转发，与设备端的通信，使用的是uid
                                icvss.equesGetLockMsgList(bid, lid, startTime, endTime, limit);
                                tvMessageResponse.append("\n\n获取门锁消息...");
                            }
                        }
                    }
                    break;

                case 24:
                    // 删除门锁消息
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0-3取值范围
                            icvss.setDoorRingTone(uid, 0);
                            tvMessageResponse.append("\n\n门铃音设置...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {

                            tvMessageResponse.append("\n\n退出登录...");
                        } else {
                            if (null == lockMsgIds || lockMsgIds.size() <= 0) {
                                tvMessageResponse.append("\n\n请先获取门锁消息，如果消息为空，请触发锁开或关");
                                return;
                            }
                            String lockMsgIdStr = lockMsgIds.get(0);
                            int delType = 0; // 为0时, 服务器将根据devid和msgId来删除报警信息, 为1时, 服务器将删除于此用户相关的所有的报警,此时将忽略msgId参数

                            lockMsgIds.remove(lockMsgIdStr);
                            if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                                tvMessageResponse.append("\n\n删除第一条门锁消息...");
                                String url = icvss.equesDelLockMsgUrl();
                                requestType = Constants.NETWORK_REQUEST_DEL_LOCK_MSG;

                                /**
                                 * 此处传参锁消息列表数据，开发者注意！！！
                                 */
//                                delLockMsgOrAlarmParameterData(new ArrayList<LockMsgOrAlarmEntity>());
//                                startRequestData(url);
                            } else {
                                if (StringUtils.isBlank(lid)) {
                                    lidError = true;
                                } else {
                                    icvss.equesDelLockMsg(bid, lid, delType, new String[]{lockMsgIdStr});
                                    tvMessageResponse.append("\n\n删除第一条门锁消息...");
                                }
                            }
                        }
                    }
                    break;

                case 25:
                    // 获取门锁报警
                    if (StringUtils.isBlank(bid)) {
                        bidError = true;
                    } else {
                        // 获取从当前时间到24小时之间的报警记录
                        long startTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24;
                        long endTime = System.currentTimeMillis();
                        int limit = 10;//加载条数,最大100
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n获取门锁报警...");
                            String url = icvss.equesGetLockAlarmListUrl(bid, 1567094400000L, 1567180799000L, 1, 10);
                            requestType = Constants.NETWORK_REQUEST_LOCK_ALARM_LIST;
//                            startRequestData(url);
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0-6取值范围
                            icvss.setBellVol(uid, 5);
                            tvMessageResponse.append("\n\n门铃音量设置...");
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_H5) {

                            tvMessageResponse.append("\n\n配置参数...");
                        } else {
                            if (StringUtils.isBlank(lid)) {
                                lidError = true;
                            } else {
                                // 需要注意的是,与服务器的通信,是使用bid; 而仅通过服务器转发,与设备端的通信,使用的是uid
                                icvss.equesGetLockAlarmList(bid, lid, startTime, endTime, limit);
                                tvMessageResponse.append("\n\n获取门锁报警...");
                            }
                        }
                    }
                    break;

                case 26:
                    if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                        // 0：高  1：低
                        icvss.setCameraResol(uid, 5);
                        tvMessageResponse.append("\n\n图像分辨率设置...");
                        return;
                    }
                    // 删除门锁报警
                    if (StringUtils.isBlank(lockBid)) {
                        bidError = true;
                    } else {
                        if (null == lockAlarmIds || lockAlarmIds.size() <= 0) {
                            tvMessageResponse.append("\n\n请先获取门锁报警，如果消息为空，请触发锁开或关");
                            return;
                        }
                        String lockAlarmIdStr = lockAlarmIds.get(0);
                        int delType = 0; // 为0时, 服务器将根据devid和msgId来删除报警信息, 为1时, 服务器将删除于此用户相关的所有的报警,此时将忽略msgId参数

                        lockAlarmIds.remove(lockAlarmIdStr);
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n删除第一条门锁报警...");
                            String url = icvss.equesDelLockMsgUrl();
                            requestType = Constants.NETWORK_REQUEST_DEL_LOCK_ALARM;

                            /**
                             * 此处传参锁报警消息列表数据，开发者注意！！！
                             */
//                            delLockMsgOrAlarmParameterData(new ArrayList<LockMsgOrAlarmEntity>());
//                            startRequestData(url);
                        } else {
                            if (StringUtils.isBlank(lockBid)) {
                                lidError = true;
                            } else {
                                icvss.euqesDelLockAlarm(lockBid, lid, delType, new String[]{lockAlarmIdStr});
                                tvMessageResponse.append("\n\n删除第一条门锁报警...");
                            }
                        }
                    }
                    break;
                case 27:
                    // 通知设备上传日志
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 10、20、30s
                            icvss.setScreenTimeout(uid, 10);
                            tvMessageResponse.append("\n\n屏幕超时设置...");
                        } else {
                            icvss.equesDeviceLogUpload(uid);
                            tvMessageResponse.append("\n\n通知设备上传日志...");
                        }
                    }
                    break;

                case 28:
                    // 通知设备远程升级
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                            tvMessageResponse.append("\n\n通知E系列设备远程升级...");
                            icvss.equesNotifyDevUpgrade(uid);
                        } else if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 0-15取值范围
                            icvss.setScreenLuminance(uid, 10);
                            tvMessageResponse.append("\n\n屏幕亮度设置...");
                        } else {
                            icvss.equesUpgradeDevice(uid);
                            tvMessageResponse.append("\n\n通知设备远程升级...");
                        }
                    }
                    break;

                case 29:
                    // 远程重启
                    if (StringUtils.isBlank(uid)) {
                        uidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            icvss.equesNotifyDevUpgrade(uid);
                            tvMessageResponse.append("\n\n通知设备远程升级...");
                        } else {
                            // 远程重启
                            remoteRestart();
                        }
                    }
                    break;

                case 30:
                    // 删除设备
                    if (StringUtils.isBlank(uid)) {
                        bidError = true;
                    } else {
                        if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                            // 恢复出厂设置
                            restoreFactory();
                        } else {
                            // 删除设备
                            removeDev();
                        }
                    }
                    break;

                case 31:
                    if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                        // 远程重启
                        remoteRestart();
                    } else {
                        // 用户是否在线
                        String userState = getString(R.string.user_offline);
                        if (judgeUserIsOnline()) {
                            userState = getString(R.string.user_online);
                        } else {
                            userState = getString(R.string.user_offline);
                        }
                        tvMessageResponse.append("\n\n用户在线状态：" + userState);
                    }
                    break;

                case 32:
                    if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                        icvss.equesDelDevice(uid);
                        tvMessageResponse.append("\n\n删除设备...");
                    } else {
                        // 退出登录
                        outLogin();
                    }
                    break;

                case 33:
                    if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_T21) {
                        // 退出登录
                        outLogin();
                    } else {
                        // 获取当前SDK版本
                        String sdkVersion = icvss.getEquesSdkVersion();
                        tvMessageResponse.append("\n\n" + "当前版本：" + sdkVersion);
                    }
                    break;

                case 34:
                    // 配置参数
//                    Intent intent = new Intent(ElectronicCatEyeActivity.this, UserInfoManifestActivity.class);
//                    startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
                    break;
                case 35:
                    if (selAddDevType == Constants.SELECT_ADD_DEV_TYPE_E) {
                        // 恢复出厂设置
                        restoreFactory();
                    } else {
                        /**
                         * 设置门铃音量
                         *
                         * @param uid:                   与设备通信时使用的ID
                         * @param ringtone_vol:          音量值（等级1-7）
                         */
                        icvss.equesSetDoorbellVol(uid, 3);
                        tvMessageResponse.append("\n\n门铃音量设置...");
                    }
                    break;

                case 36:
                    /**
                     * 设置睡眠模式
                     *
                     * @param uid:               与设备通信时使用的ID
                     * @param state:             0：关 1：开
                     */
                    icvss.equesSetDoorbellSleepMode(uid, 0);
                    tvMessageResponse.append("\n\n睡眠模式设置...");
                    break;
                default:

                    break;
            }
        }
    }

    /**
     * 退出登录
     */
    public void outLogin() {
        icvss.equesUserLogOut();
        tvMessageResponse.append("\n\n退出登录...");
    }


    /**
     * 删除设备
     */
    public void removeDev() {
        switch (selAddDevType) {
            case Constants.SELECT_ADD_DEV_TYPE_E:
            case Constants.SELECT_ADD_DEV_TYPE_T21:
                icvss.equesDelDev(uid);
                break;
            default:
                icvss.equesDelDevice(uid);
                break;
        }
        tvMessageResponse.append("\n\n删除设备...");
    }

    /**
     * 恢复出厂设置
     */
    public void restoreFactory() {
        icvss.equesNotifyDevRestore(uid);
        tvMessageResponse.append("\n\n恢复出厂设置...");
    }

    /**
     * 远程重启
     */
    public void remoteRestart() {
        switch (selAddDevType) {
            case Constants.SELECT_ADD_DEV_TYPE_E:
            case Constants.SELECT_ADD_DEV_TYPE_T21:
                icvss.equesRemoteRestartDev(uid);
                break;
            default:
                icvss.equesRestartDevice(uid);
                break;
        }
        tvMessageResponse.append("\n\n远程重启...");
    }


    /**
     * 报警时间转换为20161028160312
     *
     * @param time
     * @return
     */

    public String getLocalAlarmPicTime(String time) {
        Date d = new Date(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(d);
    }



    /**
     * 进行影子设置（相关设备设置统一设置入口）
     *
     * @param paramStr   // 需要设置的参数
     * @param paramValue // 该参数设置的数值
     */
    public void setShadowData(String paramStr, String paramValue, String paramStrT, String paramValueT) {
        String url = icvss.equesUpdateDevSettingsUrl(uid);
        updateSettingsParameterData(paramStr, paramValue, paramStrT, paramValueT);
//        startRequestData(url);
    }



    // 设备详情数据
    private DevDetailsEntity detailsEntity;
    private JSONObject settingsParams;
    private String settingsParamsStr;


    /**
     * 获取要设置的参数
     *
     * @param paramStr
     * @param paramValue
     * @param paramStrT
     * @param paramValueT
     */
    public void updateSettingsParameterData(String paramStr, String paramValue, String paramStrT, String paramValueT) {
        settingsParamsStr = null;
        settingsParams = new JSONObject();
        try {
            settingsParams.put(paramStr, paramValue);
            if (StringUtils.isNotEmpty(paramStrT) && StringUtils.isNotEmpty(paramValueT)) {
                settingsParams.put(paramStrT, paramValueT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        settingsParamsStr = settingsParams.toString();
    }

    /**
     * 请求数据
     */
    private final String TAG=ElectronicCatEyeActivity.this.getClass().getSimpleName();
//    public void startRequestData(final String url) {
//        Log.e(TAG, " startRequestData() url: " + url);
//        Log.e(TAG, " requestType: " + requestType);
//        Executors.newSingleThreadExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                switch (requestType) {
//                    case Constants.NETWORK_REQUEST_SETTINGS_DETAILS:
//                    case Constants.NETWORK_REQUEST_STATE_DETAILS:
//                    case Constants.NETWORK_REQUEST_CREATE_TEMPORARY_PWD:
//                    case Constants.NETWORK_REQUEST_LOCK_MSG_LIST:
//                    case Constants.NETWORK_REQUEST_LOCK_ALARM_LIST:
//                        OkHttpUtils
//                                .get()
//                                .url(url)
//                                .build()
//                                .execute(new MyStringCallback(requestType));
//                        break;
//                    case Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS:
//                    case Constants.NETWORK_REQUEST_DEL_LOCK_MSG:
//                    case Constants.NETWORK_REQUEST_DEL_LOCK_ALARM:
//                        if (settingsParamsStr == null) {
//                            tvMessageResponse.append("\n\nPOST 数据为空，请注意数据获取");
//                            return;
//                        }
//                        OkHttpUtils
//                                .postString()
//                                .url(url)
//                                .content(settingsParamsStr)
//                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                                .build()
//                                .execute(new MyStringCallback(requestType));
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//    }
//
//    private class MyStringCallback extends StringCallback {
//        private int requestType;
//
//        public MyStringCallback(int requestType) {
//            this.requestType = requestType;
//        }
//
//        @Override
//        public void onError(Call call, Exception e, int id) {
//            Log.e(TAG, " 请求失败...e: " + e.toString());
//        }
//
//        @Override
//        public void onResponse(String response, int id) {
//            if (!TextUtils.isEmpty(response)) {
//                //Log.e(TAG, " 请求数据...response: " + response);
//                switch (requestType) {
//                    case Constants.NETWORK_REQUEST_SETTINGS_DETAILS:
//                        tvMessageResponse.append("\n\n获取E系列设备设置详情：" + response);
//                        try {
//                            saveDevDetailsData(new JSONObject(response));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    case Constants.NETWORK_REQUEST_STATE_DETAILS:
//                        tvMessageResponse.append("\n\n获取E系列设备状态详情：" + response);
//                        try {
//                            saveDevDetailsData(new JSONObject(response));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    case Constants.NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS:
//                        // 更新设置数据结果返回
//                        tvMessageResponse.append("\n\n更新E系列设备设置结果返回：" + response);
//                        break;
//                    case Constants.NETWORK_REQUEST_CREATE_TEMPORARY_PWD:
//                        tvMessageResponse.append("\n\nE系列设备临时密码生成返回：" + response);
//                        break;
//                    default:
//                        break;
//                }
//            } else {
//                Log.e(TAG, " 请求数据为空... ");
//            }
//        }
//    }

    /**
     * 判断当前用户是否在线
     */

    public boolean judgeUserIsOnline() {
        boolean userIsOnline = false;
        if (icvss != null) {
            userIsOnline = icvss.equesIsLogin();
        }
        return userIsOnline;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ELog.v(TAG, " onDestroy start ");
        icvss.equesUserLogOut();
        ICVSSUserModule.getInstance(this).closeIcvss();
        icvss = null;
        myHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(ElectronicCatEyeActivity.this);
    }

    @Override
    public void onDisconnect(int i) {

    }

    @Override
    public void onPingPong(int i) {

    }

    @Override
    public void onMeaasgeResponse(JSONObject jsonObject) {
        Log.e("猫眼返回的数据",jsonObject.toString());
    }


    /**
     * 获取报警
     */
    private Handler alarmHanlder;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventBusEntity entity) {
        if (entity.getAction().equals(EventBusEntity.GET_ALARM_LIST)) {
            long startTime = entity.getStartTime();
            long endTime = entity.getEndTime();
            String bidTemp = entity.getBid();
            alarmHanlder = entity.getmHandler();
            // 获取从当前时间到24小时之间的报警记录
            icvss.equesGetAlarmMessageList(bidTemp, startTime, endTime, 10);
        }
    }
}
