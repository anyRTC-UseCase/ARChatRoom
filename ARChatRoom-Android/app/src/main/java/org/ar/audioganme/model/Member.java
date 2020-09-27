package org.ar.audioganme.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.Serializable;

public class Member implements Serializable {

    private String userId;
    private String name;
    private String avatarAddr;
    private int gender;

    public Member(String userId) {
        this.userId = userId;
    }

    public Member(String userId, String name,int gender, String avatarAddr) {
        this(userId);
        this.name = name;
        this.avatarAddr = avatarAddr;
        this.gender =gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarAddr() {
        return avatarAddr;
    }

    public void setAvatarAddr(String avatarIndex) {
        this.avatarAddr = avatarIndex;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void update(Member member) {
        this.name = member.name;
        this.avatarAddr = member.avatarAddr;
        this.gender =member.gender;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Member)
            return TextUtils.equals(userId, ((Member) obj).userId);
        return super.equals(obj);
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }

    public static Member fromJsonString(String str) {
        return new Gson().fromJson(str, Member.class);
    }

    @Override
    public String toString() {
        return "Member{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", avatarAddr='" + avatarAddr + '\'' +
                ", gender=" + gender +
                '}';
    }

}
