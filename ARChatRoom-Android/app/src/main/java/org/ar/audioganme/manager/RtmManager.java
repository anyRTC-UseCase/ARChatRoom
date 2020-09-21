package org.ar.audioganme.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import org.ar.audioganme.App;
import org.ar.audioganme.model.Member;
import org.ar.rtm.ChannelAttributeOptions;
import org.ar.rtm.ErrorInfo;
import org.ar.rtm.ResultCallback;
import org.ar.rtm.RtmAttribute;
import org.ar.rtm.RtmCallEventListener;
import org.ar.rtm.RtmChannel;
import org.ar.rtm.RtmChannelAttribute;
import org.ar.rtm.RtmChannelListener;
import org.ar.rtm.RtmChannelMember;
import org.ar.rtm.RtmClient;
import org.ar.rtm.RtmClientListener;
import org.ar.rtm.RtmMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RtmManager {
    private final String TAG = RtmManager.class.getSimpleName();
    private final static int HANDLER_MEMBER_ATTR_MSG =0;
    private static RtmManager instance;

    private Context mContext;
    private RtmEventListener mListener;
    private RtmClient mRtmClient;
    public RtmChannel mRtmChannel;
    private boolean mIsLogin;

    public interface RtmEventListener {
        void onRtmConnectStateChange(int state,int reason);

        void onJoinChannelSuccess();

        void onChannelAttributesLoaded();

        void onChannelAttributesUpdated(Map<String, String> attributes);

        void onChannelAttributesQuery(Map<String, String> attributes,String channelId);

        void onInitMembers(List<RtmChannelMember> members);

        void onMemberCount(int count);

        void onMemberJoined(String userId);

        void onMemberLeft(String userId);

        void onMessageReceived(RtmMessage message,String userId);

        void onChannelMessageReceived(RtmMessage message,String userId);

        void OnMembersAttributes(String userId,Map<String, String> attributes);
    }

    private RtmManager(Context context){
        mContext =context.getApplicationContext();
    }

    public static RtmManager getInstance(Context context){
        if (instance ==null){
            synchronized (RtmManager.class){
                if (instance ==null){
                    instance =new RtmManager(context);
                }
            }
        }
        return instance;
    }

    public void setListener(RtmEventListener listener){
        mListener =listener;
    }

    public void init() {
        if (mRtmClient == null) {
            try {
                mRtmClient = RtmClient.createInstance(mContext, App.RTM_APPID, mClientListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void login(String userId, ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            if (mIsLogin) {
                if (callback != null)
                    callback.onSuccess(null);
                return;
            }
            mRtmClient.login("", String.valueOf(userId), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "rtm login success");
                    mIsLogin = true;

                    if (callback != null)
                        callback.onSuccess(aVoid);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("rtm join %s", errorInfo.getErrorDescription()));
                    mIsLogin = false;

                    if (callback != null)
                        callback.onFailure(errorInfo);
                }

            });
        }
    }

    public void joinChannel(String channelId, ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            leaveChannel();
            Log.w(TAG, String.format("joinChannel %s", channelId));
            try {
                RtmChannel rtmChannel = mRtmClient.createChannel(channelId, mChannelListener);
                if (rtmChannel == null) return;
                rtmChannel.join(new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "rtm join success");
                        mRtmChannel = rtmChannel;

                        getChannelAttributes(channelId);
                        getMembers();
                        if (mListener != null)
                            mListener.onJoinChannelSuccess();
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        Log.e(TAG, String.format("rtm join %s", errorInfo.getErrorDescription()));
                        //AlertUtil.showToast("RTM login failed, see the log to get more info");
                        mRtmChannel = rtmChannel;
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public RtmClient getmRtmClient() {
        return mRtmClient;
    }

    public void getChannelAttr(String channelId){
        if (mRtmClient != null) {
            mRtmClient.getChannelAttributes(channelId, new ResultCallback<List<RtmChannelAttribute>>() {
                @Override
                public void onSuccess(List<RtmChannelAttribute> attributeList) {
                    Log.i(TAG, "onSuccess:  dong---->"+channelId);;
                    queryChannelAttributes(attributeList,channelId);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("getChannelAttributes %s", errorInfo.getErrorDescription()));
                }
            });
        }
    }



    //获取某个频道属性
    public void getChannelAttributes(String channelId) {
        if (mRtmClient != null) {
            mRtmClient.getChannelAttributes(channelId, new ResultCallback<List<RtmChannelAttribute>>() {
                @Override
                public void onSuccess(List<RtmChannelAttribute> attributeList) {
                    Log.i(TAG, "onSuccess: attributeList muyu size="+attributeList.size());
                    processChannelAttributes(attributeList);
                    if (mListener != null)
                        mListener.onChannelAttributesLoaded();
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("getChannelAttributes %s", errorInfo.getErrorDescription()));
                }
            });
        }
    }

    //判断频道属性有无host属性
    private void queryChannelAttributes(List<RtmChannelAttribute> attributeList,String channelId) {
        if (attributeList != null) {
            Map<String, String> attributes = new HashMap<>();
            for (RtmChannelAttribute attribute : attributeList) {
                attributes.put(attribute.getKey(), attribute.getValue());
            }
            if (mListener != null)
                mListener.onChannelAttributesQuery(attributes,channelId);
        }
    }

    private void processChannelAttributes(List<RtmChannelAttribute> attributeList) {
        if (attributeList != null) {
            Map<String, String> attributes = new HashMap<>();
            for (RtmChannelAttribute attribute : attributeList) {
                attributes.put(attribute.getKey(), attribute.getValue());
            }

            if (mListener != null)
                mListener.onChannelAttributesUpdated(attributes);
        }
    }

    public void getMembers() {
        if (mRtmChannel != null) {
            mRtmChannel.getMembers(new ResultCallback<List<RtmChannelMember>>() {
                @Override
                public void onSuccess(List<RtmChannelMember> rtmChannelMembers) {
                    if (mListener != null)
                        mListener.onInitMembers(rtmChannelMembers);
                    for (RtmChannelMember member : rtmChannelMembers) {
                        Log.i(TAG, "onSuccess: memberUser ="+member.getUserId());
                        getUserAttributes(member.getUserId());
                    };
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }
    }

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_MEMBER_ATTR_MSG:
                    String userId = (String) msg.obj;
                    mRtmClient.getUserAttributes(userId, new ResultCallback<List<RtmAttribute>>() {
                        @Override
                        public void onSuccess(List<RtmAttribute> rtmAttributes) {
                            Log.d(TAG, String.format("getUserAttributes %s", rtmAttributes.toString()));
                            processUserAttributes(userId, rtmAttributes);
                        }

                        @Override
                        public void onFailure(ErrorInfo errorInfo) {
                            Log.e(TAG, String.format("getUserAttributes %s", errorInfo.getErrorDescription()));
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    private void getUserAttributes(String userId) {
        if (mRtmClient != null) {
            Message message =new Message();
            message.what =HANDLER_MEMBER_ATTR_MSG;
            message.obj =userId;
            handler.sendMessage(message);
        }
    }

    private void processUserAttributes(String userId, List<RtmAttribute> attributeList) {
        if (attributeList != null) {
            Map<String, String> attributes = new HashMap<>();
            for (RtmAttribute attribute : attributeList) {
                attributes.put(attribute.getKey(), attribute.getValue());
            }
            if (mListener !=null){
                mListener.OnMembersAttributes(userId,attributes);
            }
        }

    }

    void setLocalUserAttributes(String key, String value) {
        if (mRtmClient != null) {
            RtmAttribute attribute = new RtmAttribute(key, value);
            mRtmClient.setLocalUserAttributes(Collections.singletonList(attribute), null);
        }
    }

    //设置频道属性
    public void setChannelAttributes(String channelId,List<RtmChannelAttribute> rtmChannelAttributes) {
        if (mRtmClient != null) {
            mRtmClient.setChannelAttributes(channelId,rtmChannelAttributes,options(),null);
        }
    }

    public RtmChannel getmRtmChannel() {
        return mRtmChannel;
    }

    //添加或者更新频道属性
    public void addOrUpdateChannelAttributes(String key, String value, ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            if (mRtmChannel == null) {
                //AlertUtil.showToast("RTM not login, see the log to get more info");
                return;
            }
            Log.i(TAG, "addOrUpdateChannelAttributes: key ="+key+",val ="+value);
            RtmChannelAttribute attribute = new RtmChannelAttribute(key, value);
            mRtmClient.addOrUpdateChannelAttributes(mRtmChannel.getId(), Collections.singletonList(attribute), options(), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, String.format("addOrUpdateChannelAttributes %s %s", key, value));

                    if (callback != null)
                        callback.onSuccess(aVoid);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("addOrUpdateChannelAttributes %s %s %s", key, value, errorInfo.getErrorDescription()));

                    if (callback != null)
                        callback.onFailure(errorInfo);
                }
            });
        }
    }

    private ChannelAttributeOptions options() {
        return new ChannelAttributeOptions(true);
    }

    public void deleteChannelAttributesByKey(String key,ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            if (mRtmChannel == null) {
                //AlertUtil.showToast("RTM not login, see the log to get more info");
                return;
            }

            mRtmClient.deleteChannelAttributesByKeys(mRtmChannel.getId(), Collections.singletonList(key), options(), callback);
        }
    }

    void sendChannelMessage(String content, ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            RtmMessage message = mRtmClient.createMessage(content);
            if (mRtmChannel != null) {
                mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, String.format("sendChannelMessage %s", content));

                        if (callback != null)
                            callback.onSuccess(aVoid);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        Log.e(TAG, String.format("sendChannelMessage %s", errorInfo.getErrorDescription()));

                        if (callback != null)
                            callback.onFailure(errorInfo);
                    }
                });
            }
        }
    }

    public void sendMessageToPeer(String userId, String content, ResultCallback<Void> callback) {
        if (TextUtils.isEmpty(userId)) return;

        if (mRtmClient != null) {
            RtmMessage message = mRtmClient.createMessage(content);
            mRtmClient.sendMessageToPeer(userId, message, null, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, String.format("sendMessageToPeer %s %s", userId, content));

                    if (callback != null)
                        callback.onSuccess(aVoid);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("sendMessageToPeer %s", errorInfo.getErrorDescription()));

                    if (callback != null)
                        callback.onFailure(errorInfo);
                }
            });
        }
    }

    void leaveChannel() {
        if (mRtmChannel != null) {
            Log.w(TAG, String.format("leaveChannel %s", mRtmChannel.getId()));

            mRtmChannel.leave(null);
            mRtmChannel.release();
            mRtmChannel = null;
        }
    }

    private RtmClientListener mClientListener = new RtmClientListener() {
        @Override
        public void onConnectionStateChanged(int i, int i1) {
            if (mListener!=null){
                mListener.onRtmConnectStateChange(i,i1);
            }
        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, String s) {
            Log.i(TAG, String.format("onPeerMessageReceived %s %s", rtmMessage.getText(), s));
            if (mListener != null)
                mListener.onMessageReceived(rtmMessage,s);
        }

        @Override
        public void onTokenExpired() {
        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
        }
    };

    private RtmChannelListener mChannelListener = new RtmChannelListener() {

        @Override
        public void onMemberCountUpdated(int var1) {
            if (mListener != null)
                mListener.onMemberCount(var1);

        }

        @Override
        public void onAttributesUpdated(List<RtmChannelAttribute> list) {
            Log.i(TAG, "onChannelAttributesUpdated: --2-->");
            processChannelAttributes(list);
        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
            if (mListener != null)
                mListener.onChannelMessageReceived(rtmMessage,rtmChannelMember.getUserId());
        }

        @Override
        public void onMemberJoined(RtmChannelMember rtmChannelMember) {
            String userId = rtmChannelMember.getUserId();
            Log.i(TAG, String.format("onMemberJoined %s", userId));
            if (mListener!=null){
                mListener.onMemberJoined(rtmChannelMember.getUserId());
            }
            getUserAttributes(userId);
        }

        @Override
        public void onMemberLeft(RtmChannelMember rtmChannelMember) {
            String userId = rtmChannelMember.getUserId();
            if (mListener != null)
                mListener.onMemberLeft(userId);
        }
    };
}
