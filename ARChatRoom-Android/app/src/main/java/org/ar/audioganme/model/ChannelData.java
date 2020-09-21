package org.ar.audioganme.model;

import android.text.TextUtils;
import android.util.Log;

import org.ar.audioganme.R;
import org.ar.audioganme.util.MemberUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChannelData implements Serializable {

    public static final int MAX_SEAT_NUM = 8;

    public void release() {
        mAnchorId = null;
        Arrays.fill(mSeatArray, 0, mSeatArray.length, null);
        mUserStatus.clear();
        mMemberList.clear();
        mMessageList.clear();
    }

    private String mAnchorId;
    private boolean isLock;
    private String lockVal;
    private String anchorAvatarAddr;
    private String anchorName;
    private String anchorSex;
    private String roomName;
    private String welcomeTip;
    private String announcement;
    private String isMicLock ="0";
    private String userMuted;
    private String userMutedInput;
    private String musicVal;
    private String waitVal;
    private String acceptPos;

    public String getAcceptPos() {
        return acceptPos;
    }

    public void setAcceptPos(String acceptPos) {
        this.acceptPos = acceptPos;
    }

    public String getWaitVal() {
        return waitVal;
    }

    public void setWaitVal(String waitVal) {
        this.waitVal = waitVal;
    }

    public String getUserMutedInput() {
        return userMutedInput;
    }

    public void setUserMutedInput(String userMutedInput) {
        this.userMutedInput = userMutedInput;
    }

    public String getMusicVal() {
        return musicVal;
    }

    public void setMusicVal(String musicVal) {
        this.musicVal = musicVal;
    }

    public String getUserMuted() {
        return userMuted;
    }

    public void setUserMuted(String userMuted) {
        this.userMuted = userMuted;
    }

    public String getIsMicLock() {
        return isMicLock;
    }

    public void setIsMicLock(String isMicLock) {
        this.isMicLock = isMicLock;
    }

    public String getWelcomeTip() {
        return welcomeTip;
    }

    public void setWelcomeTip(String welcomeTip) {
        this.welcomeTip = welcomeTip;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public String getAnchorSex() {
        return anchorSex;
    }

    public void setAnchorSex(String anchorSex) {
        this.anchorSex = anchorSex;
    }

    public String getAnchorAvatarAddr() {
        return anchorAvatarAddr;
    }

    public void setAnchorAvatarAddr(String mAnchorAvatarAddr) {
        this.anchorAvatarAddr = mAnchorAvatarAddr;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getLockVal() {
        return lockVal;
    }

    public void setLockVal(String val) {
        this.lockVal = val;
    }

    public boolean setAnchorId(String anchorId) {
        Log.i("isAnchor", "isAnchor: anchorId ="+anchorId);
        if (TextUtils.equals(anchorId, mAnchorId))
            return false;
        mAnchorId = anchorId;
        return true;
    }

    public String getAnchorId() {
        Log.i("isAnchor", "getAnchorId: mAnchorId ="+mAnchorId);
        return mAnchorId;
    }

    //是否有主播
    public boolean hasAnchor() {
        return !TextUtils.isEmpty(mAnchorId);
    }

    public boolean isAnchor(String userId) {
        return TextUtils.equals(userId, mAnchorId);
    }

    //主播是否为自己
    public boolean isAnchorMyself() {
        return isAnchor(String.valueOf(Constant.sUserId));
    }

    // SeatArray
    private String[] mSeatArray = new String[MAX_SEAT_NUM];

    public String[] getSeatArray() {
        return mSeatArray;
    }

    public boolean updateSeat(int position, String userId) {
        String temp = mSeatArray[position];
        Log.i("TAG", "updateSeat: temp ="+temp+",userId ="+userId+",pos ="+position);
        if (userId == temp)
            return false;
        if (userId != null && temp != null && TextUtils.equals(userId, temp))
            return false;
        mSeatArray[position] = userId;
        return true;
    }

    public int indexOfSeatArray(String userId) {
        for (int i = 0; i < mSeatArray.length; i++) {
            String id = mSeatArray[i];
            if (TextUtils.isEmpty(id)) {
                continue;
            }
            if (TextUtils.equals(userId, id)){
                return i;
            }
        }
        return -1;
    }

    public int firstIndexOfEmptySeat() {
        for (int i = 0; i < mSeatArray.length; i++) {
            String userId = mSeatArray[i];
            if (TextUtils.isEmpty(userId)) return i;
            if (!isUserOnline(userId)) return i;
        }
        return -1;
    }

    // UserStatus
    private Map<String, Boolean> mUserStatus = new HashMap<>();

    public boolean isUserOnline(String userId) {
        Boolean muted = mUserStatus.get(userId);
        return muted != null;
    }

    public boolean isUserMuted(String userId) {
        Boolean muted = mUserStatus.get(userId);
        if (muted != null) return muted;
        return false;
    }

    public void addOrUpdateUserStatus(String uid, boolean muted) {
        mUserStatus.put(String.valueOf(uid), muted);
    }

    public void removeUserStatus(String uid) {
        mUserStatus.remove(String.valueOf(uid));
    }

    // MemberList
    private List<Member> mMemberList = new ArrayList<>();

    public List<Member> getMemberList() {
        return mMemberList;
    }

    public void addOrUpdateMember(Member member) {
        int index = mMemberList.indexOf(member);
        Log.i("Member", "addOrUpdateMember: index ="+index);
        if (index >= 0) {
            mMemberList.get(index).update(member);
        } else {
            mMemberList.add(member);
        }
    }

    public void removeMember(String userId) {
        Member member = new Member(userId);
        mMemberList.remove(member);
    }

    public Member getMember(String userId) {
        Log.i("TAG", "getMember: --->"+userId+",mMeList ="+mMemberList.size());
        for (Member member : mMemberList) {
            Log.i("TAG", "getMember: me ="+member.getUserId());
            if (TextUtils.equals(userId, member.getUserId())) {
                return member;
            }
        }
        return null;
    }

    public String getName(String userId){
        Member member =getMember(userId);
        if (!TextUtils.isEmpty(member.getName())){
            return member.getName();
        }
        return userId;
    }

    public String getMemberAvatar(String userId) {
        Member member = getMember(userId);
        if (member == null) {
            return null;
        }
        return member.getAvatarAddr();
    }

    public int indexOfMemberList(String userId) {
        return mMemberList.indexOf(new Member(userId));
    }

    // MessageList
    private List<Message> mMessageList = new ArrayList<>();

    public List<Message> getMessageList() {
        return mMessageList;
    }

    public int addMessage(Message message) {
        mMessageList.add(message);
        return mMessageList.size() - 1;
    }

}
