package com.xhwl.commonlib.uiutils.udpsocket;

import android.os.Handler;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.LogRecord;

/**
 * author: glq
 * date: 2019/5/15 19:21
 * description: 接收方消息
 * update: 2019/5/15
 * version:
 */
public class ReceiveUtils {
    private static String TAG = "ReceiveUtils";
    //private static DatagramSocket socket;
    private static final int PhonePort = 6767;//手机端口号
    private static DatagramPacket packet;
    private static volatile boolean stopReceiver = false;
    String filePath = "/sdcard/AIUI/devices.txt";
    public static volatile String msg;
    public static String multicastHost = "224.0.0.1";
    private static MulticastSocket socket;
    public static void receiveMessage(int port) {
        new Thread() {
            public void run() {
                try {
                    InetAddress receiveAddress = null;
                    receiveAddress = InetAddress.getByName(multicastHost);
                    socket = new MulticastSocket(port);
                    socket.joinGroup(receiveAddress);
                } catch (IOException e) {
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
//                        setMsg(receive);
//                        EventBus.getDefault().post(receive);
                    } catch (IOException e) {
                        msg="";
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

    public static void setMsg(String msg) {
        ReceiveUtils.msg = msg;
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

