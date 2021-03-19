package com.example.smarthome.iot.impl;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceAppendFragment;
import com.example.smarthome.iot.DeviceSearchActivity;
import com.example.smarthome.iot.adapter.DeviceCategoryListAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;
import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author: glq
 * date: 2019/4/10 11:03
 * description: 添加设备
 * update: 2019/4/10
 * version:
 */
public class DeviceAppendActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static int mPosition;
    private LinearLayout mTopBackNew;
    private TextView mTopSecondTitleNew;
    /**
     * 请输入需要添加的设备内容
     */
    private TextView mDeviceSearchEditor;
    private ListView mDeviceAppendListView;
    private FrameLayout mDeviceAppendFragmentContainer;
    private DeviceCategoryListAdapter listAdapter;
    private List<DeviceListVo.ProductCollectionBean> devList = new ArrayList<>();
    private DeviceAppendFragment appendFragment;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_device_append);
        addDestoryActivity(this, "DeviceAppendActivity");
        Intent intent=getIntent();
        if (intent!=null){
            deviceInfoBean=intent.getParcelableExtra("deviceItem");
//            Log.e("无线网关数据",deviceInfoBean.toString());
            SPUtils.put(DeviceAppendActivity.this,"scan_gateway",deviceInfoBean.getId());
        }
        initView();
        initDate();
    }

    private void initDate() {
        mTopSecondTitleNew.setText("添加设备");
        listAdapter = new DeviceCategoryListAdapter(this, devList);
        mDeviceAppendListView.setAdapter(listAdapter);
        mDeviceAppendListView.setOnItemClickListener(this);
        loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);
        loadingDialog.show();
        getAllDevice(); //设备列表接口  这个接口是在增加设备时，获取所有厂家不同种类设备的
    }

    private void getAllDevice() {
//        NetWorkWrapper.smartDeviceList(new HttpHandler<DeviceListVo>() {
//            @Override
//            public void onSuccess(ServerTip serverTip, DeviceListVo deviceListVo) {
//                loadingDialog.dismiss();
//                devList.clear();
//                devList.addAll(deviceListVo.getProduct_collection());
//                //创建MyFragment对象
//                appendFragment = new DeviceAppendFragment();
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.device_append_fragment_container, appendFragment);
//                //通过bundle传值给MyFragment
//                Bundle bundle = new Bundle();
//                bundle.putParcelable(DeviceAppendFragment.TAG, devList.get(mPosition));
//                appendFragment.setArguments(bundle);
//                fragmentTransaction.commit();
//                listAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(ServerTip serverTip) {
//                super.onFailure(serverTip);
//                loadingDialog.dismiss();
//                ToastUtil.show("请求失败");
//            }
//        });

        OkGo.<String>get(Constant.HOST + Constant.Get_Device_list)
                .params("deviceType",deviceInfoBean.getDeviceType())
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
                .map(new Function<Response<String>, DeviceListVo>() {
                    @Override
                    public DeviceListVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        DeviceListVo deviceListVo = JSON.parseObject(resp.getResult(), DeviceListVo.class);

                        if (deviceListVo != null) {
                            LogUtils.e("apply", "====");
                        }
                        return deviceListVo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<DeviceListVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(DeviceListVo deviceListVo) {
                        if (deviceListVo.getProduct_collection()!=null&&deviceListVo.getProduct_collection().size()>0){
                            devList.clear();
                            devList.addAll(deviceListVo.getProduct_collection());
                            //创建MyFragment对象
                            appendFragment = new DeviceAppendFragment();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.device_append_fragment_container, appendFragment);
                            //通过bundle传值给MyFragment
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(DeviceAppendFragment.TAG, devList.get(mPosition));
                            bundle.putString(DeviceAppendFragment.ID,deviceInfoBean.getId());
                            appendFragment.setArguments(bundle);
                            fragmentTransaction. commitAllowingStateLoss();
                            listAdapter.notifyDataSetChanged();
                            LogUtils.e("onNext:", "------------------");
                        }else {
                            LogUtils.e("getAllDevice onNext:", "------------------");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(DeviceAppendActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    private void initView() {
        mTopBackNew = (LinearLayout) findViewById(R.id.top_back_new);
        mTopBackNew.setOnClickListener(this);
        mTopSecondTitleNew = (TextView) findViewById(R.id.top_second_title_new);
        mDeviceSearchEditor = findViewById(R.id.device_search_text);
        mDeviceAppendListView = (ListView) findViewById(R.id.device_append_list_view);
        mDeviceAppendFragmentContainer = (FrameLayout) findViewById(R.id.device_append_fragment_container);
        mDeviceSearchEditor.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back_new) {
            finish();
        } else if (i == R.id.device_search_text) {// 跳转至搜索页
            Intent intent = new Intent(this, DeviceSearchActivity.class);
            intent.putParcelableArrayListExtra("ProductCollection", (ArrayList<? extends Parcelable>) devList);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //拿到当前位置
        mPosition = position;
        //即使刷新adapter
        listAdapter.notifyDataSetChanged();
        for (int i = 0; i < devList.size(); i++) {
            appendFragment = new DeviceAppendFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.device_append_fragment_container, appendFragment);
            Bundle bundle = new Bundle();
            bundle.putParcelable(DeviceAppendFragment.TAG, devList.get(position));
            appendFragment.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }
}
