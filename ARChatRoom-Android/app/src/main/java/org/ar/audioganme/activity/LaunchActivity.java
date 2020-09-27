package org.ar.audioganme.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.ar.audioganme.R;
import org.ar.audioganme.login.LoginActivity;
import org.ar.audioganme.util.StatusBarUtil;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setDeepStatusBar(true, LaunchActivity.this, Color.TRANSPARENT);
        StatusBarUtil.setStatusBarColor(LaunchActivity.this, R.color.launch_color);
        setContentView(R.layout.activity_launch);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent =new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }

}
