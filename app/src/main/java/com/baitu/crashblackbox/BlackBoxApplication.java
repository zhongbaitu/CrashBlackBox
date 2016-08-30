package com.baitu.crashblackbox;

import android.app.Application;

/**
 * Created by baitu on 16/8/26.
 */
public class BlackBoxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        BlackBox.getInstance().init(this);
    }
}
