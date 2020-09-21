package org.ar.audioganme.audio;

import android.app.Activity;
import android.content.Context;

import org.ar.rtc.RtcEngine;
import org.ar.rtm.RtmChannel;
import org.ar.rtm.RtmClient;

public class AudioPresent implements IAudioPresenter {


    private IAudioModel audioModel;
    private IAudioView audioView;

    public AudioPresent(IAudioView audioView, Activity context, String channelId) {
        this.audioView = audioView;
        audioModel = new AudioModel(this,context,channelId);
    }

    @Override
    public RtcEngine getRTC() {
        return audioModel.getRTC();
    }

    @Override
    public RtmClient getRTM() {
        return audioModel.getRTM();
    }

    @Override
    public RtmChannel getRTMChannel() {
        return audioModel.getRTMChannel();
    }

    @Override
    public void release() {
        audioModel.release();
    }

    @Override
    public String getUserId() {
        return audioModel.getUserId();
    }

    @Override
    public String getUserName() {
        return audioModel.getUserName();
    }

    @Override
    public void joinRTCSuccess() {
        audioView.joinRTCSuccess();
    }

    @Override
    public void joinRTMSuccess() {
        audioView.joinRTMSuccess();
    }

    @Override
    public void memberSize(int num) {
        audioView.memberSize(num);
    }

    @Override
    public void userJoin(String uid) {
        audioView.userJoin(uid);
    }

    @Override
    public void userLeave(String uid) {
        audioView.userLeave(uid);
    }
}
