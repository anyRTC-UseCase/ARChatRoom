package org.ar.audioganme.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import org.ar.audioganme.R;
import org.ar.audioganme.login.LoginActivity;
import org.ar.audioganme.util.StatusBarUtil;

public class AudioActivity extends AppCompatActivity implements IAudioView{

    private AudioPresent audioPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarColor(AudioActivity.this, R.color.chat_center_color);
        setContentView(R.layout.activity_audio);
        String channelId = getIntent().getStringExtra("ChannelId");
        audioPresent = new AudioPresent(this,this,channelId);

    }

    @Override
    public void joinRTCSuccess() {

    }

    @Override
    public void joinRTMSuccess() {

    }

    @Override
    public void memberSize(int num) {

    }

    @Override
    public void userJoin(String uid) {

    }

    @Override
    public void userLeave(String uid) {

    }
}