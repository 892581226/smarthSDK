package com.example.smarthome.iot.entry.eventbus;

/**
 * author: glq
 * date: 2019/4/25 9:53
 * description:
 * update: 2019/4/25
 * version:
 */
public class UpdateFamilyEvent {
    private boolean isUpdate;
    private String title;
    public UpdateFamilyEvent() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UpdateFamilyEvent(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public UpdateFamilyEvent(String title) {
        this.title = title;
    }

    public UpdateFamilyEvent(boolean isUpdate, String title) {
        this.isUpdate = isUpdate;
        this.title = title;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
