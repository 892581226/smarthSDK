//package com.example.smarthome.iot.yscamera;
//
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.example.commonlib.base.BaseActivity;
//import com.example.commonlib.base.uiutils.LogUtils;
//import com.example.commonlib.base.uiutils.SPUtils;
//import com.example.commonlib.base.uiutils.StringUtils;
//import com.example.commonlib.base.uiutils.ToastUtil;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.adapter.YsCameraListAdapter;
//import com.videogo.errorlayer.ErrorInfo;
//import com.videogo.exception.BaseException;
//import com.videogo.openapi.EZOpenSDK;
//import com.videogo.openapi.bean.EZDeviceInfo;
//import com.videogo.openapi.bean.EZProbeDeviceInfoResult;
//import com.videogo.util.ConnectionDetector;
//import com.videogo.util.LogUtil;
//import com.xhwl.commonlib.base.BaseActivity;
//import com.zhy.autolayout.AutoLinearLayout;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class YsAddCameraActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener{
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle;
//    private TextView mTopBtn;
//    private View mTitleLine;
//    private EditText add_camera_no,add_camera_input_verify,add_camera_input_type;
//    private Button addBtn,searchBtn;
//    private EZProbeDeviceInfoResult mEZProbeDeviceInfo = null;
//    private static final String TAG = "YsAddCameraActivity";
//    private List<EZDeviceInfo> cameraList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ys_camera_add_input);
//        initView();
////        initDate();
//    }
//
//    private void initView() {
//        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
//        mTopBack.setOnClickListener(this);
//        mTopTitle = (TextView) findViewById(R.id.top_title);
//        mTopBtn = (TextView) findViewById(R.id.top_btn);
//        mTopBtn.setOnClickListener(this);
//        mTitleLine = (View) findViewById(R.id.title_line);
//
//        mMsgHandler = new MessageHandler();
//        mTopTitle.setText("新增摄像头");
//        add_camera_no = findViewById(R.id.add_camera_no);
//        add_camera_input_verify = findViewById(R.id.add_camera_input_verify);
//        add_camera_input_type = findViewById(R.id.add_camera_input_type);
//        addBtn = findViewById(R.id.addBtn);
//        searchBtn = findViewById(R.id.searchBtn);
//        searchBtn.setOnClickListener(this);
//        addBtn.setOnClickListener(this);
////        getYsCameraInfo();
//    }
//
//    private void getYsCameraInfo(String devSerial,String devType) {
//
////        new GetYsCameraListTask().execute();
//        new Thread() {
//            public void run() {
//
//                mEZProbeDeviceInfo = EZOpenSDK.getInstance().probeDeviceInfo(devSerial,devType);//"137234674","CS-C2C-1A1WFR"
//                if (mEZProbeDeviceInfo.getBaseException() == null){
//                    // TODO: 2018/6/25 添加设备
//                    sendMessage(MSG_QUERY_CAMERA_SUCCESS);
//                }else{
//                    switch (mEZProbeDeviceInfo.getBaseException().getErrorCode()){
//
//                        case 120023:
//                            // TODO: 2018/6/25  设备不在线，未被用户添加 （这里需要网络配置）
//                        case 120002:
//                            // TODO: 2018/6/25  设备不存在，未被用户添加 （这里需要网络配置）
//                        case 120029:
//                            // TODO: 2018/6/25  设备不在线，已经被自己添加 (这里需要网络配置)
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    LogUtil.infoLog(TAG, "probeDeviceInfo fail :" + mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                                    sendMessage(MSG_QUERY_CAMERA_FAIL, mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                                }
//                            });
//                            break;
//
//                        case 120020:
//                            // TODO: 2018/6/25 设备在线，已经被自己添加 (给出提示)
//                            sendMessage(MSG_QUERY_CAMERA_FAIL, mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                            break;
//
//                        case 120022:
//                            // TODO: 2018/6/25  设备在线，已经被别的用户添加 (给出提示)
//                        case 120024:
//                            // TODO: 2018/6/25  设备不在线，已经被别的用户添加 (给出提示)
//                            sendMessage(MSG_QUERY_CAMERA_FAIL, mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                            break;
//
//                        default:
//                            // TODO: 2018/6/25 请求异常
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showToast("Request failed = "
//                                            + mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                                }
//                            });
//
//                            break;
//                    }
//                }
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        dismissWaitDialog();
////                    }
////                });
//            }
//        }.start();
//    }
//
//    @Override
//    public void onClick(View v) {
//        int viewId = v.getId();
//        if(viewId == R.id.top_back){
//            finish();
//        } else if(viewId == R.id.top_btn){
//            // 管理
//
//        } else if(viewId == R.id.addBtn){
//            if(!StringUtils.isEmpty(add_camera_no.getText().toString().trim()) && !StringUtils.isEmpty(add_camera_input_verify.getText().toString().trim())){
//                addQueryCameraAddVerifyCode(add_camera_no.getText().toString().trim(),add_camera_input_verify.getText().toString().trim());
//            } else {
//                ToastUtil.show(YsAddCameraActivity.this,"设备序列号或验证码为空");
//            }
////            addQueryCameraAddVerifyCode();
//        } else if(viewId == R.id.searchBtn){
//            if(!StringUtils.isEmpty(add_camera_no.getText().toString().trim()) && !StringUtils.isEmpty(add_camera_input_type.getText().toString().trim())){
//                getYsCameraInfo(add_camera_no.getText().toString().trim(),add_camera_input_type.getText().toString().trim());
//            } else {
//                ToastUtil.show(YsAddCameraActivity.this,"设备序列号或类型为空");
//            }
//        }
//    }
//
//
//
//
//    private class GetYsCameraListTask extends AsyncTask<Void, Void, List<EZDeviceInfo>>{
//        public GetYsCameraListTask(){
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            LogUtils.e("onPreExecute","-----------------------");
//        }
//
//        @Override
//        protected List<EZDeviceInfo> doInBackground(Void... voids) {
//            try {
//                cameraList = EZOpenSDK.getInstance().getDeviceList(0, 20);
//
//                LogUtils.e("onLoadMoreRequested",cameraList.size()+"================");
//
//            } catch (BaseException e) {
//                e.printStackTrace();
//
//            }
////            ysCameraListAdapter.setEnableLoadMore(true);
////            ysCameraListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
////                @Override
////                public void onLoadMoreRequested() {
////                    try {
////                        cameraList = EZOpenSDK.getInstance().getDeviceList(0, 20);
////
////                        LogUtils.e("onLoadMoreRequested",cameraList.size()+"================");
////
////                    } catch (BaseException e) {
////                        e.printStackTrace();
////
////                    }
////                }
////            },cameraRv);
//            return cameraList;
//        }
//
//        @Override
//        protected void onPostExecute(List<EZDeviceInfo> result) {
//            super.onPostExecute(result);
//            LogUtils.e("onPostExecute",result.size()+"===============");
//
//        }
//
//        protected void onError(){
//
//        }
//    }
//
//    @Override
//    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//        int viewId = view.getId();
//        if(viewId == R.id.item_play_btn){
//            LogUtils.e("onItemChildClick","----------------------");
//            Intent mIntent = new Intent();
//            mIntent.setClass(YsAddCameraActivity.this, YsCameraActivity.class);
//            startActivity(mIntent);
//        }
//    }
//
//    private MessageHandler mMsgHandler = null;
//    private void sendMessage(int msgCode) {
//        if (mMsgHandler != null) {
//            Message msg = Message.obtain();
//            msg.what = msgCode;
//            mMsgHandler.sendMessage(msg);
//        } else {
//            LogUtil.errorLog(TAG, "sendMessage-> mMsgHandler object is null");
//        }
//    }
//
//    private void sendMessage(int msgCode, int errorCode) {
//        if (mMsgHandler != null) {
//            Message msg = Message.obtain();
//            msg.what = msgCode;
//            msg.arg1 = errorCode;
//            mMsgHandler.sendMessage(msg);
//        } else {
//            LogUtil.errorLog(TAG, "sendMessage-> mMsgHandler object is null");
//        }
//    }
//
//
//    protected static final int MSG_QUERY_CAMERA_FAIL = 0;
//
//    protected static final int MSG_QUERY_CAMERA_SUCCESS = 1;
//
//    private static final int MSG_LOCAL_VALIDATE_SERIALNO_FAIL = 8;
//
//    private static final int MSG_LOCAL_VALIDATE_CAMERA_PSW_FAIL = 9;
//
//    private static final int MSG_ADD_CAMERA_SUCCESS = 10;
//
//    private static final int MSG_ADD_CAMERA_FAIL = 12;
//    class MessageHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_LOCAL_VALIDATE_SERIALNO_FAIL:
//                    LogUtils.e(TAG,"MSG_LOCAL_VALIDATE_SERIALNO_FAIL-------");
////                    handleLocalValidateSerialNoFail(msg.arg1);
//                    break;
//                case MSG_LOCAL_VALIDATE_CAMERA_PSW_FAIL:
////                    handleLocalValidateCameraPswFail(msg.arg1);
//                    LogUtils.e(TAG,"MSG_LOCAL_VALIDATE_CAMERA_PSW_FAIL-------");
//                    break;
//                case MSG_QUERY_CAMERA_SUCCESS:
////                    handleQueryCameraSuccess();
//                    LogUtils.e(TAG,"MSG_QUERY_CAMERA_SUCCESS-------");
//                    ToastUtil.show(YsAddCameraActivity.this,"查询成功，该设备可增加");
//                    addBtn.setVisibility(View.VISIBLE);
//                    break;
//                case MSG_QUERY_CAMERA_FAIL:
////                    handleQueryCameraFail(msg.arg1);
//                    LogUtils.e(TAG,"MSG_QUERY_CAMERA_FAIL-------");
//                    ToastUtil.show(YsAddCameraActivity.this,"该设备不能新增");
//                    break;
//                case MSG_ADD_CAMERA_SUCCESS:
////                    handleAddCameraSuccess();
//                    LogUtils.e(TAG,"MSG_ADD_CAMERA_SUCCESS-------");
//                    ToastUtil.show(YsAddCameraActivity.this,"增加成功");
//                    SPUtils.put(YsAddCameraActivity.this,"ysCamera"+add_camera_no.getText().toString().trim(),add_camera_input_verify.getText().toString().trim());
//                    break;
//                case MSG_ADD_CAMERA_FAIL:
////                    handleAddCameraFail(msg.arg1);
//                    LogUtils.e(TAG,"MSG_ADD_CAMERA_FAIL-------");
//                    ToastUtil.show(YsAddCameraActivity.this,"增加失败");
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//
//    private void addQueryCameraAddVerifyCode(String devSerial,String verCode) {
//
//        // Local network detection
//        if (!ConnectionDetector.isNetworkAvailable(YsAddCameraActivity.this)) {
//            ToastUtil.show(YsAddCameraActivity.this,"添加失败，请检查您的网络");
//            return;
//        }
//
//        new Thread() {
//            public void run() {
//
//                try {
//                    boolean result = EZOpenSDK.getInstance().addDevice(devSerial,verCode);//"137234674", "KRCQDY"
//
//                    LogUtils.e(TAG,result+"----------------");
//                    /***********If necessary, the developer needs to save this code***********/
//                    // 添加成功过后
//                    sendMessage(MSG_ADD_CAMERA_SUCCESS);
//                } catch (BaseException e) {
//                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
//                    LogUtil.debugLog(TAG, errorInfo.toString());
//
//                    sendMessage(MSG_ADD_CAMERA_FAIL, errorInfo.errorCode);
//                    LogUtil.errorLog(TAG, "add camera fail");
//                }
//
//            }
//        }.start();
//    }
//}
