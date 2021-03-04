package com.example.smarthome.iot.entry;

import java.util.List;

/**
 * author:
 * date:
 * description: 获取家庭ID下的所有场景的树形结构
 * update:
 * version:
 */
public class SceneListVo {
    private List<SmartInfoVo.FamilysBean.ScenesBean> scenes;

    public List<SmartInfoVo.FamilysBean.ScenesBean> getScenes() {
        return scenes;
    }

    public void setScenes(List<SmartInfoVo.FamilysBean.ScenesBean> scenes) {
        this.scenes = scenes;
    }
}
