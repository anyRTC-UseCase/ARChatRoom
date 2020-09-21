package org.ar.audioganme.login;

public interface ILoginModel {
    void Login(String name,int gender,String avatarAddr);
    boolean isLogin();
    void loginFailed(String reason);

}
