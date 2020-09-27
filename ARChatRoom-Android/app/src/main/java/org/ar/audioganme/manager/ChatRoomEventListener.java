package org.ar.audioganme.manager;

import org.ar.audioganme.model.Member;
import org.ar.audioganme.model.MessageListBean;

public interface ChatRoomEventListener {

    void onRecordUpdated(String state);

    void onNetWorkDelayChanges(int rtt);

    void onRejectLineUpdated(String val,String userId);

    void onAcceptLineUpdated(String userId,String acceptPos);

    void onWaitUpdated(String val);

    void onCareOfAnchor(Member member);

    void onMusicUpdated(String val);

    void onMutedInputUpdated(String val);

    void onMutedMicUpdated(String val);

    void onMemberCountUpdate(int count);

    void onPwdLockUpdated(String val);

    void onMicLockUpdated(String val);

    void onRoomNameUpdated(String val);

    void onWelcomeTipUpdate(String val);

    void onAnnouncementUpdate(String val);

    void onSeatUpdated(String userId,int position);

    void onUserGivingGift(String fromUserId,String toUserId,int giftId);

    void onChannelMessageAdded(String userId,String message);

    void onMemberListUpdated(String userId);

    void onUserStatusChanged(String userId, Boolean muted);

    void onAudioMixingStateChanged(int state);

    void onAudioVolumeIndication(String userId, int volume);

    void onMessageAdd(MessageListBean messageListBean);

}
