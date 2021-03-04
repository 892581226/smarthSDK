package com.example.smarthome.iot.entry;

/**
 * author: glq
 * date: 2019/4/12 9:41
 * description:
 * update: 2019/4/12
 * version:
 */
public class IconUpdateItemVo {
    private int iconId;
    private String iconName;

    public IconUpdateItemVo(int iconId, String iconName) {
        this.iconId = iconId;
        this.iconName = iconName;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
