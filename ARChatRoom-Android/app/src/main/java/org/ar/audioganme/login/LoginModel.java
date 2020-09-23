package org.ar.audioganme.login;

import org.ar.audioganme.model.Member;
import org.ar.audioganme.model.UserBean;
import org.ar.audioganme.util.MemberUtil;
import org.ar.audioganme.util.SpUtil;

public class LoginModel implements ILoginModel{

    private ILoginPresenter loginPresenter;
    private String USER = "user";
    private String userId = (int)((Math.random()*9+1)*100000)+"";

    public LoginModel(ILoginPresenter loginPresenter) {
        this.loginPresenter = loginPresenter;
    }

    @Override
    public void Login(String name, int gender,String avatarAddr) {
        Member member = new Member(userId,name,gender,avatarAddr);
        SpUtil.saveObject(USER,member);
        loginPresenter.loginSuccess();
    }



    @Override
    public boolean isLogin() {
        if (SpUtil.readObject("user")==null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void loginFailed(String reason) {
        loginPresenter.loginFailed(reason);
    }


}
