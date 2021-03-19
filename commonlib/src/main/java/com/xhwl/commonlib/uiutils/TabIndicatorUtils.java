package com.xhwl.commonlib.uiutils;

import android.graphics.Paint;
import com.google.android.material.tabs.TabLayout;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

public class TabIndicatorUtils {
    /**
     * 修改TabLayout的下划线长度
     * 单位px
     */
    public static void setIndicator(TabLayout tabs, int textSize) {

        Paint paint = new Paint();
        paint.setTextSize(textSize);
        float textWidth = paint.measureText((String) tabs.getTag());
        int padding = (int) ((tabs.getWidth() / tabs.getTabCount() - textWidth) / 2);
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        LinearLayout llTab = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (tabStrip != null) {
            tabStrip.setAccessible(true);
            try {
                llTab = (LinearLayout) tabStrip.get(tabs);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (llTab != null) {
            for (int i = 0; i < llTab.getChildCount(); i++) {
                View child = llTab.getChildAt(i);
                child.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                params.leftMargin = padding;
                params.rightMargin = padding;
                child.setLayoutParams(params);
                child.invalidate();
            }
        }

    }
}
