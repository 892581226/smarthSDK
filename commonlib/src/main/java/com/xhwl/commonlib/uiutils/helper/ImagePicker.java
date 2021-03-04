package com.xhwl.commonlib.uiutils.helper;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;


import com.example.smarthome.BuildConfig;
import com.xhwl.commonlib.http.Constant;
import com.xhwl.commonlib.uiutils.BitmapUtils;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.PermissionUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * <p> 拍照、选择图库 <p/>
 * Created by zw on 2017/9/19 13:36.
 */

public class ImagePicker {
    public static final int PHOTO_ALBUM_CODE = 0x0101;// 本地
    public static final int PHOTO_CAMERA_CODE = 0x0102;// 拍照
    public static final int PHOTO_CROP_CODE = 0x0103;//剪切
    private static final String TAG = "ImagePicker";

    /**
     * 拍照 适配FileProvider
     *
     * @param activity
     * @param defaultPhotoDir
     * @return
     */
    public static String PickerFromCamera(final Activity activity, String defaultPhotoDir) {
        String path = null;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File photoTakeFile = new File(defaultPhotoDir, BitmapUtils.getCreatePhotoFileName("/IMG"));
            //photoTakeFile.delete();
            File parentFile = photoTakeFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!photoTakeFile.exists()) {
                try {
                    photoTakeFile.createNewFile();
                    path = photoTakeFile.getPath();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "图片创建失败！", Toast.LENGTH_SHORT).show();
                }
            }
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileProvider", photoTakeFile);
                LogUtils.e(TAG, uri.getPath());
            } else {
                uri = Uri.fromFile(photoTakeFile);
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);//取消人脸识别
            ComponentName componentName = intent.resolveActivity(activity.getPackageManager());
            if (componentName != null) {
                activity.startActivityForResult(intent, PHOTO_CAMERA_CODE);
            } else {
                LogUtils.e(TAG, "无法解析intent，捕获崩溃");
            }
            return path;
        } else {
            Toast.makeText(activity, "SD卡无效或没有插入！", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 拍照 适配FileProvider
     *
     * @param activity
     * @param defaultPhotoDir
     * @return
     */
    public static String PickerFromCamera(final Activity activity, String defaultPhotoDir,int requestCode) {
        if (PermissionUtils.checkEachSelfPermission(activity,new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        })){
            ToastUtil.showSingleToast("请打开相应权限");
            return "";
        }
        String path = null;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File photoTakeFile = new File(defaultPhotoDir, BitmapUtils.getCreatePhotoFileName("/IMG"));
            //photoTakeFile.delete();
            File parentFile = photoTakeFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!photoTakeFile.exists()) {
                try {
                    photoTakeFile.createNewFile();
                    path = photoTakeFile.getPath();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "图片创建失败！", Toast.LENGTH_SHORT).show();
                }
            }
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", photoTakeFile);
                LogUtils.e(TAG, uri.getPath());
            } else {
                uri = Uri.fromFile(photoTakeFile);
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);//取消人脸识别
            ComponentName componentName = intent.resolveActivity(activity.getPackageManager());
            if (componentName != null) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                LogUtils.e(TAG, "无法解析intent，捕获崩溃");
            }
            return path;
        } else {
            Toast.makeText(activity, "SD卡无效或没有插入！", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 相册
     *
     * @param activity
     */
    public static void PickerFromAlbum(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            activity.startActivityForResult(intent, PHOTO_ALBUM_CODE);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, PHOTO_ALBUM_CODE);
        }
    }

    /**
     * 相册
     *
     * @param activity
     */
    public static void PickerFromAlbum(Activity activity,int requestCode) {
        if (PermissionUtils.checkEachSelfPermission(activity,new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        })){
            ToastUtil.showSingleToast("请打开相应权限");
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            activity.startActivityForResult(intent, requestCode);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 返回结果
     *
     * @param activity
     * @param requestCode
     * @param data
     * @param photoPath
     * @return
     */
    @TargetApi(19)
    public static Bitmap PickerForResult(Activity activity, int requestCode, Intent data, String photoPath, int outWidth, int outHeight) {

        if (requestCode == PHOTO_ALBUM_CODE) {
            if (data == null) {
                Log.e(TAG, "data is null");
                return null;
            }

            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Uri selectedImage = data.getData();
                String imagePath = getPath(activity, selectedImage); //获取图片的绝对路径
                uri = Uri.parse("file:///" + imagePath); //将绝对路径转换为URL
            } else {
                uri = data.getData();
            }

            //Uri uri = data.getData();
            Log.e(TAG, "uri = " + uri);
            if (uri != null) {
                try {
                    String filepath = uri.getPath();

                    /*BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filepath, option);
                    option.inSampleSize = BitmapSampleSizeUtil.computeSampleSize(option, -1, 800 * 800);
                    option.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeFile(filepath, option);*/
                    Bitmap bitmap = readBitmapAutoSize(filepath, outWidth, outHeight);
                    if (bitmap != null)
                        return bitmap;
                } catch (OutOfMemoryError | NullPointerException error) {
                    error.printStackTrace();
                }
            } else {
                Log.v(TAG, "uri is null");
            }
        }

        if (requestCode == PHOTO_CAMERA_CODE) {
            try {
               /* BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photoPath, option);
                option.inSampleSize = BitmapSampleSizeUtil.computeSampleSize(option, -1, 800 * 800);
                option.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath, option);*/
                Bitmap bitmap = readBitmapAutoSize(photoPath, outWidth, outHeight);
                if (bitmap != null)
                    return bitmap;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * @param filePath
     * @param outWidth
     * @param outHeight
     * @return
     */
    public static Bitmap readBitmapAutoSize(String filePath, int outWidth, int outHeight) {
        // outWidth和outHeight是目标图片的最大宽度和高度，用作限制
        FileInputStream fs = null;
        BufferedInputStream bs = null;
        try {
            fs = new FileInputStream(filePath);
            bs = new BufferedInputStream(fs);
            BitmapFactory.Options options = setBitmapOption(filePath, outWidth,
                    outHeight);
            return BitmapFactory.decodeStream(bs, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert bs != null;
                bs.close();
                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param file
     * @param width
     * @param height
     * @return
     */
    private static BitmapFactory.Options setBitmapOption(String file, int width, int height) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        // 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
        BitmapFactory.decodeFile(file, opt);

        int outWidth = opt.outWidth; // 获得图片的实际高和宽
        int outHeight = opt.outHeight;

        opt.inDither = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        // 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
        opt.inSampleSize = 1;
        // 设置缩放比,1表示原比例，2表示原来的四分之一....
        // 计算缩放比
        Log.e(TAG, "outWidth = " + outWidth + "  outHeight = " + outHeight + "  width = " + width + "  height = " + height);
        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
            int sampleSize = (outWidth / width + outHeight / height) / 2;
            opt.inSampleSize = sampleSize;
        }
        opt.inJustDecodeBounds = false;// 最后把标志复原

       /* if (outWidth >outHeight){
            opt.outHeight = height;
        }else{
            opt.outWidth = width;
        }*/
        return opt;
    }

    //获取文件的Content uri路径
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                if (cursor != null) {
                    cursor.close();
                }
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param file
     */
    public static void startPhotoZoom(Activity activity, File file) {
        Uri sourceUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sourceUri = getImageContentUri(activity, file);
        } else {
            sourceUri = Uri.fromFile(file);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(sourceUri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 120);
        intent.putExtra("outputY", 120);
        intent.putExtra("return-data", false);//intent传递图片会导致溢出，回调方法data.getExtras().getParcelable("data")返回数据为空
        intent.putExtra(MediaStore.EXTRA_OUTPUT, sourceUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        activity.startActivityForResult(intent, PHOTO_CROP_CODE);
        // ((_Application) activity.getApplication()).setOutApp(true);
        // ((_Application) activity.getApplication()).setCropPic(true);
    }


    @TargetApi(19)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 通过地址获取压缩图片
     *
     * @param filePath
     * @param inSampleSize
     * @return
     */
    public static Bitmap getBitmapThumb(String filePath, int inSampleSize) {
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, option);
            option.inSampleSize = inSampleSize;
            option.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);
            if (bitmap != null)
                return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * mediaScanner 创建文件发条广播给系统
     * @param context
     * @param fileUrl
     */
    public static void sendFileBroadcast(Activity context,String fileUrl) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//media scanner 会得到扫描图片
        File f = new File(fileUrl);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

}
