package com.xhwl.commonlib.entity.tencentcloud;

import android.os.Parcel;
import android.os.Parcelable;

import com.xhwl.commonlib.application.MyAPP;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.SPUtils;

/**
 * <p>呼叫方呼出的数据类型存放   </p>
 * create by zw at 2018/8/11 0011
 */
public class CallTypeBean implements Parcelable {
    private String status;//   free 空闲，callTo 正在呼叫， called 正在被呼， calling 正在电话
    private String deviceType = "Android";//  "web"    // (iOS, Android, door, indoor)
    private int type = 1;// 例如：1:通讯对讲（各个端根据自己功能编写）
    private boolean isVideo = true;//  true视频对讲， false 语音对讲
    private String role = "业主"; // 角色：安管主任、门岗、项目经理、工程、业主/门口机
    private int roomCode;// 房间号
    private String name;// 名字
    private int msgType;// 1、呼叫 2:未接听挂断 3:接通时挂断   4:待接听拒绝   6:忙碌，【其他】 7：开门；8:开门成功；9:群呼时接听方接通（暂时还没这个需求，可以不做） 10：呼叫30s超时
    private String msg;//发送消息内容
    private String phoneNumber;
    private String address; // （门口机传门口机地址，业主传几栋几单元，物业传项目地址，门口机传几栋几单元）
    private String userType = "业主"; // 室内机传“设备”字段，业主端传“业主”字段，物业端传“物业”字段
    private String projectName;
    private String projectCode;
    private boolean hasCamera;//是否有视频（web）
    private boolean isLast;// 是否是轮呼的最后一位


    public CallTypeBean() {
        name = SPUtils.get(MyAPP.getIns(), SpConstant.SP_REALLYNAME, "");
        phoneNumber = SPUtils.get(MyAPP.getIns(), SpConstant.SP_USER_TELEPHONE, "");
        projectName = SPUtils.get(MyAPP.getIns(), SpConstant.SP_PRONAME, "");
        projectCode = SPUtils.get(MyAPP.getIns(), SpConstant.SP_PROCODE, "");
    }

    protected CallTypeBean(Parcel in) {
        status = in.readString();
        deviceType = in.readString();
        type = in.readInt();
        isVideo = in.readByte() != 0;
        role = in.readString();
        roomCode = in.readInt();
        name = in.readString();
        msgType = in.readInt();
        msg = in.readString();
        phoneNumber = in.readString();
        address = in.readString();
        userType = in.readString();
        projectCode = in.readString();
        projectName = in.readString();
        hasCamera = in.readByte() != 0;
        isLast = in.readByte() != 0;
    }

    public static final Creator<CallTypeBean> CREATOR = new Creator<CallTypeBean>() {
        @Override
        public CallTypeBean createFromParcel(Parcel in) {
            return new CallTypeBean(in);
        }

        @Override
        public CallTypeBean[] newArray(int size) {
            return new CallTypeBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(deviceType);
        dest.writeInt(type);
        dest.writeByte((byte) (isVideo ? 1 : 0));
        dest.writeString(role);
        dest.writeInt(roomCode);
        dest.writeString(name);
        dest.writeInt(msgType);
        dest.writeString(msg);
        dest.writeString(phoneNumber);
        dest.writeString(address);
        dest.writeString(userType);
        dest.writeString(projectCode);
        dest.writeString(projectName);
        dest.writeByte((byte) (hasCamera ? 1 : 0));
        dest.writeByte((byte) (isLast ? 1 : 0));
    }

    public enum MsgTypeEnum {
        CALL_TO(1),
        PRE_HANG_ON(2),
        CALL_OVER(3),
        REJECT(4),
        ANSWER(5),
        BUSY(6),
        OPEN_DOOR(7),
        OPEN_OK(8),
        CALL_TO_MULT(9),
        TIMEOUT(10),
        ELECTRICAL_OPENDOOR(11);

        private final int msgType;

        MsgTypeEnum(int msgType) {
            this.msgType = msgType;
        }

        public int getMsgType() {
            return msgType;
        }
    }


    public CallTypeBean setMsgType(int msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public CallTypeBean setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CallTypeBean setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getUserType() {
        return userType;
    }

    public CallTypeBean setUserType(String userType) {
        this.userType = userType;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public CallTypeBean setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public CallTypeBean setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public int getType() {
        return type;
    }

    public CallTypeBean setType(int type) {
        this.type = type;
        return this;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public CallTypeBean setVideo(boolean video) {
        isVideo = video;
        return this;
    }

    public String getRole() {
        return role;
    }

    public CallTypeBean setRole(String role) {
        this.role = role;
        return this;
    }

    public int getRoomCode() {
        return roomCode;
    }

    public CallTypeBean setRoomCode(int roomCode) {
        this.roomCode = roomCode;
        return this;
    }

    public String getName() {
        return name;
    }

    public CallTypeBean setName(String name) {
        this.name = name;
        return this;
    }

    public int getMsgType() {
        return msgType;
    }

    public CallTypeBean setMsgType(MsgTypeEnum type) {
        this.msgType = type.getMsgType();
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public CallTypeBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public CallTypeBean setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public CallTypeBean setProjectCode(String projectCode) {
        this.projectCode = projectCode;
        return this;
    }

    public boolean isHasCamera() {
        return hasCamera;
    }

    public CallTypeBean setHasCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
        return this;
    }

    public boolean isLast() {
        return isLast;
    }

    public CallTypeBean setLast(boolean last) {
        isLast = last;
        return this;
    }

    @Override
    public String toString() {
        return "CallTypeBean{" +
                "status='" + status + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", type=" + type +
                ", isVideo=" + isVideo +
                ", role='" + role + '\'' +
                ", roomCode=" + roomCode +
                ", name='" + name + '\'' +
                ", msgType=" + msgType +
                ", msg='" + msg + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", userType='" + userType + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectCode='" + projectCode + '\'' +
                ", hasCamera=" + hasCamera +
                ", isLast=" + isLast +
                '}';
    }
}
