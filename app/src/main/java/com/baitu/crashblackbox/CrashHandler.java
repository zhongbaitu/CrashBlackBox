package com.baitu.crashblackbox;

import android.content.Context;
import android.os.Looper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by baitu on 16/8/14.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler mInstance; // 单例模式

    private Context mContext; // 程序mContext对象

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        if (mInstance == null) {
            synchronized (CrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new CrashHandler();
                }
            }
        }

        return mInstance;
    }

    public void init(Context mContext) {
        this.mContext = mContext;
        // 获取系统默认的UncaughtException处理器
        Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String stackTrace = getExceptionStackTrace(ex);

        CrashTraceInfo crashTraceInfo = new CrashTraceInfo();
        crashTraceInfo.setCrashInfo(stackTrace);
        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
        String time = mDateFormat.format(new Date());
        crashTraceInfo.setTime(time);
        crashTraceInfo.setNetwork(Utils.getNetworkType(mContext));
        crashTraceInfo.setSDCardCanWrite(Utils.checkSdWritable());
        double size = Utils.getSdcardAvaiableSize() / 1024 / 1024;
        crashTraceInfo.setSDCardSize(Double.toString(size));

        FileManager.saveCrashTraceAsync(crashTraceInfo);

        showCrashDialog(crashTraceInfo);
    }

    private void showCrashDialog(final CrashTraceInfo crashTraceInfo){
        new Thread() {

            @Override
            public void run() {
                Looper.prepare();

                CrashDialogController crashDialogController = new CrashDialogController(mContext);
                crashDialogController.setCrashTraceInfo(crashTraceInfo);
                crashDialogController.showDialog();

                Looper.loop();
            }

        }.start();

    }

    private String getExceptionStackTrace(final Throwable ex) {
        if (ex == null) {
            return "";
        }
        ex.printStackTrace();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[" + ex.getMessage() + "]");

        stringBuilder.append("\n");
        StackTraceElement[] stackTrace = ex.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            stringBuilder.append("\n");
            stringBuilder.append("at ");
            stringBuilder.append(stackTrace[i].getClassName());
            stringBuilder.append(".");
            stringBuilder.append(stackTrace[i].getMethodName());
            stringBuilder.append("(");
            stringBuilder.append(stackTrace[i].getFileName());
            stringBuilder.append(":");
            stringBuilder.append(stackTrace[i].getLineNumber());
            stringBuilder.append(")");
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}