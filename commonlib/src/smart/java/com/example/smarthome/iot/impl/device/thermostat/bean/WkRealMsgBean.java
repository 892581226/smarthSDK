package com.example.smarthome.iot.impl.device.thermostat.bean;

import java.io.Serializable;
import java.util.List;

public class WkRealMsgBean implements Serializable {
    private WkRealMagData realState;

    public WkRealMagData getRealState() {
        return realState;
    }

    public void setRealState(WkRealMagData realState) {
        this.realState = realState;
    }

    public class WkRealMagData implements Serializable{
        private String dev_id;
        private String dev_type;
        private List<WkRealMsgData2> stateInfos;

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

        public List<WkRealMsgData2> getStateInfos() {
            return stateInfos;
        }

        public void setStateInfos(List<WkRealMsgData2> stateInfos) {
            this.stateInfos = stateInfos;
        }

        public class WkRealMsgData2 implements Serializable{
            private Integer dev_ep_id;
            private String state;

            public Integer getDev_ep_id() {
                return dev_ep_id;
            }

            public void setDev_ep_id(Integer dev_ep_id) {
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
