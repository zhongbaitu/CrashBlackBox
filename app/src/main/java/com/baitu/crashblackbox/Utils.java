package com.baitu.crashblackbox;

import android.graphics.Bitmap;
import android.os.Environment;

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
public class Utils {

    public static String getSdcardPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null) {
            return "";
        }
        return sdcard.getPath();
    }

    public static String saveImageOoSDCard(Bitmap bmp) {
        String tempImg = Utils.getSdcardPath() + Constant.PATH_TEST;
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
        String dir = Utils.getSdcardPath() + Constant.PATH_TEST;
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
        String dir = Utils.getSdcardPath() + Constant.PATH_TEST;
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
}
