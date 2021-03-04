package com.xhwl.commonlib.http.netrequest;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * 请求参数的封装
 *
 * @author jayce
 * @date 2015/05/24
 * Add parameter for http request , can only be the kind included in bases[] , or throw exception !
 */
public class HttpParams {

    //private BaseRequest mBaseRequest;

    private Map<String, String> params = new HashMap<>();

    private Map<String, File> fileMap = new HashMap<>();

    private Map<String, Object> mObjectMap = new HashMap<>();


    Class bases[] = {Integer.class, Long.class, Short.class, Float.class, Double.class, String.class, JSONArray.class, JSONObject.class, File.class, Object.class};


//    public HttpParams() {
//        mBaseRequest = new BaseRequest();
//    }

    /**
     * 添加键值对参数 与 setEntity 互斥 data
     *
     * @param key
     * @param param
     */
    public void add(String key, String param) {
       // mBaseRequest = params;
        try {
            if (isValidate(param)) {
                params.put(key, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String key, File param) {
        // mBaseRequest = params;
        try {
            if (isValidate(param)) {
                fileMap.put(key, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String key, Object param) {
        // mBaseRequest = params;
        try {
            if (isValidate(param)) {
                mObjectMap.put(key, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一个对象当做参数data 与 add 互斥 data
     *
     */
//    public void setEntity(Object o) {
//        mBaseRequest.key = o;
//    }


    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, File> getMapParams() {
        return fileMap;
    }


    public boolean isValidate(String param) throws Exception {
        Class cls = param.getClass();
        for (Class c : bases) {
            if (cls == c)
                return true;
        }
        throw new Exception("param " + param + " is not allowed.");
    }

    public boolean isValidate(File param) throws Exception {
        Class cls = param.getClass();
        for (Class c : bases) {
            if (cls == c)
                return true;
        }
        throw new Exception("param " + param + " is not allowed.");
    }

    public boolean isValidate(Object param) throws Exception {
        Class cls = param.getClass();
        for (Class c : bases) {
            if (cls == c)
                return true;
        }
        throw new Exception("param " + param + " is not allowed.");
    }
}
