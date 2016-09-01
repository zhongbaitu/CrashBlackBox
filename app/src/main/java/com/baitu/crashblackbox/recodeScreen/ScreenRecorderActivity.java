package com.baitu.crashblackbox.recodeScreen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.baitu.crashblackbox.BlackBoxUtils;
import com.baitu.crashblackbox.R;

import java.io.File;

import cn.dxjia.ffmpeg.library.FFmpegNativeHelper;

public class ScreenRecorderActivity extends AppCompatActivity {

    public static final String KEY_IS_CONVERT_GIF = "is_convert_gif";
    public static final String KEY_FILE_NAME = "file_name";

    private VideoView mVideoView;
    private GifPlayView mGifPlayView;
    private ProgressDialog mProgressDialog;

    private boolean mConvertGif = false;

    public static final void startInNewTask(Context context, String fileName, boolean isConvertGif){
        Intent intent = new Intent(context, ScreenRecorderActivity.class);
        intent.putExtra(KEY_IS_CONVERT_GIF, isConvertGif);
        intent.putExtra(KEY_FILE_NAME, fileName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_recorder);

        init();

        processIntent();
    }

    private void init(){
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setMediaController(new MediaController(this));
        mGifPlayView = (GifPlayView) findViewById(R.id.gif_view);
        mGifPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGifPlayView.rePlay();
            }
        });
    }

    private void processIntent(){
        Intent intent = getIntent();
        mConvertGif = intent.getBooleanExtra(KEY_IS_CONVERT_GIF, false);
        String fileName = intent.getStringExtra(KEY_FILE_NAME);
        if(!TextUtils.isEmpty(fileName)){
            File mp4File = new File(BlackBoxUtils.getAppPath() + fileName);
            if(mConvertGif){
                showConvertToGifLoadingDialog();
                convertMp4ToGif(mp4File);
            }else{
                showThumb(mp4File);
            }
        }
    }

    private void showThumb(File mp4File){
        mVideoView.setVideoPath(mp4File.getAbsolutePath());
        mVideoView.start();
        mVideoView.requestFocus();
    }

    private void convertMp4ToGif(final File mp4File){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String gifFileName = System.currentTimeMillis() + ".gif";
                File gifFile = new File(BlackBoxUtils.getAppPath() + gifFileName);
                if(mp4File.exists()){
                    StringBuilder commandBuilder = new StringBuilder();
                    commandBuilder.append("ffmpeg -i ");
                    commandBuilder.append(mp4File);
                    commandBuilder.append(" ");
                    commandBuilder.append(gifFile.getAbsolutePath());
                    String command = commandBuilder.toString();
                    FFmpegNativeHelper.runCommand(command);


                    convertDone(gifFile, mp4File);
                }
            }
        }).start();
    }

    private void convertDone(final File gifFile, final File deleteFile){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deleteMp4File(deleteFile);
                mProgressDialog.dismiss();
                mGifPlayView.play(gifFile.getAbsolutePath());
            }
        });
    }

    private void deleteMp4File(final File file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                file.delete();
            }
        }).start();
    }

    private void showConvertToGifLoadingDialog(){
        mProgressDialog = ProgressDialog.show(this, "", "生成gif图中. 请稍等...", true);
        mProgressDialog.show();
    }
}
