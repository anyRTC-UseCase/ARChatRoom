package org.ar.audioganme.model;

import android.text.TextUtils;

import org.ar.audioganme.util.MemberUtil;


public class Constant {

    public static final String sUserId = MemberUtil.getUserId();
    public static final String sName = MemberUtil.getName();
    public static final String sAvatarAddr = MemberUtil.getAvatarAdrr();
    public static final int sGender =MemberUtil.getGender();
    public static boolean isEffectOpen;

    public static boolean isMyself(String userId) {
        return TextUtils.equals(userId, String.valueOf(Constant.sUserId));
    }
}
