package com.baitu.crashblackbox;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
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
        String tempImg = Utils.getSdcardPath();
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
}
