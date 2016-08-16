package com.baitu.crashblackbox;

import android.content.Context;
import android.os.Looper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by baitu on 16/8/14.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler instance; // 单例模式

    private Context context; // 程序Context对象
    private DateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss.SSS", Locale.CHINA);

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }

        return instance;
    }

    public void init(Context context) {
        this.context = context;
        // 获取系统默认的UncaughtException处理器
        Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String stackTrace = getExceptionStackTrace(ex);
        showCrashDialog(stackTrace);
    }

    private void showCrashDialog(final String stackTrace){
        new Thread() {

            @Override
            public void run() {
                Looper.prepare();

                CrashDialogController crashDialogController = new CrashDialogController(context);
                crashDialogController.setStackTrace(stackTrace);
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
