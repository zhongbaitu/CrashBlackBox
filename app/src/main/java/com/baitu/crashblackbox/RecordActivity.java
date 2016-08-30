package com.baitu.crashblackbox;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;

public class RecordActivity extends Activity {

    public static final int CODE_RECORDE_REQUEST = 1;

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

    public static void start(Context context){
        context.startActivity(new Intent(context, RecordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        init();
    }

    private void init(){
        findViewById(R.id.btn_mp4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordeMP4();
            }
        });

        findViewById(R.id.btn_gif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordeGif();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_RECORDE_REQUEST && resultCode == RESULT_OK) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            RecordService.StartAndBind(this, mServiceConnection);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void recordeMP4(){
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, CODE_RECORDE_REQUEST);
    }

    private void recordeGif(){

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            RecordService recordService = binder.getRecordService();
            recordService.setMediaProjection(mMediaProjection);
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            recordService.startRecord();
            unbindService(this);
            RecordActivity.this.finish();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };
}
