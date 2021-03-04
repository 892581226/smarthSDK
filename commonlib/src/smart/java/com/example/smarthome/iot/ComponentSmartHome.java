//package com.example.smarthome.iot;
//
//import com.billy.cc.core.component.CC;
//import com.billy.cc.core.component.CCResult;
//import com.billy.cc.core.component.CCUtil;
//import com.billy.cc.core.component.IComponent;
//import com.example.commonlib.base.uiutils.LogUtils;
//
//public class ComponentSmartHome implements IComponent {
//    @Override
//    public String getName() {
//        return "ComponentSmartHome";
//    }
//
//    @Override
//    public boolean onCall(CC cc) {
//        String actionName = cc.getActionName();
//        switch (actionName) {
//            case "getSmartFragment": //
//                String userId = cc.getParamItem("userId");
//                boolean updateRoom = cc.getParamItem("updateRoom");
//                LogUtils.e("ComponentSmartHome",userId+"==========="+updateRoom);
//
//                //返回处理结果给调用方
//                CC.sendCCResult(cc.getCallId(), CCResult.success("getFragmentKey", HomeSmartFragment.newInstance(userId)));
//                return false;
//            case "destoryFragment":
////                HomeSmartFragment fragment = HomeSmartFragment.newInstance("13602590172",false);
//////                fragment.onDetach();
////                fragment.onDestroy();
//                return false;
//            case "updateFragment":
//                String id = cc.getParamItem("userId");
//                LogUtils.e("ComponentSmartHome updateFragment",id+"===========");
//                CC.sendCCResult(cc.getCallId(), CCResult.success("updateFragmentKey", HomeSmartFragment.newInstance(id)));
////                HomeSmartFragment smartFragment = cc.getParamItem("fragment");
////                if (smartFragment != null) {
////
////                    CC.sendCCResult(cc.getCallId(), CCResult.success());//回调结果
////                } else {
////                    //回调错误信息
////                    CC.sendCCResult(cc.getCallId(), CCResult.error("no fragment params"));
////                }
//                return false;
//            default:
//                //其它actionName当前组件暂时不能响应，可以通过如下方式返回状态码为-12的CCResult给调用方
//                CC.sendCCResult(cc.getCallId(), CCResult.errorUnsupportedActionName());
//                return false;
//        }
//    }
//}
