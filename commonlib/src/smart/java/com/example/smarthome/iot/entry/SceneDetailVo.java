package com.example.smarthome.iot.entry;


/**
 * {
 *     "scene": {
 *       "senceId": "1149848955845218305",
 *       "sceneName": "测试1",
 *       "icon": "icon_iot_scene_holiday",
 *       "familyId": "1149153130102001664",
 *       "sceneType": "create",
 *       "devOnlineNum": 1,
 *       "devNum": 1,
 *       "iseffective": false,
 *       "taskTpye": "once",
 *       "tasks": [
 *         {
 *           "deviceInfo": {
 *             "deviceId": "D0CF5EFFFE541230",
 *             "gatewayId": "F88479057B4A",
 *             "deviceType": "131301",
 *             "supplierId": "DYxjSE290221",
 *             "deviceName": "海令一路开关",
 *             "deviceIcon": "https://img.xhmind.com/20190620110451650.png",
 *             "online": true,
 *             "field": {
 *               "switch1": "1",
 *               "which_button": "0"
 *             }
 *           },
 *           "taskInfo": {
 *             "isdelayed": true,
 *             "delayTime": 9000
 *           }
 *         }
 *       ]
 *     }
 *   }
 *
 */
public class SceneDetailVo {

    private SmartInfoVo.FamilysBean.ScenesBean scene;

    public SmartInfoVo.FamilysBean.ScenesBean getScene() {
        return scene;
    }

    public void setScene(SmartInfoVo.FamilysBean.ScenesBean scene) {
        this.scene = scene;
    }
}
