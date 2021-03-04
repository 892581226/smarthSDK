package com.xhwl.commonlib.uiutils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Stephen on 2016/9/19.
 * 图片处理工具
 */
public class ImageUtil {

    //放大缩小图片
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }

    //将Drawable转化为Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                        : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    //获得圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    //获得带倒影的图片方法
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,
                0, height / 2, width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap,
                deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                bitmap.getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 压缩图片
     */
    private static int MAX_LENGTH = 800;

    public static String getCompassPhotoPathNew(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 在此属性为true不读取原图，只读取图的头信息
        options.inJustDecodeBounds = true;
        try {
            InputStream in = new FileInputStream(new File(path));
            // 读取信息到options里面并回收否则会导致内存溢出
            BitmapFactory.decodeStream(in, null, options)/* .recycle() */;
            /**
             * 如果图片的宽高都小于MAX_LENGTHxMAX_LENGTH，则返回原图路径
             */
            if (options.outHeight < MAX_LENGTH && options.outWidth < MAX_LENGTH) {
                return path;
            } else {
                /**
                 * 取大的
                 */
                int big = options.outHeight > options.outWidth ? options.outHeight
                        : options.outWidth;

                BitmapFactory.Options options2 = new BitmapFactory.Options();

                /**
                 * 缩放比例
                 */
                int scale = big / MAX_LENGTH;
                /**
                 * 3000/MAX_LENGTH取整刚好是1,所以还是原图，不需要缩放，直接返回path
                 */
                if (scale == 1) {
                    return path;
                } else {

                    /**
                     * 用大的边除以MAX_LENGTH换算出缩放比例
                     */
                    options2.inSampleSize = scale;

                    // 在此属性为true不读取原图，只读取图的头信息
                    options2.inJustDecodeBounds = false;
                    // 流一旦读过就不会有了，所以需要才重新加载
                    InputStream in2 = new FileInputStream(new File(path));
                    Bitmap bitmap = BitmapFactory.decodeStream(in2, null,
                            options2);
                    // 判断数据库创建目录
                    File f = new File(FileUtils.DEFAULT_PHOTODIR + "temp.jpg");
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    String compassPhotoPath = FileUtils.DEFAULT_PHOTODIR + System.currentTimeMillis() + ".jpg";

                    File file = new File(compassPhotoPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(file));

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    bitmap.recycle();
                    try {
                        bos.flush();
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return path;
                    }
                    return compassPhotoPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return path;
        }

    }


    /**
     * 将图片转换成Base64编码的字符串
     *
     * @param path
     * @return base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            // 创建一个字符流大小的数组。
            data = new byte[is.available()];
            // 写入数组
            is.read(data);
            // 用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public static String getBase64TypeByFileName(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        String sub = name.substring(lastIndexOf + 1, name.length()).toLowerCase();
        if (sub.equals("jpg") || sub.equals("jpeg")) {
            return "jpg";
        }
        if (sub.equals("png")) {
            return "png";
        }
        if (sub.equals("gif")) {
            return "gif";
        }
        return "jpg";
    }

    public static String saveBitmap(Bitmap bitmap, String sSDCardPath) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmmss", Locale.getDefault());
        String temp = dateFormat.format(now);
        String saveName = temp + ".jpg";

        String savePath = sSDCardPath;
        if (TextUtils.isEmpty(sSDCardPath))
            return "";

        try {
            File path = new File(savePath);
            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(path, saveName);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return savePath + saveName;
    }

    public static String savePNGBitmap(Bitmap bitmap, String sSDCardPath)
    {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
        String temp = dateFormat.format(now);
        String saveName = temp+".png";


        String savePath = sSDCardPath ;
        if(TextUtils.isEmpty(sSDCardPath))
            return "";

        try {
            File path = new File(savePath);
            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(path, saveName);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return savePath + saveName;
    }
}
