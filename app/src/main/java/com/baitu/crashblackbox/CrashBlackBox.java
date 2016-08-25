package com.baitu.crashblackbox;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;

/**
 * Created by baitu on 16/8/25.
 */
public class CrashBlackBox implements SensorEventListener{

    private static CrashBlackBox mInstance;

    private Context mContext;
    private SensorManager mSensorManager;
    private Vibrator mVibrator;

    public static CrashBlackBox getInstance() {
        if (mInstance == null) {
            synchronized (CrashBlackBox.class) {
                if (mInstance == null) {
                    mInstance = new CrashBlackBox();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mContext = context;
        CrashHandler.getInstance().init(mContext);
        mSensorManager = (SensorManager) context.getSystemService(mContext.SENSOR_SERVICE);
        mVibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        if (mSensorManager != null) {
            mSensorManager.registerListener(
                    this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void showMenuDialog(){
        String items[] = {"查看历史Crash信息", "查看自定义Log信息", "录制小视频"};
        new AlertDialog.Builder(mContext).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        CrashInfoListActivity.start(mContext);
                        break;
                }
            }
        }).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > 19 || Math.abs(values[1]) > 19 || Math.abs(values[2]) > 19)) {
                mVibrator.vibrate(500);
                showMenuDialog();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onDestory(){
        mSensorManager.unregisterListener(this);
    }
}
