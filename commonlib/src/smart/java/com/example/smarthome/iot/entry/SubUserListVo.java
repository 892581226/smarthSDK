package com.example.smarthome.iot.entry;

import java.util.List;

/**
 * author:
 * date:
 * description: 子账号列表
 * update:
 * version:
 */
public class SubUserListVo {

    private List<SubUserListBean> subUserList;

    public List<SubUserListBean> getSubUserList() {
        return subUserList;
    }

    public void setSubUserList(List<SubUserListBean> subUserList) {
        this.subUserList = subUserList;
    }

    public static class SubUserListBean {
        /**
         * subUserId : 0007
         * state : 1
         * createTime : 20191017175637
         */

        private String subUserId;
        private String state;
        private String createTime;

        public String getSubUserId() {
            return subUserId;
        }

        public void setSubUserId(String subUserId) {
            this.subUserId = subUserId;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
