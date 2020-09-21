package org.ar.audioganme.model;

public class WaitMicBean {

    public String waitAvatar;
    public String waitName;
    public int waitApplyPos;
    private String userId;

    public WaitMicBean(String userId,String waitAvatar, String waitName, int waitApplyPos) {
        this.waitAvatar = waitAvatar;
        this.waitName = waitName;
        this.waitApplyPos = waitApplyPos;
        this.userId=userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWaitAvatar() {
        return waitAvatar;
    }

    public void setWaitAvatar(String waitAvatar) {
        this.waitAvatar = waitAvatar;
    }

    public String getWaitName() {
        return waitName;
    }

    public void setWaitName(String waitName) {
        this.waitName = waitName;
    }

    public int getWaitApplyPos() {
        return waitApplyPos;
    }

    public void setWaitApplyPos(int waitApplyPos) {
        this.waitApplyPos = waitApplyPos;
    }
}
