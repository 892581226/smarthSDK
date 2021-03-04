package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceSearchAdapter;
import com.example.smarthome.iot.entry.DeviceAppendItemVo;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.ClearEditText;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: glq
 * date: 2019/4/10 16:00
 * description: 搜索设备
 * update: 2019/4/10
 * version: V1.4.1
 */
public class DeviceSearchActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private ImageView mBaseSearchBack;
    private ClearEditText mBaseSearchEditor;
    /**
     * 搜索
     */
    private TextView mBaseSearchStart;
    private RecyclerView mDeviceSearchRv;
    private DeviceSearchAdapter mDeviceSearchAdapter;
    private List<DeviceAppendItemVo> mDeviceAppendItemVos = new ArrayList<>();
    private List<DeviceListVo.ProductCollectionBean> devList = new ArrayList<>();
    private List<DeviceListVo.ProductCollectionBean.DevListBean> devListBeans = new ArrayList<>();
    private List<DeviceListVo.ProductCollectionBean.DevListBean> devSearchBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_search);
        initView();
        initData();
    }

    private void initData() {
        devList = getIntent().getParcelableArrayListExtra("ProductCollection");
        if (devList != null && devList.size() > 0) {
            for (int i = 0; i < devList.size(); i++) {
                if (devList.get(i).getDev_list() != null && devList.get(i).getDev_list().size() > 0) {
                    devListBeans.addAll(devList.get(i).getDev_list());
                }
            }
            devSearchBeans.addAll(devListBeans);
        }
    }

    private void initView() {
        mBaseSearchBack = (ImageView) findViewById(R.id.base_search_back);
        mBaseSearchBack.setOnClickListener(this);
        mBaseSearchEditor = (ClearEditText) findViewById(R.id.base_search_editor);
        mBaseSearchStart = (TextView) findViewById(R.id.base_search_start);
        mBaseSearchStart.setOnClickListener(this);
        mDeviceSearchRv = (RecyclerView) findViewById(R.id.device_search_rv);
        mDeviceSearchRv.setLayoutManager(new LinearLayoutManager(this));
        mDeviceSearchAdapter = new DeviceSearchAdapter(devListBeans);
        mDeviceSearchRv.setAdapter(mDeviceSearchAdapter);
        mDeviceSearchAdapter.setOnItemChildClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.base_search_back) {
            finish();
        } else if (i1 == R.id.base_search_start) {
            devSearchBeans.clear();
            // 开始搜索
            if (devListBeans != null && devListBeans.size() > 0) {
                if (!StringUtils.isEmpty(mBaseSearchEditor.getText().toString().trim())) {
                    for (int i = 0; i < devListBeans.size(); i++) {
                        if (devListBeans.get(i).getDeviceName().contains(mBaseSearchEditor.getText().toString().trim())) {
                            devSearchBeans.add(devListBeans.get(i));
                        }
                    }
                } else {
                    devSearchBeans.addAll(devListBeans);
                }
                mDeviceSearchAdapter.setNewData(devSearchBeans);
            }
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.item_iot_device_search_item) {
            if (devSearchBeans.size() > 0) {
                Intent intent = new Intent(this, DeviceConnectStepActivity.class);
                intent.putExtra("DeviceConnectStepActivity", devSearchBeans.get(position));
                startActivity(intent);
            }
        }
//        return false;
    }
}
