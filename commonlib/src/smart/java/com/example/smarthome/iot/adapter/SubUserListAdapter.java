package com.example.smarthome.iot.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.SubUserListVo;
import com.example.smarthome.iot.util.SubStringUtil;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.util.List;

/**
 * author:
 * date:
 * description: 子账号列表
 * update:
 * version:
 */
public class SubUserListAdapter extends BaseQuickAdapter<SubUserListVo.SubUserListBean, BaseViewHolder> {
    private boolean isManageMode;

    public SubUserListAdapter(List<SubUserListVo.SubUserListBean> data) {
        super(R.layout.item_iot_sub_user_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SubUserListVo.SubUserListBean item) {
        helper.setText(R.id.item_sub_user_name, item.getSubUserId());
        if (!StringUtils.isEmpty(item.getCreateTime())) {
            helper.setText(R.id.item_sub_user_createtime, SubStringUtil.shareFamily(item.getCreateTime()));
        } else {
            helper.setText(R.id.item_sub_user_createtime,"");
        }

        helper.addOnClickListener(R.id.item_sub_user_manage);
        helper.addOnClickListener(R.id.item_sub_user_delete_iv);
        ImageView delView = helper.getView(R.id.item_sub_user_delete_iv);
        if (isManageMode) {
            delView.setVisibility(View.VISIBLE);
        } else {
            delView.setVisibility(View.GONE);
        }
        View item_sub_user_divline = helper.getView(R.id.item_sub_user_divline);
        if(helper.getAdapterPosition()==getData().size()-1){
            item_sub_user_divline.setVisibility(View.GONE);
        }else {
            item_sub_user_divline.setVisibility(View.VISIBLE);
        }
    }

    public boolean isManageMode() {
        return isManageMode;
    }

    public void setManageMode(boolean manageMode) {
        isManageMode = manageMode;
    }
}
