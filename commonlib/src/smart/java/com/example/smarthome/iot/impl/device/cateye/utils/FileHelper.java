package com.example.smarthome.iot.impl.device.cateye.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.eques.icvss.utils.ELog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileHelper {
	
	public static final String TAG = "FileHelper";

	private static final int FILE_BUFFER_SIZE = 51200;

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/";
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/";
		}
	}

	public static String getCamPath() {
		String rootPath = getRootFilePath();
		String camPicPath = rootPath + "DingDong" + File.separator;
		return camPicPath;
	}

	public static boolean fileIsExist(String filePath) {
		if (filePath == null || filePath.length() < 1) {
			Log.e(TAG,"param invalid, filePath: " + filePath);
			return false;
		}

		File f = new File(filePath);
		
		if (!f.exists()) {
			return false;
		}
		return true;
	}

	public static InputStream readFile(String filePath) {
		if (null == filePath) {
			Log.e(TAG,"Invalid param. filePath: " + filePath);
			return null;
		}

		InputStream is = null;

		try {
			if (fileIsExist(filePath)) {
				File f = new File(filePath);
				is = new FileInputStream(f);
			} else {
				return null;
			}
		} catch (Exception ex) {
			Log.e(TAG,"Exception, ex: " + ex.toString());
			return null;
		}
		return is;
	}

	public static boolean createDirectory(String filePath) {
		if (null == filePath) {
			return false;
		}

		File file = new File(filePath);

		if (file.exists()) {
			Log.v(TAG, " Capture file.exists true");
			return true;
		}
		return file.mkdirs();

	}

	public static boolean deleteDirectory(String filePath) {
		if (null == filePath) {
			Log.e(TAG,"Invalid param. filePath: " + filePath);
			return false;
		}

		File file = new File(filePath);

		if (file == null || !file.exists()) {
			return false;
		}

		if (file.isDirectory()) { //能够识别的目录
			File[] list = file.listFiles();
			for (int i = 0; i < list.length; i++) {
					if (list[i].isDirectory()) {
						deleteDirectory(list[i].getAbsolutePath());
					} else {
						list[i].delete();
					}
			}
		}

		file.delete();
		return true;
	}

	public static boolean writeFile(String filePath, InputStream inputStream) {
		if (null == filePath || filePath.length() < 1) {
			return false;
		}

		try {
			File file = new File(filePath);
			if (file.exists()) {
				deleteDirectory(filePath);
			}

			String pth = filePath.substring(0, filePath.lastIndexOf("/"));
			boolean ret = createDirectory(pth);
			if (!ret) {
				Log.e(TAG,"createDirectory fail path = " + pth);
				return false;
			}

			boolean ret1 = file.createNewFile();
			if (!ret) {
				Log.e(TAG,"createNewFile fail filePath = " + filePath);
				return false;
			}

			FileOutputStream fileOutputStream = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int c = inputStream.read(buf);
			while (-1 != c) {
				fileOutputStream.write(buf, 0, c);
				c = inputStream.read(buf);
			}

			fileOutputStream.flush();
			fileOutputStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	public static boolean writeFile(String filePath, String fileContent) {
		return writeFile(filePath, fileContent, false);
	}

	public static boolean writeFile(String filePath, String fileContent,
			boolean append) {
		if (null == filePath || fileContent == null || filePath.length() < 1
				|| fileContent.length() < 1) {
			Log.e(TAG,"Invalid param. filePath: " + filePath + ", fileContent: "
					+ fileContent);
			return false;
		}

		try {
			File file = new File(filePath);
			if (!file.exists()) {
				if (!file.createNewFile()) {
					return false;
				}
			}

			BufferedWriter output = new BufferedWriter(new FileWriter(file,
					append));
			output.write(fileContent);
			output.flush();
			output.close();
		} catch (IOException ioe) {
			Log.e(TAG,"writeFile ioe: " + ioe.toString());
			return false;
		}

		return true;
	}

	public static long getFileSize(String filePath) {
		if (null == filePath) {
			Log.e(TAG,"Invalid param. filePath: " + filePath);
			return 0;
		}

		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return 0;
		}

		return file.length();
	}

	public static long getFileModifyTime(String filePath) {
		if (null == filePath) {
			Log.e(TAG,"Invalid param. filePath: " + filePath);
			return 0;
		}

		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return 0;
		}

		return file.lastModified();
	}

	public static boolean setFileModifyTime(String filePath, long modifyTime) {
		if (null == filePath) {
			Log.e(TAG,"Invalid param. filePath: " + filePath);
			return false;
		}

		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return false;
		}

		return file.setLastModified(modifyTime);
	}

	public static boolean copyFile(ContentResolver cr, String fromPath,
			String destUri) {
		if (null == cr || null == fromPath || fromPath.length() < 1
				|| null == destUri || destUri.length() < 1) {
			Log.e(TAG,"copyFile Invalid param. cr=" + cr + ", fromPath=" + fromPath
					+ ", destUri=" + destUri);
			return false;
		}

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(fromPath);
			if (null == is) {
				Log.e(TAG,"Failed to open inputStream: " + fromPath + "->"
						+ destUri);
				return false;
			}

			// check output uri
			String path = null;
			Uri uri = null;

			String lwUri = destUri.toLowerCase();
			if (lwUri.startsWith("content://")) {
				uri = Uri.parse(destUri);
			} else if (lwUri.startsWith("file://")) {
				uri = Uri.parse(destUri);
				path = uri.getPath();
			} else {
				path = destUri;
			}

			// open output
			if (null != path) {
				File fl = new File(path);
				String pth = path.substring(0, path.lastIndexOf("/"));
				File pf = new File(pth);

				if (pf.exists() && !pf.isDirectory()) {
					pf.delete();
				}

				pf = new File(pth + File.separator);

				if (!pf.exists()) {
					if (!pf.mkdirs()) {
						Log.e(TAG,"Can't make dirs, path=" + pth);
					}
				}

				pf = new File(path);
				if (pf.exists()) {
					if (pf.isDirectory())
						deleteDirectory(path);
					else
						pf.delete();
				}

				os = new FileOutputStream(path);
				fl.setLastModified(System.currentTimeMillis());
			} else {
				os = new ParcelFileDescriptor.AutoCloseOutputStream(
						cr.openFileDescriptor(uri, "w"));
			}

			// copy file
			byte[] dat = new byte[1024];
			int i = is.read(dat);
			while (-1 != i) {
				os.write(dat, 0, i);
				i = is.read(dat);
			}

			is.close();
			is = null;

			os.flush();
			os.close();
			os = null;

			return true;

		} catch (Exception ex) {
			Log.e(TAG,"Exception, ex: " + ex.toString());
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception ex) {
				}
				;
			}
			if (null != os) {
				try {
					os.close();
				} catch (Exception ex) {
				}
				;
			}
		}
		return false;
	}

	public static byte[] readAll(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		byte[] buf = new byte[1024];
		int c = is.read(buf);
		while (-1 != c) {
			baos.write(buf, 0, c);
			c = is.read(buf);
		}
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

	public static byte[] readFile(Context ctx, Uri uri) {
		if (null == ctx || null == uri) {
			Log.e(TAG,"Invalid param. ctx: " + ctx + ", uri: " + uri);
			return null;
		}

		InputStream is = null;
		String scheme = uri.getScheme().toLowerCase();
		if (scheme.equals("file")) {
			is = readFile(uri.getPath());
		}

		try {
			is = ctx.getContentResolver().openInputStream(uri);
			if (null == is) {
				return null;
			}

			byte[] bret = readAll(is);
			is.close();
			is = null;

			return bret;
		} catch (FileNotFoundException fne) {
			Log.e(TAG,"FilNotFoundException, ex: " + fne.toString());
		} catch (Exception ex) {
			Log.e(TAG,"Exception, ex: " + ex.toString());
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception ex) {
				}
				;
			}
		}
		return null;
	}

	public static boolean writeFile(String filePath, byte[] content) {
		if (null == filePath || null == content) {
			Log.e(TAG,"Invalid param. filePath: " + filePath + ", content: "
					+ content);
			return false;
		}

		FileOutputStream fos = null;
		try {
			String pth = filePath.substring(0, filePath.lastIndexOf("/"));
			File pf = null;
			pf = new File(pth);
			if (pf.exists() && !pf.isDirectory()) {
				pf.delete();
			}
			pf = new File(filePath);
			if (pf.exists()) {
				if (pf.isDirectory())
					FileHelper.deleteDirectory(filePath);
				else
					pf.delete();
			}

			pf = new File(pth + File.separator);
			if (!pf.exists()) {
				if (!pf.mkdirs()) {
					Log.e(TAG,"Can't make dirs, path=" + pth);
				}
			}

			fos = new FileOutputStream(filePath);
			fos.write(content);
			fos.flush();
			fos.close();
			fos = null;
			pf.setLastModified(System.currentTimeMillis());

			return true;

		} catch (Exception ex) {
			Log.e(TAG,"Exception, ex: " + ex.toString());
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (Exception ex) {
				}
				;
			}
		}
		return false;
	}

	private static String getEntryName(String baseDirPath, File file) {
		if (!baseDirPath.endsWith(File.separator)) {
			baseDirPath = baseDirPath + File.separator;
		}

		String filePath = file.getAbsolutePath();
		if (file.isDirectory()) {
			filePath = filePath + "/";
		}

		int index = filePath.indexOf(baseDirPath);
		return filePath.substring(index + baseDirPath.length());
	}

	/**
	 * Resize the bitmap
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); 
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + File.separator + tempList[i]);
				delFolder(path + File.separator + tempList[i]);
			}
		}
	}

	public static String[] getPathFolderName(String folderPath) {
		ArrayList<String> camFolderNames = new ArrayList<String>();
		File f = new File(folderPath);
		String[] files = f.list();
		if(files != null){
			for (int i = 0; i < files.length; i++) {
				String path = folderPath + File.separator + files[i];
				File f2 = new File(path);
				if (f2.isDirectory()) {
					int len = f2.list().length;
					if(len > 0){
						camFolderNames.add(f2.getName());
					}else{
						deleteDirectory(path);
					}
				}
			}
		}
		
		String[] files02 = new String[camFolderNames.size()];
		for (int i = 0; i < camFolderNames.size(); i++) {
			files02[i] = camFolderNames.get(i);
		}
		return files02;
	}

	/**
	 * 
	 * @param folderPath
	 * @return
	 */
	public static String[] getPathImageNames(String folderPath) {
		File file01 = new File(folderPath);
		String[] files01 = file01.list();

		if(files01 == null){
		    ELog.e(TAG, "ERROR, getPathImageNames file.list() is Null...");
		    return null;
		}
		
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < files01.length; i++) {
			File file02 = new File(folderPath + File.separator + files01[i]);
			if (!file02.isDirectory()) {
				if (isImageFile(file02.getName())) {
					arrayList.add(file02.getName());
				}
			}
		}
		
		String[] files02 = new String[arrayList.size()];
		for (int i = 0; i < arrayList.size(); i++) {
			files02[i] = arrayList.get(i);
		}
		return files02;
	}

	public static boolean isImageFile(String fileName) {
		String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		if (fileEnd.equalsIgnoreCase("jpg")) {
			return true;
		} else if (fileEnd.equalsIgnoreCase("png")) {
			return true;
		} else if (fileEnd.equalsIgnoreCase("bmp")) {
			return true;
		} else {
			return false;
		}
	}
    
    public static ArrayList<String> readFileByLines(String path) {
		File f = new File(path);
		
		BufferedReader br = null;
		String tmp = null;
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new FileReader(f));
			while((tmp = br.readLine()) != null) {
				list.add(tmp);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return list;
	}
    
	/**
	 * 图片是否损坏
	 */
	public static boolean isComplete(String filePath){
		BitmapFactory.Options options = null;
		if (options == null) {
			options = new BitmapFactory.Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options); //filePath代表图片路径
		if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
			//表示图片已损毁
			return false;
			}
		return true;
	}
	
	public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
    
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }
}