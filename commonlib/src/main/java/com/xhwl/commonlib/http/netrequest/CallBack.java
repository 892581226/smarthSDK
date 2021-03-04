package com.xhwl.commonlib.http.netrequest;

import java.io.IOException;

public interface CallBack {
    void onFinish(String content, int code);

    void onFailure(IOException e);

    void onError(Exception e);//StreamTools UnsupportedEncodingException ex fixme:
}
