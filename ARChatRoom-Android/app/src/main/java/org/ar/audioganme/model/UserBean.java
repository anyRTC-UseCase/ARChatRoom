package org.ar.audioganme.model;

import java.io.Serializable;

public class UserBean implements Serializable {
    public String name;
    public String userId;
    public int gender;//0:男.1:女

    public UserBean(String name, String userId, int gender) {
        this.name = name;
        this.userId = userId;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public int getGender() {
        return gender;
    }
}
