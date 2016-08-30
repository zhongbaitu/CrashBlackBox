package com.baitu.crashblackbox;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by baitu on 16/8/25.
 */
public class BlackBox implements SensorEventListener{

    private static BlackBox mInstance;

    private Context mContext;
    private SensorManager mSensorManager;
    private Vibrator mVibrator;

    public static BlackBox getInstance() {
        if (mInstance == null) {
            synchronized (BlackBox.class) {
                if (mInstance == null) {
                    mInstance = new BlackBox();
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
        String items[] = {"查看历史Crash信息", "录制小视频", "查看自定义Log信息"};
        new AlertDialog.Builder(mContext).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        CrashInfoListActivity.start(mContext);
                        break;
                    case 1:
                        laungthScreenRecorder();
                         break;
                }
            }
        }).show();
    }

    private void laungthScreenRecorder(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RecordActivity.start(mContext);
        }else{
            Toast.makeText(mContext, "屏幕录制只支持5.0及以上的机器", Toast.LENGTH_SHORT).show();
        }
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
