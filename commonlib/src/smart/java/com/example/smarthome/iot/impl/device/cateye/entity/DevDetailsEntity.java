package com.example.smarthome.iot.impl.device.cateye.entity;

import java.io.Serializable;

/**
 * Date：2019/8/30
 * Description：设备详情
 * @author hgz
 */
public class DevDetailsEntity implements Serializable {

    // 人体侦测开-关状态 0 关闭 1 开启
    int pir_stat = -2;
    // 自动报警时间
    int alm_dly = -2;
    // 灵敏度高低
    int alm_sens = -2;
    // 报警模式 0 抓拍 1 录像
    int alm_mod = -2;
    // 报警铃音
    int alm_tone = -2;
    // 报警音量等级
    int alm_vol = -2;
    // 门铃铃音
    int ring_tone = -2;
    // 门铃音量
    int ring_vol = -2;
    // 门铃灯开-关状态 0 关闭 1 开启
    int ring_led = -2;
    // 分辨率，取值 [0, 1]。0 - 720P，1 - VGA
    int resol = -2;
    // 报警拍照的照片张数，取值 {1, 3, 5}
    int alm_pic_num = -2;
    // 逗留报警时间，取值 {5, 10, 15, 20}
    int linger_alm_time = -2;
    // 屏幕亮度等级，取值 [1, 15]
    int lcd_lum = -2;
    // 屏幕超时，取值 {10, 30}
    int lcd_timeout = -2;
    // T1工作模式
    int wifi_save_power = -2;
    // 0 自动 1 一直白天 2 一直夜晚
    int daynight_switch = -2;
    // 锁未关门提醒
    int lock_off_remind = -2;
    // 设备的分辨率
    int camera_width = -2;
    int camera_height = -2;

    // 设备软件版本
    String rev = "V1.00.00";
    // T1帧率
    int framerate = -2;
    // 锁版本号
    int vnum = -2;
    // 电量等级，取值 [0, 4]
    int bat_lvl = -2;
    // 充电状态，取值 [0, 2]，0 - 未插充电器，1 - 充电中，2 - 充满
    int chg_stat = -2;
    // 信号等级，取值 [0, 3]
    int sig_lvl = -2;
    // sd卡插拔状态，取值 [0, 1]。0 - 拔SD卡，1 - 插SD卡
    int sd_stat = -2;
    // sd卡总量，单位：MB
    int sd_total = -2;
    // sd卡余量，单位：MB
    int sd_rem = -2;

    // 锁状态
    int lock_state = -2;
    // 锁电量
    int battery = -2;
    // 主舌锁状态
    int main_bolt_state = -2;
    // 反锁状态
    int back_lock_state = -2;

    public DevDetailsEntity() {
    }

    /**
     *  E系列主要设置参数
     * @param pir_stat
     * @param alm_dly
     * @param alm_sens
     * @param alm_mod
     * @param alm_tone
     * @param alm_vol
     * @param ring_tone
     * @param ring_vol
     * @param ring_led
     * @param resol
     * @param alm_pic_num
     * @param linger_alm_time
     * @param lcd_lum
     * @param lcd_timeout
     * @param wifi_save_power
     * @param daynight_switch
     * @param lock_off_remind
     * @param camera_width
     * @param camera_height
     */
    public DevDetailsEntity(
            int pir_stat,
            int alm_dly,
            int alm_sens,
            int alm_mod,
            int alm_tone,
            int alm_vol,
            int ring_tone,
            int ring_vol,
            int ring_led,
            int resol,
            int alm_pic_num,
            int linger_alm_time,
            int lcd_lum,
            int lcd_timeout,
            int wifi_save_power,
            int daynight_switch,
            int lock_off_remind,
            int camera_width,
            int camera_height) {
        this.pir_stat = pir_stat;
        this.alm_dly = alm_dly;
        this.alm_sens = alm_sens;
        this.alm_mod = alm_mod;
        this.alm_tone = alm_tone;
        this.alm_vol = alm_vol;
        this.ring_tone = ring_tone;
        this.ring_vol = ring_vol;
        this.ring_led = ring_led;
        this.resol = resol;
        this.alm_pic_num = alm_pic_num;
        this.linger_alm_time = linger_alm_time;
        this.lcd_lum = lcd_lum;
        this.lcd_timeout = lcd_timeout;
        this.wifi_save_power = wifi_save_power;
        this.daynight_switch = daynight_switch;
        this.lock_off_remind = lock_off_remind;
        this.camera_width = camera_width;
        this.camera_height = camera_height;
    }

