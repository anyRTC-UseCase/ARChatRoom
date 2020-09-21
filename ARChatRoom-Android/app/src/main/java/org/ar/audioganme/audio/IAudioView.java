package org.ar.audioganme.audio;

public interface IAudioView {

    void joinRTCSuccess();
    void joinRTMSuccess();
    void memberSize(int num);
    void userJoin(String uid);
    void userLeave(String uid);
}
