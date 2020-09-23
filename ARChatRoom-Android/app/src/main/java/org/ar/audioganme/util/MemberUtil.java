package org.ar.audioganme.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.ArrayRes;

import org.ar.audioganme.App;
import org.ar.audioganme.R;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.model.Member;

import java.util.Random;


public class MemberUtil {

    public static Member getMember(){
        if (SpUtil.readObject("user") !=null){
            Member member = (Member)SpUtil.readObject("user");
            return member;
        }else {
            throw new SecurityException("Member为空....");
        }
    }

    public static String getUserId() {
        String userId = getMember().getUserId();
        return userId;
    }

    public static String getName() {
        return getMember().getName();
    }

    public static int getGender(){
        return getMember().getGender();
    }

    private static String randomName(@ArrayRes int resId) {
        String[] names = App.instance.getResources().getStringArray(resId);
        return names[new Random().nextInt(names.length - 1)];
    }

    public static String getAvatar(boolean isMan) {
        String avatarAdrr =null;
        String[] images =null;
        if (isMan){
            images =App.instance.getResources().getStringArray(R.array.avatar_man);
        }else {
           images =App.instance.getResources().getStringArray(R.array.avatar_woman);
        }
        int index = new Random().nextInt(images.length);
        avatarAdrr =images[index];
        SpUtil.putString("avatarAdrr",avatarAdrr);
        return avatarAdrr;
    }

    public static String getAvatarAdrr() {
        String avatarAdrr = getMember().getAvatarAddr();
        if (TextUtils.isEmpty(avatarAdrr)){
            return null;
        }
        return avatarAdrr;
    }

    public static int getAvatarIndex(String avatarAddr){
        String[] avatarRes =App.instance.getResources().getStringArray(R.array.avatar);
        for (int i = 0; i <avatarRes.length; i++) {
            if (avatarRes[i].equals(avatarAddr)){
                return i;
            }
        }
        return 0;
    }

    public static int getAvatarResId(String avatarAddr) {
        TypedArray images = App.instance.getResources().obtainTypedArray(R.array.random_avatar_images);
        int resId = images.getResourceId(getAvatarIndex(avatarAddr), R.drawable.ic_unkown);
        images.recycle();
        return resId;
    }

}
