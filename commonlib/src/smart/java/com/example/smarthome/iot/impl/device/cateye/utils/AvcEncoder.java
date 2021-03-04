package com.example.smarthome.iot.impl.device.cateye.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Environment;

import com.eques.icvss.utils.ELog;
import com.example.smarthome.iot.impl.device.cateye.activity.VideoCallActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static android.media.MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
import static android.media.MediaCodec.BUFFER_FLAG_KEY_FRAME;


public class AvcEncoder {
    private final static String TAG = "MeidaCodec";

    //编码类型
    private final static String mime = "video/avc";

    private MediaCodec mediaCodec;
    private int m_width;
    private int m_height;
    private int m_framerate;

    public byte[] configbyte;
    //转成后的数据
    private byte[] yuv420 = null;

    DatagramSocket socket;

    private int colorFormat;

    public AvcEncoder(int width, int height, int framerate, int bitrate) {
        m_width = width;
        m_height = height;
        m_framerate = framerate;
        //这里的大小要通过计算，而不是网上简单的 *3/2
        yuv420 = new byte[getYuvBuffer(width, height)];

        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);

        //确定当前MediaCodec支持的图像格式
        colorFormat = selectColorFormat();
        ELog.e("wzj", "AvcEncoder -->colorFormat: ", colorFormat);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);

        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        try {
            mediaCodec = MediaCodec.createEncoderByType("video/avc");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //配置编码器参数
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        //启动编码器
        mediaCodec.start();
        //创建保存编码后数据的文件
        createfile();
    }

    public boolean isSemi() {
        return colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
    }

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/eques.h264";
    private BufferedOutputStream outputStream;

    private void createfile() {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void StopEncoder() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRuning = false;

    public void StopThread() {
        isRuning = false;
        try {
            StopEncoder();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartEncoderThread() {
        Thread EncoderThread = new Thread(new Runnable() {

            @Override
            public void run() {
                isRuning = true;
                byte[] input;
                long pts;
                long generateIndex = 0;

                while (isRuning) {
                    //访问VideoCallActivity用来缓冲待解码数据的队列
                    //从缓冲队列中取出一帧
                    try {
                        input = VideoCallActivity.YUVQueue.poll(100, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        continue;
                    }

                    if (input == null) {
                        continue;
                    }

                    if (input != null) {
                        try {
                            long startMs = System.currentTimeMillis();
                            //编码器输入缓冲区
                            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
                            //编码器输出缓冲区
                            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
                            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                            if (inputBufferIndex >= 0) {
                                pts = computePresentationTime(generateIndex);

                                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];

                                inputBuffer.clear();
                                inputBuffer.put(input, 0, input.length);

                                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                                generateIndex += 1;
                            }

                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                            while (outputBufferIndex >= 0) {
                                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                                byte[] outData = new byte[bufferInfo.size];

                                outputBuffer.get(outData);
                                if ((bufferInfo.flags & BUFFER_FLAG_CODEC_CONFIG) != 0) {
                                    configbyte = new byte[bufferInfo.size];
                                    configbyte = outData;
                                } else if ((bufferInfo.flags & BUFFER_FLAG_KEY_FRAME) != 0) {
                                    byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                                    System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);

                                    //把编码后的视频帧从编码器输出缓冲区中拷贝出来
                                    System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
                                    sendSocketPacket(keyframe);
                                } else {
                                    sendSocketPacket(outData);
                                }
                                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    } else {

                    }
                }
            }
        });
        EncoderThread.start();
    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / m_framerate;
    }

    private void sendSocketPacket(byte[] data) {
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), 10000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //在结束视频时，容易出现socket为空的问题。
        if (null == socket) {
            ELog.w(TAG, "socket is null. send error !");
            return;
        }
        try {
            if (packet != null) {
                socket.send(packet);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        data = null;
    }

    //通过mimeType确定支持的格式
    public static int selectColorFormat() {
        MediaCodecInfo codecInfo = selectCodec(mime);
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mime);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            if (isRecognizedFormat(colorFormat)) {
                return colorFormat;
            }
        }
        return 0;   // not reached
    }

    private static boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            // these are the formats we know how to handle for this test
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
//            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:

//            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
//            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                return false;
        }
    }


    private static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    //计算YUV的buffer的函数，需要根据文档计算，而不是简单“*3/2”
    public int getYuvBuffer(int width, int height) {
        // stride = ALIGN(width, 16)
        int stride = (int) Math.ceil(width / 16.0) * 16;
        // y_size = stride * height
        int y_size = stride * height;
        // c_stride = ALIGN(stride/2, 16)
        int c_stride = (int) Math.ceil(width / 32.0) * 16;
        // c_size = c_stride * height/2
        int c_size = c_stride * height / 2;
        // size = y_size + c_size * 2
        return y_size + c_size * 2;
    }

}