package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.util.SpUtil;

public class VolumeDialog extends Dialog implements SeekBar.OnSeekBarChangeListener {

    private SeekBar mMusicBar,mVolumeBar,mEarBar;
    private ImageView mMusicImg,mVolumeImg,mEarImg,mBack;
    private int musicVal,volumeVal,earVal;
    private ChatRoomManager chatRoomManager;

    public VolumeDialog(@NonNull Context context, ChatRoomManager chatRoomManager) {
        super(context,R.style.dialog);
        this.chatRoomManager =chatRoomManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_volume);
        mMusicImg =findViewById(R.id.music_icon);
        mMusicBar =findViewById(R.id.music_bar);
        mVolumeImg =findViewById(R.id.volume_icon);
        mVolumeBar =findViewById(R.id.volume_bar);
        mEarImg =findViewById(R.id.ear_icon);
        mEarBar =findViewById(R.id.ear_bar);
        mBack =findViewById(R.id.volume_close);
        musicVal = SpUtil.getInt("musicVal",40);
        volumeVal = SpUtil.getInt("volumeVal",60);
        earVal = SpUtil.getInt("earVal",0);

        mMusicBar.setProgress(musicVal);
        mVolumeBar.setProgress(volumeVal);
        mEarBar.setProgress(earVal);

        if (musicVal == 0){
            mMusicImg.setImageResource(R.drawable.music_close);
        }else {
            mMusicImg.setImageResource(R.drawable.music_open);
        }
        if (volumeVal == 0){
            mVolumeImg.setImageResource(R.drawable.volume_close);
        }else {
            mVolumeImg.setImageResource(R.drawable.volume_open);
        }
        if (earVal == 0){
            mEarImg.setImageResource(R.drawable.ear_close);
        }else {
            mEarImg.setImageResource(R.drawable.ear_open);
        }

        mMusicBar.setOnSeekBarChangeListener(this);
        mVolumeBar.setOnSeekBarChangeListener(this);
        mEarBar.setOnSeekBarChangeListener(this);
        //mLocalBar.setThumb(getNewDrawable(R.drawable.seek_thumb,150,150));

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
       switch (seekBar.getId()){
           case R.id.music_bar:
               new Thread(()->{
                   chatRoomManager.getRtcManager().adjustAudioMixingVolume(i);
               }).start();
               if (i == 0){
                   mMusicImg.setImageResource(R.drawable.music_close);
               }else {
                   mMusicImg.setImageResource(R.drawable.music_open);
               }
               SpUtil.putInt("musicVal",i);
               break;
           case R.id.volume_bar:
               new Thread(()->{
                   chatRoomManager.getRtcManager().adjustRecordingSignalVolume(i);
               }).start();
               if (i == 0){
                   mVolumeImg.setImageResource(R.drawable.volume_close);
               }else {
                   mVolumeImg.setImageResource(R.drawable.volume_open);
               }
               SpUtil.putInt("volumeVal",i);
               break;
           case R.id.ear_bar:
               if (i ==0){
                   chatRoomManager.getRtcManager().enableInEarMonitoring(false);
                   mEarImg.setImageResource(R.drawable.ear_close);
               }else {
                   chatRoomManager.getRtcManager().enableInEarMonitoring(true);
                   mEarImg.setImageResource(R.drawable.ear_open);
               }
               new Thread(()->{
                   chatRoomManager.getRtcManager().setInEarMonitoringVolume(i);
               }).start();

               SpUtil.putInt("earVal",i);
               break;
           default:
               break;
       }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
