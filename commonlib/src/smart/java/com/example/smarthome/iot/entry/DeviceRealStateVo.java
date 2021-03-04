package com.example.smarthome.iot.entry;

import java.util.List;

public class DeviceRealStateVo {

    /**
     * realState : {"dev_id":"D0CF5EFFFE541230","dev_type":"131301","stateInfos":[{"dev_ep_id":1,"state":"{\"State\":\"1\"}"}]}
     */

    private RealStateBean realState;

    public RealStateBean getRealState() {
        return realState;
    }

    public void setRealState(RealStateBean realState) {
        this.realState = realState;
    }

    public static class RealStateBean {
        /**
         * dev_id : D0CF5EFFFE541230
         * dev_type : 131301
         * stateInfos : [{"dev_ep_id":1,"state":"{\"State\":\"1\"}"}]
         */

        private String dev_id;
        private String dev_type;
        private List<StateInfosBean> stateInfos;

        public String getDev_id() {
            return dev_id;
        }

        public void setDev_id(String dev_id) {
            this.dev_id = dev_id;
        }

        public String getDev_type() {
            return dev_type;
        }

        public void setDev_type(String dev_type) {
            this.dev_type = dev_type;
        }

        public List<StateInfosBean> getStateInfos() {
            return stateInfos;
        }

        public void setStateInfos(List<StateInfosBean> stateInfos) {
            this.stateInfos = stateInfos;
        }

        public static class StateInfosBean {
            /**
             * dev_ep_id : 1
             * state : {"State":"1"}
             */

            private int dev_ep_id;
            private String state;

            public int getDev_ep_id() {
                return dev_ep_id;
            }

            public void setDev_ep_id(int dev_ep_id) {
                this.dev_ep_id = dev_ep_id;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }
        }
    }
}
