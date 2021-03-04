package com.xhwl.commonlib.http.netrequest;

/**
 * 公共返回码
 *
 * @author jerry
 * @date 2016/03/21
 */
public class ErrorType {

    public static final int E_LOCAL = 300; //本地网络框架报告的异常
    public static final int E_NETWORK = 301;  //网络错误
    public static final int E_JSON_PARSE = 302; //json解析错误
    public static final int E_IO_EXCEPTION = 303;

    public static final int E_OK = 200; //正常
    public static final int E_TOKEN = 101; // token验证失败
    public static final int E_MSG_CODE_EXPIRE = 110;//验证码已过期
    public static final int E_MSG_CODE = 111;//验证码无效
    public static final int E_PWD = 113;//密码不正确
    public static final int E_USER_EXIST = 114;//用户名不存在
    public static final int E_PERMISION = 115;//没有权限

    public static final int E_PARAM = -1;//缺少参数值
    public static final int E_CODE = -2;//二维码错误
    public static final int E_MARRY = -3;//没有匹配的信息
    public static final int E_MSG_SEND = -4;//短信发送失败
    public static final int E_ = -5;//账号已注册过

    public static final int E_SYSTEM = 201;//系统异常，操作失败
    public static final int E_NO_RESOURCE = 202;//没有相关数据返回
    public static final int E_RE_LOGIN = 203;//操作成功，修改了电话号码，需要重新登录
    public static final int E_NEED_LOGIN = 400;//用户没有登录
    public static final int E_TOKEN_EXPIRE = 401;//用户token过期
    public static final int E_LOGOUT = 402;//登出成功


    /**
     * 需要提示给用户
     * errno 112:表示应用版本为最新; -4:网络不可用（无法上网）
     * @param errno
     * @return
     */
    public static boolean isNeedTipToUser(int errno) {
        return errno != 0 && errno != E_IO_EXCEPTION;
    }
}
