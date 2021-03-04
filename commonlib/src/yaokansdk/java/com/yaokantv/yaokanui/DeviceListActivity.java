package com.yaokantv.yaokanui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.smarthome.R;
import com.jaeger.library.StatusBarUtil;
import com.yaokantv.sdkdemo.YaoKanSDK;
import com.yaokantv.sdkdemo.AppManager;
import com.yaokantv.sdkdemo.BrandActivity;
import com.yaokantv.yaokansdk.callback.YaokanSDKListener;
import com.yaokantv.yaokansdk.manager.Yaokan;
import com.yaokantv.yaokansdk.model.YkDevice;
import com.yaokantv.yaokansdk.model.YkMessage;
import com.yaokantv.yaokansdk.model.e.MsgType;
import com.yaokantv.yaokansdk.utils.CommonAdapter;
import com.yaokantv.yaokansdk.utils.DlgUtils;
import com.yaokantv.yaokansdk.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends BaseActivity implements YaokanSDKListener {
    CommonAdapter<YkDevice> adapter;
    List<YkDevice> mList = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        setMTitle(R.string.title_device_list, TITLE_LOCATION_CENTER);
        reload();
    }

    @Override
    protected void reload() {
        Yaokan.instance().loadDevices(mList);
    }

    @Override
    protected void initView() {
        hideBack();
        mList.addAll(Yaokan.instance().exportDeviceListFromDB());
        listView = findViewById(R.id.lv_device);
        showSetting(R.mipmap.add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(activity, SoftApConfigActivity.class));
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 98);
                }
            }
        });
        adapter = new CommonAdapter<YkDevice>(this, mList, R.layout.lv_item) {
            @Override
            public void convert(ViewHolder helper, final YkDevice item, final int position) {
                String status = item.isOnline() ? "在线" : "离线";
                String name="";
                switch (item.getName()){
                    case "YKK-1011":
                        name="小苹果";
                        break;
                    case  "YKK-1013":
                        name="大苹果";
                        break;
                    case  "YKK-DS16A":
                        name="空调伴侣";
                        break;
                }
                helper.setText(R.id.tv_item, /*getMac(item.getName()) */name+ " " + status);
                helper.setText(R.id. tv_item_type, /*getMac(item.getName()) */item.getMac());
                Log.e(TAG, "convert: "+status+"--"+item.getMac()+"--"+item.getName()+"--"+item.getDid()+"---"+mList.toString() );
                helper.setOnclickListener(R.id.btn_test, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Yaokan.instance().test(item.getDid());
                    }
                });
                helper.setOnclickListener(R.id.btn_more, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DeviceListActivity.this, BrandActivity.class);
                        intent.putExtra("from", "d");
                        YaoKanSDK.curMac = mList.get(position).getMac();
                        YaoKanSDK.curDid = mList.get(position).getDid();
                        YaoKanSDK.curRf = mList.get(position).getName().contains("RF") ? "1" : "0";
                        startActivity(intent);
                    }
                });
                helper.setOnclickListener(R.id.btn_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DlgUtils.createDefDlg(activity, "", "是否删除该设备?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Yaokan.instance().deleteRemoteByMac(item.getMac());
                                Yaokan.instance().apReset(item.getMac(), item.getDid());
                                Yaokan.instance().deleteDevice(item.getMac());
                                mList.remove(item);
                                adapter.notifyDataSetChanged();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }
                });
            }
        };
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DlgUtils.createDefDlg(activity, "", "是否删除该设备?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Yaokan.instance().deleteRemoteByMac(mList.get(position).getMac());
                        Yaokan.instance().apReset(mList.get(position).getMac(),mList.get(position).getDid());
                        Yaokan.instance().deleteDevice(mList.get(position).getMac());
                        mList.remove(mList.get(position));
                        adapter.notifyDataSetChanged();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                return true;
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, RcListActivity.class);
                YaoKanSDK.curMac = mList.get(position).getMac();
                YaoKanSDK.curDid = mList.get(position).getDid();
                YaoKanSDK.curRf = mList.get(position).getName().contains("RF") ? "1" : "0";
                Config.setDid(YaoKanSDK.curDid);
                Config.setMac(YaoKanSDK.curMac);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 98 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(this, SoftApConfigActivity.class));
        }
    }

    String getMac(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            StringBuilder newM = new StringBuilder();
            int i = 0;
            for (char c : mac.toCharArray()) {
                newM.append(c);
                if (i % 2 == 1 && i != mac.length() - 1) {
                    newM.append(":");
                }
                i++;
            }
            mac = newM.toString();
        }
        return mac;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //AppManager.getAppManager().AppExit(this);
        finish();
    }

    @Override
    public void onReceiveMsg(final MsgType msgType, final YkMessage ykMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (msgType) {
                    case DeviceOnline:
                    case DeviceOffline:

                        if (ykMessage != null && ykMessage.getData() != null && ykMessage.getData() instanceof YkDevice) {
                            YkDevice device = (YkDevice) ykMessage.getData();
                            //设备上线之后要把设备保存进去
                            Yaokan.instance().inputYkDeviceToDB(device);
                            if (mList.contains(device)) {
                                mList.remove(device);
                            }
                            if (!TextUtils.isEmpty(device.getDid())) {
                                mList.add(device);
                                notifyList();
                            }
                        }
                        break;
                }
            }
        });
    }

    void notifyList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
