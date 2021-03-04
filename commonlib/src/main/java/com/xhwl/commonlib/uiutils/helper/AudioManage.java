package com.xhwl.commonlib.uiutils.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;


import com.xhwl.commonlib.uiutils.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by stephon on 2018/4/17.
 */

public class AudioManage {
    private MediaRecorder mMediaRecorder;  //MediaRecorder可以实现录音和录像。需要严格遵守API说明中的函数调用先后顺序.
    private String mDir;             // 文件夹的名称
    private String mCurrentFilePath;
    private static AudioManage mInstance;
    private boolean isPrepared; // 标识MediaRecorder准备完毕
    private MediaPlayer mediaPlayer;
    private Context context;

    private AudioManage(Context con, String dir) {
        mDir = dir;
        this.context = con;
        mediaPlayer = new MediaPlayer();
    }

    private OnAudioStatusUpdateListener audioStatusUpdateListener;
    private long startTime;

    /**
     * 回调“准备完毕”
     *
     * @author songshi
     */
    public interface AudioStateListener {
        void wellPrepared();    // prepared完毕
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener audioStateListener) {
        mListener = audioStateListener;
    }

    /**
     * 使用单例实现 AudioManage
     *
     * @return
     */
    //DialogManage主要管理Dialog，Dialog主要依赖Context，而且此Context必须是Activity的Context，
    //如果DialogManage写成单例实现，将是Application级别的，将无法释放，容易造成内存泄露，甚至导致错误
    public static AudioManage getInstance(Context con, String dir) {
        if (mInstance == null) {
            synchronized (AudioManage.class) {   // 同步
                if (mInstance == null) {
                    mInstance = new AudioManage(con, dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 准备录音
     */
    public File prepareAudio() {
        File file = null;
        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = GenerateFileName(); // 文件名字
            file = new File(dir + File.separator + fileName);  // 路径+文件名字
            //MediaRecorder可以实现录音和录像。需要严格遵守API说明中的函数调用先后顺序.
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            }
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder.setOutputFile(file.getAbsolutePath());    // 设置输出文件
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);    // 设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);    // 设置音频的格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);    // 设置音频的编码为AMR_NB
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            startTime = System.currentTimeMillis();
            updateMicStatus();
            isPrepared = true; // 准备结束
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 生成文件名称
     *
     * @return
     */
    private String GenerateFileName() {
        // TODO Auto-generated method stub
//        return UUID.randomUUID().toString() + ".amr"; // 音频文件格式 ,随机生成名字
        return StringUtils.getDate() + ".aac";     // 生成带有时间的名字
    }

    /**
     * 播放音频
     *
     * @param uri 也可以是本地路径
     */
    public void play(Uri uri, boolean loop) {
        try {
            if (!loop) {
                mediaPlayer.reset();
            }
            mediaPlayer.setLooping(loop);
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepareAsync();//较大的用这个
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     *
     * @param path http
     */
    public void play(String path, boolean loop) {
        try {
            if (!loop) {
                mediaPlayer.reset();
            }
            mediaPlayer.setLooping(loop);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();//较大的用这个
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        mediaPlayer.reset();
        mediaPlayer.stop();
    }

    /**
     * 释放资源
     */
    public void release() {
//        mMediaRecorder.stop();
//        mMediaRecorder.release();
//        mMediaRecorder = null;
        try {
            mMediaRecorder.stop();
        } catch (IllegalStateException e) {
            // TODO 如果当前java状态和jni里面的状态不一致，
            //e.printStackTrace();
            mMediaRecorder = null;
            mMediaRecorder = new MediaRecorder();
        } catch (Exception e) {
            mMediaRecorder = null;
            mMediaRecorder = new MediaRecorder();
        }
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    /**
     * 取消（释放资源+删除文件）
     */
    public void cancel() {
        release();
        //play("http://stor.cloudmusics.cn/mp3/2018/04/c85ffc4b1a6df43f54258709d5bbd17b.mp3");
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void deleteAudio() {
        //删除录下的音频文件
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();    //删除录音文件
            mCurrentFilePath = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        public void onUpdate(double db, long time);
    }
}
