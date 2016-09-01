package com.baitu.crashblackbox.recodeScreen;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.baitu.crashblackbox.BlackBoxUtils;

import java.io.IOException;

public class RecordService extends Service {

    private static final String FILTER = "stop_record";

    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;
    private int mWidth;
    private int mHeight;
    private int mDpi;
    private RecorderBroadCast mRecorderBroadCast;
    private String mFileName;
    private boolean mConvertGif = false;

    public static void StartAndBind(Context context, ServiceConnection serviceConnection){
        Intent intent = new Intent(context, RecordService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        mFileName = System.currentTimeMillis() + ".mp4";
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(BlackBoxUtils.getAppPath() + mFileName);
        mMediaRecorder.setVideoSize(500, 500);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mMediaRecorder.setVideoFrameRate(30);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorderBroadCast = new RecorderBroadCast();
        IntentFilter filter=new IntentFilter(FILTER);
        registerReceiver(mRecorderBroadCast, filter);
    }

    public void startRecord(){
        createVirtualDisplay();
        mMediaRecorder.start();
        showNotification();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopRecord(){
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setPreviewDisplay(null);
        mMediaRecorder.setOnInfoListener(null);
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mVirtualDisplay.release();
        mMediaProjection.stop();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("VirtualScreen", mWidth, mHeight, mDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    public void setMediaProjection(MediaProjection mediaProjection){
        mMediaProjection = mediaProjection;
    }

    public void setConfig(int width, int height, int dpi){
        mWidth = width;
        mHeight = height;
        mDpi = dpi;
    }

    public void setConvertGif(boolean convertGif) {
        mConvertGif = convertGif;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  new RecordBinder();
    }

    private void showNotification(){
        Intent action=new Intent(FILTER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder
                .setContentTitle("正在录制：")
                .setContentInfo("点击停止录制")
                .setTicker("开始录制屏幕")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.sym_def_app_icon);

        Notification notification = builder.build();

        startForeground(1, notification);
    }

    private class RecorderBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            stopRecord();
            ScreenRecorderActivity.startInNewTask(context, mFileName, mConvertGif);
            destory();
        }
    }

    private void destory(){
        stopSelf();
    }

    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mRecorderBroadCast);
        super.onDestroy();
    }
}
