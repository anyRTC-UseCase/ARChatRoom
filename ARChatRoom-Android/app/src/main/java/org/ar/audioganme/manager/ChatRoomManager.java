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
    public static final String MSG_TYPE_GIFT="gift";
    public static final String MSG_TYPE_MESSAGE ="msg";

    private static ChatRoomManager instance;

    private RtcManager mRtcManager;
    private RtmManager mRtmManager;
    private ChatRoomEventListener mListener;

    private ChannelData mChannelData = new ChannelData();

    public static final String[] giftNameArray={"棒棒糖","星星","魔术帽","独角兽","王冠","宝箱","跑车","火箭"};

    public static final int[] giftArray={R.drawable.anim_a,R.drawable.anim_b,R.drawable.anim_c,R.drawable.anim_d,R.drawable.anim_e,R.drawable.anim_f,R.drawable.anim_g,R.drawable.anim_h};

    private Map<String, String> attrUpdateMap =new HashMap<>();

    public interface QueryChannelAttributes{
        void onHasAttribute(String userId,boolean isHas);
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
    void onSeatUpdated(String userId,int position) {
        if (mListener != null) {
            mListener.onSeatUpdated(userId,position);
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
        attrUpdateMap.clear();
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
            jsonObject.put("giftId",String.valueOf(giftId));
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
                    case MSG_TYPE_GIFT:
                        String toUserId = jsonObject.getString("userId");
                        int giftId = Integer.parseInt(jsonObject.getString("giftId"));
                        if (mListener!=null){
                            mListener.onUserGivingGift(userId,toUserId,giftId);
                            String toName =mChannelData.getName(toUserId);
                            String name =mChannelData.getName(userId);
                            if (mChannelData.isAnchor(toUserId)){
                                addGiftMessage(name,toUserId,"主播",giftId);
                            }else if (mChannelData.isAnchor(userId)){
                                addGiftMessage("主播",toUserId,toName,giftId);
                            } else {
                                addGiftMessage(name,toUserId,toName,giftId);
                            }
                        }
                        break;
                    case MSG_TYPE_MESSAGE:
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
    private void addGiftMessage(String name,String toUserId,String toName,int giftId){
        if (mListener !=null){
            mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_GIFT,
                    name,"赠送", (TextUtils.isEmpty(toUserId)? "所有人":toName),"一个"+giftNameArray[giftId]));
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

        @Override
        public void onNetWorkDelayChanges(int rtt) {
            if (mListener != null)
                mListener.onNetWorkDelayChanges(rtt);
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
                                "并24小时对互动房间进行巡查，如果发现低俗、骂人、人身攻击等违规行为。官方将进行封房封号处理。"));

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
        public void onChannelAttributesUpdated(Map<String, String> attributes,boolean isQuery) {
            //查
            if (isQuery){
                if (!attributes.containsKey(AttributeKey.KEY_HOST)){
                    if (queryListener !=null){
                        queryListener.onHasAttribute(null,false);
                    }
                }else {
                    for (Map.Entry<String,String> queryEntry : attributes.entrySet()){
                        String query =queryEntry.getKey();
                        switch (query){
                            case AttributeKey.KEY_HOST:
                                String value = queryEntry.getValue();
                                if (queryListener!=null){
                                    Log.i(TAG, "onHasAttribute: true");
                                    queryListener.onHasAttribute(value,true);
                                }
                                break;
                            case AttributeKey.KEY_ROOM_NAME:
                                String roomName =queryEntry.getValue();
                                mChannelData.setRoomName(roomName);
                                break;
                            default:
                                break;
                        }
                    }
                }
                return;
            }
            int oldKeySize = attrUpdateMap.size();
            int newKeySize =attributes.size();
            if (oldKeySize < newKeySize){
               //增
                for (Map.Entry<String,String> insetEntry : attributes.entrySet()){
                    String insetKey =insetEntry.getKey();
                    String insetVal =insetEntry.getValue();
                    if (!attrUpdateMap.containsKey(insetKey)){
                        Log.i(TAG, "onChannelAttributesUpdated: insetKey ="+insetKey);
                        updateAttributes(insetKey,insetVal);
                    }
                }
            }else if (oldKeySize > newKeySize){
                //删
                for (Map.Entry<String, String> deleteEntry : attrUpdateMap.entrySet()) {
                    String deleteKey = deleteEntry.getKey();
                    String userId =deleteEntry.getValue();
                    if (!attributes.containsKey(deleteKey)){
                        Log.i(TAG, "onChannelAttributesUpdated: deleteKey ="+deleteKey);
                        if (deleteKey.contains("seat")){
                            int index = AttributeKey.indexOfSeatKey(deleteKey);
                            if (index >= 0) {
                                String value = null;
                                if (updateSeatArray(index, value)) {
                                    if (mListener != null){
                                        mListener.onSeatUpdated(userId,index);
                                    }
                                }
                            }
                        }
                        switch (deleteKey){
                            case AttributeKey.KEY_IS_LOCK:
                                mChannelData.setLock(false);
                                mChannelData.setLockVal(null);
                                if (mListener !=null){
                                    mListener.onPwdLockUpdated("");
                                }
                                break;
                            case AttributeKey.KEY_WAITING_LIST:
                                mChannelData.setWaitVal(null);
                                if (mListener !=null){
                                    mListener.onWaitUpdated(null);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }else {
                //改 oldKeySize = newKeySize
                for (Map.Entry<String,String> oldEntry : attrUpdateMap.entrySet()){
                    String oldKey = oldEntry.getKey();
                    String oldVal= oldEntry.getValue();
                    for (Map.Entry<String,String> newEntry : attributes.entrySet()){
                        String newKey = newEntry.getKey();
                        String newVal= newEntry.getValue();
                        if (TextUtils.equals(newKey,oldKey)){
                            if (!TextUtils.equals(newVal,oldVal)){
                                Log.i(TAG, "onChannelAttributesUpdated: newKey ="+newKey+",newVal ="+newVal);
                                updateAttributes(newKey,newVal);
                            }
                        }
                    }
                }
            }
            attrUpdateMap =attributes;
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
            if (mListener != null){
                if (mChannelData.isAnchor(userId)){
                    mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_SYSYTEM,"系统：主持人离开了"));
                }else {
                    mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_MEMBER_CHANGE,userId,"离开了房间"));
                }
            }
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
                        mListener.onAcceptLineUpdated(userId);
                    }
                }else if ("rejectLine".equals(cmd)){
                    String reason =jsonObject.getString("reason");
                    if (mListener !=null){
                        mListener.onRejectLineUpdated(reason,userId);
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

    private void updateAttributes(String key,String value){
        if (key.contains("seat")){
            int index = AttributeKey.indexOfSeatKey(key);
            if (index >= 0) {
                if (updateSeatArray(index, value)) {
                    Log.i(TAG, String.format("onChannelAttributesUpdated %s %s", key, value));
                    if (mListener != null){
                        mListener.onSeatUpdated(value,index);
                    }
                }
            }
        }
        switch (key){
            case AttributeKey.KEY_HOST:
                Member member = mChannelData.getMember(value);
                if (mListener !=null && member !=null ){
                    if (!TextUtils.isEmpty(member.getName())){
                        if (!mChannelData.isAnchor(member.getUserId())){
                            mListener.onCareOfAnchor(member);
                        }
                    }
                }
                mChannelData.setAnchorId(value);
                break;
            case AttributeKey.KEY_ROOM_NAME:
                mChannelData.setRoomName(value);
                if (mListener !=null){
                    mListener.onRoomNameUpdated(value);
                }
                break;
            case AttributeKey.KEY_WELCOME_TIP:
                mChannelData.setWelcomeTip(value);
                break;
            case AttributeKey.KEY_NOTICE:
                mChannelData.setAnnouncement(value);
                if (mListener !=null){
                    mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_SYSYTEM,"系统：主持人修改了公告"));
                }
                break;
            case AttributeKey.KEY_IS_MIC_LOCK:
                mChannelData.setIsMicLock(value);
                if (mListener !=null){
                    mListener.onMicLockUpdated(value);
                }
                break;
            case AttributeKey.KEY_IS_LOCK:
                mChannelData.setLock(true);
                mChannelData.setLockVal(value);
                if (mListener !=null){
                    mListener.onMessageAdd(new MessageListBean(MessageListBean.MSG_SYSYTEM,"系统：主持人设置了密码"));
                    mListener.onPwdLockUpdated(value);
                }
                break;
            case AttributeKey.KEY_MUTE_MIC_LIST:
                mChannelData.setUserMuted(value);
                if (mListener !=null){
                    mListener.onMutedMicUpdated(value);
                }
                break;
            case AttributeKey.KEY_MUTE_INPUT_LIST:
                mChannelData.setUserMutedInput(value);
                if (mListener !=null){
                    mListener.onMutedInputUpdated(value);
                }
                break;
            case AttributeKey.KEY_MUSIC:
                mChannelData.setMusicVal(value);
                if (mListener !=null){
                    mListener.onMusicUpdated(value);
                }
                break;
            case AttributeKey.KEY_WAITING_LIST:
                mChannelData.setWaitVal(value);
                if (mListener !=null){
                    mListener.onWaitUpdated(value);
                }
                break;
            default:
                break;
        }
    }
}
