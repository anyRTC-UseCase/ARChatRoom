package org.ar.audioganme.model;

import android.text.TextUtils;

import org.ar.audioganme.util.MemberUtil;


public class Constant {
    public static boolean isEffectOpen;
    public static boolean isMyself(String userId) {
        return TextUtils.equals(userId, String.valueOf(MemberUtil.getUserId()));
    }
}
