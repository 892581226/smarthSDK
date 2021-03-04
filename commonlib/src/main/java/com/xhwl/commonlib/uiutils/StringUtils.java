package com.xhwl.commonlib.uiutils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xhwl.commonlib.application.MyAPP;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by carey on 2016/5/31 0031.
 * String类型操作
 */
public class StringUtils {
    private static final String TAG = "StringUtils";

    /**
     * 判断字符串是否为空 包括 null字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.trim().length() == 0 || str.equals("null"));
    }

    /***
     * 获取短信验证码的（yzm）参数
     *
     * @return
     */
    public static String getValidateCodeYzm() {
        String strMd5 = StringUtils.getMD5("wl");
        String subStr = strMd5.substring(0, 16);
        long milli = System.currentTimeMillis() / 1000L + 1;
        String milliMd5 = StringUtils.getMD5(String.valueOf(milli));
        String milliStr = milliMd5.substring(milliMd5.length() - 16, milliMd5.length());
        return subStr + milliStr;
    }

    /***
     * 获取当前的时间戳
     */
    public static long getCurrentTime() {
        return (System.currentTimeMillis() / 1000L + 1);
    }

    /**
     * 是否到达某个日期（与当前时间比）
     *
     * @param date ""表示过期
     */
    public static boolean isExpired(String date) {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        boolean bRet = true;
        Date mDate = null;
        try {
            if (!isEmpty(date)) {
                mDate = sdf.parse(date);
                bRet = mDate.before(nowDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bRet;
    }

    /**
     * 是否大于某个日期
     */
    public static boolean isBefore(String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        boolean bRet = true;
        Date startDate = null;
        Date endDate = null;
        try {
            if (!isEmpty(startDateStr) && !isEmpty(endDateStr)) {
                startDate = sdf.parse(startDateStr);
                endDate = sdf.parse(endDateStr);
                bRet = startDate.before(endDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bRet;
    }

    public static int minDistance(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        int timeLong = (int) (endDate.getTime() - startDate.getTime());
        if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return timeLong;
        }
        return 0;
    }

    /**
     * 计算两个日期型的时间相差多少时间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static String dateDistance(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 60 * 1000) {
            return timeLong / 1000 + "秒前";
        } else if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return timeLong + "分钟前";
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong + "小时前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong + "天前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
            return timeLong + "周前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.format(startDate);
        }
    }

    /**
     * 判断当天是否是工作日 (工作日：true；节假日：false)
     *
     * @return
     */
    public static boolean isWorkDay() {
        //判断是否周六日
        Calendar c = Calendar.getInstance();
        int isWeek = c.get(Calendar.DAY_OF_WEEK);
        if (isWeek == Calendar.SUNDAY || isWeek == Calendar.SATURDAY) {
            return false;
        }
        return true;
    }

    /**
     * 格式化价格 返回(￥00.00)
     *
     * @param price
     * @return
     */
    public static String formatPrice(double price) {
        // 想要转换成指定国家的货币格式
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        // 把转换后的货币String类型返回
        return format.format(price);
    }

    /**
     * 定时弹出软键盘
     */
    public static void popUpKeyboard(final View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(view, 0);
                           }
                       },
                400);
    }


    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 判断是否含有中文特殊字符
     * 英文特殊字符  _`~!@#$%^&*()+=|{}':;',\[\].<>/?~
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 禁止EditText输入空格
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 禁止EditText输入特殊字符
     * @param editText
     */
    public static void setEditTextInhibitInputSpeChat(EditText editText){

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[-`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(source.equals(" ")){
                    return "";
                }else if(matcher.find()){
                    return "";
                } else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 验证密码长度
     *
     * @param str
     * @return
     */
    private static boolean checkPassword(String str) {

        return true;
    }

    /**
     * 获取当前时间(时：小写的“h”是12小时制，大写的“H”是24小时制)
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH");
        return sDateFormat.format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return sDateFormat.format(new Date());
    }

    /**
     * 格式化价格字符串 返回(￥00.00)
     *
     * @param priceStr
     * @return
     */
    public static String formatPrice(String priceStr) {
        double price = 0.0D;
        if (!isEmpty(priceStr)) {
            priceStr = Pattern.compile("[^0-9.]").matcher(priceStr).replaceAll(""); //替换所有数字小数点以外的字符
            try {
                price = Double.valueOf(priceStr);
            } catch (NumberFormatException ex) {
                Log.w("formatPrice", ex.getMessage());
            }
        }
        // 想要转换成指定国家的货币格式
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        // 把转换后的货币String类型返回
        return format.format(price);
    }


    /**
     * 获取图片地址字符串中的第一张图片
     *
     * @param imagesStr
     * @return
     */
    public static String getFirstImage(String imagesStr) {
        String[] imageArray = getImages(imagesStr);
        return imageArray == null ? null : imageArray[0];
    }

    /**
     * 获取图片数组
     *
     * @param imagesStr 图片地址字符串 格式：http://xcpnet.net/xxx.png|http://xcpnet.net/yyy.png
     * @return
     */
    public static String[] getImages(String imagesStr) {
        if (isEmpty(imagesStr)) {
            return null;
        } else {
            String[] images = imagesStr.split(",");
            for (int i = 0; i < images.length; i++) {
                String img = images[i];
                if (!img.startsWith("http:")) {
                    images[i] = img;
                }
            }
            return images;
        }
    }

    /**
     * 将时间戳转换为时间
     */
    public static String parseDate(long times) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(times * 1000));
    }

