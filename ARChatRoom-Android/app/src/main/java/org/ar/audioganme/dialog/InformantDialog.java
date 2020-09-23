package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;
import org.ar.audioganme.activity.ChatActivity;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.model.Constant;
import org.ar.audioganme.model.Member;
import org.ar.audioganme.model.Seat;
import org.ar.audioganme.util.AlertUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InformantDialog extends Dialog implements View.OnClickListener {

    private final static String TAG =InformantDialog.class.getSimpleName();
    private CircleImageView mAvatar;
    private TextView mName;
    private ImageView imgGender;
    private Button mDownMic,mProhibitMic,mProhibitChat,mGoOut,mCareOf,mGift;
    private LinearLayout ll_infor;

    private Context context;
    private ChatRoomManager chatRoomManager;
    private boolean isMic,isAnchor;
    private String seatId;
    private String userId;
    private ChannelData channelData;
    private Member member;
    private boolean isMuteMic,isMuteChat;
    private String mUserMutedMic,mUserMutedInput;
    private List<String> mMuteMicList;
    private List<String> mMuteInputList;
    private GiftCallBack giftCallBack;
    public interface GiftCallBack{
        void setGiveGift(String userId);
    }

    public InformantDialog(@NonNull Context context, ChatRoomManager chatRoomManager, String seatId,boolean isMic,boolean isAnchor,GiftCallBack giftCallBack) {
        super(context);
        this.context =context;
        this.chatRoomManager =chatRoomManager;
        this.isMic =isMic;
        this.seatId =seatId;
        this.giftCallBack =giftCallBack;
        this.isAnchor =isAnchor;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_informant);
        init();
        initView();
    }

    private void init() {
        mAvatar =findViewById(R.id.informant_avatar);
        mName =findViewById(R.id.informant_name);
        imgGender =findViewById(R.id.informant_gender);
        ll_infor =findViewById(R.id.ll_information);
        mDownMic =findViewById(R.id.information_down_mic);
        mProhibitMic =findViewById(R.id.information_prohibit_mic);
        mProhibitChat =findViewById(R.id.information_prohibit_chat);
        mGoOut =findViewById(R.id.information_go_out);
        mCareOf =findViewById(R.id.information_care_of);
        mGift =findViewById(R.id.information_give_gift);

        mDownMic.setOnClickListener(this);
        mProhibitMic.setOnClickListener(this);
        mProhibitChat.setOnClickListener(this);
        mGoOut.setOnClickListener(this);
        mCareOf.setOnClickListener(this);
        mGift.setOnClickListener(this);

        channelData =chatRoomManager.getChannelData();
        if (seatId ==null){
            dismiss();
        }
        userId =seatId;
        Log.i(TAG, "init: userID ="+userId);
        member = channelData.getMember(userId);
        mMuteMicList =new ArrayList<>();
        mMuteInputList =new ArrayList<>();
        mUserMutedMic =channelData.getUserMuted();
        mUserMutedInput =channelData.getUserMutedInput();
        Log.i(TAG, "init: setChannelAttr  ----> UserMuted ="+mUserMutedMic+",Input ="+mUserMutedInput);
        try {
            if (mUserMutedMic !=null){
                JSONArray jsonArray =new JSONArray(mUserMutedMic);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    mMuteMicList.add(String.valueOf(jsonArray.get(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (mUserMutedInput !=null){
                JSONArray jsonArray =new JSONArray(mUserMutedInput);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    mMuteInputList.add(String.valueOf(jsonArray.get(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        if (isAnchor){
            if(Constant.isMyself(userId)){
                RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                ll_infor.setLayoutParams(params);
                ll_infor.setBackgroundResource(R.drawable.show_avatar_samll);
                mDownMic.setVisibility(View.GONE);
                mProhibitMic.setVisibility(View.GONE);
                mProhibitChat.setVisibility(View.GONE);
                mGoOut.setVisibility(View.GONE);
                mCareOf.setVisibility(View.GONE);
                mGift.setVisibility(View.GONE);
            }else {
                if (isMic){
                    RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    ll_infor.setLayoutParams(params);
                    ll_infor.setBackgroundResource(R.drawable.show_avatar);
                    mDownMic.setVisibility(View.VISIBLE);
                    mProhibitMic.setVisibility(View.VISIBLE);
                    mProhibitChat.setVisibility(View.VISIBLE);
                    mGoOut.setVisibility(View.VISIBLE);
                    mCareOf.setVisibility(View.VISIBLE);
                    mGift.setVisibility(View.VISIBLE);
                }else {
                    RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            AlertUtil.dip2px(context,300));
                    ll_infor.setLayoutParams(params);
                    mDownMic.setVisibility(View.GONE);
                    mProhibitMic.setVisibility(View.GONE);
                    mProhibitChat.setVisibility(View.VISIBLE);
                    mGoOut.setVisibility(View.VISIBLE);
                    mCareOf.setVisibility(View.GONE);
                    mGift.setVisibility(View.GONE);
                }
            }
        }else {
            if (Constant.isMyself(userId)){
                RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                ll_infor.setLayoutParams(params);
                ll_infor.setBackgroundResource(R.drawable.show_avatar_samll);
                ll_infor.setLayoutParams(params);
                mDownMic.setVisibility(View.GONE);
                mProhibitMic.setVisibility(View.GONE);
                mProhibitChat.setVisibility(View.GONE);
                mGoOut.setVisibility(View.GONE);
                mCareOf.setVisibility(View.GONE);
                mGift.setVisibility(View.GONE);
            }else {
                RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                ll_infor.setLayoutParams(params);
                ll_infor.setBackgroundResource(R.drawable.show_avatar_samll);
                mDownMic.setVisibility(View.GONE);
                mProhibitMic.setVisibility(View.GONE);
                mProhibitChat.setVisibility(View.GONE);
                mGoOut.setVisibility(View.GONE);
                mCareOf.setVisibility(View.GONE);
                mGift.setVisibility(View.VISIBLE);
            }
        }

        if (channelData.isUserOnline(userId)){
            AlertUtil.showAvatar(channelData.getMemberAvatar(userId),mAvatar);
            mName.setText(member.getName());
            if (member.getGender() ==0){
                imgGender.setImageResource(R.drawable.man);
            }else {
                imgGender.setImageResource(R.drawable.girl);
            }
        }else {
            mAvatar.setImageResource(R.drawable.ic_unkown);
        }

        if (mMuteMicList !=null){
            if (mMuteMicList.contains(userId)){
                mProhibitMic.setText("取消禁麦");
                isMuteMic =false;
            }else {
                mProhibitMic.setText("禁麦");
                isMuteMic =true;
            }
        }
        if (mMuteInputList !=null){
            if (mMuteInputList.contains(userId)){
                mProhibitChat.setText("取消禁言");
                isMuteChat =false;
            }else {
                mProhibitChat.setText("禁言");
                isMuteChat =true;
            }
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
            case R.id.information_down_mic:  //下麦
                chatRoomManager.toAudience(userId, null);
                dismiss();
                break;
            case R.id.information_prohibit_mic: //禁麦
                if (isMuteMic){
                    mProhibitMic.setText("取消禁麦");
                    isMuteMic =false;
                }else {
                    mProhibitMic.setText("禁麦");
                    isMuteMic =true;
                }
                setMuteMicChannelAttr();
                break;
            case R.id.information_prohibit_chat: //禁言
                if (isMuteChat){
                    mProhibitChat.setText("取消禁言");
                    isMuteChat =false;
                }else {
                    mProhibitChat.setText("禁言");
                    isMuteChat =true;
                }
                setMuteChatChannelAttr();
                break;
            case R.id.information_go_out: //请出
                AlertUtil.showToast("敬请期待");
                dismiss();
                break;
            case R.id.information_care_of: //转交
                chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_HOST, userId,null);
                Toast.makeText(context, "主持人已转交"+userId, Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.information_give_gift: //送礼
                giftCallBack.setGiveGift(userId);
                dismiss();
                break;
            default:
                break;
        }
    }

    private void setMuteMicChannelAttr() {
        Log.i(TAG, "setChannelAttr: list ="+mMuteMicList);
        if (mMuteMicList !=null){
            if (mMuteMicList.contains(userId)){
                mMuteMicList.remove(userId);
            }else {
                mMuteMicList.add(userId);
            }
        }
        JSONArray jsonArray =new JSONArray();
        for (int i = 0; i <mMuteMicList.size() ; i++) {
            jsonArray.put(mMuteMicList.get(i));
        }
        Log.i(TAG, "setChannelAttr:  --> "+ jsonArray.toString());
        chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_MUTE_MIC_LIST,jsonArray.toString(),null);
    }

    private void setMuteChatChannelAttr() {
        Log.i(TAG, "setChannelAttr: list ="+mMuteInputList);
        if (mMuteInputList !=null){
            if (mMuteInputList.contains(userId)){
                mMuteInputList.remove(userId);
            }else {
                mMuteInputList.add(userId);
            }
        }
        JSONArray jsonArray =new JSONArray();
        for (int i = 0; i <mMuteInputList.size() ; i++) {
            jsonArray.put(mMuteInputList.get(i));
        }
        Log.i(TAG, "setChannelAttr:  --> "+ jsonArray.toString());
        chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_MUTE_INPUT_LIST,jsonArray.toString(),null);
    }
}
