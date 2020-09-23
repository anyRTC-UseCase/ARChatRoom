package org.ar.audioganme.model;

import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

public class AttributeKey {
    public static final String KEY_ANCHOR_ID = "anchorId";
    public static final String[] KEY_SEAT_ARRAY = initSeatKeys();
    public static final String KEY_USER_INFO = "userInfo";
    public static final String KEY_SEAT_ANCHOR = "seat0";
    public static final String KEY_HOST = "host";
    public static final String KEY_ROOM_NAME = "roomName";
    public static final String KEY_NOTICE = "notice";
    public static final String KEY_WELCOME_TIP = "welecomeTip";
    public static final String KEY_ANCHOR_AVATAR = "AnchorAvatar";
    public static final String KEY_ANCHOR_NAME = "AnchorName";
    public static final String KEY_ANCHOR_SEX = "AnchorSex";
    public static final String KEY_IS_LOCK = "isLock";
    public static final String KEY_IS_MIC_LOCK = "isMicLock";
    public static final String KEY_MUTE_MIC_LIST = "MuteMicList";
    public static final String KEY_MUTE_INPUT_LIST = "MuteInputList";
    public static final String KEY_MUSIC = "music";
    public static final String KEY_WAITING_LIST = "waitinglist";

    public static final String KEY_NAME = "name";
    public static final String KEY_UID = "uid";
    public static final String KEY_HEAD = "head";
    public static final String KEY_SEX = "sex";

    private static String[] initSeatKeys() {
        String[] strings = new String[ChannelData.MAX_SEAT_NUM];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = String.format(Locale.getDefault(), "seat%d", (i+1));
        }
        return strings;
    }

    public static int indexOfSeatKey(String key) {
        for (int i = 0; i < KEY_SEAT_ARRAY.length; i++) {
            if (TextUtils.equals(key, KEY_SEAT_ARRAY[i])) return i;
        }
        return -1;
    }
}
