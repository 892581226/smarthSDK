package com.example.smarthome.iot.entry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * author: glq
 * date: 2019/4/9 14:01
 * description: 场景列表实体类
 * update: 2019/4/9
 * version: 
 */
public class SceneManageItemVo implements Parcelable {
    private int total;
    private List<Item> defaultSceneList;
    private List<Item> sceneList;

    public SceneManageItemVo() {
    }

    protected SceneManageItemVo(Parcel in) {
        total = in.readInt();
    }

    public static final Creator<SceneManageItemVo> CREATOR = new Creator<SceneManageItemVo>() {
        @Override
        public SceneManageItemVo createFromParcel(Parcel in) {
            return new SceneManageItemVo(in);
        }

        @Override
        public SceneManageItemVo[] newArray(int size) {
            return new SceneManageItemVo[size];
        }
    };

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Item> getDefaultSceneList() {
        return defaultSceneList;
    }

    public void setDefaultSceneList(List<Item> defaultSceneList) {
        this.defaultSceneList = defaultSceneList;
    }

    public List<Item> getSceneList() {
        return sceneList;
    }

    public void setSceneList(List<Item> sceneList) {
        this.sceneList = sceneList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total);
    }

    public static class Item implements Parcelable {
        public String name;
        public String icon;
        private int id;
        private int state;
        private int familyId;
        private String createTime;
        public boolean isOn;
        public int onLineDevCount;
        public boolean isCheck;

        public Item() {
        }

        public Item(String name) {
            this.name = name;
        }


        protected Item(Parcel in) {
            name = in.readString();
            icon = in.readString();
            id = in.readInt();
            state = in.readInt();
            familyId = in.readInt();
            createTime = in.readString();
            isOn = in.readByte() != 0;
            onLineDevCount = in.readInt();
            isCheck = in.readByte() != 0;
        }

        public static final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getFamilyId() {
            return familyId;
        }

        public void setFamilyId(int familyId) {
            this.familyId = familyId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public boolean isOn() {
            return isOn;
        }

        public void setOn(boolean on) {
            isOn = on;
        }

        public int getOnLineDevCount() {
            return onLineDevCount;
        }

        public void setOnLineDevCount(int onLineDevCount) {
            this.onLineDevCount = onLineDevCount;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(icon);
            dest.writeInt(id);
            dest.writeInt(state);
            dest.writeInt(familyId);
            dest.writeString(createTime);
            dest.writeByte((byte) (isOn ? 1 : 0));
            dest.writeInt(onLineDevCount);
            dest.writeByte((byte) (isCheck ? 1 : 0));
        }
    }
}
