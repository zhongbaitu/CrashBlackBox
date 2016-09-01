package com.baitu.crashblackbox.recodeScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by baitu on 16/9/1.
 */
public class GifPlayView extends View {

    public static final String TAG = "GifPlayView";

    private Movie mMovie;
    private long mMovieStart = 0;

    public GifPlayView(Context context) {
        super(context);
    }

    public GifPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GifPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void play(String path){
        Log.v(TAG, "path:"+path);
        mMovie = Movie.decodeFile(path);
        invalidate();
    }

    public void rePlay(){
        mMovieStart = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mMovie == null){
            return;
        }else{
            Log.v(TAG, "movie = null");
        }
        Log.v(TAG, "draw");
        long now = android.os.SystemClock.uptimeMillis();

        if (mMovieStart == 0) {
            mMovieStart = now;
        }

        if (mMovie != null) {
            int dur = mMovie.duration();
            Log.v(TAG, "dur:"+dur);
            if (dur == 0) {
                dur = 1000;
            }

            int relTime = (int) ((now - mMovieStart) % dur);
            mMovie.setTime(relTime);
            mMovie.draw(canvas, 0, 0);

            invalidate();
        }
    }
}
