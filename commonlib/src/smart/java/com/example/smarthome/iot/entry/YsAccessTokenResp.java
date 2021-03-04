package com.example.smarthome.iot.entry;

/**
 *  萤石获取accesstoken
 *
 */
public class YsAccessTokenResp {

    /**
     * data : {"accessToken":"at.7mj8655pa5voj0vj8n8w995fbzomxfp8-7hybmy734q-1dl44oy-xvnaaqt6j","expireTime":1568278984090}
     * code : 200
     * msg : 操作成功!
     */

    private DataBean data;
    private String code;
    private String msg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * accessToken : at.7mj8655pa5voj0vj8n8w995fbzomxfp8-7hybmy734q-1dl44oy-xvnaaqt6j
         * expireTime : 1568278984090
         */

        private String accessToken;
        private long expireTime;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }
    }
}
