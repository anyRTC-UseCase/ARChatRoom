package org.ar.audioganme.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.ar.audioganme.R;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.model.Constant;
import org.ar.audioganme.model.Member;
import org.ar.audioganme.model.Message;
import org.ar.audioganme.model.MessageListBean;
import org.ar.audioganme.model.UserBean;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.util.MemberUtil;
import org.ar.audioganme.util.SpUtil;
import org.ar.rtm.ChannelAttributeOptions;
import org.ar.rtm.ErrorInfo;
import org.ar.rtm.ResultCallback;
import org.ar.rtm.RtmChannel;
import org.ar.rtm.RtmChannelAttribute;
import org.ar.rtm.RtmChannelMember;
import org.ar.rtm.RtmMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class ChatRoomManager extends SeatManager implements MessageManager {

    private final String TAG = ChatRoomManager.class.getSimpleName();

    private static ChatRoomManager instance;

    private RtcManager mRtcManager;
    private RtmManager mRtmManager;
    private ChatRoomEventListener mListener;

    private ChannelData mChannelData = new ChannelData();

    public static final String[] giftNameArray={"棒棒糖","星星","魔术帽","独角兽","王冠","宝箱","跑车","火箭"};

    public static final int[] giftArray={R.drawable.anim_a,R.drawable.anim_b,R.drawable.anim_c,R.drawable.anim_d,R.drawable.anim_e,R.drawable.anim_f,R.drawable.anim_g,R.drawable.anim_h};

    private Map<String, String> attrQueryMap =new HashMap<>();
    private Map<String, String> attrUpdateMap =new HashMap<>();

    public interface QueryChannelAttributes{
        void onHasAttribute(String userId);
        void OnNothing();
    }

    private QueryChannelAttributes queryListener;

    @Override
    public ChannelData getChannelData() {
        return mChannelData;
    }

    public RtmChannel getChannel(){
        return mRtmManager.getmRtmChannel();
    }

    @Override
    MessageManager getMessageManager() {
        return this;
    }

    @Override
    public RtcManager getRtcManager() {
        return mRtcManager;
    }

    @Override
    public RtmManager getRtmManager() {
        return mRtmManager;
    }



    @Override
    void onSeatUpdated(int position) {
        if (mListener != null) {
            Log.i(TAG, "onSeatUpdated: --->pos ="+position);
            mListener.onSeatUpdated(position);
        }
    }

    private ChatRoomManager(Context context) {
        mRtcManager = RtcManager.getInstance(context);
        mRtcManager.setListener(mRtcListener);
        mRtmManager = RtmManager.getInstance(context);
        mRtmManager.setListener(mRtmListener);
    }

    public static ChatRoomManager instance(Context context) {
        if (instance == null) {
            synchronized (ChatRoomManager.class) {
                if (instance == null)
                    instance = new ChatRoomManager(context);
            }
        }
        return instance;
    }

    public void setListener(ChatRoomEventListener listener) {
        mListener = listener;
    }

    public void login(String userId,String mChannel){
        mRtmManager.login(userId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "onSuccess: dong ---->");
                mRtmManager.getChannelAttr(mChannel);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    public void joinChannel(String channelId) {
        mRtcManager.joinChannel(channelId, Constant.sUserId);
        mRtmManager.setLocalUserAttributes(AttributeKey.KEY_NAME, Constant.sName);
        mRtmManager.setLocalUserAttributes(AttributeKey.KEY_UID, Constant.sUserId);
        mRtmManager.setLocalUserAttributes(AttributeKey.KEY_HEAD, Constant.sAvatarAddr);
        mRtmManager.setLocalUserAttributes(AttributeKey.KEY_SEX, String.valueOf(Constant.sGender));
    }

    public void leaveChannel() {
        mRtcManager.leaveChannel();
        mRtmManager.leaveChannel();
        mChannelData.release();
    }

    private void checkAndBeAnchor() {
        String myUserId = String.valueOf(Constant.sUserId);

        if (mChannelData.isAnchorMyself()) {
            int index = mChannelData.indexOfSeatArray(myUserId);
            if (index == -1) {
                index = mChannelData.firstIndexOfEmptySeat();
            }
            toBroadcaster(myUserId, index);
        } else {
            if (mChannelData.hasAnchor()) return;
            mRtmManager.addOrUpdateChannelAttributes(AttributeKey.KEY_HOST, myUserId, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    toBroadcaster(myUserId, mChannelData.firstIndexOfEmptySeat());
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }
    }

    public void sendGift(String userId,int giftId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd","gift");
            jsonObject.put("giftId",giftId);
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRtmManager.sendChannelMessage(jsonObject.toString(),null);
    }

    @Override
    public void sendOrder(String userId, String orderType, String content, ResultCallback<Void> callback) {
        if (!mChannelData.isAnchorMyself()) return;
        Message message = new Message(orderType, content, Constant.sUserId);
        mRtmManager.sendMessageToPeer(userId, message.toJsonString(), callback);
    }

    @Override
    public void sendMessage(String text) {
//        Message message = new Message(text, Constant.sUserId);
//        mRtmManager.sendChannelMessage(message.toJsonString(), new ResultCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                addMessage(message);
//            }
//
//            @Override
//            public void onFailure(ErrorInfo errorInfo) {
//
//            }
//        });
    }

    @Override
    public void sendChannelMessage(String text) {
        JSONObject jsonObject =new JSONObject();
        try {
            jsonObject.put("cmd","msg");
            jsonObject.put("content",text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRtmManager.sendChannelMessage(jsonObject.toString(),null);
    }

    @Override
    public void processMessage(RtmMessage rtmMessage,String userId) {
        Message message = Message.fromJsonString(rtmMessage.getText());
        switch (message.getMessageType()) {
            case Message.MESSAGE_TYPE_TEXT:

            case Message.MESSAGE_TYPE_IMAGE:
                addMessage(message);
                break;
            case Message.MESSAGE_TYPE_GIFT:
                if (mListener != null)
//                    mListener.onUserGivingGift(message.getSendId());
                break;
            case Message.MESSAGE_TYPE_ORDER:
                String myUserId = String.valueOf(Constant.sUserId);
                switch (message.getOrderType()) {
                    case Message.ORDER_TYPE_AUDIENCE:
                        toAudience(myUserId, null);
                        break;
                    case Message.ORDER_TYPE_BROADCASTER:
                        toBroadcaster(myUserId, Integer.valueOf(message.getContent()));
                        break;
                    case Message.ORDER_TYPE_MUTE:
                        muteMic(myUserId, Boolean.valueOf(message.getContent()));
                        break;
                }
                break;
        }
    }

    @Override
    public void processChannelMessage(RtmMessage rtmMessage,String userId) {
        try {
            JSONObject jsonObject = new JSONObject(rtmMessage.getText());
            if (!jsonObject.isNull("cmd")){
                String cmd = jsonObject.getString("cmd");
                switch (cmd){
                    case "gift":
                        String toUserId = jsonObject.getString("userId");
                        int giftId = jsonObject.getInt("giftId");
                        if (mListener!=null){
                            mListener.onUserGivingGift(userId,toUserId,giftId);
                            Member toMember =mChannelData.getMember(toUserId);
                            Member member =mChannelData.getMember(userId);
                            Log.i(TAG, "processChannelMessage: toUser ="+toUserId+",UserId ="+userId);
                            if (mChannelData.isAnchor(toUserId)){
                                mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_GIFT,
                                        member.getName(),"赠送", (TextUtils.isEmpty(toUserId)? "所有人":"主播"),"一个"+giftNameArray[giftId]));
                            }else if (mChannelData.isAnchor(userId)){
                                mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_GIFT,
                                        "主播","赠送",(TextUtils.isEmpty(toUserId)? "所有人":toMember.getName()),"一个"+giftNameArray[giftId]));
                            }
                            else {
                                mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_GIFT,
                                        member.getName(),"赠送", (TextUtils.isEmpty(toUserId)? "所有人":toMember.getName()),"一个"+giftNameArray[giftId]));
                            }
                        }
                        break;
                    case "msg":
                        String content = jsonObject.getString("content");
                        if (mListener!=null){
                            mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_NORMAL,mChannelData.getName(userId)+":",content));
                        }
                        break;

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMessage(Message message) {
        int position = mChannelData.addMessage(message);
//        if (mListener != null)
//            mListener.onMessageAdded(position);
    }

    private RtcManager.RtcEventListener mRtcListener = new RtcManager.RtcEventListener() {
        @Override
        public void onJoinChannelSuccess(String channelId) {
            mRtmManager.joinChannel(channelId, null);
        }

        @Override
        public void onUserOnlineStateChanged(String uid, boolean isOnline) {
            if (isOnline) {
                mChannelData.addOrUpdateUserStatus(uid, false);

                if (mListener != null)
                    mListener.onUserStatusChanged(String.valueOf(uid), false);
            } else {
                mChannelData.removeUserStatus(uid);

                if (mListener != null)
                    mListener.onUserStatusChanged(String.valueOf(uid), null);
            }
        }

        @Override
        public void onUserMuteAudio(String uid, boolean muted) {
            mChannelData.addOrUpdateUserStatus(uid, muted);

            if (mListener != null)
                mListener.onUserStatusChanged(String.valueOf(uid), muted);
        }

        @Override
        public void onAudioMixingStateChanged(int state) {
            if (mListener != null)
                mListener.onAudioMixingStateChanged(state);
        }

        @Override
        public void onAudioVolumeIndication(String uid, int volume) {
            if (mListener != null)
                mListener.onAudioVolumeIndication(String.valueOf(uid), volume);
        }
    };

    public void setQueryListener(QueryChannelAttributes listener){
        queryListener =listener;
    }

    private RtmManager.RtmEventListener mRtmListener = new RtmManager.RtmEventListener() {

        @Override
        public void onRtmConnectStateChange(int state, int reason) {

        }

        @Override
        public void onJoinChannelSuccess() {
            if (mListener != null){
                mListener.onMessageAdd(new MessageListBean
                        (MessageListBean.MSG_SYSYTEM,"系统：官方倡导绿色交友，" +
                                "并24小时对互动房间进行巡查，如果发现低俗、骂人、人身攻击等违规行为。官方将进行封房封号处理"));

                if (!TextUtils.isEmpty(mChannelData.getWelcomeTip())){
                    mListener.onMessageAdd(new MessageListBean
                            (MessageListBean.MSG_SYSYTEM,mChannelData.getWelcomeTip()));
                }
            }
        }

        @Override
        public void onChannelAttributesLoaded() {
            //checkAndBeAnchor();
        }

        @Override
        public void onChannelAttributesQuery(Map<String, String> attributes,String channelId) {
            boolean hasAttributes =false;
            if (attrQueryMap.size() !=0){
                for (Map.Entry<String, String> entryOld : attrQueryMap.entrySet()) {
                    String keyOld = entryOld.getKey();
                    if (!attributes.containsKey(keyOld)){
                        Log.i(TAG, "onChannelAttributesUpdated: QuerykeyOld ="+keyOld);
                        switch (keyOld){
                            case AttributeKey.KEY_IS_LOCK:
                                mChannelData.setLock(false);
                                mChannelData.setLockVal(null);
                                if (mListener !=null){
                                    mListener.onPwdLockUpdated("");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                Log.i(TAG, "onChannelAttributesQuery: dong key="+key);
                switch (key) {
                    case AttributeKey.KEY_HOST:
                        hasAttributes =true;
                        String userId = entry.getValue();
                        if (mChannelData.setAnchorId(userId)){
                            Log.i(TAG, String.format("onChannelAttributesUpdated--> %s %s", key, userId));
                        }
                        if (queryListener!=null){
                            queryListener.onHasAttribute(userId);
                        }
                        break;
                    case AttributeKey.KEY_ROOM_NAME:
                        String roomName =entry.getValue();
                        mChannelData.setRoomName(roomName);
                        break;
                    case AttributeKey.KEY_ANCHOR_AVATAR:
                        String addr =entry.getValue();
                        mChannelData.setAnchorAvatarAddr(addr);
                        break;
                    case AttributeKey.KEY_IS_LOCK:
                        String val =entry.getValue();
                        mChannelData.setLock(true);
                        mChannelData.setLockVal(val);
                        break;
                    case AttributeKey.KEY_ANCHOR_NAME:
                        String name =entry.getValue();
                        mChannelData.setAnchorName(name);
                        break;
                    case AttributeKey.KEY_ANCHOR_SEX:
                        String sex =entry.getValue();
                        mChannelData.setAnchorSex(sex);
                        break;
                    case AttributeKey.KEY_MUTE_MIC_LIST:
                        String userVal =entry.getValue();
                        mChannelData.setUserMuted(userVal);
                        break;
                    case AttributeKey.KEY_MUTE_INPUT_LIST:
                        String userMutedInput =entry.getValue();
                        mChannelData.setUserMutedInput(userMutedInput);
                        break;
                    case AttributeKey.KEY_MUSIC:
                        String musicVal =entry.getValue();
                        mChannelData.setMusicVal(musicVal);
                        break;
                    default:
                        break;
                }
            }
            Log.i(TAG, "onChannelAttributesQuery: dong hasAttributes ="+hasAttributes);
            if (!hasAttributes){
                if (queryListener !=null){
                    queryListener.OnNothing();
                }
            }
            attrQueryMap =attributes;
        }

        @Override
        public void onChannelAttributesUpdated(Map<String, String> attributes) {
            if (attrQueryMap.size() !=0){
                for (Map.Entry<String, String> entryOld : attrQueryMap.entrySet()) {
                    String keyOld = entryOld.getKey();
                    Log.i(TAG, "onChannelAttributesUpdated: keyOld ----> ="+keyOld);
                    if (!attributes.containsKey(keyOld)){
                        Log.i(TAG, "onChannelAttributesUpdated: keyOld ="+keyOld);
                        if (keyOld.contains("seat")){
                            int index = AttributeKey.indexOfSeatKey(keyOld);
                            if (index >= 0) {
                                String value = null;
                                if (updateSeatArray(index, value)) {
                                    if (mListener != null){
                                        mListener.onSeatUpdated(index);
                                    }
                                }
                            }
                        }
                        switch (keyOld){
                            case AttributeKey.KEY_IS_LOCK:
                                mChannelData.setLock(false);
                                mChannelData.setLockVal(null);
                                if (mListener !=null){
                                    mListener.onPwdLockUpdated("");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                Log.i(TAG, "onChannelAttributesUpdated: key ="+key);
                if (key.contains("seat")){
                    int index = AttributeKey.indexOfSeatKey(key);
                    if (index >= 0) {
                        String value =entry.getValue();
                        if (updateSeatArray(index, value)) {
                            Log.i(TAG, String.format("onChannelAttributesUpdated %s %s", key, value));
                            if (mListener != null){
                                Log.i(TAG, "onChannelAttributesUpdated: dong index ="+index);
                                mListener.onSeatUpdated(index);
                            }
                        }
                    }
                }
                switch (key){
                    case AttributeKey.KEY_ROOM_NAME:
                        String roomName = entry.getValue();
                        mChannelData.setRoomName(roomName);
                        if (mListener !=null){
                            mListener.onRoomNameUpdated(roomName);
                        }
                        break;
                    case AttributeKey.KEY_WELCOME_TIP:
                        String welcomeVal = entry.getValue();
                        mChannelData.setWelcomeTip(welcomeVal);
                        break;
                    case AttributeKey.KEY_NOTICE:
                        String notice =entry.getValue();
                        mChannelData.setAnnouncement(notice);
                        if (mListener !=null){
                            mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_SYSYTEM,"系统：主持人修改了公告"));
                        }
                        break;
                    case AttributeKey.KEY_IS_MIC_LOCK:
                        String val =entry.getValue();
                        mChannelData.setIsMicLock(val);
                        if (mListener !=null){
                            mListener.onMicLockUpdated(val);
                        }
                        break;
                    case AttributeKey.KEY_IS_LOCK:
                        String lockVal =entry.getValue();
                        mChannelData.setLock(true);
                        mChannelData.setLockVal(lockVal);
                        if (mListener !=null){
                            //mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_SYSYTEM,"系统：主持人设置了密码"));
                            mListener.onPwdLockUpdated(lockVal);
                        }
                        break;
                    case AttributeKey.KEY_MUTE_MIC_LIST:
                        String userVal =entry.getValue();
                        mChannelData.setUserMuted(userVal);
                        if (mListener !=null){
                            mListener.onMutedMicUpdated(userVal);
                        }
                        break;
                    case AttributeKey.KEY_MUTE_INPUT_LIST:
                        String userMeteVal =entry.getValue();
                        mChannelData.setUserMutedInput(userMeteVal);
                        if (mListener !=null){
                            mListener.onMutedInputUpdated(userMeteVal);
                        }
                        break;
                    case AttributeKey.KEY_MUSIC:
                        String musicVal =entry.getValue();
                        mChannelData.setMusicVal(musicVal);
                        if (mListener !=null){
                            mListener.onMusicUpdated(musicVal);
                        }
                        break;
                    case AttributeKey.KEY_HOST:
                        String userHost = entry.getValue();
                        Member member = mChannelData.getMember(userHost);
                        Log.i(TAG, "onCareOfAnchor KEY_HOST: member ="+member+",userHost ="+userHost);
                        if (mListener !=null && member !=null){
                            if (!TextUtils.isEmpty(member.getName())){
                                if (!mChannelData.isAnchor(member.getUserId())){
                                    mListener.onCareOfAnchor(member);
                                }
                            }
                        }
                        mChannelData.setAnchorId(userHost);
                        break;
                    case AttributeKey.KEY_WAITING_LIST:
                        String waitVal =entry.getValue();
                        mChannelData.setWaitVal(waitVal);
                        Log.i(TAG, "onWaitUpdated KEY_WAITING_LIST:  --->listener ="+mListener);
                        if (mListener !=null){
                            mListener.onWaitUpdated(waitVal);
                        }
                        break;
                    default:
                        break;
                }
            }
            attrQueryMap =attributes;
        }

        @Override
        public void onInitMembers(List<RtmChannelMember> members) {
            for (RtmChannelMember member : members) {
                mChannelData.addOrUpdateMember(new Member(member.getUserId()));
            }
            if (mListener != null) {
                mListener.onMemberListUpdated(null);
            }
        }

        @Override
        public void onMemberCount(int count) {
            if (mListener!=null){
                mListener.onMemberCountUpdate(count);
            }
        }

        @Override
        public void onMemberJoined(String userId) {
            if (mListener != null)
                mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_MEMBER_CHANGE,userId,"进入了房间"));

        }


        @Override
        public void onMemberLeft(String userId) {
            if (mListener != null)
                mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_MEMBER_CHANGE,userId,"离开了房间"));

        }

        @Override
        public void onMessageReceived(RtmMessage message,String userId) {
            processMessage(message,userId);
            try {
                JSONObject jsonObject =new JSONObject(message.getText());
                String cmd =jsonObject.getString("cmd");
                if ("acceptLine".equals(cmd)){
                    String acceptPos =jsonObject.getString("seat");
                    if (mListener !=null){
                        mChannelData.setAcceptPos(acceptPos);
                        mListener.onAcceptLineUpdated(acceptPos);
                    }
                }else if ("rejectLine".equals(cmd)){
                    String reason =jsonObject.getString("reason");
                    if (mListener !=null){
                        mListener.onRejectLineUpdated(reason);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChannelMessageReceived(RtmMessage message,String userId) {
            processChannelMessage(message,userId);
        }

        @Override
        public void OnMembersAttributes(String userId, Map<String, String> attributes) {
            Log.i(TAG, "onMemberJoined: uid ="+userId);
            String name =null,uid=null,head=null,sex=null;
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                switch (entry.getKey()){
                    case AttributeKey.KEY_NAME:
                        name =entry.getValue();
                        break;
                    case AttributeKey.KEY_UID:
                        uid =entry.getValue();
                        break;
                    case AttributeKey.KEY_HEAD:
                        head =entry.getValue();
                        break;
                    case AttributeKey.KEY_SEX:
                        sex =entry.getValue();
                        break;
                    default:
                        break;
                }
            }
            Log.i(TAG, "onMemberJoined: uid ="+uid+",name="+name+",sex="+sex+",head="+head);
            if (name !=null && sex!=null && head!=null){
                Member member =new Member(userId,name,Integer.parseInt(sex),head);
                mChannelData.addOrUpdateMember(member);
                if (mListener != null)
                    mListener.onMemberListUpdated(userId);
            }
        }
    };

}
