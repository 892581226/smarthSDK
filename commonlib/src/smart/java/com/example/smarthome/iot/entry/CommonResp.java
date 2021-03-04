package com.example.smarthome.iot.entry;

/**
 *  请求返回值
 *
 */
public class CommonResp {
    private String state;
    private String flag;
    private String message;
    private String result;
    private String errorCode;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "CommonResp{" +
                "state='" + state + '\'' +
                ", flag='" + flag + '\'' +
                ", message='" + message + '\'' +
                ", result='" + result + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
