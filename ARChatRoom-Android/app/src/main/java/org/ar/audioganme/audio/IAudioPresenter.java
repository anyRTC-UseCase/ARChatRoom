package org.ar.audioganme.audio;

import android.content.Context;

import org.ar.rtc.RtcEngine;
import org.ar.rtm.RtmChannel;
import org.ar.rtm.RtmClient;

public interface IAudioPresenter {

    RtcEngine getRTC();
    RtmClient getRTM();
    RtmChannel getRTMChannel();
    void release();
    String getUserId();
    String getUserName();


    void joinRTCSuccess();
    void joinRTMSuccess();
    void memberSize(int num);
    void userJoin(String uid);
    void userLeave(String uid);
}
