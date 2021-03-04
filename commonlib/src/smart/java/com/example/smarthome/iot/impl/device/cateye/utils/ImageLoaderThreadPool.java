package com.example.smarthome.iot.impl.device.cateye.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import com.eques.icvss.utils.ELog;
import com.example.smarthome.R;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载类 http://blog.csdn.net/lmj623565791/article/details/41874561
 * 
 * @author zhy
 * 
 */
public class ImageLoaderThreadPool {
    private static ImageLoaderThreadPool mInstance;

    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    private static final int DEAFULT_THREAD_COUNT = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的Handler
     */
    //private Handler mUIHandler;

    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;

    private boolean isDiskCacheEnable = true;

    private static final String TAG = "ImageLoader";

    public enum Type {
        FIFO, LIFO;
    }

    private ImageLoaderThreadPool(int threadCount, Type type) {
        init(threadCount, type);
    }

    /**
     * 初始化
     * 
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        initBackThread();

        // 获取我们应用的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        // 创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;
        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 初始化后台轮询线程
     */
    private void initBackThread() {
        // 后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // 线程池去取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放一个信号量
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            };
        };

        mPoolThread.start();
    }

    public static ImageLoaderThreadPool getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoaderThreadPool.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderThreadPool(DEAFULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static ImageLoaderThreadPool getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoaderThreadPool.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderThreadPool(threadCount, type);
                }
            }
        }
        return mInstance;
    }
    
    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler = new Handler(Looper.getMainLooper()){
          public void handleMessage(Message msg) {
           // 获取得到图片，为imageview回调设置图片
              ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
              Bitmap bm = holder.bitmap;
              ImageView imageview = holder.imageView;
              String imageTag = holder.imageTag;
              
              String tag = imageview.getTag().toString();
              if(tag == imageTag){
                  if(bm != null){
                      imageview.setImageBitmap(bm);    
                  }else{
//                      imageview.setImageResource(R.drawable.image_empty_photo);
                  }
              }
          };  
    };

    /**
     * 根据path为imageview设置图片
     * @param urlStr
     * @param imageView
     * @param imagePath
     */
    public void loadImage(final String urlStr, final ImageView imageView, final String imagePath, final String imageTag){
		 // 根据path在缓存中获取bitmap
        Bitmap bm = getBitmapFromLruCache(imageTag);

        if (bm != null) {
            refreashBitmap(imageTag, imageView, bm);
        } else {
            addTask(buildTask(urlStr, imageView, imagePath, imageTag));
        }
    }
    
    public Bitmap loadImageFromLocal(final String path,
            final ImageView imageView) {
        Bitmap bm;
        // 加载图片
        // 图片的压缩
        // 1、获得图片需要显示的大小
        ImageSizeUtil.ImageSize imageSize = ImageSizeUtil.getImageViewSize(imageView);
        // 2、压缩图片
        bm = decodeSampledBitmapFromPath(path, imageSize.width,
                imageSize.height);
        return bm;
    }
    
    /**
     * 根据图片需要显示的宽和高对图片进行压缩
     * 
     * @param path
     * @param width
     * @param height
     * @return
     */
    protected Bitmap decodeSampledBitmapFromPath(String path, int width,
            int height) {
        // 获得图片的宽和高，并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
                width, height);

        // 使用获得到的InSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 判断图片是否正常,如果大小为0或者损坏,删除后再重新下载
     * @param filePath
     */
    private boolean checkImageIsNormal(String filePath) {
        boolean isNormal = true;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options); // filePath代表图片路径
        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
            // 表示图片已损毁
            isNormal = false;
        }
        return isNormal;
    }

    /**
     * 根据传入的参数，新建一个任务
     * @param urlStr
     * @param imageView
     * @param imagePath
     * @return
     */
    private Runnable buildTask(final String urlStr, final ImageView imageView, final String imagePath, final String imageTag){
        return new Runnable() {
            public void run() {
                Bitmap bm = null;
                
                File file = new File(imagePath);
                if (file.exists() && checkImageIsNormal(imagePath))// 如果在本地文件中发现
                {
                    bm = loadImageFromLocal(file.getAbsolutePath(), imageView);
                    
                    if(bm == null){
                        ELog.e(TAG, " loadImageFromLocal bm == null...");
                    }
                } else {
                    InputStream inputStream = HttpsURLConnectionHelp.requesByGetToStream(urlStr);
                    if (inputStream != null) {
                        boolean downloadState = FileHelper.writeFile(imagePath, inputStream);
                        if (downloadState)// 如果下载成功
                        {
                            bm = loadImageFromLocal(file.getAbsolutePath(), imageView);
                        }
                    } else {
                        ELog.e(TAG, "ERROR, ImageLoader: getImageFromHttps InputStream is null...");
                    }
                }
                
                // 3、把图片加入到缓存
                addBitmapToLruCache(imageTag, bm);
                refreashBitmap(imageTag, imageView, bm);
                mSemaphoreThreadPool.release(); // 释放阻塞线程
            }
        };
    }

    /**
     * 从任务队列取出一个方法
     * 
     * @return
     */
    private Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }
        return null;
    }

    private void refreashBitmap(final String imageTag, final ImageView imageView,
            Bitmap bm) {
        Message message = Message.obtain();
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.imageTag = imageTag;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    /**
     * 将图片加入LruCache
     * 
     * @param
     * @param bm
     */
    protected void addBitmapToLruCache(String tag, Bitmap bm) {
        if (getBitmapFromLruCache(tag) == null) {
            if (bm != null)
                mLruCache.put(tag, bm);
        }
    }
    
    /**
     * 清理本地缓存
     */
    public void clearCache() {
        if (mLruCache != null) {
            if (mLruCache.size() > 0) {
                mLruCache.evictAll();
            }
            //mLruCache = null;
        }
    }
    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if (mPoolThreadHandler == null)
                mSemaphorePoolThreadHandler.acquire();
        } catch (InterruptedException e) {
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 获得缓存图片的地址
     * 
     * @param context
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName)
    {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()))
        {
            cachePath = context.getExternalCacheDir().getPath();
        } else
        {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
    /**
     * 根据path在缓存中获取bitmap
     * 
     * @param key
     * @return
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    private class ImgBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String imageTag; //图片本地路径作为加载图片Tag
    }
}
