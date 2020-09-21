package org.ar.audioganme.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.ar.audioganme.R;
import org.ar.audioganme.dialog.TipDialog;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.util.StatusBarUtil;

public class SettingInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG =SettingInfoActivity.class.getSimpleName();
    public static final int ROOM_TYPE_NAME =0;
    public static final int ROOM_TYPE_ANNOUNCEMENT =1;
    public static final int ROOM_TYPE_WELCOME =2;
    private int NAME_CHAR_MIN=4;
    private int NAME_CHAR_MAX=32;
    private int ANNOUNCEMENT_CHAR_MAX=192;
    private int WELCOME_CHAR_MAX=16;
    private boolean isNAmeRight =true;
    private boolean isAnnouncementRight =true;
    private boolean isWelcomeRight =true;
    private int mRoomTye;
    private RelativeLayout rl_name,rl_announcement,rl_welcome;
    private TextView mInfoTitle,mWelcomeCount,mAnnouncementCount;
    private ImageView mInfoBack;
    private EditText mEdName,mEdAnnouncement,mEdWelcome;
    private TipDialog tipDialog;
    private ChatRoomManager chatRoomManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setDeepStatusBar(true, SettingInfoActivity.this, Color.TRANSPARENT);
        StatusBarUtil.setStatusBarColor(SettingInfoActivity.this, R.color.white);
        setContentView(R.layout.activity_setting_info);
        init();
        initView();
        setListener();
    }

    private void init() {
        mRoomTye =getIntent().getIntExtra("RoomType",ROOM_TYPE_NAME);
        mInfoBack =findViewById(R.id.room_setting_info_back);
        mInfoTitle =findViewById(R.id.info_title);
        mWelcomeCount =findViewById(R.id.welcome_count);
        mAnnouncementCount=findViewById(R.id.announcement_count);
        mEdName =findViewById(R.id.edit_name);
        mEdAnnouncement =findViewById(R.id.edit_announcement);
        mEdWelcome =findViewById(R.id.edit_welcome);
        rl_name =findViewById(R.id.rl_setting_name);
        rl_announcement =findViewById(R.id.rl_setting_announcement);
        rl_welcome =findViewById(R.id.rl_setting_welcome);

        chatRoomManager = ChatRoomManager.instance(this);

        mInfoBack.setOnClickListener(this);
    }

    private void initView(){
        switch (mRoomTye){
            case ROOM_TYPE_NAME:
                mInfoTitle.setText("房间名称");
                mEdName.setHint(chatRoomManager.getChannelData().getRoomName());
                rl_name.setVisibility(View.VISIBLE);
                rl_welcome.setVisibility(View.GONE);
                rl_announcement.setVisibility(View.GONE);
                break;
            case ROOM_TYPE_ANNOUNCEMENT:
                mInfoTitle.setText("公告");
                mAnnouncementCount.setText("192");
                mEdAnnouncement.setHint(chatRoomManager.getChannelData().getAnnouncement());
                rl_name.setVisibility(View.GONE);
                rl_welcome.setVisibility(View.GONE);
                rl_announcement.setVisibility(View.VISIBLE);
                break;
            case ROOM_TYPE_WELCOME:
                mInfoTitle.setText("欢迎语");
                mWelcomeCount.setText("16");
                mEdWelcome.setHint(chatRoomManager.getChannelData().getWelcomeTip());
                rl_name.setVisibility(View.GONE);
                rl_welcome.setVisibility(View.VISIBLE);
                rl_announcement.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void setListener() {
        mEdName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged: name count ="+editable.length());
                if (editable.length()<NAME_CHAR_MIN || editable.length()>NAME_CHAR_MAX){
                    isNAmeRight =false;
                }else {
                    isNAmeRight =true;
                }
            }
        });
        mEdAnnouncement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged: announcement count ="+editable.length());
                if (editable.length()>ANNOUNCEMENT_CHAR_MAX){
                    isAnnouncementRight =false;
                }else {
                    isAnnouncementRight =true;
                }
                int count =ANNOUNCEMENT_CHAR_MAX -editable.length();
                if (count<0){
                    count =0;
                }
                mAnnouncementCount.setText(String.valueOf(count));
            }
        });
        mEdWelcome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged: welcome count ="+editable.length());
                if (editable.length()>WELCOME_CHAR_MAX){
                    isWelcomeRight =false;
                }else {
                    isWelcomeRight =true;
                }
                int count =WELCOME_CHAR_MAX -editable.length();
                if (count<0){
                    count =0;
                }
                mWelcomeCount.setText(String.valueOf(count));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.room_setting_info_back:
                setChannelAttr();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==KeyEvent.KEYCODE_BACK){
            setChannelAttr();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setChannelAttr(){
        if (mRoomTye == ROOM_TYPE_NAME){
            if (TextUtils.isEmpty(mEdName.getText().toString())){
                finish();
                return;
            }
            if (!isNAmeRight){
                AlertUtil.showTipDialog(this,"内容保持在4~32之内");
                return;
            }
            showSaveAttrDialog(AttributeKey.KEY_ROOM_NAME,mEdName.getText().toString());

        }else if (mRoomTye ==ROOM_TYPE_ANNOUNCEMENT){
            if (TextUtils.isEmpty(mEdAnnouncement.getText().toString())){
                finish();
                return;
            }
            if (!isAnnouncementRight){
                AlertUtil.showTipDialog(this,"内容不得超过192个字符");
                return;
            }
            showSaveAttrDialog(AttributeKey.KEY_NOTICE,mEdAnnouncement.getText().toString());

        }else {
            if (TextUtils.isEmpty(mEdWelcome.getText().toString())){
                finish();
                return;
            }
            if (!isWelcomeRight){
                AlertUtil.showTipDialog(this,"内容不得超过16个字符");
                return;
            }
            showSaveAttrDialog(AttributeKey.KEY_WELCOME_TIP,mEdWelcome.getText().toString());
        }
    }

    private void showSaveAttrDialog(String key,String val){
        tipDialog =new TipDialog(this, "是否保存", "不保存", "保存", () -> finish(), () -> {
            if (mRoomTye ==ROOM_TYPE_NAME){
                Intent intent =getIntent().putExtra("AnnVal",val);
                setResult(2,intent);
            }
            chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(key,val,null);
            finish();
        });
        tipDialog.show();
    }
}
