package org.ar.audioganme.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;
import org.ar.audioganme.activity.ChatActivity;
import org.ar.audioganme.activity.RoomInfoActivity;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.model.Constant;
import org.ar.audioganme.util.AlertUtil;

public class FunctionDialog extends Dialog implements View.OnClickListener {

    private TextView mRoomInfo,mFreeMic,mSettingPaw,mVolume,mRecord,mSoundEffect;
    private ImageView mClose;
    private ChatActivity activity;
    private ChatRoomManager chatRoomManager;
    private PwdDialog pwdDialog;
    private VolumeDialog volumeDialog;
    private RecordDialog recordDialog;
    private boolean isHasPwd;
    private TipDialog tipDialog;
    private EffectCallBack effectCallBack;
    public interface EffectCallBack{
        void onStateChange(boolean enabled);
    }

    public FunctionDialog(@NonNull ChatActivity activity, ChatRoomManager chatRoomManager,EffectCallBack effectCallBack) {
        super(activity,R.style.dialog);
        this.activity =activity;
        this.chatRoomManager =chatRoomManager;
        this.effectCallBack =effectCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_function);
        init();
        initView();
    }

    private void init(){
        mRoomInfo =findViewById(R.id.room_info);
        mFreeMic =findViewById(R.id.free_mic);
        mSettingPaw =findViewById(R.id.setting_paw);
        mVolume =findViewById(R.id.volume);
        mRecord =findViewById(R.id.record);
        mSoundEffect =findViewById(R.id.sound_effect);
        mClose =findViewById(R.id.function_close);

        isHasPwd =chatRoomManager.getChannelData().isLock();

        tipDialog =new TipDialog(activity, "是否取消密码", "否", "是", null, new TipDialog.ConfirmCallBack() {
            @Override
            public void onClick() {
                chatRoomManager.getRtmManager().deleteChannelAttributesByKey(AttributeKey.KEY_IS_LOCK,null);
            }
        });

        mRoomInfo.setOnClickListener(this);
        mFreeMic.setOnClickListener(this);
        mSettingPaw.setOnClickListener(this);
        mVolume.setOnClickListener(this);
        mRecord.setOnClickListener(this);
        mSoundEffect.setOnClickListener(this);
        mClose.setOnClickListener(this);
    }

    private void initView(){
        Log.i("TAG", "initView: isHasPwd ="+isHasPwd);
        if (isHasPwd){
            mSettingPaw.setText("取消密码");
        }else {
            mSettingPaw.setText("设置密码");
        }

        if ("1".equals(chatRoomManager.getChannelData().getIsMicLock())){
            mFreeMic.setSelected(false);
            mFreeMic.setText("非自由上麦");
        }else {
            mFreeMic.setSelected(true);
            mFreeMic.setText("自由上麦");
        }
        if (Constant.isEffectOpen){
            mSoundEffect.setSelected(true);
        }else {
            mSoundEffect.setSelected(false);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.function_close:
                dismiss();
                break;
            case R.id.room_info:
                Intent intent =new Intent(activity, RoomInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
                dismiss();
                break;
            case R.id.free_mic:
                mFreeMic.setSelected(!mFreeMic.isSelected());
                if (mFreeMic.isSelected()){
                    mFreeMic.setText("自由上麦");
                    chatRoomManager.getRtmManager().
                            addOrUpdateChannelAttributes(AttributeKey.KEY_IS_MIC_LOCK,"0",null);
                }else {
                    mFreeMic.setText("非自由上麦");
                    chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_IS_MIC_LOCK,"1",null);
                }
                dismiss();
                break;
            case R.id.setting_paw:
                if (isHasPwd){
                    tipDialog.show();
                }else {
                    pwdDialog =new PwdDialog(activity,chatRoomManager,true);
                    pwdDialog.show();
                }
                dismiss();
                break;
            case R.id.volume:
                if (volumeDialog ==null){
                    volumeDialog =new VolumeDialog(activity,chatRoomManager);
                }
                volumeDialog.show();
                dismiss();
                break;
            case R.id.record:
                recordDialog =new RecordDialog(activity,"开始录音","管理录音",true);
                recordDialog.show();
                dismiss();
                break;
            case R.id.sound_effect:
                Constant.isEffectOpen = !mSoundEffect.isSelected();
                mSoundEffect.setSelected(!mSoundEffect.isSelected());
                effectCallBack.onStateChange(mSoundEffect.isSelected());
                dismiss();
                break;
            default:
                break;
        }
    }

}
