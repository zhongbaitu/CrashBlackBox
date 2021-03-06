package com.baitu.crashblackbox.crash;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baitu.crashblackbox.BlackBoxUtils;
import com.baitu.crashblackbox.R;

/**
 * Created by baitu on 16/8/16.
 */

public class CrashDialogController implements View.OnClickListener{

    private Context mContext;
    private View mParentView;

    private TextView mCrashStackTraceTv;
    private View mScreenShotBtn, mCopyLogBtn, mExitBtn;

    public CrashDialogController(Context context) {
        mContext = context;
        init();
    }

    private void init(){
        mParentView = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_crash, null);
        mCrashStackTraceTv = (TextView) mParentView.findViewById(R.id.text_stack_Trace);
        mScreenShotBtn =  mParentView.findViewById(R.id.btn_screenshot);
        mScreenShotBtn.setOnClickListener(this);
        mCopyLogBtn =  mParentView.findViewById(R.id.btn_copy_log);
        mCopyLogBtn.setOnClickListener(this);
        mExitBtn = mParentView.findViewById(R.id.btn_exit);
        mExitBtn.setOnClickListener(this);
    }

    public void setCrashTraceInfo(CrashTraceInfo info){
        StringBuilder builder = new StringBuilder();
        builder.append("时间：");
        builder.append(info.getTime());
        builder.append("\n");
        builder.append("网络：");
        builder.append(info.getNetwork());
        builder.append("\n");
        builder.append("SD卡是否可写：");
        builder.append(info.isSDCardCanWrite());
        builder.append("\n");
        builder.append("SD卡剩余容量：");
        builder.append(info.getSDCardSize()+"MB");
        builder.append("\n");
        builder.append("\n");
        builder.append("---------------");
        builder.append("\n");
        builder.append(info.getCrashInfo());
        mCrashStackTraceTv.setText(builder.toString());
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(mParentView);
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_screenshot:
                saveScreenshot();
                break;
            case R.id.btn_copy_log:
                copyLog();
                break;
            case R.id.btn_exit:
                exit();
                break;
        }
    }

    private void saveScreenshot(){
        mCrashStackTraceTv.setDrawingCacheEnabled(true);
        mCrashStackTraceTv.buildDrawingCache();
        final Bitmap bitmap = mCrashStackTraceTv.getDrawingCache();
        BlackBoxUtils.saveImageOoSDCard(bitmap);
        Toast.makeText(mContext, "图片已保存到SDCard", Toast.LENGTH_SHORT).show();
    }

    private void copyLog(){
        String log = mCrashStackTraceTv.getText().toString();
        if(!TextUtils.isEmpty(log)){
            ClipboardManager copy = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            copy.setText(log);
            Toast.makeText(mContext, "已复制到黏贴板", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "no log", Toast.LENGTH_SHORT).show();
        }
    }

    private void exit(){
        Process.killProcess(Process.myPid());
        System.exit(1);
    }
}
