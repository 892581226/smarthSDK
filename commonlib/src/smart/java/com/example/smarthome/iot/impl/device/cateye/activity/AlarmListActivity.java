package com.example.smarthome.iot.impl.device.cateye.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.utils.ELog;
import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.cateye.entity.AlarmMessageInfo;
import com.example.smarthome.iot.impl.device.cateye.entity.EventBusEntity;
import com.example.smarthome.iot.impl.device.cateye.utils.ICVSSUserModule;
import com.xhwl.commonlib.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AlarmListActivity extends BaseActivity implements OnClickListener{

    public final static int MSG_GET_ALARM_LIST = 1001;
    private ListView lvAlarmList;
    private TextView tv_nowaDays;
    private AlarmListAdapter alarmListAdapter;
    private ICVSSUserInstance icvss;
    private String bidTemp;
    int dayIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmlist);
        initUI();
        initData();
        getAlarmList();
    }

    Handler mHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_ALARM_LIST:
                    ELog.i("AlarmListActivity, MSG_GET_ALARM_LIST start------->");
                    ArrayList<AlarmMessageInfo> infos = (ArrayList<AlarmMessageInfo>)msg.obj;
                    setData(infos);
                    break;
            }
        }
    };

    private void initUI(){
        TextView tv_yesterDay = (TextView) findViewById(R.id.tv_yesterDay);
        tv_nowaDays = (TextView) findViewById(R.id.tv_nowaDays);
        TextView tv_tomorrow = (TextView) findViewById(R.id.tv_tomorrow);
        
        tv_yesterDay.setOnClickListener(this);
        tv_tomorrow.setOnClickListener(this);
        
        lvAlarmList = (ListView)findViewById(R.id.lv_alarmList);
        View emptyView =  findViewById(android.R.id.empty);
        lvAlarmList.setEmptyView(emptyView);
    }
    
    private void initData() {
        icvss = ICVSSUserModule.getInstance(null).getIcvss();
        bidTemp = getIntent().getStringExtra("bid");
    }

    private void setData(ArrayList<AlarmMessageInfo> infos){
        if(alarmListAdapter == null){
            ELog.i("AlarmListActivity, alarmListAdapter == null start------->");
            alarmListAdapter = new AlarmListAdapter(this, infos, icvss);
        }else{
            alarmListAdapter.onRefersh(infos);
        }
        lvAlarmList.setAdapter(alarmListAdapter);
    }

    public void onClick(View view) {
        if (view.getId()==R.id.tv_yesterDay){
            dayIndex--;
            getAlarmList();
        }else if (view.getId()==R.id.tv_tomorrow){
            dayIndex++;
            getAlarmList();
        }
    }

    
    private void getAlarmList(){
        long start = getTimesmorning(System.currentTimeMillis()).getTime();
        long end = getTimesnight(System.currentTimeMillis()).getTime();
        
        ELog.i("AlarmListActivity, dayIndex000: ", dayIndex);
        long time = dayIndex * (1000 * 60 * 60 * 24);
        
        start += time;
        end += time;
        tv_nowaDays.setText(format(new Date(start), "yyyy-MM-dd"));

        ELog.i("AlarmListActivity, start: ", start);
        ELog.i("AlarmListActivity, end: ", end);
        ELog.i("AlarmListActivity, dayIndex111: ", dayIndex);
        ELog.i("AlarmListActivity, bidTemp: ", bidTemp);

        EventBusEntity entity = new EventBusEntity();
        entity.setAction(EventBusEntity.GET_ALARM_LIST);
        entity.setStartTime(start);
        entity.setEndTime(end);
        entity.setBid(bidTemp);
        entity.setmHandler(mHanlder);
        EventBus.getDefault().post(entity);
    }
    
    public String format(Date date, String format) {
        String dateString = null;
        DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        try {
            dateString = df.format(date);
        } catch (Exception e) {
            dateString = df.format(new Date());
        }
        return dateString;
    }
    
    // 获得当天0点时间
    public static Date getTimesmorning(long milliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得当天24点时间
    public static Date getTimesnight(long milliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.MILLISECOND, 59);
        return cal.getTime();
    }
}
