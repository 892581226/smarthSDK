package com.example.smarthome.iot.entry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * author: glq
 * date: 2019/6/13 10:04
 * description:
 * update: 2019/6/13
 * version:
 */
public class DeviceListVo {

    private List<ProductCollectionBean> product_collection;

    public List<ProductCollectionBean> getProduct_collection() {
        return product_collection;
    }

    public void setProduct_collection(List<ProductCollectionBean> product_collection) {
        this.product_collection = product_collection;
    }

    public static class ProductCollectionBean implements Parcelable {
        /**
         * deviceKind : 开关
         * dev_list : [{"deviceType":"131301","deviceIcon":"xx","deviceName":"海令一路开关","devNo":"1313010001"},{"deviceType":"131302","deviceIcon":"xx","deviceName":"海令二路开关","devNo":"1313010002"},{"deviceType":"131303","deviceIcon":"xx","deviceName":"海令三路开关","devNo":"1313010003"},{"deviceType":"131304","deviceIcon":"xx","deviceName":"海令四路开关","devNo":"1313010004"},{"deviceType":"131305","deviceIcon":"xx","deviceName":"海令六路开关","devNo":"1313010005"}]
         */

        private String deviceKind;
        private List<DevListBean> dev_list;

        public String getDeviceKind() {
            return deviceKind;
        }

        public void setDeviceKind(String deviceKind) {
            this.deviceKind = deviceKind;
        }

        public List<DevListBean> getDev_list() {
            return dev_list;
        }

        public void setDev_list(List<DevListBean> dev_list) {
            this.dev_list = dev_list;
        }

        public static class DevListBean implements Parcelable{
            /**
             * deviceType : 131301
             * deviceIcon : xx
             * deviceName : 海令一路开关
             * devNo : 1313010001
             */

            private String deviceType;
            private String actualIcon;//实物图logo;对应商家对应的设备图片
            private String deviceName;
            private String devNo;
            private String sensorType;
            private String virtuaIcon;//概念图logo;对应UI提供的图片
            private String sensor;

            @Override
            public String toString() {
                return "DevListBean{" +
                        "deviceType='" + deviceType + '\'' +
                        ", actualIcon='" + actualIcon + '\'' +
                        ", deviceName='" + deviceName + '\'' +
                        ", devNo='" + devNo + '\'' +
                        ", sensorType='" + sensorType + '\'' +
                        ", virtuaIcon='" + virtuaIcon + '\'' +
                        ", sensor='" + sensor + '\'' +
                        '}';
            }

            public String getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(String deviceType) {
                this.deviceType = deviceType;
            }

            public String getActualIcon() {
                return actualIcon;
            }

            public void setActualIcon(String actualIcon) {
                this.actualIcon = actualIcon;
            }

            public String getDeviceName() {
                return deviceName;
            }

            public void setDeviceName(String deviceName) {
                this.deviceName = deviceName;
            }

            public String getDevNo() {
                return devNo;
            }

            public void setDevNo(String devNo) {
                this.devNo = devNo;
            }

            public String getSensorType() {
                return sensorType;
            }

            public void setSensorType(String sensorType) {
                this.sensorType = sensorType;
            }

            public String getVirtuaIcon() {
                return virtuaIcon;
            }

            public void setVirtuaIcon(String virtuaIcon) {
                this.virtuaIcon = virtuaIcon;
            }

            public String getSensor() {
                return sensor;
            }

            public void setSensor(String sensor) {
                this.sensor = sensor;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.deviceType);
                dest.writeString(this.actualIcon);
                dest.writeString(this.deviceName);
                dest.writeString(this.devNo);
                dest.writeString(this.sensorType);
                dest.writeString(this.virtuaIcon);
                dest.writeString(this.sensor);
            }

            public DevListBean() {
            }

            protected DevListBean(Parcel in) {
                this.deviceType = in.readString();
                this.actualIcon = in.readString();
                this.deviceName = in.readString();
                this.devNo = in.readString();
                this.sensorType = in.readString();
                this.virtuaIcon = in.readString();
                this.sensor = in.readString();
            }

            public static final Creator<DevListBean> CREATOR = new Creator<DevListBean>() {
                @Override
                public DevListBean createFromParcel(Parcel source) {
                    return new DevListBean(source);
                }

                @Override
                public DevListBean[] newArray(int size) {
                    return new DevListBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.deviceKind);
            dest.writeList(this.dev_list);
        }

        public ProductCollectionBean() {
        }

        protected ProductCollectionBean(Parcel in) {
            this.deviceKind = in.readString();
            this.dev_list = new ArrayList<DevListBean>();
            in.readList(this.dev_list, DevListBean.class.getClassLoader());
        }

        public static final Creator<ProductCollectionBean> CREATOR = new Creator<ProductCollectionBean>() {
            @Override
            public ProductCollectionBean createFromParcel(Parcel source) {
                return new ProductCollectionBean(source);
            }

            @Override
            public ProductCollectionBean[] newArray(int size) {
                return new ProductCollectionBean[size];
            }
        };
    }
}
