package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.IconUpdateAdapter;
import com.example.smarthome.iot.entry.IconUpdateItemVo;
import com.xhwl.commonlib.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author: glq
 * date: 2019/4/8 14:22
 * description: 自定义图标
 * update: 2019/4/8
 * version: V1.4.1
 */
public class IconUpdateActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private RecyclerView mIconUpdateRv;
    private IconUpdateAdapter mIconUpdateAdapter;
    private List<IconUpdateItemVo> mIconUpdateItemVos = new ArrayList<>();
    private boolean isRoomIcon;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_icon_update);
        initView();
        initDate();
    }

    private void initDate() {
        if (isRoomIcon) {
            mTopTitle.setText("房间图标");
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_master,"icon_iot_room_master"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_guest,"icon_iot_room_guest"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_children,"icon_iot_room_children"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_veranda,"icon_iot_room_veranda"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_living,"icon_iot_room_living"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_shower,"icon_iot_room_shower"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_kitchen,"icon_iot_room_kitchen"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_veranda_small,"icon_iot_room_veranda_small"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_study,"icon_iot_room_study"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_utility,"icon_iot_room_utility"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_dining,"icon_iot_room_dining"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_room_corridor,"icon_iot_room_corridor"));
        } else {
            mTopTitle.setText("场景图标");
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_go_home,"icon_iot_scene_go_home"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_sleep,"icon_iot_scene_sleep"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_leave_home,"icon_iot_scene_leave_home"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_holiday,"icon_iot_scene_holiday"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_watch_film,"icon_iot_scene_watch_film"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_writing,"icon_iot_scene_writing"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_rest,"icon_iot_scene_rest"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_eat,"icon_iot_scene_eat"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_meeting,"icon_iot_scene_meeting"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_get_up,"icon_iot_scene_get_up"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_celebrate,"icon_iot_scene_celebrate"));
            mIconUpdateItemVos.add(new IconUpdateItemVo(R.drawable.icon_iot_scene_exercise,"icon_iot_scene_exercise"));
        }

        mIconUpdateAdapter = new IconUpdateAdapter(mIconUpdateItemVos);
        mIconUpdateRv.setAdapter(mIconUpdateAdapter);
        mIconUpdateAdapter.setOnItemChildClickListener(this);
    }

    private void initView() {
        isRoomIcon = getIntent().getBooleanExtra("isRoomIcon", false);
        isUpdate = getIntent().getBooleanExtra("isUpdate", false);
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mIconUpdateRv = (RecyclerView) findViewById(R.id.icon_update_rv);
        mIconUpdateRv.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.top_back) {
            finish();
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.item_iot_update_icon) {// 选了了一张图片
            Intent intent = new Intent();
            // 获取用户计算后的结果
            String iconName = mIconUpdateItemVos.get(position).getIconName();
            intent.putExtra("iconName", iconName); //将计算的值回传回去
            //通过intent对象返回结果，必须要调用一个setResult方法，
            if (isRoomIcon) {
                if (!isUpdate) {
                    setResult(ActionConstant.ADD_ROOM_RESULT_CODE, intent); //新增房间
                } else {
                    setResult(ActionConstant.UPDATE_ROOM_RESULT_CODE, intent); //更新房间
                }
            } else {
                if (!isUpdate) {
                    setResult(ActionConstant.ADD_SCENE_RESULT_CODE, intent); //新增场景
                } else {
                    setResult(ActionConstant.UPDATE_SCENE_RESULT_CODE, intent); //更新场景
                }
            }
            finish();
        }
//        return false;
    }


}
