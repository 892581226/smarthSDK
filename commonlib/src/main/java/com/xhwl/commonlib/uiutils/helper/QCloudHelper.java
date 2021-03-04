package com.xhwl.commonlib.uiutils.helper;

import com.google.gson.Gson;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.data.ILiveMessage;
import com.tencent.ilivesdk.data.msg.ILiveCustomMessage;
import com.tencent.imsdk.TIMCustomElem;
import com.xhwl.commonlib.entity.tencentcloud.CallTypeBean;
import com.xhwl.commonlib.uiutils.LogUtils;

/**
 * <p>  处理c2c聊天会话 </p>
 * create by zw at 2018/8/13 0013
 */
public class QCloudHelper {
    private static final String TAG = "QCloudHelper";

    /**
     * 发送c2c消息
     *
     * @param bean 文本内容
     */
    public static void sendC2CMsg(CallTypeBean bean, String sender, String receiver) {
        String msg = new Gson().toJson(bean, CallTypeBean.class);
        TIMCustomElem timCustomElem = new TIMCustomElem();
        timCustomElem.setData(msg.getBytes());
        if (bean.getMsgType() == CallTypeBean.MsgTypeEnum.CALL_TO.getMsgType()) { //主呼发送推送描述给ios消息
            timCustomElem.setDesc(bean.getName() + "来电");
        }
        ILiveCustomMessage message = new ILiveCustomMessage(timCustomElem);
        message.setSender(sender);
        message.setMsgType(ILiveMessage.ILIVE_CONVERSATION_C2C);
        ILiveCallBack callBack = new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                // 处理发消息成功
                LogUtils.w(TAG, "send msg success" + bean.toString());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                // 处理发消息失败
//                ToastUtil.show("send msg fail:errCode:" + errMsg + "==errMsg:" + errMsg);
                LogUtils.e(TAG, "msg=" + msg + "errCode:" + errCode + "===errMsg:" + errMsg);
            }
        };
        ILiveRoomManager.getInstance().sendC2CMessage(receiver, message, callBack);
    }
}
