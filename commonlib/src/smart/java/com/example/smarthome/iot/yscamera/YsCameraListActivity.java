//package com.example.smarthome.iot.yscamera;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSON;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.example.commonlib.base.BaseActivity;
//import com.example.commonlib.base.uiutils.LogUtils;
//import com.example.commonlib.base.uiutils.SPUtils;
//import com.example.commonlib.base.uiutils.SpConstant;
//import com.example.commonlib.base.uiutils.StringUtils;
//import com.example.commonlib.base.uiutils.ToastUtil;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.adapter.YsCameraListAdapter;
//import com.example.smarthome.iot.entry.CommonResp;
//import com.example.smarthome.iot.net.Constant;
//import com.lzy.okgo.OkGo;
//import com.lzy.okgo.convert.StringConvert;
//import com.lzy.okgo.model.Response;
//import com.lzy.okrx2.adapter.ObservableResponse;
//import com.videogo.constant.IntentConsts;
//import com.videogo.errorlayer.ErrorInfo;
//import com.videogo.exception.BaseException;
//import com.videogo.exception.ErrorCode;
//import com.videogo.openapi.EZOpenSDK;
//import com.videogo.openapi.bean.EZDeviceInfo;
//import com.videogo.util.ConnectionDetector;
//import com.videogo.util.LogUtil;
//import com.xhwl.commonlib.base.BaseActivity;
//import com.zhy.autolayout.AutoLinearLayout;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//
//public class YsCameraListActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle;
//    private TextView mTopBtn;
//    private View mTitleLine;
//    private RecyclerView cameraRv;
//    private YsCameraListAdapter ysCameraListAdapter;
//    private List<EZDeviceInfo> cameraList = new ArrayList<>();
//    private int pageSize = 20;
//    private int pageIndex = 0;
//    private SwipeRefreshLayout mSwipeRefreshLayout;
//    private String userId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ys_camera_list);
//        initView();
////        initDate();
//    }
//
//    private void initView() {
//        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
//        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
//        mTopBack.setOnClickListener(this);
//        mTopTitle = (TextView) findViewById(R.id.top_title);
//        mTopBtn = (TextView) findViewById(R.id.top_btn);
//        mTopBtn.setOnClickListener(this);
//        mTitleLine = (View) findViewById(R.id.title_line);
//
//        mTopTitle.setText("摄像头列表");
//        cameraRv = findViewById(R.id.camera_rv);
//        cameraRv.setLayoutManager(new GridLayoutManager(this, 1));
//        ysCameraListAdapter = new YsCameraListAdapter(cameraList);
//        ysCameraListAdapter.bindToRecyclerView(cameraRv);
//        ysCameraListAdapter.setOnItemChildClickListener(this);
//
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
//        initRefreshLayout();
//        mSwipeRefreshLayout.setRefreshing(true);
////        getYsCameraList();
//
//        ysCameraListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                LogUtils.e("setOnLoadMoreListener", pageIndex + "------------");
////                pageIndex++;
////                getList(pageIndex, pageSize);
//                loadMore();
//            }
//        }, cameraRv);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        getList(pageIndex, pageSize);
//        refresh();
//    }
//
//    private void getYsCameraList() {
//
//        new GetYsCameraListTask().execute();
//    }
//
//    @Override
//    public void onClick(View v) {
//        int viewId = v.getId();
//        if (viewId == R.id.top_back) {
//            finish();
//        } else if (viewId == R.id.top_btn) {
//            // 管理
//
//        }
//    }
//
//
//    private class GetYsCameraListTask extends AsyncTask<Void, Void, List<EZDeviceInfo>> {
//        public GetYsCameraListTask() {
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            LogUtils.e("onPreExecute", "-----------------------");
//        }
//
//        @Override
//        protected List<EZDeviceInfo> doInBackground(Void... voids) {
//            try {
//                cameraList = EZOpenSDK.getInstance().getDeviceList(0, 20);
//
//                LogUtils.e("onLoadMoreRequested", cameraList.size() + "================");
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
//            LogUtils.e("onPostExecute", result.size() + "===============");
//            ysCameraListAdapter.setNewData(result);
//        }
//
//        protected void onError() {
//
//        }
//    }
//
//    @Override
//    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//        int viewId = view.getId();
//        if (viewId == R.id.item_play_btn) {
//            LogUtils.e("onItemChildClick", "----------------------");
//            Intent mIntent = new Intent();
//            mIntent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, ((YsCameraListAdapter) adapter).getData().get(position));//cameraList.get(position)
//            mIntent.setClass(YsCameraListActivity.this, YsCameraActivity.class);
//            startActivity(mIntent);
//        } else if (viewId == R.id.tab_setdevice_btn) {
////            mEZDeviceInfo = cameraList.get(position);
////            new DeleteDeviceTask().execute();
//            deleteYsDevice(cameraList.get(position).getDeviceSerial());
//        }
//    }
//
//
//    private void deleteYsDevice(String devSerial) {
//        //删除摄像头
//        if(!StringUtils.isEmpty(devSerial)){
//            OkGo.<String>get(Constant.HOST+Constant.Ezviz_DelDev)
//                    .params("deviceSerial", devSerial)
//                    .params("userId", userId)
//                    .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
//                    .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
//                    .subscribeOn(Schedulers.io())
//                    .doOnSubscribe(new Consumer<Disposable>() {
//                        @Override
//                        public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
////                        showLoading();
//                            LogUtils.e("accept",disposable.isDisposed()+"===");
//                        }
//                    })
//                    .map(new Function<Response<String>, CommonResp>() {
//                        @Override
//                        public CommonResp apply(Response<String> stringResponse) throws Exception {
//                            LogUtils.e("apply","===="+stringResponse.body());
//                            CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
//                            LogUtils.e("resp",resp.getState()+"=====");
//                            return resp;
//                        }
//                    })
//                    .observeOn(AndroidSchedulers.mainThread())//
//                    .subscribe(new Observer<CommonResp>() {
//                        @Override
//                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
////                        addDisposable(d);
//                            LogUtils.e("onSubscribe","========---====");
//                        }
//
//                        @Override
//                        public void onNext(CommonResp resp) {
//                            if(resp !=null){
//                                if(resp.getErrorCode().equalsIgnoreCase("200")){
////                                    sendMessage(EZVIZ_DELETE_DEVICE_SUCCESS);
//                                    refresh();
//                                    EventBus.getDefault().post("YsCameraRefresh");
//                                } else {
////                                    sendMessage(EZVIZ_DELETE_DEVICE_FAIL);
//                                }
//                            } else {
////                                sendMessage(EZVIZ_DELETE_DEVICE_FAIL);
//                            }
//
//                        }
//
//
//                        @Override
//                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                            e.printStackTrace();
////                            sendMessage(EZVIZ_DELETE_DEVICE_FAIL);
//                        }
//
//                        @Override
//                        public void onComplete() {
////                            loadingDialog.dismiss();
//                            LogUtils.e("onComplete","============");
//                        }
//                    });
//        }
//    }
//
//    EZDeviceInfo mEZDeviceInfo;
//
//    private class DeleteDeviceTask extends AsyncTask<Void, Void, Boolean> {
//
//        private int mErrorCode = 0;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            if (!ConnectionDetector.isNetworkAvailable(YsCameraListActivity.this)) {
//                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
//                return false;
//            }
//
//            try {
//                EZOpenSDK.getInstance().deleteDevice(mEZDeviceInfo.getDeviceSerial());
//                return true;
//            } catch (BaseException e) {
//                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
//                mErrorCode = errorInfo.errorCode;
//                LogUtils.e("YsCameraListActivity", errorInfo.toString());
//
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//            if (result) {
//                ToastUtil.show(YsCameraListActivity.this, "删除成功");
//                getYsCameraList();
//            } else {
//                switch (mErrorCode) {
//                    case ErrorCode.ERROR_WEB_SESSION_ERROR:
////                        ActivityUtils.handleSessionException(EZDeviceSettingActivity.this);
//                        break;
//                    case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
////                        ActivityUtils.handleSessionException(EZDeviceSettingActivity.this);
//                        break;
//                    case ErrorCode.ERROR_WEB_NET_EXCEPTION:
//                        ToastUtil.show(YsCameraListActivity.this, "删除失败，请检查您的网络");
////                        showToast(R.string.alarm_message_del_fail_network_exception);
//                        break;
//                    case ErrorCode.ERROR_WEB_DEVICE_VALICATECODE_ERROR:
//                        ToastUtil.show(YsCameraListActivity.this, "验证码错误，请核对");
////                        showToast(R.string.verify_code_error);
//                    default:
//                        ToastUtil.show(YsCameraListActivity.this, "删除失败");
////                        showToast(R.string.alarm_message_del_fail_txt, mErrorCode);
//                        break;
//                }
//            }
//        }
//    }
//
//
//    private void getList(int index, int size) {
//        Observable.create(new ObservableOnSubscribe<List<EZDeviceInfo>>() {
//            @Override
//            public void subscribe(ObservableEmitter<List<EZDeviceInfo>> emitter) throws Exception {
//                cameraList = EZOpenSDK.getInstance().getDeviceList(index, size);
//                LogUtils.e("subscribe", pageIndex + "------------" + cameraList.size());
//                emitter.onNext(cameraList);
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(new Consumer<List<EZDeviceInfo>>() {
////                    @Override
////                    public void accept(List<EZDeviceInfo> list) throws Exception {
////
////                        LogUtils.e("YsCameralist", list.size() + "----------");
////                        if (pageIndex == 0) {
////                            mSwipeRefreshLayout.setRefreshing(false);
////                            ysCameraListAdapter.setNewData(list);
////                            ysCameraListAdapter.disableLoadMoreIfNotFullPage();
////                        } else {
////                            ysCameraListAdapter.addData(list);
////                        }
////                    }
////
////                })
//                .subscribe(new Observer<List<EZDeviceInfo>>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
////                        addDisposable(d);
//                        LogUtils.e("onSubscribe", "========---====");
//                    }
//
//                    @Override
//                    public void onNext(List<EZDeviceInfo> list) {
//                        LogUtils.e("onNext:", "------------------");//
//                        LogUtils.e("YsCameralist", list.size() + "----------");
//                        if (pageIndex == 0) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            setData(true, list);
////                            ysCameraListAdapter.setNewData(list);
//                            ysCameraListAdapter.disableLoadMoreIfNotFullPage();
//                        } else {
//                            boolean isRefresh = pageIndex == 1;
//                            setData(isRefresh, list);
////                            ysCameraListAdapter.addData(list);
//                        }
//                    }
//
//
//                    @Override
//                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        e.printStackTrace();
//                        ToastUtil.show(YsCameraListActivity.this, "请求失败");
//                        if (pageIndex == 0) {
//                            ysCameraListAdapter.setEnableLoadMore(true);
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        } else {
//                            ysCameraListAdapter.loadMoreFail();
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
////                        dismissLoading();
//                        LogUtils.e("onComplete", "============");
//                    }
//
//                })
//        ;
//    }
//
//    private void initRefreshLayout() {
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh();
//            }
//        });
//    }
//
//    private void refresh() {
//        pageIndex = 0;
//        ysCameraListAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
//        getList(pageIndex, pageSize);
//    }
//
//    private void loadMore() {
//        getList(pageIndex, pageSize);
//    }
//
//    private void setData(boolean isRefresh, List data) {
//        pageIndex++;
//        final int size = data == null ? 0 : data.size();
//        if (isRefresh) {
//            ysCameraListAdapter.setNewData(data);
//        } else {
//            if (size > 0) {
//                ysCameraListAdapter.addData(data);
//            }
//        }
//        if (size < pageSize) {
//            //第一页如果不够一页就不显示没有更多数据布局
//            ysCameraListAdapter.loadMoreEnd(isRefresh);
////            ToastUtil.show(YsCameraListActivity.this, "no more data");
//        } else {
//            ysCameraListAdapter.loadMoreComplete();
//        }
//    }
//}
