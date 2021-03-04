package com.example.smarthome.iot.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.xhwl.commonlib.uiutils.UiTools;


import java.util.List;

/**
 * author: glq
 * date: 2019/4/11 10:16
 * description: 首页场景控制列表适配器
 * update: 2019/4/11
 * version:
 */
public class SceneControlAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.ScenesBean, BaseViewHolder> {
    public SceneControlAdapter(List<SmartInfoVo.FamilysBean.ScenesBean> data) {
        super(R.layout.item_iot_scene_control_layout_cons,data);//item_iot_scene_control_layout
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.ScenesBean item) {
        helper.addOnClickListener(R.id.item_iot_scene_control);
        ImageView imageView = helper.getView(R.id.item_iot_scene_control_img);
        ImageView imageView1 = helper.getView(R.id.item_iot_scene_control_pitch_on);
        TextView scName = helper.getView(R.id.item_iot_scene_control_name);
        if(item.getSceneType()!=null && !item.getSceneType().equalsIgnoreCase("create")){
            scName.setText(item.getSceneName().substring(0,2));
        } else {
            String name = item.getSceneName();
            if(name.length()>2){
                scName.setText(name.substring(0,2)+"...");
            } else {
                scName.setText(name);
            }
        }
        imageView.setImageResource(UiTools.getResource(mContext,item.getIcon(), "drawable", R.drawable.icon_iot_scene_more));
        if (item.isIseffective()) {
            imageView1.setVisibility(View.VISIBLE);
        } else {
            imageView1.setVisibility(View.GONE);
        }

    }
}
