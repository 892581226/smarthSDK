package com.example.smarthome.iot.entry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * author:
 * date:
 * description: 家庭下房间列表接口返回
 * update:
 * version:
 */
public class RoomListVo {
    private List<RoomItem> rooms;

    public List<RoomItem> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomItem> rooms) {
        this.rooms = rooms;
    }

    public static class RoomItem implements Parcelable {
        private String roomId;
        private String icon;
        private String roomName;
        private String devNum;
        private String devOnlineNum;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getDevNum() {
            return devNum;
        }

        public void setDevNum(String devNum) {
            this.devNum = devNum;
        }

        public String getDevOnlineNum() {
            return devOnlineNum;
        }

        public void setDevOnlineNum(String devOnlineNum) {
            this.devOnlineNum = devOnlineNum;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.roomId);
            dest.writeString(this.icon);
            dest.writeString(this.roomName);
            dest.writeString(this.devNum);
            dest.writeString(this.devOnlineNum);
        }

        public RoomItem() {
        }

        protected RoomItem(Parcel in) {
            this.roomId = in.readString();
            this.icon = in.readString();
            this.roomName = in.readString();
            this.devNum = in.readString();
            this.devOnlineNum = in.readString();
        }

        public static final Creator<RoomItem> CREATOR = new Creator<RoomItem>() {
            @Override
            public RoomItem createFromParcel(Parcel source) {
                return new RoomItem(source);
            }

            @Override
            public RoomItem[] newArray(int size) {
                return new RoomItem[size];
            }
        };
    }
}
