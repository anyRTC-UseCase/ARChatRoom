package org.ar.audioganme.login;

public interface ILoginPresenter {
    void Login(String name,int gender,String avatarAddr);
    boolean isLogin();
    void loginSuccess();
    void loginFailed(String reason);
}
