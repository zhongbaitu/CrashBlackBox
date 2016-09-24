package com.baitu.crashblackbox;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baitu.crashblackbox.crash.CrashInfoListActivity;
import com.baitu.crashblackbox.recodeScreen.RecordActivity;
import com.baitu.crashblackbox.recodeScreen.ScreenRecordListActivity;

public class ShakeDialogActivity extends AppCompatActivity {

    public static void start(Context context){
        context.startActivity(new Intent(context, ShakeDialogActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_dialog);

        showMenuDialog();
    }

    public void showMenuDialog(){
        String items[] = {"查看历史Crash信息", "录制小视频", "查看屏幕录制视频", "查看自定义Log信息"};
        Dialog dialog = new AlertDialog.Builder(this).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        CrashInfoListActivity.start(ShakeDialogActivity.this);
                        break;
                    case 1:
                        laungthScreenRecorder();
                        break;
                    case 2:
                        ScreenRecordListActivity.start(ShakeDialogActivity.this);
                        break;
                }
                finishActivity();
            }
        }).show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finishActivity();
            }
        });
    }

    private void finishActivity(){
        finish();
    }

    private void laungthScreenRecorder(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RecordActivity.start(this);
        }else{
            Toast.makeText(this, "屏幕录制只支持5.0及以上的机器", Toast.LENGTH_SHORT).show();
        }
    }
}
