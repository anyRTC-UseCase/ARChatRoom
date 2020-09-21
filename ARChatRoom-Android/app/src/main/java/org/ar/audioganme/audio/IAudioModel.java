package org.ar.audioganme.audio;

import org.ar.rtc.RtcEngine;
import org.ar.rtm.RtmChannel;
import org.ar.rtm.RtmClient;

public interface IAudioModel {

     RtcEngine getRTC();
     RtmClient getRTM();
     RtmChannel createRTMChannel();
     RtmChannel getRTMChannel();
     void release();

     String getUserId();
     String getUserName();




}
