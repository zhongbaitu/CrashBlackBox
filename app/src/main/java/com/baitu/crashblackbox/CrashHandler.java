package com.baitu.crashblackbox;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.support.v7.app.AlertDialog;

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
        handleException(ex);
    }

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();

                ex.printStackTrace();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("[" + ex.getMessage() + "]");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("crash:");
                stringBuilder.append("--------------");
                StackTraceElement[] stackTrace = ex.getStackTrace();
                for(int i = 0; i < stackTrace.length; i++){
                    stringBuilder.append("\n");
                    stringBuilder.append(stackTrace[i].getFileName());
                    stringBuilder.append("\n");
                    stringBuilder.append(stackTrace[i].getClassName());
                    stringBuilder.append("\n");
                    stringBuilder.append(stackTrace[i].getMethodName());
                    stringBuilder.append("\n");
                    stringBuilder.append(stackTrace[i].getLineNumber());
                }

                builder.setMessage(stringBuilder.toString());
                builder.setNegativeButton("截图", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                builder.show();

                Looper.loop();
            }

        }.start();

        return true;                                                                                                                }
}
