package com.example.smarthome.iot.impl.device.cateye.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.eques.icvss.utils.ELog;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageLoaderTask extends AsyncTask<String, Void, BitmapDrawable> {

    private final String TAG = "ImageLoaderUtil";

    private Context ctx;
    private ImageView imageView; // 消息显示列表
    private String imageLocalPath; // 图片保存本地路径
    private String imageUrl;
    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, BitmapDrawable> mMemoryCache;

    public ImageLoaderTask(Context c, ImageView lv, String path) {
        initMemoryCanche();
        
        this.ctx = c;
        this.imageView = lv;
        this.imageLocalPath = path;
    }

    /**
     * 初始化图片缓存空间
     */
    public void initMemoryCanche() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;

        // 获取应用程序最大可用内存
        int maxMemory1 = ((int) Runtime.getRuntime().maxMemory()) / 1024 / 1024;
        // 应用程序已获得内存
        long totalMemory = ((int) Runtime.getRuntime().totalMemory()) / 1024 / 1024;
        // 应用程序已获得内存中未使用内存
        long freeMemory = ((int) Runtime.getRuntime().freeMemory()) / 1024 / 1024;

        Log.e(TAG, "DEBUG, ImageAdapter maxMemory1:" + maxMemory1);
        Log.e(TAG, "DEBUG, ImageAdapter totalMemory:" + totalMemory);
        Log.e(TAG, "DEBUG, ImageAdapter freeMemory:" + freeMemory);

        mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable drawable) {
                return drawable.getBitmap().getByteCount();
            }
        };
    }

    /**
     * 将一张图片存储到LruCache中。
     * 
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @param drawable
     *            LruCache的值，这里传入从网络上下载的BitmapDrawable对象。
     */
    public void addBitmapToMemoryCache(String key, BitmapDrawable drawable) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, drawable);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     * 
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的BitmapDrawable对象，或者null。
     */
    public BitmapDrawable getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 异步下载图片的任务。
     * 
     * @author guolin
     */
    @Override
    protected BitmapDrawable doInBackground(String... params) {
        imageUrl = params[0];
        Bitmap bitmap = null;
        BitmapDrawable drawable = null;
        
        if(StringUtils.isBlank(imageLocalPath)){
            ELog.e(TAG, "ERROR, ImageLoaderTask doInBackground imageLocalPath isBlank...");
            return drawable;
        }
        
        if(StringUtils.isBlank(imageUrl)){
            ELog.e(TAG, "ERROR, ImageLoaderTask doInBackground imageUrl isBlank...");
            return drawable;
        }
        
        if (FileHelper.fileIsExist(imageLocalPath)) {
            bitmap = decodeFile(imageLocalPath);
            if (bitmap == null) {
                ELog.e(TAG, "DEBUG, bitmap is Null...");
            } else {
                ELog.e(TAG, "DEBUG, return bitmap ");
            }
        } else {
            bitmap = downloadBitmap(imageUrl, imageLocalPath);
            ELog.e(TAG, "DEBUG, return bitmap ");
        }
        if (bitmap == null) {
            ELog.e(TAG, "DEBUG, bitmap is Null...");
        } else {
            // 在后台开始下载图片
            drawable = new BitmapDrawable(ctx.getResources(), bitmap);
            addBitmapToMemoryCache(imageUrl, drawable);
        }
        return drawable;
    }

    @Override
    protected void onPostExecute(BitmapDrawable drawable) {
        if (imageView != null && drawable != null) {
            imageView.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 建立HTTP请求，并获取Bitmap对象。
     * 
     * @param imageUrl
     *            图片的URL地址
     * @return 解析后的Bitmap对象
     */
    private Bitmap downloadBitmap(String imageUrl, String path) {
        Bitmap bitmap = null;

        ELog.e(TAG, "INFO, ImageloaderTask downloadBitmap imageUrl:", imageUrl);
        
        InputStream inputStream = HttpsURLConnectionHelp
                .requesByGetToStream(imageUrl);
        if (inputStream == null) {
            ELog.e(TAG, "DEBUG, bitmap is Null...");
        } else {
            boolean bo = FileHelper.writeFile(path, inputStream);
            ELog.e(TAG, "downloadBitmap bo:" + bo);
            if (bo) {
                bitmap = decodeFile(path);
                if (bitmap == null) {
                    ELog.e(TAG, "DEBUG, bitmap is Null...");
                } else {
                    ELog.e(TAG, "DEBUG, return bitmap ");
                }
            }
        }
        return bitmap;
    }

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    public Bitmap decodeFile(String path) {
        Bitmap bitmap = null;
        try {
            File file = new File(path);
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 100;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file),
                    null, o2);
        } catch (FileNotFoundException e) {

        }
        return bitmap;
    }
}