    /**
     * E系列状态值
     * @param rev
     * @param framerate
     * @param vnum
     * @param bat_lvl
     * @param chg_stat
     * @param sig_lvl
     * @param sd_stat
     * @param sd_total
     * @param sd_rem
     * @param lock_state
     * @param battery
     * @param main_bolt_state
     * @param back_lock_state
     */
    public DevDetailsEntity(
            String rev,
            int framerate,
            int vnum,
            int bat_lvl,
            int chg_stat,
            int sig_lvl,
            int sd_stat,
            int sd_total,
            int sd_rem,
            int lock_state,
            int battery,
            int main_bolt_state,
            int back_lock_state) {
        this.rev = rev;
        this.framerate = framerate;
        this.vnum = vnum;
        this.bat_lvl = bat_lvl;
        this.chg_stat = chg_stat;
        this.sig_lvl = sig_lvl;
        this.sd_stat = sd_stat;
        this.sd_total = sd_total;
        this.sd_rem = sd_rem;
        this.lock_state = lock_state;
        this.battery = battery;
        this.main_bolt_state = main_bolt_state;
        this.back_lock_state = back_lock_state;
    }

    public int getPir_stat() {
        return pir_stat;
    }

    public void setPir_stat(int pir_stat) {
        this.pir_stat = pir_stat;
    }

    public int getAlm_dly() {
        return alm_dly;
    }

    public void setAlm_dly(int alm_dly) {
        this.alm_dly = alm_dly;
    }

    public int getAlm_sens() {
        return alm_sens;
    }

    public void setAlm_sens(int alm_sens) {
        this.alm_sens = alm_sens;
    }

    public int getAlm_mod() {
        return alm_mod;
    }

    public void setAlm_mod(int alm_mod) {
        this.alm_mod = alm_mod;
    }

    public int getAlm_tone() {
        return alm_tone;
    }

    public void setAlm_tone(int alm_tone) {
        this.alm_tone = alm_tone;
    }

    public int getAlm_vol() {
        return alm_vol;
    }

    public void setAlm_vol(int alm_vol) {
        this.alm_vol = alm_vol;
    }

    public int getRing_tone() {
        return ring_tone;
    }

    public void setRing_tone(int ring_tone) {
        this.ring_tone = ring_tone;
    }

    public int getRing_vol() {
        return ring_vol;
    }

    public void setRing_vol(int ring_vol) {
        this.ring_vol = ring_vol;
    }

    public int getRing_led() {
        return ring_led;
    }

    public void setRing_led(int ring_led) {
        this.ring_led = ring_led;
    }

    public int getResol() {
        return resol;
    }

    public void setResol(int resol) {
        this.resol = resol;
    }

    public int getAlm_pic_num() {
        return alm_pic_num;
    }

    public void setAlm_pic_num(int alm_pic_num) {
        this.alm_pic_num = alm_pic_num;
    }

    public int getLinger_alm_time() {
        return linger_alm_time;
    }

    public void setLinger_alm_time(int linger_alm_time) {
        this.linger_alm_time = linger_alm_time;
    }

    public int getLcd_lum() {
        return lcd_lum;
    }

    public void setLcd_lum(int lcd_lum) {
        this.lcd_lum = lcd_lum;
    }

    public int getLcd_timeout() {
        return lcd_timeout;
    }

    public void setLcd_timeout(int lcd_timeout) {
        this.lcd_timeout = lcd_timeout;
    }

