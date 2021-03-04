package com.xhwl.commonlib.uiutils.udpsocket;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * author: glq
 * date: 2019/5/15 19:21
 * description: 发送方
 * update: 2019/5/15
 * version:
 */
public class SendUtils {
    private static String TAG = "SendUtils";
    private static InetAddress mAddress;
    private static DatagramSocket socket = null;
    private static String ip = "255.255.255.255"; //发送给整个局域网
    private static final int SendPort = 9999;  //发送方和接收方需要端口一致

    public static void SendUtils (final Context context, final String content) {
        //初始化socket
        try {
            socket = new DatagramSocket();
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
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                DatagramPacket recvPacket1 = new DatagramPacket(sendBuf, sendBuf.length, mAddress, SendPort);
                try {
                    socket.send(recvPacket1);
                    socket.close();
                    Log.e(TAG, "已将内容发送给了AIUI端内容为：" + content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

