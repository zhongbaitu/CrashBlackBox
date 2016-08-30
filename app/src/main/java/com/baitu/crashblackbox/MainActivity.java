package com.baitu.crashblackbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mCrashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BlackBox.getInstance().init(this);

        mCrashButton = (Button) findViewById(R.id.test);
        mCrashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCrashButton = null;
                mCrashButton.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        BlackBox.getInstance().onDestory();
        super.onDestroy();
    }
}
