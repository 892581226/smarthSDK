package com.example.smarthome.iot.util;


public class SubStringUtil {
    /**
     * 共享设备时间显示
     * @param str
     * @return
     */
    public static String shareFamily(String str) {
        String s="";
        if(str.length()>=8){
            s = str.substring(0,4)+"/"+str.substring(4,6)+"/"+str.substring(6,8);
        }
        return s;
    }
}
