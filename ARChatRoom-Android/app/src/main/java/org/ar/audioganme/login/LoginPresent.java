package org.ar.audioganme.login;

import android.text.TextUtils;

public class LoginPresent implements ILoginPresenter {


    private ILoginModel loginModel;
    private ILoginView loginVIew;

    public LoginPresent(ILoginView loginVIew) {
        this.loginVIew = loginVIew;
        loginModel = new LoginModel(this);
    }



    @Override
    public void Login(String name, int gender,String avatarAddr) {
        if (TextUtils.isEmpty(name)){
            loginModel.loginFailed("请输入昵称");
            return;
        }
        loginModel.Login(name,gender,avatarAddr);
    }

    @Override
    public boolean isLogin() {
        return loginModel.isLogin();
    }


    @Override
    public void loginSuccess() {
        loginVIew.loginSuccess();
    }

    @Override
    public void loginFailed(String reason) {
        loginVIew.loginFailed(reason);
    }



}
