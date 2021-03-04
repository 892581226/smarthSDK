package com.example.smarthome.iot.entry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ScenePanelMsgVo {

    /**
     * ScenePanelMsg : {"deviceId":"000B57FFFEDDE8DE","deviceType":"131305","familyId":"","userId":"","switchCount":6,"switchs":[{"sceneId":"","switchNum":"switch1","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch2","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch3","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch4","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch5","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch6","taskType":"","sceneName":"","sceneState":false}]}
     */

    private ScenePanelMsgBean ScenePanelMsg;

    public ScenePanelMsgBean getScenePanelMsg() {
        return ScenePanelMsg;
    }

    public void setScenePanelMsg(ScenePanelMsgBean ScenePanelMsg) {
        this.ScenePanelMsg = ScenePanelMsg;
    }

    public static class ScenePanelMsgBean {
        /**
         * deviceId : 000B57FFFEDDE8DE
         * deviceType : 131305
         * familyId :
         * userId :
         * switchCount : 6
         * switchs : [{"sceneId":"","switchNum":"switch1","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch2","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch3","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch4","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch5","taskType":"","sceneName":"","sceneState":false},{"sceneId":"","switchNum":"switch6","taskType":"","sceneName":"","sceneState":false}]
         */

        private String deviceId;
        private String deviceType;
        private String familyId;
        private String userId;
        private int switchCount;
        private List<SwitchsBean> switchs;
        private List<String> records;

        public List<String> getRecords() {
            return records;
        }

        public void setRecords(List<String> records) {
            this.records = records;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getFamilyId() {
            return familyId;
        }

        public void setFamilyId(String familyId) {
            this.familyId = familyId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getSwitchCount() {
            return switchCount;
        }

        public void setSwitchCount(int switchCount) {
            this.switchCount = switchCount;
        }

        public List<SwitchsBean> getSwitchs() {
            return switchs;
        }

        public void setSwitchs(List<SwitchsBean> switchs) {
            this.switchs = switchs;
        }

        public static class SwitchsBean implements Parcelable {
            /**
             * sceneId :
             * switchNum : switch1
             * taskType :
             * sceneName :
             * sceneState : false
             */

            private String sceneId;
            private String switchNum;
            private String taskType;
            private String sceneName;
            private boolean sceneState;

            public String getSceneId() {
                return sceneId;
            }

            public void setSceneId(String sceneId) {
                this.sceneId = sceneId;
            }

            public String getSwitchNum() {
                return switchNum;
            }

            public void setSwitchNum(String switchNum) {
                this.switchNum = switchNum;
            }

            public String getTaskType() {
                return taskType;
            }

            public void setTaskType(String taskType) {
                this.taskType = taskType;
            }

            public String getSceneName() {
                return sceneName;
            }

            public void setSceneName(String sceneName) {
                this.sceneName = sceneName;
            }

            public boolean isSceneState() {
                return sceneState;
            }

            public void setSceneState(boolean sceneState) {
                this.sceneState = sceneState;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.sceneId);
                dest.writeString(this.switchNum);
                dest.writeString(this.taskType);
                dest.writeString(this.sceneName);
                dest.writeByte(this.sceneState ? (byte) 1 : (byte) 0);
            }

            public SwitchsBean() {
            }

            protected SwitchsBean(Parcel in) {
                this.sceneId = in.readString();
                this.switchNum = in.readString();
                this.taskType = in.readString();
                this.sceneName = in.readString();
                this.sceneState = in.readByte() != 0;
            }

            public static final Parcelable.Creator<SwitchsBean> CREATOR = new Parcelable.Creator<SwitchsBean>() {
                @Override
                public SwitchsBean createFromParcel(Parcel source) {
                    return new SwitchsBean(source);
                }

                @Override
                public SwitchsBean[] newArray(int size) {
                    return new SwitchsBean[size];
                }
            };
        }
    }
}
