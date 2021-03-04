package com.xhwl.commonlib.uiutils.udpsocket;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * author: glq
 * date: 2019/5/15 19:24
 * description: 边发送边接收
 * update: 2019/5/15
 * version:
 */
public class SendAndReceiveUtils {
    private static String TAG = "SendAndReceiveUtils";
    private static InetAddress mAddress;
    private static DatagramSocket socket = null;
    private static String ip = "255.255.255.255"; //发送给整个局域网
    private static int port = 8899;  //发送方和接收方需要端口一致
    private static String personIp = "127.0.0.1"; //发送给本机
    private static final int personPort = 7070;  //发送方和接收方需要端口一致

    public static void sendSearchGateWay(Context context, final String content, final String sid, int port) {
        //初始化socket
        try {
            socket = new DatagramSocket();
            //设置超时时间
            socket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            mAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //创建线程发送信息
        new Thread() {
            private byte[] sendBuf;

            public void run() {
                try {
                    sendBuf = content.getBytes("utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, mAddress, port);
                try {
                    //已发送完成
                    socket.send(packet);
                    byte[] receive = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
                    while (true) {
                        socket.receive(receivePacket);
                        String result = new String(receivePacket.getData(), 0, receivePacket.getLength(), "utf-8");
                        Log.e(TAG, "result: "+ result);
                        if (!TextUtils.isEmpty(result) && result.contains(sid)) {
                            break;
                        }
                    }
                } catch (SocketTimeoutException timeOutException) {
                    Log.e("result4: ", "开始执行重启");
                    socket.close();
//                    sendContextToAIUI_person("请先连接网关");
//                    App.getInstance().exit();
//                    RestartAPPTool.restartAPP(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}

