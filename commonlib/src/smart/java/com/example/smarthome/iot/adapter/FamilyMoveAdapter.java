package com.example.smarthome.iot.adapter;

import androidx.annotation.Nullable;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.SmartInfoList;

import java.util.List;

public class FamilyMoveAdapter extends BaseQuickAdapter<SmartInfoList.FamilysBean, BaseViewHolder> {

    public FamilyMoveAdapter(@Nullable List<SmartInfoList.FamilysBean> data) {
        super(R.layout.family_move, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartInfoList.FamilysBean item) {
        helper.addOnClickListener(R.id.move_family_tv);
        TextView delView = helper.getView(R.id.family_name);
        delView.setText(item.getFamilyName());


    }
}