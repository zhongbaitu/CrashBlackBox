package com.baitu.crashblackbox.recodeScreen;

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

import com.baitu.crashblackbox.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RecordActivity extends Activity {

    public static final int CODE_RECORDE_REQUEST = 1;

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

    private boolean mConvertGif = false;

    public static void start(Context context){
        context.startActivity(new Intent(context, RecordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        init();
    }

    private void init(){
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        findViewById(R.id.btn_mp4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordMP4();
            }
        });

        findViewById(R.id.btn_gif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordGif();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_RECORDE_REQUEST && resultCode == RESULT_OK) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            RecordService.StartAndBind(this, mServiceConnection);
        }
    }

    private void recordMP4(){
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, CODE_RECORDE_REQUEST);
    }

    private void recordGif(){
        mConvertGif = true;
        recordMP4();
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
            recordService.setConvertGif(mConvertGif);
            recordService.startRecord();
            unbindService(this);
            RecordActivity.this.finish();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };
}
