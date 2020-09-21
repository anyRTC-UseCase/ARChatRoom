package org.ar.audioganme.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.ar.audioganme.App;
import org.ar.rtc.Constants;
import org.ar.rtc.IRtcEngineEventHandler;
import org.ar.rtc.RtcEngine;

import java.util.Locale;


public final class RtcManager {

    public interface RtcEventListener {
        void onJoinChannelSuccess(String channelId);

        void onUserOnlineStateChanged(String uid, boolean isOnline);

        void onUserMuteAudio(String uid, boolean muted);

        void onAudioMixingStateChanged(int state);

        void onAudioVolumeIndication(String uid, int volume);
    }

    private final String TAG = RtcManager.class.getSimpleName();

    private static RtcManager instance;

    private Context mContext;
    private RtcEventListener mListener;
    private RtcEngine mRtcEngine;
    private String mUserId;

    private RtcManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static RtcManager getInstance(Context context) {
        if (instance == null) {
            synchronized (RtcManager.class) {
                if (instance == null)
                    instance = new RtcManager(context);
            }
        }
        return instance;
    }

    public RtcEngine getRtcEngine() {
        return mRtcEngine;
    }

    public void setListener(RtcEventListener listener) {
        mListener = listener;
    }

    public void init() {
        if (mRtcEngine == null) {
            try {
                mRtcEngine = RtcEngine.create(mContext, App.RTC_APPID, mEventHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mRtcEngine != null) {
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_STANDARD, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
            mRtcEngine.enableAudioVolumeIndication(500, 3, false);
        }
    }

    void joinChannel(String channelId, String userId) {
        if (mRtcEngine != null)
            mRtcEngine.joinChannel("", channelId, null, userId);
            mRtcEngine.setEnableSpeakerphone(true);
    }

    public void setClientRole(int role) {
        if (mRtcEngine != null)
            mRtcEngine.setClientRole(role);
    }

    public void setEffectsVolume(int i){
        if (mRtcEngine !=null){
            mRtcEngine.getAudioEffectManager().setEffectsVolume(i);
        }
    }

    public void adjustRecordingSignalVolume(int i){
        if (mRtcEngine !=null){
            mRtcEngine.adjustRecordingSignalVolume(i);
        }
    }

    public void enableInEarMonitoring(boolean enabled){
        if (mRtcEngine !=null){
            mRtcEngine.enableInEarMonitoring(enabled);
        }
    }

    public void setInEarMonitoringVolume(int i){
        if (mRtcEngine !=null){
            mRtcEngine.setInEarMonitoringVolume(i);
        }
    }

    public void muteAllRemoteAudioStreams(boolean muted) {
        if (mRtcEngine != null)
            mRtcEngine.muteAllRemoteAudioStreams(muted);
    }

    public void muteLocalAudioStream(boolean muted) {
        if (mRtcEngine != null)
            mRtcEngine.muteLocalAudioStream(muted);
        if (mListener != null)
            mListener.onUserMuteAudio(mUserId, muted);
    }

    public void stopAllEffects(){
        if (mRtcEngine !=null){
            mRtcEngine.getAudioEffectManager().stopAllEffects();
        }
    }

    public void playEffect(int soundId,String path){
        if (mRtcEngine !=null){
            mRtcEngine.getAudioEffectManager().playEffect(soundId,path,0,1.0,1.0,100.0,true);
        }
    }

    public void startAudioMixing(String filePath) {
        if (mRtcEngine != null) {
            mRtcEngine.startAudioMixing(filePath, false, false, -1);
        }
    }

    public void pauseAudioMixing() {
        if (mRtcEngine != null) {
            mRtcEngine.pauseAudioMixing();
        }
    }

    public void resumeAudioMixing() {
        if (mRtcEngine != null) {
            mRtcEngine.resumeAudioMixing();
        }
    }

    public void stopAudioMixing() {
        if (mRtcEngine != null)
            mRtcEngine.stopAudioMixing();
    }

    public void  adjustAudioMixingVolume(int volume) {
        if (mRtcEngine != null){
            Log.i(TAG, "adjustAudioMixingVolume: v ="+volume);
            mRtcEngine.adjustAudioMixingVolume(volume);
            //mRtcEngine.adjustAudioMixingPlayoutVolume(volume);
        }
    }

    public void setVoiceChanger(int type) {
        if (mRtcEngine != null)
            mRtcEngine.setParameters(String.format(Locale.getDefault(), "{\"che.audio.morph.voice_changer\": %d}", type));
    }

    public void setReverbPreset(int type) {
        if (mRtcEngine != null)
            mRtcEngine.setParameters(String.format(Locale.getDefault(), "{\"che.audio.morph.reverb_preset\": %d}", type));
    }

    void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
            setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        }
    }

