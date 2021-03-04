package com.example.smarthome.iot.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.RoomListVo;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.util.List;

/**
 * author: glq
 * date: 2019/6/13 15:18
 * description:
 * update: 2019/6/13
 * version:
 */
public class RoomListAdapter extends BaseQuickAdapter<RoomListVo.RoomItem, BaseViewHolder> {
    private boolean isManageMode;

    public RoomListAdapter(List<RoomListVo.RoomItem> data) {
        super(R.layout.item_iot_family_manage_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomListVo.RoomItem item) {
        helper.setText(R.id.item_family_name, item.getRoomName());
        if (StringUtils.isEmpty(item.getDevNum()) || item.getDevNum().equals("0")) {
            helper.setText(R.id.item_family_info, "快去添加设备吧");
        } else {
            helper.setText(R.id.item_family_info, String.format(mContext.getResources().getString(R.string.room_dev_num),item.getDevNum(),item.getDevOnlineNum()));
        }

        helper.addOnClickListener(R.id.item_family_manage);
        helper.addOnClickListener(R.id.item_family_delete_iv);
        ImageView delView = helper.getView(R.id.item_family_delete_iv);
        if (isManageMode) {
            delView.setVisibility(View.VISIBLE);
//            helper.setVisible(R.id.item_family_delete_iv, true);
        } else {
            delView.setVisibility(View.GONE);
//            helper.setVisible(R.id.item_family_delete_iv, false);
        }
        View item_room_divline = helper.getView(R.id.item_room_divline);
        if(helper.getAdapterPosition()==getData().size()-1){
            item_room_divline.setVisibility(View.GONE);
        }else {
            item_room_divline.setVisibility(View.VISIBLE);
        }
    }

    public boolean isManageMode() {
        return isManageMode;
    }

    public void setManageMode(boolean manageMode) {
        isManageMode = manageMode;
    }
}
