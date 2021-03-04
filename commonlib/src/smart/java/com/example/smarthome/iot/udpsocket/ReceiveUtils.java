package com.example.smarthome.iot.udpsocket;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * author: glq
 * date: 2019/5/15 19:21
 * description: 接收方消息
 * update: 2019/5/15
 * version:
 */
public class ReceiveUtils {
    private static String TAG = "ReceiveUtils";
    private static DatagramSocket socket;
    private static final int PhonePort = 6767;//手机端口号
    private static DatagramPacket packet;
    private static volatile boolean stopReceiver = false;
    String filePath = "/sdcard/AIUI/devices.txt";
    private static volatile String msg = "";

    public static void receiveMessage(int port) {
        new Thread() {
            public void run() {
                try {
                    socket = new DatagramSocket(port);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                byte[] receBuf = new byte[1024];
                packet = new DatagramPacket(receBuf, receBuf.length);
                while (!stopReceiver) {
                    try {
                        socket.receive(packet);
                        String receive = new String(packet.getData(), 0, packet.getLength(), "utf-8");
                        Log.e(TAG, "收到的内容为：" + receive);
                        msg = receive;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static void closeSoket(){
        if(socket!=null){
            stopReceiver = true;
            socket.close();
        }
    }

    public static String getMsg (){
        return msg;
    }

    public static void init(){
        if(socket!=null){
            socket.close();
        }
        socket = null;
        stopReceiver = false;
    }
}