    /**
     * 验证手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null) {
            Pattern p = Pattern.compile("1[0-9]{10}");
            return p.matcher(phoneNumber).matches();
        }
        return false;
    }

    /***
     * 身份证号验证
     * @param IDCard
     * @return
     */
    public static boolean isIDCard(String IDCard) {
        if (IDCard != null) {
            String IDCardRegex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
            return IDCard.matches(IDCardRegex);
        }
        return false;
    }

    /**
     * 判断是否是车牌号
     */
    public static boolean isCarNo(String CarNum) {
        //匹配第一位汉字
        String str = "京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼甲乙丙己庚辛壬寅辰戍午未申";
        if (!(CarNum == null || CarNum.equals(""))) {
            String s1 = CarNum.substring(0, 1);//获取字符串的第一个字符
            if (str.contains(s1)) {
                String s2 = CarNum.substring(1, CarNum.length());
                //不包含I O i o的判断
                if (s2.contains("I") || s2.contains("i") || s2.contains("O") || s2.contains("o")) {
                    return false;
                } else {
                    if (!CarNum.matches("^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$")) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * MD5加密
     *
     * @param info
     * @return
     */
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 格式化日期显示
     */
    public static String formatDate(String mDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = null;
        try {
            d = formatter.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatter.format(d);
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                Log.e("TAG", "isQQClientAvailable: " + pn);
                ;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


    public static String formatPlayCount(long playCount) {
        String standardPlayCount = "";
        if (playCount < 0) {
            standardPlayCount = "0";
        } else if (playCount < 10000) {
            standardPlayCount = String.valueOf(playCount);
        } else if (playCount < 100000000) {
            standardPlayCount = String.format(Locale.getDefault(), "%d.%02d万", playCount / 10000, playCount % 10000 / 100);
        } else if (playCount > 100000000) {
            standardPlayCount = String.format(Locale.getDefault(), "%d.%02d亿", playCount / 100000000, playCount % 100000000 / 1000000);
        }
        return standardPlayCount;
    }


    // 设置不可编辑且无点击事件
    public static void setCanNotEditNoClick(View view) {
        view.setFocusable(false);
        view.setFocusableInTouchMode(false);
//        view.setOnClickListener(null);
    }


    // 设置可编辑
    public static void setCanEdit(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
    }

    /**
     * 保存手机号到通讯录
     */

    //根据电话号码查询姓名（在一个电话打过来时，如果此电话在通讯录中，则显示姓名）
    public static void savePhone(String name, String[] phone) {
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phone);
        ContentResolver resolver = MyAPP.getIns().getApplicationContext().getContentResolver();
//        ContactsContract.Data.DISPLAY_NAME //查询 该电话的客户姓名

        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data.HAS_PHONE_NUMBER}, null, null, null); //从raw_contact表中返回display_name
        int count = cursor.getCount();
        if (count > 0) {
            if (cursor.moveToFirst()) {
                String hasPhone = cursor.getString(0);//查询该电话有没有人
                if (TextUtils.isEmpty(hasPhone) || "0".equals(hasPhone)) {//没有该电话
                    insertPhone(name, phone);
                } else {
//                    ToastUtil.showCenterToast("该电话号码已存在!");
                }

            }
        } else {
            insertPhone(name, phone);
        }

        cursor.close();
    }


    private static void insertPhone(String name, String[] phone) {
//        deleteContactPhoneNumber(name);
        if (phone.length == 0) {
            return;
        }

        //创建一个空的ContentValues
        ContentValues values = new ContentValues();
        //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
        Uri rawContactUri = MyAPP.getIns().getApplicationContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        //往data表插入姓名数据
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);//内容类型
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);//设置联系人名字  
        MyAPP.getIns().getApplicationContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);//向联系人URI添加联系人名字
        //往data表插入电话数据
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone[0]);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);//插入手机号码
        MyAPP.getIns().getApplicationContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        //往data表插入其他电话数据
        for (int i = 0; i < phone.length - 1; i++) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone[i + 1]);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);//插入除了其他号码
            MyAPP.getIns().getApplicationContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
