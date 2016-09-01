package com.baitu.crashblackbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by baitu on 16/8/16.
 */
public class BlackBoxUtils {

    public static String getSdcardPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null) {
            return "";
        }
        return sdcard.getPath();
    }

    public static String getAppPath(){
        return getSdcardPath() + Constant.PATH_TEST;
    }

    public static String saveImageOoSDCard(Bitmap bmp) {
        String tempImg = BlackBoxUtils.getSdcardPath() + Constant.PATH_TEST;
        File dir = new File(tempImg);
        if (dir.exists() == false) {
            dir.mkdirs();
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String strDate = df.format(new Date());
        String strRand = String.format("%d", (int) (Math.random() * 10000));

        tempImg += "/" + strDate + strRand + ".jpg";
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(tempImg);
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.close();
            return tempImg;
        } catch (Exception e) {
        }
        return null;
    }

    public static void saveJsonToFile(String jsonStr, String fileName) {
        String dir = BlackBoxUtils.getSdcardPath() + Constant.PATH_TEST;
        File fDir = new File(dir);
        if (fDir.exists() == false) {
            fDir.mkdirs();
        }
        String file = dir + "/" + fileName;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonStr.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getJsonFromFile(String fileName) {
        String dir = BlackBoxUtils.getSdcardPath() + Constant.PATH_TEST;
        File fDir = new File(dir);
        if (fDir.exists() == false) {
            fDir.mkdirs();
        }
        String file = dir + "/" + fileName;
        String result = null;
        try {
            byte[] buffer = new byte[10240];
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            result = new String(bos.toByteArray());
            bos.close();
            fis.close();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 检查网络状态
     */
    public static String getNetworkType(Context context) {
        if (isNetworkAvailable(context)) {
            if (isWifiConnected(context)) {
                return "wifi网络";
            }
        } else {
            return "网络不可用";
        }
        return "移动网络";
    }

    /**
     * 是否连接WIFI
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * sd卡是否可写
     */
    public static boolean checkSdWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) == false) {
            return false;
        }
        File file = Environment.getExternalStorageDirectory();
        if (file != null) {
            if (file.canWrite()) {
                return true;
            }
        }
        return false;
    }

    public static long getSdcardAvaiableSize() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File filePath = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(filePath.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }
}
