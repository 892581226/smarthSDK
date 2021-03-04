package com.example.smarthome.iot.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.example.smarthome.R;
import com.example.smarthome.iot.entry.SmartInfoVo;

import java.util.List;

/**
 * author: glq
 * date: 2019/4/8 15:41
 * description: 场景管理管理Adapter
 * update: 2019/4/8
 * version: V1.4.1
 */
public class SceneManageAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.ScenesBean, BaseViewHolder> {

    private boolean isManageMode;

    public SceneManageAdapter(List<SmartInfoVo.FamilysBean.ScenesBean> data) {
        super(R.layout.item_smart_iot_scene_name, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.ScenesBean item) {
        helper.setText(R.id.item_scene_name_tv, item.getSceneName());
        helper.setText(R.id.item_scene_dev_num_tv, String.format(mContext.getResources().getString(R.string.scene_dev_num),item.getDevNum(),item.getDevOnlineNum()));
        if (item.isIseffective()) {
            // 已开启
            helper.setText(R.id.item_scene_switch_tv,"开启");
            helper.setBackgroundRes(R.id.item_scene_switch_tv, R.drawable.btn_blue_sold_12t);
            helper.setTextColor(R.id.item_scene_switch_tv, mContext.getResources().getColor(R.color.white));
        } else  {
            // 未开启 -- 默认
            helper.setText(R.id.item_scene_switch_tv,"关闭");
            helper.setBackgroundRes(R.id.item_scene_switch_tv, R.drawable.btn_blue_corner_white_12t);
            helper.setTextColor(R.id.item_scene_switch_tv, mContext.getResources().getColor(R.color.yellow));
        }

        View scene_name_divline = helper.getView(R.id.scene_name_divline);
        if(helper.getAdapterPosition()==getData().size()-1){
            scene_name_divline.setVisibility(View.GONE);
        }else {
            scene_name_divline.setVisibility(View.VISIBLE);
        }
        ImageView delView = helper.getView(R.id.item_scene_delete_iv);
        if (isManageMode) {
//            helper.setVisible(R.id.item_scene_delete_iv, true);
            delView.setVisibility(View.VISIBLE);
        } else {
//            helper.setVisible(R.id.item_scene_delete_iv, false);
            delView.setVisibility(View.GONE);
        }

        helper.addOnClickListener(R.id.item_scene_delete_iv);
        helper.addOnClickListener(R.id.item_scene_switch_tv);
        helper.addOnClickListener(R.id.item_scene_layout);

    }

    public boolean isManageMode() {
        return isManageMode;
    }

    public void setManageMode(boolean manageMode) {
        isManageMode = manageMode;
    }
}