    public int getWifi_save_power() {
        return wifi_save_power;
    }

    public void setWifi_save_power(int wifi_save_power) {
        this.wifi_save_power = wifi_save_power;
    }

    public int getDaynight_switch() {
        return daynight_switch;
    }

    public void setDaynight_switch(int daynight_switch) {
        this.daynight_switch = daynight_switch;
    }

    public int getLock_off_remind() {
        return lock_off_remind;
    }

    public void setLock_off_remind(int lock_off_remind) {
        this.lock_off_remind = lock_off_remind;
    }

    public int getCamera_width() {
        return camera_width;
    }

    public void setCamera_width(int camera_width) {
        this.camera_width = camera_width;
    }

    public int getCamera_height() {
        return camera_height;
    }

    public void setCamera_height(int camera_height) {
        this.camera_height = camera_height;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public int getVnum() {
        return vnum;
    }

    public void setVnum(int vnum) {
        this.vnum = vnum;
    }

    public int getBat_lvl() {
        return bat_lvl;
    }

    public void setBat_lvl(int bat_lvl) {
        this.bat_lvl = bat_lvl;
    }

    public int getChg_stat() {
        return chg_stat;
    }

    public void setChg_stat(int chg_stat) {
        this.chg_stat = chg_stat;
    }

    public int getSig_lvl() {
        return sig_lvl;
    }

    public void setSig_lvl(int sig_lvl) {
        this.sig_lvl = sig_lvl;
    }

    public int getSd_stat() {
        return sd_stat;
    }

    public void setSd_stat(int sd_stat) {
        this.sd_stat = sd_stat;
    }

    public int getSd_total() {
        return sd_total;
    }

    public void setSd_total(int sd_total) {
        this.sd_total = sd_total;
    }

    public int getSd_rem() {
        return sd_rem;
    }

    public void setSd_rem(int sd_rem) {
        this.sd_rem = sd_rem;
    }

    public int getLock_state() {
        return lock_state;
    }

    public void setLock_state(int lock_state) {
        this.lock_state = lock_state;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getMain_bolt_state() {
        return main_bolt_state;
    }

    public void setMain_bolt_state(int main_bolt_state) {
        this.main_bolt_state = main_bolt_state;
    }

    public int getBack_lock_state() {
        return back_lock_state;
    }

    public void setBack_lock_state(int back_lock_state) {
        this.back_lock_state = back_lock_state;
    }

    @Override
    public String toString() {
        return "DevDetailsEntity{" +
                "pir_stat=" + pir_stat +
                ", alm_dly=" + alm_dly +
                ", alm_sens=" + alm_sens +
                ", alm_mod=" + alm_mod +
                ", alm_tone=" + alm_tone +
                ", alm_vol=" + alm_vol +
                ", ring_tone=" + ring_tone +
                ", ring_vol=" + ring_vol +
                ", ring_led=" + ring_led +
                ", resol=" + resol +
                ", alm_pic_num=" + alm_pic_num +
                ", linger_alm_time=" + linger_alm_time +
                ", lcd_lum=" + lcd_lum +
                ", lcd_timeout=" + lcd_timeout +
                ", wifi_save_power=" + wifi_save_power +
                ", daynight_switch=" + daynight_switch +
                ", lock_off_remind=" + lock_off_remind +
                ", camera_width=" + camera_width +
                ", camera_height=" + camera_height +
                ", rev='" + rev + '\'' +
                ", framerate=" + framerate +
                ", vnum=" + vnum +
                ", bat_lvl=" + bat_lvl +
                ", chg_stat=" + chg_stat +
                ", sig_lvl=" + sig_lvl +
                ", sd_stat=" + sd_stat +
                ", sd_total=" + sd_total +
                ", sd_rem=" + sd_rem +
                ", lock_state=" + lock_state +
                ", battery=" + battery +
                ", main_bolt_state=" + main_bolt_state +
                ", back_lock_state=" + back_lock_state +
                '}';
    }
}
