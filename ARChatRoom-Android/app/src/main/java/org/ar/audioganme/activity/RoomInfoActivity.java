package org.ar.audioganme.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.ar.audioganme.R;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.util.StatusBarUtil;

public class RoomInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout rl_Room_name,rl_announcement,rl_welcome;
    private ImageView mBack;
    private TextView mNameVal;
    private ChannelData channelData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setDeepStatusBar(true, RoomInfoActivity.this, Color.TRANSPARENT);
        StatusBarUtil.setStatusBarColor(RoomInfoActivity.this, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        init();
    }

    private void init(){
        channelData =ChatRoomManager.instance(this).getChannelData();
        rl_Room_name =findViewById(R.id.rl_room_name_val);
        rl_announcement =findViewById(R.id.rl_announcement);
        rl_welcome =findViewById(R.id.rl_welcome_speech);
        mNameVal =findViewById(R.id.room_name_val);
        mBack =findViewById(R.id.room_info_back);

        rl_Room_name.setOnClickListener(this);
        rl_announcement.setOnClickListener(this);
        rl_welcome.setOnClickListener(this);
        mBack.setOnClickListener(this);
        Log.i("TAG", "init: getRoomName ="+channelData.getRoomName());
        mNameVal.setText(channelData.getRoomName());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==KeyEvent.KEYCODE_BACK){
            Intent i =new Intent(RoomInfoActivity.this, ChatActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
            overridePendingTransition(0,0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        Intent intent =new Intent();
        switch (view.getId()){
            case R.id.rl_room_name_val:
                intent.setClass(this,SettingInfoActivity.class);
                intent.putExtra("RoomType",SettingInfoActivity.ROOM_TYPE_NAME);
                startActivityForResult(intent,0);
                break;
            case R.id.rl_announcement:
                intent.setClass(this,SettingInfoActivity.class);
                intent.putExtra("RoomType",SettingInfoActivity.ROOM_TYPE_ANNOUNCEMENT);
                startActivity(intent);
                break;
            case R.id.rl_welcome_speech:
                intent.setClass(this,SettingInfoActivity.class);
                intent.putExtra("RoomType",SettingInfoActivity.ROOM_TYPE_WELCOME);
                startActivity(intent);
                break;
            case R.id.room_info_back:
                Intent intent1 =new Intent(RoomInfoActivity.this, ChatActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent1);
                finish();
                overridePendingTransition(0,0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "onActivityResult: requestCode ="+requestCode+",resultCode ="+resultCode);
        if (requestCode == 0 && resultCode == 2) {
            String annVal = data.getStringExtra("AnnVal");
            mNameVal.setText(annVal);
        }
    }
}
