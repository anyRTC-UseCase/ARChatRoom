package org.ar.audioganme.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.ar.audioganme.R;
import org.ar.audioganme.dialog.PwdDialog;
import org.ar.audioganme.manager.ChatRoomEventListener;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.model.Constant;
import org.ar.audioganme.model.Member;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.util.MemberUtil;
import org.ar.audioganme.util.SpUtil;
import org.ar.audioganme.util.StatusBarUtil;
import org.ar.rtm.ChannelAttributeOptions;
import org.ar.rtm.ErrorInfo;
import org.ar.rtm.ResultCallback;
import org.ar.rtm.RtmAttribute;
import org.ar.rtm.RtmChannelAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener , ChatRoomManager.QueryChannelAttributes {

    private static final String TAG =RoomActivity.class.getSimpleName();
    private final int PERMISSION_REQ_ID = 22;
    private ImageView mBack;
    private TextView myRoom;
    private EditText mEtRoomId;
    private Button mGoConfirm;
    private String mUserId;
    private String mChannelId;
    private ChatRoomManager manager;
    private PwdDialog pwdDialog;
    private boolean isRoom =false;
    private PwdDialog.PwdCallBack callBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setDeepStatusBar(true, RoomActivity.this, Color.TRANSPARENT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initView();
        init();
        initPwdCallBack();
    }

    private void initView() {
        mBack =findViewById(R.id.room_back);
        myRoom =findViewById(R.id.my_room);
        mEtRoomId=findViewById(R.id.et_room_id);
        mGoConfirm =findViewById(R.id.go_room_confirm);
        mBack.setOnClickListener(this);
        myRoom.setOnClickListener(this);
        mGoConfirm.setOnClickListener(this);
    }

    private void init() {
        Member member = MemberUtil.getMember();
        mUserId =member.getUserId();
        manager =ChatRoomManager.instance(this);
        manager.setQueryListener(this);
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_ID);
        }
    }

    private void initPwdCallBack(){
        callBack =new PwdDialog.PwdCallBack() {
            @Override
            public void onSucceed() {
                intentChat(false);
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.room_back:
                finish();
                break;
            case R.id.my_room:
                mChannelId =mUserId;
                manager.login(mUserId,mChannelId);
                isRoom =true;
                break;
            case R.id.go_room_confirm:
                if (TextUtils.isEmpty(mEtRoomId.getText().toString())){
                    AlertUtil.showToast("请输入房间ID");
                    return;
                }
                mChannelId =mEtRoomId.getText().toString();
                manager.login(mUserId,mChannelId);
                isRoom =false;
                break;
            default:
                break;
        }
    }

    private void intentChat(boolean isAnchor){
        Intent i =new Intent(RoomActivity.this, ChatActivity.class);
        i.putExtra(ChatActivity.KEY_CHANNEL_ID,mChannelId);
        i.putExtra(ChatActivity.KEY_ANCHOR_IS,isAnchor);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    @Override
    public void onHasAttribute(String userId,boolean isHas) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isHas){
                    if (userId.equals(mUserId)){
                        intentChat(true);
                    }else {
                        if (manager.getChannelData().isLock()){
                            Toast.makeText(RoomActivity.this, "密码房,请输入密码", Toast.LENGTH_SHORT).show();
                            pwdDialog =new PwdDialog(RoomActivity.this,manager,false,callBack);
                            pwdDialog.show();
                        }else {
                            intentChat(false);
                        }
                    }
                }else {
                    if (isRoom || mUserId.equals(mChannelId)){
                        //设置主播：host，头像索引，名字，性别，和房间名称
                        Member member = MemberUtil.getMember();
                        List<RtmChannelAttribute> rtmChannelAttributes =new ArrayList<>();
                        rtmChannelAttributes.add(new RtmChannelAttribute(AttributeKey.KEY_HOST,member.getUserId()));
                        rtmChannelAttributes.add(new RtmChannelAttribute(AttributeKey.KEY_ROOM_NAME,"一起聊天吧"));
                        manager.getRtmManager().setChannelAttributes(mUserId,rtmChannelAttributes);
                        intentChat(true);
                    }else {
                        AlertUtil.showToast("无"+mChannelId+"房间");
                    }
                }

            }
        });
    }
}
