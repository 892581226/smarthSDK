package com.xhwl.commonlib.http.resp;

/**
 * 网络failure统一封装
 *
 * @author jerry
 * @date 2016/03/21
 */
public class ServerTip {
    private boolean state;
    public int errorCode;
    private String flag;
    public String message;

    public ServerTip() {

    }

    public ServerTip(int errorCode, String message, boolean state, String flag) {
        this.errorCode = errorCode;
        this.message = message;
        this.state = state;
        this.flag = flag;
    }

    public int errorCode() {
        return errorCode;
    }

    public String message() {
        return message;
    }

    public void errorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void message(String message) {
        this.message = message;
    }


    public boolean state() {
        return state;
    }

    public String flag() {
        return flag;
    }

}
