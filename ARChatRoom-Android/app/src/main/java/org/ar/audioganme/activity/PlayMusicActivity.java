package org.ar.audioganme.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import org.ar.audioganme.R;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.util.SpUtil;
import org.ar.audioganme.util.StatusBarUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayMusicActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgWu_undulate,imgZui_undulate;
    private ImageView imgWu_stop,imgWu_play,imgZui_stop,imgZui_play;
    private ImageView music_back;

    private boolean isPlayWu,isPlayZui;
    private boolean isPauseWu,isPauseZui;
    private ChatRoomManager chatRoomManager;
    private Context context;
    private AnimationDrawable animationWu,animationZui;
    private String musicVal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setDeepStatusBar(true, PlayMusicActivity.this, Color.TRANSPARENT);
        StatusBarUtil.setStatusBarColor(PlayMusicActivity.this, R.color.white);
        setContentView(R.layout.activity_music_list);
        init();
        initView();
    }

    private void init() {
        imgWu_undulate =findViewById(R.id.music_wuming_undulate);
        imgWu_play =findViewById(R.id.music_wuming_play);
        imgWu_stop =findViewById(R.id.music_wuming_stop);
        imgZui_undulate =findViewById(R.id.music_zuimei_undulate);
        imgZui_play =findViewById(R.id.music_zuimei_play);
        imgZui_stop =findViewById(R.id.music_zuimei_stop);
        music_back =findViewById(R.id.music_back);

        chatRoomManager =ChatRoomManager.instance(this);
        animationWu = (AnimationDrawable) imgWu_undulate.getDrawable();
        animationZui = (AnimationDrawable) imgZui_undulate.getDrawable();
        musicVal =chatRoomManager.getChannelData().getMusicVal();

        imgWu_play.setOnClickListener(this);
        imgWu_stop.setOnClickListener(this);
        imgZui_play.setOnClickListener(this);
        imgZui_stop.setOnClickListener(this);
        music_back.setOnClickListener(this);
    }

    private void initView() {
        imgWu_undulate.setVisibility(View.GONE);
        imgZui_undulate.setVisibility(View.GONE);
        imgWu_stop.setVisibility(View.GONE);
        imgZui_stop.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(musicVal)){
            try {
                JSONObject jsonObject =new JSONObject(musicVal);
                String name =jsonObject.getString("name");
                String state =jsonObject.getString("state");
                if ("无名之辈".equals(name)){
                    if ("open".equals(state)){
                        imgWu_play.setImageResource(R.drawable.pause);
                        imgWu_undulate.setVisibility(View.VISIBLE);
                        imgWu_stop.setVisibility(View.VISIBLE);
                        if (!animationWu.isRunning()){
                            animationWu.start();
                        }
                        isPlayWu =true;
                        isPauseWu =true;
                    }else if ("pause".equals(state)){
                        imgWu_play.setImageResource(R.drawable.play);
                        imgWu_undulate.setVisibility(View.VISIBLE);
                        imgWu_stop.setVisibility(View.VISIBLE);
                        if (animationWu.isRunning()){
                            animationWu.stop();
                        }
                        isPauseWu =true;
                    }
                } else if ("最美的光".equals(name)) {
                    if ("open".equals(state)){
                        imgZui_play.setImageResource(R.drawable.pause);
                        imgZui_undulate.setVisibility(View.VISIBLE);
                        imgZui_stop.setVisibility(View.VISIBLE);
                        if (!animationZui.isRunning()){
                            animationZui.start();
                        }
                        isPlayZui =true;
                        isPauseZui =true;
                    }else if ("pause".equals(state)){
                        imgZui_play.setImageResource(R.drawable.play);
                        imgZui_undulate.setVisibility(View.VISIBLE);
                        imgZui_stop.setVisibility(View.VISIBLE);
                        if (animationZui.isRunning()){
                            animationZui.stop();
                        }
                        isPauseZui =true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==KeyEvent.KEYCODE_BACK){
            Intent intent =new Intent(PlayMusicActivity.this, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            overridePendingTransition(0,0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.music_wuming_play:
                String path = Environment.getExternalStorageDirectory().getPath()+"/wumingzhibei.m4a";
                if (isPlayWu){
                    chatRoomManager.getRtcManager().pauseAudioMixing();
                    imgWu_play.setImageResource(R.drawable.play);
                    if (animationWu.isRunning()){
                        animationWu.stop();
                    }
                    isPlayWu =false;
                    setChannelMusicAttr("无名之辈","pause");
                }else {
                    if (isPauseWu){
                        chatRoomManager.getRtcManager().resumeAudioMixing();
                    }else {
                        chatRoomManager.getRtcManager().stopAudioMixing();
                        chatRoomManager.getRtcManager().startAudioMixing(path);
                        imgWu_undulate.setVisibility(View.VISIBLE);
                        isPauseWu =true;
                    }
                    imgWu_play.setImageResource(R.drawable.pause);
                    imgZui_play.setImageResource(R.drawable.play);
                    imgWu_stop.setVisibility(View.VISIBLE);
                    imgZui_stop.setVisibility(View.GONE);
                    if (!animationWu.isRunning()){
                        animationWu.start();
                    }
                    if (animationZui.isRunning()){
                        animationZui.stop();
                    }
                    imgZui_undulate.setVisibility(View.GONE);
                    isPlayWu =true;
                    isPlayZui =false;
                    isPauseZui =false;
                    setChannelMusicAttr("无名之辈","open");
                }
                break;
            case R.id.music_wuming_stop:
                chatRoomManager.getRtcManager().stopAudioMixing();
                isPauseWu =false;
                imgWu_play.setImageResource(R.drawable.play);
                imgWu_stop.setVisibility(View.GONE);
                if (animationWu.isRunning()){
                    animationWu.stop();
                }
                imgWu_undulate.setVisibility(View.GONE);
                setChannelMusicAttr("无名之辈","close");
                break;
            case R.id.music_zuimei_play:
                String path1 = Environment.getExternalStorageDirectory().getPath()+"/zuimeideguang.mp3";
                if (isPlayZui){
                    chatRoomManager.getRtcManager().pauseAudioMixing();
                    imgZui_play.setImageResource(R.drawable.play);
                    if (animationZui.isRunning()){
                        animationZui.stop();
                    }
                    isPlayZui =false;
                    setChannelMusicAttr("最美的光","pause");
                }else {
                    if (isPauseZui){
                        chatRoomManager.getRtcManager().resumeAudioMixing();
                    }else {
                        chatRoomManager.getRtcManager().stopAudioMixing();
                        chatRoomManager.getRtcManager().startAudioMixing(path1);
                        imgZui_undulate.setVisibility(View.VISIBLE);
                        isPauseZui =true;
                    }
                    imgZui_play.setImageResource(R.drawable.pause);
                    imgWu_play.setImageResource(R.drawable.play);
                    imgZui_stop.setVisibility(View.VISIBLE);
                    imgWu_stop.setVisibility(View.GONE);

                    if (animationWu.isRunning()){
                        animationWu.stop();
                    }
                    if (!animationZui.isRunning()){
                        animationZui.start();
                    }
                    imgWu_undulate.setVisibility(View.GONE);
                    isPlayZui =true;
                    isPlayWu =false;
                    isPauseWu =false;
                    setChannelMusicAttr("最美的光","open");
                }
                break;
            case R.id.music_zuimei_stop:
                chatRoomManager.getRtcManager().stopAudioMixing();
                isPauseZui =false;
                imgZui_play.setImageResource(R.drawable.play);
                imgZui_stop.setVisibility(View.GONE);
                if (animationZui.isRunning()){
                    animationZui.stop();
                }
                imgZui_undulate.setVisibility(View.GONE);
                setChannelMusicAttr("最美的光","close");
                break;
            case R.id.music_back:
                Intent intent =new Intent(PlayMusicActivity.this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
                break;
            default:
                break;
        }
        int  musicVal = SpUtil.getInt("musicVal",40);
        chatRoomManager.getRtcManager().adjustAudioMixingVolume(musicVal);
    }

    private void setChannelMusicAttr(String name,String state){
        JSONObject jsonObject =new JSONObject();
        try {
            jsonObject.put("name",name);
            jsonObject.put("state",state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_MUSIC,jsonObject.toString(),null);
    }
}
