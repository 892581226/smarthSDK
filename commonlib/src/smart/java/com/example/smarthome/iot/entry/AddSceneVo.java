package com.example.smarthome.iot.entry;

import java.util.List;

/**
 * 增加场景接口请求bean
 * 目前只支持一键执行场景
 * taskType: once/schedule/trigger
 * tasks:比较复杂，首先拿到设置的设备状态，然后对设备状态进行base64，接着将值赋给field，再组织一个task-json数据，最终将这个task-json进行base64，作为一个元素添加都tasks中。关于task-json的格式，内容太长具体看文档。
 */
public class AddSceneVo {
    private String familyId;
    private String sceneIcon;
    private String sceneName;
    private String taskType;
    private List<String> tasks;
    private String sceneId;//更新场景

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getSceneIcon() {
        return sceneIcon;
    }

    public void setSceneIcon(String sceneIcon) {
        this.sceneIcon = sceneIcon;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    @Override
    public String toString() {
        return "AddSceneVo{" +
                "familyId='" + familyId + '\'' +
                ", sceneIcon='" + sceneIcon + '\'' +
                ", sceneName='" + sceneName + '\'' +
                ", taskType='" + taskType + '\'' +
                ", tasks=" + tasks +
                ", sceneId='" + sceneId + '\'' +
                '}';
    }
}