//        //插入raw_contacts表，并获取_id属性
//        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//        ContentResolver resolver = this.getContext().getContentResolver();
//        ContentValues values = new ContentValues();
//        long contact_id = ContentUris.parseId(resolver.insert(uri, values));
//        //插入data表
//        uri = Uri.parse("content://com.android.contacts/data");
//        //add Name
//        values.put("raw_contact_id", contact_id);
//        values.put(ContactsContract.Data.MIMETYPE,"vnd.android.cursor.item/name");
//        values.put("data2", name);
//        values.put("data1", name);
//        resolver.insert(uri, values);
//        values.clear();
//        //add Phone
//        values.put("raw_contact_id", contact_id);
//        values.put(ContactsContract.Data.MIMETYPE,"vnd.android.cursor.item/phone_v2");
//        values.put("data2", "2");   //手机
//        values.put("data1", phone);
//        resolver.insert(uri, values);
//        values.clear();


//        ContentValues values = new ContentValues(); //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
//        Uri rawContactUri = MyAPP.getIns().getApplicationContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);//获取id
//        long rawContactId = ContentUris.parseId(rawContactUri); //往data表入姓名数据
//        values.clear();
//        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId); //添加id
//        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);//添加内容类型（MIMETYPE）
//        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);//添加名字，添加到first name位置
//        MyAPP.getIns().getApplicationContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values); //往data表入电话数据
//        values.clear();
//        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
//        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
//        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
//        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
//        MyAPP.getIns().getApplicationContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
//        ToastUtil.showDebug("保存电话");
    }

    /**`
     * 添加联系人信息
     *
     */
    public static void insertConstacts(String name, String[] list) {
        try {
            //该店管家用户是否存在，存在的话，就追加号码，不存在新增联系人
            ContentValues values = new ContentValues();
            Uri rawContactUri = MyAPP.getIns().getApplicationContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
            long rawContactId = ContentUris.parseId(rawContactUri);
            for (int i = 0; i < list.length; i++) {
                if (!isThePhoneExist(list[i])) {
                    //往data表插入姓名数据
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);//内容类型
                    values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);//设置联系人名字  
                    MyAPP.getIns().getApplicationContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);//向联系人URI添加联系人名字

                    //往data表插入电话号码
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, list[i]);
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    MyAPP.getIns().getApplicationContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                    //插入data表
//                    Uri uri = Uri.parse("content://com.android.contacts/data");
//                    MyAPP.getIns().getApplicationContext().getContentResolver().insert(uri, values);

                }
            }
            Log.i(TAG, "insertConstacts:  成功 ");
        } catch (Exception e) {
            Log.i(TAG, "insertConstacts:  e = " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 判断某个手机号是否存在
     */
    public static boolean isThePhoneExist(String phoneNum) {
        //uri=  content://com.android.contacts/data/phones/filter/#
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phoneNum);
            ContentResolver resolver = MyAPP.getIns().getApplicationContext().getContentResolver();
            cursor = resolver.query(uri, new String[]{ContactsContract.Data.DISPLAY_NAME},
                    null, null, null); //从raw_contact表中返回display_name
            if (cursor.moveToFirst()) {
                Log.i(TAG, "name=" + cursor.getString(0) + " , phoneNum = " + phoneNum);
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            //Log.i(TAG, "163 e =" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }


    /**
     * 删除联系人
     *
     * @param contactName
     */
    public static void deleteContactPhoneNumber(String contactName) {
        //根据姓名求id
        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
        ContentResolver resolver = MyAPP.getIns().getApplicationContext().getContentResolver();
        String where = ContactsContract.PhoneLookup.DISPLAY_NAME;
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, where + "=?", new String[]{contactName}, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            //根据id删除data中的相应数据
            resolver.delete(uri, where + "=?", new String[]{contactName});
            uri = ContactsContract.Data.CONTENT_URI;
            resolver.delete(uri, ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[]{id + ""});
        }
    }

    /**
     * 更新联系人
     *
     * @param contactName
     * @param phoneNumber
     */
    public static void updateContactPhoneNumber(String contactName, String phoneNumber) {
        Uri uri = ContactsContract.Data.CONTENT_URI;//对data表的所有数据操作
        ContentResolver resolver = MyAPP.getIns().getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.DATA1, phoneNumber);
        int result = resolver.update(uri, values, ContactsContract.Data.MIMETYPE + "=? and " + ContactsContract.PhoneLookup.DISPLAY_NAME + "=?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, contactName});
        if (result < 0) {
            ToastUtil.showCenterToast("更新失败");
        }
    }


    /**
     * 获取联系人
     *
     * @param
     */
    public static List<String> fetchContact() {
        List<String> list = new ArrayList<>();
        ContentResolver cr = MyAPP.getIns().getApplicationContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                List<String> numberlist = new ArrayList<>();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {

                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        //   Log.e("called", phoneNo + "  " + name + "  " + id);
                        switch (type) {

                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                try {
                                    if (phoneNo.equals("") || phoneNo != null) {
                                        Log.e("test", phoneNo + "  name   " + name + "  type  " + "home");
                                        numberlist.add(phoneNo);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                try {
                                    if (phoneNo.equals("") || phoneNo != null) {
                                        Log.e("test", phoneNo + "  name   " + name + "  type  " + "mobile");
                                        numberlist.add(phoneNo);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                try {
                                    if (phoneNo.equals("") || phoneNo != null) {
                                        Log.e("test", phoneNo + "  name   " + name + "  type  " + "work");
                                        numberlist.add(phoneNo);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                                try {
                                    if (phoneNo.equals("") || phoneNo != null) {
                                        Log.e("test", phoneNo + "  name   " + name + "  type  " + "eaxhome");
                                        numberlist.add(phoneNo);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                                try {
                                    if (phoneNo.equals("") || phoneNo != null) {
                                        Log.e("test", phoneNo + "  name   " + name + "  type  " + "faxwork");
                                        numberlist.add(phoneNo);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                                try {
                                    if (phoneNo.equals("") || phoneNo != null) {
                                        Log.e("test", phoneNo + "  name   " + name + "  type  " + "main");
                                        numberlist.add(phoneNo);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                                try {
                                    if (phoneNo.equals("") || phoneNo != null) {
                                        Log.e("test", phoneNo);
                                        numberlist.add(phoneNo);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                break;
                        }
                    }
                    pCur.close();

           /* HashSet<ContactClass> h = new HashSet<>();
            h.addAll(numberlist);
            numberlist.clear();
            numberlist.addAll(h);*/
//                    contactClass.setPhoneNumList(numberlist);
                    list.addAll(numberlist);
                    for (int i = 0; i < numberlist.size(); i++) {
//                        Log.e("inner", numberlist.get(i).getName() + " number  " + numberlist.get(i).getPhoneNum());
                    }
                }
            }

           /* try {
                HashSet hs = new HashSet();

                hs.addAll(list); // demoArrayList= name of arrayList from which u want to remove duplicates
                List<ContactClass> classList = new ArrayList<>();
                list.clear();
                classList.addAll(hs);
                saveToRealm(classList);
                Log.e("resize", classList.size() + "");
            } catch (Exception e) {
            }*/
        }
        return list;
    }
}
