package com.xhwl.commonlib.http;



public interface HttpConnectionInter {
    void onFinish(String content);
    void onError(Exception e);
}
