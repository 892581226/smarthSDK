package com.xhwl.commonlib.http.retrofit;

public class BaseResponse {


    /**
     * Test Example
     * state : false
     * flag :
     * message : 系统异常，操作失败!
     * errorCode : 201
     */

    private boolean state;
    private String flag;
    private String message;
    private int errorCode;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
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

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
