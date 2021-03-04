package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceAppendAdapter;
import com.example.smarthome.iot.entry.DeviceListVo;


import java.util.ArrayList;
import java.util.List;

/**
 * author: glq
 * date: 2019/4/10 14:27
 * description: 添加设备的fragment
 * update: 2019/4/10
 * version:
 */
public class DeviceAppendFragment extends Fragment implements BaseQuickAdapter.OnItemChildClickListener {
    public static final String TAG = "MyFragment";
    public static final String ID="gatewayId";
    private DeviceListVo.ProductCollectionBean productCollectionBean;
    private RecyclerView recyclerView;
    private DeviceAppendAdapter appendAdapter;
    private List<DeviceListVo.ProductCollectionBean.DevListBean> appendItemVos = new ArrayList<>();
    private String gatewayId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_iot_device_fragment_layout, null);
        recyclerView = view.findViewById(R.id.fragment_iot_device_rv);
        //得到数据
        productCollectionBean = getArguments().getParcelable(TAG);
        appendItemVos = productCollectionBean.getDev_list();
//        gatewayId = getArguments().getString(DeviceAppendFragment.ID);
//        Log.e("无线网关数据网关ID", gatewayId);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        appendAdapter = new DeviceAppendAdapter(appendItemVos);
        recyclerView.setAdapter(appendAdapter);
        appendAdapter.setOnItemChildClickListener(this);
        return view;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(getActivity(), DeviceConnectStepActivity.class);
        intent.putExtra("DeviceConnectStepActivity",appendItemVos.get(position));
        startActivity(intent);
    }
}
