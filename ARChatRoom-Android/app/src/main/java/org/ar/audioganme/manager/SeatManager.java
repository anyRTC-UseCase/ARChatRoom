package org.ar.audioganme.manager;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.model.Constant;
import org.ar.audioganme.model.Message;
import org.ar.audioganme.model.Seat;
import org.ar.rtc.Constants;
import org.ar.rtm.ErrorInfo;
import org.ar.rtm.ResultCallback;


public abstract class SeatManager {

    private final String TAG = SeatManager.class.getSimpleName();

    abstract ChannelData getChannelData();

    abstract MessageManager getMessageManager();

    abstract RtcManager getRtcManager();

    abstract RtmManager getRtmManager();

    public final void toBroadcaster(String userId, int position) {
        Log.d(TAG, String.format("toBroadcaster %s %d", userId, position));
        ChannelData channelData = getChannelData();
        if (Constant.isMyself(userId)) {
            int index = channelData.indexOfSeatArray(userId);
            Log.i(TAG, "toBroadcaster: index ="+index);
            if (index >= 0) {
                if (position == index) {
                    getRtcManager().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                } else {
                    Log.i(TAG, "toBroadcaster:zhao  --1-->");
                    changeSeat(userId, index, position, new ResultCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getRtcManager().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                        }

                        @Override
                        public void onFailure(ErrorInfo errorInfo) {

                        }
                    });
                }
            } else {
                Log.i(TAG, "toBroadcaster:zhao  --12-->");
                occupySeat(userId, position, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getRtcManager().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {

                    }
                });
            }
        } else {
            getMessageManager().sendOrder(userId, Message.ORDER_TYPE_BROADCASTER, String.valueOf(position), null);
        }
    }

    public final void toAudience(String userId, ResultCallback<Void> callback) {
        Log.d(TAG, String.format("toAudience %s", userId));

        ChannelData channelData = getChannelData();
        if (Constant.isMyself(userId)) {
            resetSeat(channelData.indexOfSeatArray(userId), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    getRtcManager().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

                    if (callback != null)
                        callback.onSuccess(aVoid);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    if (callback != null) {
                        callback.onFailure(errorInfo);
                    }
                }
            });
        } else {
            getMessageManager().sendOrder(userId, Message.ORDER_TYPE_AUDIENCE, null, callback);
        }
    }

    private void occupySeat(String userId, int position, ResultCallback<Void> callback) {
        modifySeat(position, userId, callback);
    }

    private void resetSeat(int position, ResultCallback<Void> callback) {
        modifySeat(position, null, callback);
    }

    private void changeSeat(String userId, int oldPosition, int newPosition, ResultCallback<Void> callback) {
        resetSeat(oldPosition, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (getChannelData().updateSeat(oldPosition, null)) {
                    // don't wait onChannelAttributesUpdated, refresh now
                    onSeatUpdated(oldPosition);
                }
                occupySeat(userId, newPosition, callback);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
            }
        });
    }

    public void muteMic(String userId, boolean muted) {
        if (Constant.isMyself(userId)) {
            if (!getChannelData().isUserOnline(userId)) return;
            getRtcManager().muteLocalAudioStream(muted);
        } else {
            if (!getChannelData().isAnchorMyself()) return;
            getMessageManager().sendOrder(userId, Message.ORDER_TYPE_MUTE, String.valueOf(muted), null);
        }
    }

    private void modifySeat(int position, String userId, ResultCallback<Void> callback) {
        Log.i(TAG, "zhao modifySeat: position ="+position +",userId ="+userId);
        if (position >= 0 && position < AttributeKey.KEY_SEAT_ARRAY.length){
            if (TextUtils.isEmpty(userId)){
                getRtmManager().deleteChannelAttributesByKey(AttributeKey.KEY_SEAT_ARRAY[position],callback);
            }else {
                getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_SEAT_ARRAY[position], userId, callback);
            }
        }
    }

    abstract void onSeatUpdated(int position);

    final boolean updateSeatArray(int position, String value) {
        return getChannelData().updateSeat(position, value);
    }

}
