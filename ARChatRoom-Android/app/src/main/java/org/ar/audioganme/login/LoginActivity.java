package org.ar.audioganme.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.ar.audioganme.R;
import org.ar.audioganme.activity.RoomActivity;
import org.ar.audioganme.audio.AudioActivity;
import org.ar.audioganme.model.Member;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.util.MemberUtil;
import org.ar.audioganme.util.SpUtil;
import org.ar.audioganme.util.StatusBarUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements ILoginView, View.OnClickListener {

    private LoginPresent loginPresent;
    private CircleImageView mHeadPortrait;
    private ImageView mSelectGirlTag,mSelectManTag;
    private TextView mSelectMan,mSelectGirl;
    private EditText mEtInput;
    private Button mLogin;
    private int gender;
    private Toast mToast;
    private String mAvatarAddr;
    private boolean isBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setDeepStatusBar(true,LoginActivity.this, Color.TRANSPARENT);
        setContentView(R.layout.activity_login);
        loginPresent = new LoginPresent(this);
        Log.i("TAG", "onCreate: isLogin ="+loginPresent.isLogin());
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loginPresent.isLogin() && !isBack){
            Intent intent =new Intent(this, RoomActivity.class);
            startActivityForResult(intent,0);
            return;
        }
        init();
    }

    private void initView() {
        mHeadPortrait =findViewById(R.id.head_portrait);
        mSelectMan =findViewById(R.id.select_man);
        mSelectManTag =findViewById(R.id.select_man_tag);
        mSelectGirl =findViewById(R.id.select_girl);
        mSelectGirlTag =findViewById(R.id.select_girl_tag);
        mEtInput =findViewById(R.id.et_input_name);
        mLogin=findViewById(R.id.login_confirm);
        mSelectMan.setOnClickListener(this);
        mSelectGirl.setOnClickListener(this);
        mLogin.setOnClickListener(this);

    }

    private void init() {
        mAvatarAddr =MemberUtil.getAvatar(true);
        AlertUtil.setAvatar(this,mAvatarAddr,mHeadPortrait);
        mSelectMan.setSelected(true);
        mSelectGirl.setSelected(false);
        mSelectMan.setTextColor(getResources().getColor(R.color.white));
        mSelectGirl.setTextColor(getResources().getColor(R.color.select_girl_color));
        mSelectManTag.setVisibility(View.VISIBLE);
        mSelectGirlTag.setVisibility(View.GONE);
        gender =0;
    }

    @Override
    public void loginSuccess() {
        Intent intent =new Intent(this, RoomActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    public void loginFailed(String reason) {
        showToast(reason);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select_man:
                mSelectManTag.setVisibility(View.VISIBLE);
                mSelectGirlTag.setVisibility(View.GONE);
                mSelectMan.setSelected(true);
                mSelectGirl.setSelected(false);
                mSelectMan.setTextColor(getResources().getColor(R.color.white));
                mSelectGirl.setTextColor(getResources().getColor(R.color.select_girl_color));
                gender =0;
                mAvatarAddr =MemberUtil.getAvatar(true);
                AlertUtil.setAvatar(this,mAvatarAddr,mHeadPortrait);
                break;
            case R.id.select_girl:
                mSelectGirlTag.setVisibility(View.VISIBLE);
                mSelectManTag.setVisibility(View.GONE);
                mSelectMan.setSelected(false);
                mSelectGirl.setSelected(true);
                mSelectGirl.setTextColor(getResources().getColor(R.color.white));
                mSelectMan.setTextColor(getResources().getColor(R.color.select_man_color));
                gender =1;
                mAvatarAddr =MemberUtil.getAvatar(false);
                AlertUtil.setAvatar(this,mAvatarAddr,mHeadPortrait);
                break;
            case R.id.login_confirm:
                loginPresent.Login(mEtInput.getText().toString(),gender,mAvatarAddr);
                mEtInput.setText("");
                break;
            default:
                break;
        }
    }

    private void showToast(String msg){
        if (mToast != null) {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        } else {
            mToast = Toast.makeText(this,msg,Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 2) {
            isBack = data.getBooleanExtra("isBack",false);
        }
    }
}