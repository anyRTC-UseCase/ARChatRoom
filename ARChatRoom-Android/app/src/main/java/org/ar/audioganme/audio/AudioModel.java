package org.ar.audioganme.audio;

import android.app.Activity;

import org.ar.audioganme.model.Member;
import org.ar.audioganme.util.MemberUtil;
import org.ar.audioganme.util.SpUtil;
import org.ar.audioganme.model.UserBean;
import org.ar.rtc.IRtcEngineEventHandler;
import org.ar.rtc.RtcEngine;
import org.ar.rtm.RtmChannel;
import org.ar.rtm.RtmChannelAttribute;
import org.ar.rtm.RtmChannelListener;
import org.ar.rtm.RtmChannelMember;
import org.ar.rtm.RtmClient;
import org.ar.rtm.RtmClientListener;
import org.ar.rtm.RtmMessage;

import java.util.List;
import java.util.Map;

public class AudioModel implements IAudioModel {

    private String USER = "user";
    private IAudioPresenter audioPresenter;
    private Activity context;
    private String channelId;
    private RtcEngine rtcEngine;
    private RtmClient rtmClient;
    private RtmChannel rtmChannel;

    public AudioModel(IAudioPresenter audioPresenter, Activity context, String channelId) {
        this.audioPresenter = audioPresenter;
        this.channelId = channelId;
        this.context = context;
        try {
            rtcEngine = RtcEngine.create(context,"",rtcEngineEventHandler);
            rtmClient = RtmClient.createInstance(context,"",rtmClientListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public RtcEngine getRTC() {
        return rtcEngine;
    }

    @Override
    public RtmClient getRTM() {
        return rtmClient;
    }

    @Override
    public RtmChannel createRTMChannel() {
       rtmChannel = rtmClient.createChannel(channelId,rtmChannelListener);
       return rtmChannel;
    }

    @Override
    public RtmChannel getRTMChannel() {
        return rtmChannel;
    }

    @Override
    public void release() {

    }

    @Override
    public String getUserId() {
        Member member = MemberUtil.getMember();
        if (member!=null){
            return member.getUserId();
        }else {
            return "";
        }
    }

    @Override
    public String getUserName() {
        Member member = MemberUtil.getMember();
        if (member!=null){
            return member.getName();
        }else {
            return "";
        }
    }

    private IRtcEngineEventHandler rtcEngineEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onWarning(int warn) {

        }

        @Override
        public void onError(int err) {
        }

        @Override
        public void onJoinChannelSuccess(String channel, String uid, int elapsed) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    audioPresenter.joinRTCSuccess();
                }
            });

        }

        @Override
        public void onRejoinChannelSuccess(String channel, String uid, int elapsed) {
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
        }

        @Override
        public void onClientRoleChanged(int oldRole, int newRole) {
        }

        @Override
        public void onUserJoined(String uid, int elapsed) {
        }

        @Override
        public void onUserOffline(String uid, int reason) {
        }

        @Override
        public void onFirstRemoteAudioFrame(String uid, int elapsed) {
        }

        @Override
        public void onRemoteAudioStateChanged(String uid, int state, int reason, int elapsed) {
        }
    };

    private RtmClientListener rtmClientListener = new RtmClientListener() {
        @Override
        public void onConnectionStateChanged(int var1, int var2) {

        }

        @Override
        public void onMessageReceived(RtmMessage var1, String var2) {

        }

        @Override
        public void onTokenExpired() {

        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> var1) {

        }
    };

    private RtmChannelListener rtmChannelListener = new RtmChannelListener() {
        @Override
        public void onMemberCountUpdated(final int var1) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    audioPresenter.memberSize(var1);
                }
            });

        }

        @Override
        public void onAttributesUpdated(List<RtmChannelAttribute> var1) {

        }

        @Override
        public void onMessageReceived(RtmMessage var1, RtmChannelMember var2) {

        }

        @Override
        public void onMemberJoined(final RtmChannelMember var1) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    audioPresenter.userJoin(var1.getUserId());
                }
            });

        }

        @Override
        public void onMemberLeft(final RtmChannelMember var1) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    audioPresenter.userLeave(var1.getUserId());
                }
            });

        }
    };
}