    private IRtcEngineEventHandler mEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, String uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.i(TAG, "onJoinChannelSuccess: mListener ="+mListener);
            mUserId = uid;
            if (mListener != null)
                mListener.onJoinChannelSuccess(channel);
        }

        @Override
        public void onLastmileProbeResult(LastmileProbeResult result) {
            Log.i("dongmuyu", "onLastmileProbeResult: rtt ="+result.rtt +",jitter ="+result.downlinkReport.jitter);
            super.onLastmileProbeResult(result);
        }

        @Override
        public void onLastmileQuality(int quality) {
            super.onLastmileQuality(quality);
            Log.i("dongmuyu", "onLastmileQuality:  quality ="+quality);
        }

        @Override
        public void onClientRoleChanged(int oldRole, int newRole) {
            super.onClientRoleChanged(oldRole, newRole);
            Log.i(TAG, String.format("onClientRoleChanged %d %d", oldRole, newRole));

            if (mListener != null) {
                if (newRole == Constants.CLIENT_ROLE_BROADCASTER)
                    mListener.onUserOnlineStateChanged(mUserId, true);
                else if (newRole == Constants.CLIENT_ROLE_AUDIENCE)
                    mListener.onUserOnlineStateChanged(mUserId, false);
            }
        }

        @Override
        public void onUserJoined(String uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            //Log.i(TAG, String.format("onUserJoined %d", uid));

            if (mListener != null)
                mListener.onUserOnlineStateChanged(uid, true);
        }

        @Override
        public void onUserOffline(String uid, int reason) {
            super.onUserOffline(uid, reason);
            if (mListener != null)
                mListener.onUserOnlineStateChanged(uid, false);
        }

        @Override
        public void onUserMuteAudio(String uid, boolean muted) {
            super.onUserMuteAudio(uid, muted);
            Log.i(TAG, String.format("onUserMuteAudio %d %b", uid, muted));

            if (mListener != null)
                mListener.onUserMuteAudio(uid, muted);
        }

        @Override
        public void onRemoteAudioStateChanged(String uid, int state, int reason, int elapsed) {
            super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
            boolean muted=false;
            if (reason == Constants.REMOTE_AUDIO_REASON_REMOTE_MUTED){
                muted =true;
            }else if (reason ==Constants.REMOTE_AUDIO_REASON_REMOTE_UNMUTED){
                muted =false;
            }
            if (mListener != null)
                mListener.onUserMuteAudio(uid, muted);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);

            for (AudioVolumeInfo info : speakers) {
                if (info.volume > 0) {
                    String uid =null;
                    if (TextUtils.isEmpty(info.uid)){
                        uid =mUserId;
                    }else {
                        uid = info.uid;
                    }
                    if (mListener != null)
                        mListener.onAudioVolumeIndication(uid, info.volume);
                }
            }
        }

        @Override
        public void onAudioMixingStateChanged(int state, int errorCode) {
            super.onAudioMixingStateChanged(state, errorCode);

            if (mListener != null)
                mListener.onAudioMixingStateChanged(state);
        }
    };

}
