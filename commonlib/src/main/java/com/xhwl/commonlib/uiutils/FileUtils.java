package com.xhwl.commonlib.uiutils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xhwl.commonlib.application.MyAPP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * @author zw.
 * @date 17/4/9.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    /**
     * SDCard路径
     */
    public static final String SDCARD_PATH = FileUtils.getSDPath();
    /**
     * 文件缓存根目录
     */
    public static final String CACHEDIR = "xhwl/community";
    /**
     * 图片
     */
    public static final String SAVEDIR_IMAGE = SDCARD_PATH + "/" + CACHEDIR + "/image";
    /**
     * 缓存图片
     */
    public static final String CACHE_IMAGE = createRootPath()  + "/image";
    /**
     * 图片-faceDetection
     */
    public static final String SAVEDIR_IMAGE_FACE = SAVEDIR_IMAGE + "/AIface";
    /**
     * 保存拍照图片 默认路径
     */
    public static final String DEFAULT_PHOTODIR = SDCARD_PATH + "/DCIM/Camera";
    /**
     * 语音
     */
    public static final String SAVEDIR_VOICE = SDCARD_PATH + "/" + CACHEDIR + "/voice";
    /**
     * 视频
     */
    public static final String SAVEDIR_VIDEO = SDCARD_PATH + "/" + CACHEDIR + "/video";
    /**
     * 文件信息存储
     */
    public static final String SAVEDIR_LOG = SDCARD_PATH + "/" + CACHEDIR + "/log";

    public static final String SAVEDIR_QCLOUD_LOG = SDCARD_PATH + "/" + CACHEDIR + "/cloudlog";
    /**
     * 蓝牙模块日志
     */
    public static final String SAVEDIR_BLE_LOG = SDCARD_PATH + "/" + CACHEDIR + "/blelog";

    /**
     * 人脸模块图片存放路径
     */
    public static final String SAVEDIR_AIFACE_LOG = SDCARD_PATH + "/" + CACHEDIR + "/AIFace";

    /**
     * 创建根缓存目录
     *
     * @return
     */
    public static String createRootPath() {
        String cacheRootPath = "";
        //判断sd卡是否可用
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // /sdcard/Android/data/<application package>/cache
            File file = MyAPP.getIns().getExternalCacheDir();
            if (file != null) {
                cacheRootPath = file.getPath();
            }
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = MyAPP.getIns().getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    /**
     * 递归创建文件夹－－－－－－－》这里为什么会选择用递归？影响性能和效率的，直接mkdirs()即可
     *
     * @param dirPath
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
//            if (file.getParentFile().exists()) {
//                LogUtils.i("----- 创建文件夹" + file.getAbsolutePath());
//                file.mkdir();
//                return file.getAbsolutePath();
//            } else {
//                createDir(file.getParentFile().getAbsolutePath());
//                LogUtils.i("----- 创建文件夹" + file.getAbsolutePath());
//                file.mkdir();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     *
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                LogUtils.i(TAG, "----- 创建文件" + file.getAbsolutePath());
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
                LogUtils.i(TAG, "----- 创建文件" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取图片缓存目录
     *
     * @return 创建失败, 返回""
     */
    public static String getImageCachePath() {
        String path = createDir(createRootPath() + File.separator + "img" + File.separator);
        return path;
    }

    /**
     * 获取图片裁剪缓存目录
     *
     * @return 创建失败, 返回""
     */
    public static String getImageCropCachePath() {
        String path = createDir(createRootPath() + File.separator + "imgCrop" + File.separator);
        return path;
    }

    /**
     * 将内容写入文件
     *
     * @param filePath eg:/mnt/sdcard/demo.txt
     * @param content  内容
     */
    public static void writeFileSdcard(String filePath, String content, boolean isAppend) {
        createFile(new File(filePath));
        try {
            FileOutputStream fout = new FileOutputStream(filePath, isAppend);
            byte[] bytes = content.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录一些异常状态 腾讯云
     */
    public static void writeWarnInfo(String filePath, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(DateUtils.parseDateTime(System.currentTimeMillis())).append("]")
                .append(content);
        LogUtils.w(TAG, "file content write ===" + builder.toString());
        writeFileSdcard(filePath, builder.toString(), true);
    }

    /**
     * 打开Asset下的文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static InputStream openAssetFile(Context context, String fileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * 获取Raw下的文件内容
     *
     * @param context
     * @param resId
     * @return 文件内容
     */
    public static String getFileFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveBitmap(Bitmap bm, String urlName, int quality, String defaultUrl, Context context) {
        if (bm == null) return false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File tmpDir = new File(defaultUrl);
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }
            File img = new File(tmpDir.getAbsolutePath() + "/" + urlName);
            Luban.Builder with = Luban.with(context);
            with.load(img)
                    .ignoreBy(100)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {}
                        @Override
                        public void onSuccess(File file) {
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.PNG, quality, fos);
                                fos.flush();
                                fos.close();
                                return;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(Throwable e) {}
                    }).launch();
        }
        return false;
    }

    public static boolean saveBitmap(Bitmap bm, String urlName, int quality, String defaultUrl) {
        if (bm == null) {
            return false;
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File tmpDir = new File(defaultUrl);
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }
            File img = new File(tmpDir.getAbsolutePath() + "/" + urlName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(img);
                bm.compress(Bitmap.CompressFormat.PNG, quality, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static boolean saveBitmap(Bitmap bm, Context context) {
        return saveBitmap(bm, "img-" + System.currentTimeMillis(), 100, SAVEDIR_IMAGE, context);
    }

    public static boolean saveBitmap(Bitmap bm) {
        return saveBitmap(bm, "img-" + System.currentTimeMillis(), 100, SAVEDIR_IMAGE);
    }

    public static synchronized boolean saveBitmap(Bitmap bm,File file) throws IOException{
        OutputStream os = new FileOutputStream(file);
        boolean flag = bm.compress(Bitmap.CompressFormat.PNG,100,os);
        os.flush();
        os.close();
        return flag;
    }

    public static synchronized void clearFile(File file) throws IOException{
        FileWriter fileWriter = null;
        fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * 保存网络上的图片到文件
     * @param context
     * @param url
     * @param file
     */
    public static void saveBitmap(Context context,String url,File file){
        Glide.with(context).load(url).
                asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                try {
                    if (!FileUtils.saveBitmap(resource,file)){
                        clearFile(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static File getFile(String defaultUrl, String FileUrl) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File tmpDir = new File(defaultUrl);
            if (!tmpDir.exists()) {
                tmpDir.mkdir();
            }
            File file = new File(tmpDir.getAbsolutePath() + "/" + FileUrl);
            if (!file.exists()) {
                return null;
            }
            return file;
        }
        return null;
    }

    public static File getFile(String FileUrl) {
        return getFile(CACHEDIR, FileUrl);
    }


    /**
     * 文件拷贝
     *
     * @param src  源文件
     * @param desc 目的文件
     */
    public static void fileChannelCopy(File src, File desc) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(src);
            fo = new FileOutputStream(desc);
            FileChannel in = fi.getChannel();//得到对应的文件通道
            FileChannel out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null) fo.close();
                if (fi != null) fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileLen 单位B
     * @return
     */
    public static String formatFileSizeToString(long fileLen) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileLen < 1024) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576) {
            fileSizeString = df.format((double) fileLen / 1024) + "K";
        } else if (fileLen < 1073741824) {
            fileSizeString = df.format((double) fileLen / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 删除指定文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFile(File file) throws IOException {
        return deleteFileOrDirectory(file);
    }

    /**
     * 删除指定文件，如果是文件夹，则递归删除
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFileOrDirectory(File file) throws IOException {
        try {
            if (file != null && file.isFile()) {
                return file.delete();
            }
            if (file != null && file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 删除空文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 递归删除文件夹下的子文件
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFileOrDirectory(childFiles[i]);
                }
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取文件内容
     *
     * @param path
     * @return
     */
    public static String getFileOutputString(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path), 8192);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append("\n").append(line);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SD卡路径
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }




    /**
     * 图片质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static Bitmap getimage(String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }


    /**
     * 图片按比例大小压缩方法
     * @param image （根据Bitmap图片压缩）
     * @return
     */
    public static Bitmap compressScale(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        Log.i(TAG, w + "---------------" + h);
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        float hh = 512f;
        float ww = 512f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        //return bitmap;
    }

    /**
     * 保存文件到相册
     * @param context
     * @param bmp
     * @return
     */
    public static boolean saveImageToGallery(Context context,Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "小七当家";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            if (bmp != null) {
                boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                fos.flush();
                fos.close();

                //把文件插入到系统图库
                //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

                //保存图片后发送广播通知更新数据库
                Uri uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                if (isSuccess) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void getBitmapFromUrl(String url,SimpleTarget<Bitmap> target){
        Glide.with(MyAPP.getIns()).load(url).asBitmap().into(target);
    }

    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * url转bitmap
     */
    static Bitmap bitmap = null;
    public static Bitmap returnBitMap(final String url){
        new Thread(() -> {
            URL imageurl = null;
            try {
                imageurl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        if (bitmap != null){
            return bitmap;
        } else {
            return returnBitMap(url);
        }
    }


    // TODO 海康isc云瞳抓图路径
    /**
     * 抓图路径格式：/storage/emulated/0/Android/data/com.xhwl.xhwlownerapp/files/Pictures/_20180917151634445.jpg
     */
    public static String getCaptureImagePath(Context context, String userPhone,String fileName) {
        File file = context.getExternalFilesDir(DIRECTORY_PICTURES);
        String path = file.getAbsolutePath() + File.separator + userPhone + "/" +fileName + ".jpg";
        Log.i(TAG, "getCaptureImagePath: " + path);
        return path;
    }



    // TODO 海康isc云瞳抓图路径
    /**
     * 抓图路径格式：/storage/emulated/0/Android/data/com.xhwl.xhwlownerapp/files/Pictures/_20180917151634445.jpg
     */
    public static String getCaptureImagePath(Context context, String fileName) {
        File file = context.getExternalFilesDir(DIRECTORY_PICTURES);
        String path = file.getAbsolutePath() + File.separator + fileName + ".jpg";
        Log.i(TAG, "getCaptureImagePath: " + path);
        return path;
    }



    /**
     * 录像路径格式：/storage/emulated/0/Android/data/com.xhwl.xhwlownerapp/files/Movies/_20180917151636872.mp4
     */
    public static String getLocalRecordPath(Context context) {
        File file = context.getExternalFilesDir(DIRECTORY_MOVIES);
        String path = file.getAbsolutePath() + File.separator + FileUtils.getFileName("Movies") + ".mp4";
        Log.i(TAG, "getLocalRecordPath: " + path);
        return path;
    }

    /**
     * 获取文件名称（监控点名称_年月日时分秒毫秒）
     *
     * @return 文件名称
     */
    public static String getFileName(String name) {
        Calendar calendar = Calendar.getInstance();
        return name + "_" +
                String.format(Locale.CHINA, "%04d%02d%02d%02d%02d%02d%03d",
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        calendar.get(Calendar.SECOND),
                        calendar.get(Calendar.MILLISECOND));
    }


}
