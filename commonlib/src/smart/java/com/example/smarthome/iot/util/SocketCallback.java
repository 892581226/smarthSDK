package com.example.smarthome.iot.util;

/**
 * 作者：created by @author{ John } on 2019/5/8 0008下午 2:21
 * 描述：
 * 修改备注：
 */
public interface SocketCallback {
    void onConnected();
    void onDisconnected();
    void onReconnected();
    void onSend();
    void onReceived(byte[] msg);
    void onError(String msg);
    void onSendHeart();
}
