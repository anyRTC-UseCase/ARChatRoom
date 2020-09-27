package org.ar.audioganme.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.CustomDialog;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.lzf.easyfloat.interfaces.OnPermissionResult;
import com.lzf.easyfloat.permission.PermissionUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import org.ar.audioganme.R;
import org.ar.audioganme.adapter.MessageAdapter;
import org.ar.audioganme.adapter.OnlineMemberAdapter;
import org.ar.audioganme.adapter.SeatGridAdapter;
import org.ar.audioganme.dialog.AnnouncementDialog;
import org.ar.audioganme.dialog.AutoTipDialog;
import org.ar.audioganme.dialog.FunctionDialog;
import org.ar.audioganme.dialog.GiftDialog;
import org.ar.audioganme.dialog.InformantDialog;
import org.ar.audioganme.dialog.MemberDialog;
import org.ar.audioganme.dialog.ProgressDialog;
import org.ar.audioganme.dialog.TipDialog;
import org.ar.audioganme.dialog.WaitMicDialog;
import org.ar.audioganme.manager.ChatRoomEventListener;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.model.Constant;
import org.ar.audioganme.model.GiftBean;
import org.ar.audioganme.model.Member;
import org.ar.audioganme.model.MessageListBean;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.util.MemberUtil;
import org.ar.audioganme.util.SpUtil;
import org.ar.audioganme.util.StatusBarUtil;
import org.ar.audioganme.weight.CommentDialogFragment;
import org.ar.audioganme.weight.SpreadView;
import org.ar.rtc.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.ar.audioganme.manager.ChatRoomManager.giftArray;
import static org.ar.audioganme.manager.ChatRoomManager.giftNameArray;


public class ChatActivity extends AppCompatActivity implements ChatRoomEventListener,
        View.OnClickListener,SeatGridAdapter.OnItemClickListener {

    private final String TAG = ChatActivity.class.getSimpleName();
    public static final String KEY_CHANNEL_ID = "channelId";
    public static final String KEY_ANCHOR_IS = "isAnchor";

    public static final int NET_WORK_RTT_SUPER = 30;
    public static final int NET_WORK_RTT_WELL = 40;
    public static final int NET_WORK_RTT_GOOD = 60;
    public static final int NET_WORK_RTT_BAD = 100;

    public static final int RECORD_ANIM_START = 0;
    public static final int RECORD_ANIM_STOP = 1;

    private RelativeLayout rlAnchor, rlTourist;

    private TextView mChatId, mChatSourceVal, mChatName,tvMessageInput,tvMessageHostInput;
    private ImageView mChatSourceImg, mChatBack, mChatExit,mFunction,mLock,mAnchorMic,mRecordAnim;
    private TextView toTouristMic,mMusicName;
    private ImageView mAnchorAvatar,mAnchorGender,tourist_present,anchor_present,touristMicImg;
    private TextView mAnchorName,mChatAnnouncement,mChatOnline,anchorJoinMicCount;
    private Button mPlayMusic;
    private HorizontalScrollView mHsEffect;
    private Button mEffect_hhh,mEffect_qihong,mEffect_guzhang,mEffect_ganga,mEffect_wuya,mEffect_mymom;
    private SpreadView anchorAnim;
    private AppCompatImageView anchorMute;

    private RecyclerView rvSeatGrid,rvMessageList;

    private String[] musicPath ={
            "wumingzhibei.m4a",
            "zuimeideguang.mp3",
            "awkward.wav",
            "chipmunk.wav",
            "guzhang.wav",
            "qihong.wav",
            "wodema.wav",
            "wuya.wav"
    };

    private SeatGridAdapter mSeatAdapter;
    private MessageAdapter messageAdapter;

    private ChatRoomManager mManager;
    private ChannelData mChannelData;
    private String mChannelId;
    private FunctionDialog mFunctionDialog;
    private AnnouncementDialog mAnnouncementDialog;
    private InformantDialog mInformantDialog;
    private WaitMicDialog mWaitMicDialog;
    private TipDialog mTipDialog;
    private AutoTipDialog autoTipDialog;
    private FunctionDialog.EffectCallBack effectCallBack;
    private InformantDialog.GiftCallBack giftCallBack;
    private boolean isAnchor;
    //isTouristJoinMic是否是上下麦, isWaitJoinMic是否是等待上麦
    private boolean isTouristJoinMic,isWaitJoinMic;

    private boolean isFirstWelcome;
    private boolean isProMuted; //是否为禁麦
    public Animation mMusicAnimation;

    private LinearLayoutManager msgLayoutManager;

    //gift
    private RelativeLayout rlGift;
    private ImageView ivGift;
    private TextView tvGiftTip;
    private HashMap<String,Integer> mWaitingMap;
    private List<Integer> mMicPosList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarColor(ChatActivity.this, R.color.chat_center_color);
        setContentView(R.layout.activity_chat);
        init();
        initMessageList();
        initSeatRecyclerView();
        initManager();
        initView();
        initMusicVolume();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        rlAnchor =findViewById(R.id.rl_anchor_function);
        rlTourist =findViewById(R.id.rl_tourist_function);
        tvMessageInput = findViewById(R.id.tv_input);
        tvMessageHostInput = findViewById(R.id.anchor_input);
        tourist_present = findViewById(R.id.tourist_present);
        anchor_present =findViewById(R.id.anchor_present);
        rvMessageList = findViewById(R.id.rv_message_list);
        msgLayoutManager = new LinearLayoutManager(this);
        rvMessageList.setLayoutManager(msgLayoutManager);
        rvSeatGrid =findViewById(R.id.rv_seat_grid);
        toTouristMic =findViewById(R.id.tourist_join_mic);
        mAnchorAvatar =findViewById(R.id.anchor_head_portrait);
        mAnchorGender =findViewById(R.id.anchor_gender);
        mAnchorName =findViewById(R.id.anchor_name);
        mChatId =findViewById(R.id.chat_id);
        mMusicName =findViewById(R.id.chat_music_name);
        mChatSourceImg =findViewById(R.id.chat_source_img);
        mChatSourceVal =findViewById(R.id.chat_source_val);
        anchorAnim =findViewById(R.id.anchor_anim);
        mChatBack =findViewById(R.id.chat_back);
        mChatExit =findViewById(R.id.chat_exit);
        mChatName =findViewById(R.id.chat_name);
        touristMicImg =findViewById(R.id.tourist_mic_img);
        mAnchorMic =findViewById(R.id.anchor_join_mic);
        mPlayMusic =findViewById(R.id.play_music);
        mLock = findViewById(R.id.pwd_room_lock);
        anchorMute=findViewById(R.id.anchor_mute);
        mChatAnnouncement =findViewById(R.id.chat_notice);
        mChatOnline =findViewById(R.id.chat_online);
        mFunction =findViewById(R.id.anchor_fun_more);
        rlGift = findViewById(R.id.rl_gift);
        tvGiftTip = findViewById(R.id.tv_gift_tip);
        ivGift = findViewById(R.id.iv_gift);
        anchorJoinMicCount =findViewById(R.id.anchor_join_mic_count);
        mHsEffect =findViewById(R.id.effect_hs);
        mEffect_hhh =findViewById(R.id.effect_hhh);
        mEffect_qihong =findViewById(R.id.effect_qihong);
        mEffect_guzhang =findViewById(R.id.effect_guzhang);
        mEffect_wuya =findViewById(R.id.effect_wuya);
        mEffect_ganga =findViewById(R.id.effect_ganga);
        mEffect_mymom =findViewById(R.id.effect_mymom);
        mRecordAnim =findViewById(R.id.record_anim);

        mChannelId =getIntent().getStringExtra(KEY_CHANNEL_ID);
        isAnchor =getIntent().getBooleanExtra(KEY_ANCHOR_IS,false);
        mMusicAnimation = AnimationUtils.loadAnimation(this,R.anim.animation_rotate);
        mWaitingMap =new HashMap<>();
        mMicPosList =new ArrayList<>();
        progressDialog =new ProgressDialog(this);
        progressDialog.show();

        mChatBack.setOnClickListener(this);
        mChatExit.setOnClickListener(this);
        toTouristMic.setOnClickListener(this);
        mFunction.setOnClickListener(this);
        mChatAnnouncement.setOnClickListener(this);
        mPlayMusic.setOnClickListener(this);
        tvMessageInput.setOnClickListener(this);
        tvMessageHostInput.setOnClickListener(this);
        mChatOnline.setOnClickListener(this);
        mAnchorMic.setOnClickListener(this);
        tourist_present.setOnClickListener(this);
        anchor_present.setOnClickListener(this);
        mEffect_hhh.setOnClickListener(this);
        mEffect_qihong.setOnClickListener(this);
        mEffect_guzhang.setOnClickListener(this);
        mEffect_wuya.setOnClickListener(this);
        mEffect_ganga.setOnClickListener(this);
        mEffect_mymom.setOnClickListener(this);
        mAnchorAvatar.setOnClickListener(this);
        anchorJoinMicCount.setOnClickListener(this);
        touristMicImg.setOnClickListener(this);
        setCallBack();
    }

    private void setCallBack(){
        effectCallBack = enabled -> {
            if (enabled){
                mHsEffect.setVisibility(View.VISIBLE);
            }else {
                mHsEffect.setVisibility(View.GONE);
            }
        };
        giftCallBack = userId -> {
            GiftDialog giftDialog = new GiftDialog();
            giftDialog.showDialog(userId, getSupportFragmentManager(), (userId1, giftId) -> showGift("我", userId1,giftId));
        };
    }

    private void initMusicVolume(){
        mManager.getRtcManager().adjustAudioMixingVolume(40);
        mManager.getRtcManager().adjustRecordingSignalVolume(60);
        mManager.getRtcManager().enableInEarMonitoring(false);
        mManager.getRtcManager().setInEarMonitoringVolume(0);
    }

    private void initMessageList(){
        messageAdapter = new MessageAdapter();
        rvMessageList.setAdapter(messageAdapter);
        messageAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String userId ;
                switch (view.getId()){
                    case R.id.tv_fromId:
                        Log.i(TAG, "onItemChildClick: tv_fromId name ="+messageAdapter.getData().get(position).getId());
                        userId = messageAdapter.getData().get(position).getId();
                        if ("我".equals(userId)){
                            mInformantDialog =new InformantDialog(ChatActivity.this,mManager,MemberUtil.getUserId(),false,isAnchor,giftCallBack);
                        }else if ("主播".equals(userId)){
                            mInformantDialog =new InformantDialog(ChatActivity.this,mManager,mChannelData.getAnchorId(),false,isAnchor,giftCallBack);
                        } else {
                            mInformantDialog =new InformantDialog(ChatActivity.this,mManager,userId,false,isAnchor,giftCallBack);
                        }
                        mInformantDialog.show();
                        break;
                    case R.id.tv_toId:
                        Log.i(TAG, "onItemChildClick:tv_toId  name ="+messageAdapter.getData().get(position).getToId());
                        userId =messageAdapter.getData().get(position).getToId();
                        if ("主播".equals(userId)){
                            mInformantDialog =new InformantDialog(ChatActivity.this,mManager,mChannelData.getAnchorId(),false,isAnchor,giftCallBack);
                        }else if ("麦上全体人员".equals(userId)){
                            return;
                        }else {
                            mInformantDialog =new InformantDialog(ChatActivity.this,mManager,userId,false,isAnchor,giftCallBack);
                        }
                        mInformantDialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initSeatRecyclerView() {
        rvSeatGrid.setHasFixedSize(true);

        RecyclerView.ItemAnimator animator = rvSeatGrid.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);

        mSeatAdapter = new SeatGridAdapter(this);
        mSeatAdapter.setOnItemClickListener(this);
        rvSeatGrid.setAdapter(mSeatAdapter);
        rvSeatGrid.setLayoutManager(new GridLayoutManager(this, 4));

        int spacing = getResources().getDimensionPixelSize(R.dimen.dp_25);
        rvSeatGrid.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, spacing, 0, 0);

            }
        });
    }

    private void initManager() {
        mManager =ChatRoomManager.instance(this);
        mManager.setListener(this);
        mChannelData =mManager.getChannelData();
        mManager.joinChannel(mChannelId);
    }

    private void initView() {
        mChatId.setText("ID:"+mChannelId);
        mChatName.setText(mChannelData.getRoomName());
        mMusicName.setVisibility(View.GONE);
        if (mChannelData.isLock()){
            mLock.setVisibility(View.VISIBLE);
        }else {
            mLock.setVisibility(View.GONE);
        }
        try {
            AlertUtil.writeSD(this,musicPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //closeMusic("无名之辈","close");
    }

    //设置布局,主播和游客.
    private void setLayout(Member member) {
        if (isAnchor){ //主播
            rlAnchor.setVisibility(View.VISIBLE);
            rlTourist.setVisibility(View.GONE);
            mManager.getRtcManager().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            mManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_SEAT_ANCHOR,MemberUtil.getUserId(),null);
            setAnchor();
        }else {  //游客
            rlAnchor.setVisibility(View.GONE);
            rlTourist.setVisibility(View.VISIBLE);
            setGuest(member);
        }
    }

    private void setAnchor(){
        if ("1".equals(mChannelData.getIsMicLock())){
            anchorJoinMicCount.setVisibility(View.VISIBLE);
        }else {
            anchorJoinMicCount.setVisibility(View.GONE);
        }
        mAnchorAvatar.setImageResource(MemberUtil.getAvatarResId(MemberUtil.getAvatarAddr()));
        mAnchorName.setText(MemberUtil.getName());
        if (MemberUtil.getGender() ==0){ //男生
            mAnchorGender.setImageResource(R.drawable.man);
        }else { //女生
            mAnchorGender.setImageResource(R.drawable.girl);
        }
    }

    private void setGuest(Member member){
        int sex;
        String name;
        if (member ==null){
            Log.i(TAG, "setGuest: getAnchorId ="+mChannelData.getAnchorId());
            Member memberGuest =mChannelData.getMember(mChannelData.getAnchorId());
            if (memberGuest ==null){
                return;
            }
            AlertUtil.showAvatar(memberGuest.getAvatarAddr(),mAnchorAvatar);
            sex = memberGuest.getGender();
            name = memberGuest.getName();
        }else {
            Log.i(TAG, "onCareOfAnchor: Avatar ="+member.getUserId()+",sex="+ member.getGender()+",name ="+member.getName());
            AlertUtil.showAvatar(member.getAvatarAddr(),mAnchorAvatar);
            sex = member.getGender();
            name = member.getName();
        }
        mHsEffect.setVisibility(View.GONE);
        mAnchorName.setText(name);
        if (sex ==0){
            mAnchorGender.setImageResource(R.drawable.man);
        }else {
            mAnchorGender.setImageResource(R.drawable.girl);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.chat_back:
                showFloat();
                break;
            case R.id.chat_exit:
                finish();
                break;
            case R.id.tourist_join_mic:
                setApplyJoinMic();
                break;
            case R.id.anchor_join_mic_count:
                if (isAnchor){
                    mWaitMicDialog =new WaitMicDialog(this, mManager, mWaitingMap,isAnchor,(userId, pos, isAgree) -> {
                        if (pos !=-1){
                            mWaitingMap.remove(userId);
                            if (!isAgree && mMicPosList.contains(pos)){
                                mMicPosList.remove(pos);
                            }
                        }else {
                            //为快速上麦
                            mWaitingMap.clear();
                            mMicPosList.clear();
                        }
                        anchorJoinMicCount.setText(String.valueOf(mWaitingMap.size()));
                    });
                    mWaitMicDialog.show();
                }
                break;
            case R.id.anchor_join_mic:
                mAnchorMic.setSelected(!mAnchorMic.isSelected());
                if (mAnchorMic.isSelected()){
                    anchorMute.setVisibility(View.GONE);
                    mManager.getRtcManager().getRtcEngine().enableLocalAudio(true);
                }else {
                    anchorMute.setVisibility(View.VISIBLE);
                    mManager.getRtcManager().getRtcEngine().enableLocalAudio(false);
                }
                break;
            case R.id.tourist_mic_img:
                if (isProMuted){
                    AlertUtil.showToast("禁麦中，无法操作");
                }else {
                    touristMicImg.setSelected(!touristMicImg.isSelected());
                    mManager.getRtcManager().getRtcEngine().muteLocalAudioStream(!touristMicImg.isSelected());
                }
                break;
            case R.id.anchor_fun_more:
                mFunctionDialog =new FunctionDialog(this,mManager,effectCallBack);
                mFunctionDialog.show();
                break;
            case R.id.chat_notice: {
                mAnnouncementDialog = new AnnouncementDialog(this, mChannelData);
                mAnnouncementDialog.show();
            }
                break;
            case R.id.play_music:
                if (isAnchor){
                    Intent intent =new Intent(this,PlayMusicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }else {
                    autoTipDialog =new AutoTipDialog(this,R.drawable.red_tip,"只有主播才能播放音乐");
                    autoTipDialog.show();
                }
                break;
            case R.id.anchor_head_portrait:
                mInformantDialog =new InformantDialog(this,mManager,mChannelData.getAnchorId(),true,isAnchor,giftCallBack);
                mInformantDialog.show();
                break;
            case R.id.tv_input:
            case R.id.anchor_input: {
                CommentDialogFragment dialogFragment = new CommentDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), text -> {
                    mManager.sendChannelMessage(text);
                    addMessage(new MessageListBean(MessageListBean.MSG_NORMAL, "我","："+text));
                });
            }
                break;
            case R.id.chat_online:
                showMemberList();
                break;
            case R.id.anchor_present:
            case R.id.tourist_present:
                GiftDialog giftDialog = new GiftDialog();
                giftDialog.showDialog("", getSupportFragmentManager(), (userId, giftId) -> showGift("我",userId,giftId));
                break;
            case R.id.effect_hhh:
                playEffect(11,"chipmunk.wav");
                break;
            case R.id.effect_guzhang:
                playEffect(12,"guzhang.wav");
                break;
            case R.id.effect_ganga:
                playEffect(13,"awkward.wav");
                break;
            case R.id.effect_wuya:
                playEffect(14,"wuya.wav");
                break;
            case R.id.effect_qihong:
                playEffect(15,"qihong.wav");
                break;
            case R.id.effect_mymom:
                playEffect(16,"wodema.wav");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            showFloat();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setMuteAudio(){
        String mutedMicVal = mChannelData.getUserMuted();
        List<String> mutedMicList =new ArrayList<>();
        try {
            if (mutedMicVal !=null){
                JSONArray jsonArray =new JSONArray(mutedMicVal);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    mutedMicList.add(String.valueOf(jsonArray.get(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        InformantDialog.setMuteMicChannelAttr(mutedMicList,MemberUtil.getUserId(),mManager);
    }

    public void showFloat() {
        int version = android.os.Build.VERSION.SDK_INT;
        if (version < 21) {
            AlertUtil.showToast("暂不支持该设备");
            return;
        }
        if (!PermissionUtils.checkPermission(ChatActivity.this)) {
            PermissionUtils.requestPermission(ChatActivity.this, new OnPermissionResult() {
                @Override
                public void permissionResult(boolean b) {
                    if (!b) {
                        AlertUtil.showToast("请打开悬浮窗权限");
                    }
                }
            });
            return;
        }
        moveTaskToBack(true);
        EasyFloat.with(this)
                .setShowPattern(ShowPattern.FOREGROUND)
                .setDragEnable(true)
                .setGravity(Gravity.RIGHT | Gravity.BOTTOM)
                .setSidePattern(SidePattern.RESULT_SIDE)
                .setLayout(R.layout.float_chat_window, view -> {
                    CircleImageView avatar =view.findViewById(R.id.float_window_avatar);
                    TextView roomName =view.findViewById(R.id.float_window_room_name);
                    ImageView exit =view.findViewById(R.id.float_window_exit);
                    if (isAnchor){
                        avatar.setImageResource(MemberUtil.getAvatarResId(MemberUtil.getAvatarAddr()));
                    }else {
                        String anchorId =mChannelData.getAnchorId();
                        Member member =mChannelData.getMember(anchorId);
                        avatar.setImageResource(MemberUtil.getAvatarResId(member.getAvatarAddr()));
                    }
                    roomName.setText(mChannelData.getRoomName());
                    exit.setOnClickListener(view1 -> {
                        EasyFloat.dismissAppFloat();
                        finish();
                    });
                    avatar.setOnClickListener(view12 -> {
                        EasyFloat.dismissAppFloat();
                        Log.i(TAG, "onClick: SDK_INT ="+(Build.VERSION.SDK_INT >= 29));
                        if (Build.VERSION.SDK_INT >= 29) {
                            Intent intent =new Intent(ChatActivity.this, ChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        } else {
                            moveToFront();
                        }
                    });
                }).registerCallbacks(new OnFloatCallbacks() {
            @Override
            public void createdResult(boolean b, String s, View view) { }

            @Override public void show(View view) { }

            @Override public void hide(View view) { }

            @Override public void dismiss() {
            }

            @Override public void touchEvent(View view, MotionEvent motionEvent) { }

            @Override public void drag(View view, MotionEvent motionEvent) { }

            @Override public void dragEnd(View view) { }
        }).show();
    }

    @TargetApi(11)
    protected void moveToFront() {
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            for (int i = 0; i < recentTasks.size(); i++) {
                // bring to front
                if (recentTasks.get(i).baseActivity.toShortString().indexOf("org.ar.audioganme.activity.ChatActivity") > -1) {
                    manager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                }
            }
        }
    }

    private void playEffect(int id,String path){
        path =Environment.getExternalStorageDirectory().getPath()+"/"+path;
        Log.i(TAG, "playEffect: path ="+path +",id ="+id);
        mManager.getRtcManager().stopAllEffects();
        mManager.getRtcManager().playEffect(id,path);
    }

    private void setApplyJoinMic(){
        if (!isWaitJoinMic){
            if (isTouristJoinMic){
                mManager.toAudience(String.valueOf(MemberUtil.getUserId()),null);
                toTouristMic.setText("上麦");
                isTouristJoinMic =false;
            }else {
                int pos=-1 ;
                if ("0".equals(mChannelData.getIsMicLock())){
                    for (int i = 0; i <mChannelData.getSeatArray().length ; i++) {
                        if (mChannelData.getSeatArray()[i] ==null){
                            pos = i;
                            break;
                        }
                    }
                    if (pos !=-1){
                        mManager.toBroadcaster(String.valueOf(MemberUtil.getUserId()), pos);
                        toTouristMic.setText("下麦");
                        isTouristJoinMic =true;
                    }else {
                        AlertUtil.showToast("座位已满,无法上麦");
                    }
                }else {
                    String val =mChannelData.getWaitVal();
                    Log.i(TAG, "setApplyJoinMic: val ="+val);
                    JSONArray jsonArray1 = null;
                    if (val !=null){
                        try {
                            jsonArray1 = new JSONArray(val);
                            for (int i = 0; i <jsonArray1.length() ; i++) {
                                JSONObject jsonObject =jsonArray1.getJSONObject(i);
                                int seatIndex =Integer.parseInt(jsonObject.getString("seat"));
                                Log.i(TAG, "setApplyJoinMic: mMicPosList ="+mMicPosList);
                                if (!mMicPosList.contains((seatIndex -1))){
                                    mMicPosList.add((seatIndex-1));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i <mChannelData.getSeatArray().length ; i++) {
                        if (mChannelData.getSeatArray()[i] ==null){
                            Log.i(TAG, "setApplyJoinMic: mMicPosList ="+mMicPosList+",pos="+pos);
                            if (!mMicPosList.contains(i)){
                                pos = i;
                                break;
                            }
                        }
                    }
                    if (pos !=-1){
                        try {
                            JSONObject jsonObject =new JSONObject();
                            JSONArray jsonArray;
                            if (val ==null){
                                jsonArray =new JSONArray();
                            }else {
                                jsonArray =new JSONArray(val);
                            }
                            jsonObject.put("userid",MemberUtil.getUserId());
                            jsonObject.put("seat",String.valueOf(pos+1));
                            jsonArray.put(jsonObject);
                            Log.i(TAG, "setApplyJoinMic: --->jsonArray ="+jsonArray.toString());
                            mTipDialog =new TipDialog(this, "是否申请上麦", "取消", "确定",null, new TipDialog.ConfirmCallBack() {
                                @Override
                                public void onClick() {
                                    mManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_WAITING_LIST,jsonArray.toString(),null);
                                    toTouristMic.setText(String.valueOf(jsonArray.length()));
                                    isTouristJoinMic =true;
                                    isWaitJoinMic =true;
                                }
                        });
                         mTipDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        AlertUtil.showToast("座位已满,无法申请上麦");
                    }
                }
            }
        }else {
            mWaitMicDialog =new WaitMicDialog(this, mManager, mWaitingMap, isAnchor, (userId, pos, isAgree) -> {
                isWaitJoinMic =false;
                isTouristJoinMic =false;
                toTouristMic.setText("上麦");
                autoTipDialog =new AutoTipDialog(ChatActivity.this,R.drawable.success_tip,"取消成功");
                autoTipDialog.show();
            });
            mWaitMicDialog.show();
        }
    }

    @Override
    public void onItemClick(View view, int position, String seatId) {
        Log.i(TAG, "onItemClick: seatId ="+seatId+",MicLock ="+mChannelData.getIsMicLock());
        if (!TextUtils.isEmpty(seatId)){
            if (isAnchor){
                //显示对方信息
                mInformantDialog =new InformantDialog(this,mManager,seatId,true,isAnchor,giftCallBack);
                mInformantDialog.show();
            }else {
                //显示送礼
                mInformantDialog =new InformantDialog(this,mManager,seatId,true,isAnchor,giftCallBack);
                mInformantDialog.show();
            }
        }else {
            if (isAnchor){
                if ("0".equals(mChannelData.getIsMicLock())){
                    autoTipDialog =new AutoTipDialog(this,R.drawable.red_tip,"主持人不可以更改麦位");
                    autoTipDialog.show();
                }
            }else {
                if ("0".equals(mChannelData.getIsMicLock())){
                    mManager.toBroadcaster(String.valueOf(MemberUtil.getUserId()), position);
                    toTouristMic.setText("下麦");
                    isTouristJoinMic =true;
                }else {
                    //弹出对话申请上麦对话框
                    if (!isWaitJoinMic){
                        String val =mChannelData.getWaitVal();
                        try {
                            JSONObject jsonObject =new JSONObject();
                            JSONArray jsonArray;
                            if (val ==null){
                                jsonArray =new JSONArray();
                            }else {
                                jsonArray =new JSONArray(val);
                            }
                            jsonObject.put("userid",MemberUtil.getUserId());
                            jsonObject.put("seat",String.valueOf(position+1));
                            jsonArray.put(jsonObject);
                            mTipDialog =new TipDialog(this, "是否申请上麦", "取消", "确定", null,new TipDialog.ConfirmCallBack() {
                                @Override
                                public void onClick() {
                                    mManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_WAITING_LIST,jsonArray.toString(),null);
                                    toTouristMic.setText(String.valueOf(jsonArray.length()));
                                    isWaitJoinMic =true;
                                }
                            });
                            mTipDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        mWaitMicDialog =new WaitMicDialog(this, mManager, mWaitingMap, isAnchor, (userId, pos, isAgree) -> {
                            isWaitJoinMic =false;
                            isTouristJoinMic =false;
                            toTouristMic.setText("上麦");
                            autoTipDialog =new AutoTipDialog(ChatActivity.this,R.drawable.success_tip,"取消成功");
                            autoTipDialog.show();
                        });
                        mWaitMicDialog.show();
                    }
                }
            }
        }
    }

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RECORD_ANIM_START:
                    mRecordAnim.setVisibility(View.VISIBLE);
                    mRecordAnim.setSelected(!mRecordAnim.isSelected());
                    handler.sendEmptyMessageDelayed(RECORD_ANIM_START,1000);
                    break;
                case RECORD_ANIM_STOP:
                    mRecordAnim.setVisibility(View.GONE);
                    handler.removeMessages(RECORD_ANIM_START);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRecordUpdated(String state) {
        runOnUiThread(()->{
            if ("1".equals(state)){
                //开启录音动画
                handler.sendEmptyMessage(RECORD_ANIM_START);
            }else {
                //关闭录音动画
                handler.sendEmptyMessage(RECORD_ANIM_STOP);
            }
        });
    }

    @Override
    public void onRejectLineUpdated(String val,String userId) {
        runOnUiThread(()->{
            Log.i(TAG, "onRejectLineUpdated: sUserId="+MemberUtil.getUserId());
            if (!isAnchor){
                isWaitJoinMic =false;
                isTouristJoinMic =false;
                toTouristMic.setText("上麦");
                autoTipDialog =new AutoTipDialog(this,R.drawable.red_tip,val);
                autoTipDialog.show();
            }
        });
    }

    @Override
    public void onAcceptLineUpdated(String userId,String seatPos) {
        runOnUiThread(()->{
            Log.i(TAG, "onAcceptLineUpdated: userId="+MemberUtil.getUserId()+",seatPos ="+seatPos);
            String seatId =mChannelData.getSeatArray()[Integer.parseInt(seatPos)-1];
            if (seatId==null){
                if (!isAnchor){
                    mManager.toBroadcaster(MemberUtil.getUserId(),Integer.parseInt(seatPos)-1);
                    isWaitJoinMic =false;
                    isTouristJoinMic =true;
                    toTouristMic.setText("下麦");
                    autoTipDialog =new AutoTipDialog(this,R.drawable.success_tip,"同意上麦");
                    autoTipDialog.show();
                }
            }else{
                int pos =-1;
                for (int i = 0; i <mChannelData.getSeatArray().length ; i++) {
                    if (mChannelData.getSeatArray()[i] ==null){
                        pos = i;
                        break;
                    }
                }
                Log.i(TAG, "onAcceptLineUpdated: pos ="+pos);
            }

        });
    }

    @Override
    public void onWaitUpdated(String val) {
        runOnUiThread(()->{
            Log.i(TAG, "onWaitUpdated: jsonArray val="+val);
            mWaitingMap.clear();
            if (!TextUtils.isEmpty(val)){
                try {
                    JSONArray jsonArray =new JSONArray(val);
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject jsonObject =jsonArray.getJSONObject(i);
                        String userId =jsonObject.getString("userid");
                        int seatIndex =Integer.parseInt(jsonObject.getString("seat"));
                        if (!mWaitingMap.containsKey(userId)){
                            mWaitingMap.put(userId,seatIndex);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "onWaitUpdated: map size ="+mWaitingMap.size());
            anchorJoinMicCount.setText(String.valueOf(mWaitingMap.size()));
            if ("1".equals(mChannelData.getIsMicLock()) && isWaitJoinMic){
                toTouristMic.setText(String.valueOf(mWaitingMap.size()));
            }
        });
    }

    @Override
    public void onCareOfAnchor(Member member) {
        runOnUiThread(()->{
            Log.i(TAG, "onCareOfAnchor: member ="+member.getUserId());
            if (MemberUtil.getUserId().equals(member.getUserId())){
                isAnchor =true;
                mManager.toAudience(member.getUserId(),null);
            }else {
                isAnchor =false;
            }
            setLayout(member);
        });
    }

    @Override
    public void onMusicUpdated(String val) {
        runOnUiThread(()->{
            try {
                JSONObject jsonObject =new JSONObject(val);
                String name =jsonObject.getString("name");
                String state =jsonObject.getString("state");
                Log.i(TAG, "onMusicUpdated: name ="+name+",state ="+state);
                if ("无名之辈".equals(name)){
                    if ("close".equals(state)){
                        mMusicName.setVisibility(View.GONE);
                        mMusicName.setText("");
                        mPlayMusic.clearAnimation();
                    }else if ("open".equals(state)){
                        mMusicName.setVisibility(View.VISIBLE);
                        mMusicName.setText("无名之辈");
                        mPlayMusic.startAnimation(mMusicAnimation);
                    }else {
                        mMusicName.setVisibility(View.VISIBLE);
                        mMusicName.setText("无名之辈");
                        mPlayMusic.clearAnimation();
                    }
                } else if ("最美的光".equals(name)) {
                    if ("close".equals(state)){
                        mMusicName.setVisibility(View.GONE);
                        mMusicName.setText("");
                        mPlayMusic.clearAnimation();
                    }else if ("open".equals(state)){
                        mMusicName.setVisibility(View.VISIBLE);
                        mMusicName.setText("最美的光");
                        mPlayMusic.startAnimation(mMusicAnimation);
                    }else {
                        mMusicName.setVisibility(View.VISIBLE);
                        mMusicName.setText("最美的光");
                        mPlayMusic.clearAnimation();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onMutedInputUpdated(String val) {
        runOnUiThread(()->{
            if (!isAnchor){
                if (val !=null & val.contains(MemberUtil.getUserId())){
                    tvMessageInput.setClickable(false);
                    tvMessageInput.setHint("禁言中...");
                }else {
                    tvMessageInput.setClickable(true);
                    tvMessageInput.setHint("聊点什么吧");
                }
            }
        });
    }

    @Override
    public void onMemberCountUpdate(int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatOnline.setText("在线："+count);
            }
        });
    }

    @Override
    public void onPwdLockUpdated(String val) {
        runOnUiThread(()->{
            if (TextUtils.isEmpty(val)){
                mLock.setVisibility(View.GONE);
            }else {
                mLock.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onMicLockUpdated(String val) {
        runOnUiThread(()->{
            if (isAnchor){
                if ("0".equals(val)){
                    anchorJoinMicCount.setVisibility(View.GONE);
                }else {
                    anchorJoinMicCount.setVisibility(View.VISIBLE);
                }
            }
            mSeatAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onRoomNameUpdated(String val) {
        runOnUiThread(()->{
            Log.i(TAG, "onRoomNameUpdated:zhao val="+val);
            mChatName.setText(val);
        });
    }

    @Override
    public void onWelcomeTipUpdate(String val) {
        runOnUiThread(()->{
            Log.i(TAG, "onWelcomeTipUpdate: zhao val="+val);
            if (!isFirstWelcome){
                if (!TextUtils.isEmpty(val)){
                    onMessageAdd(new MessageListBean(MessageListBean.MSG_ATTR_UP,"",val));
                }
            }
            isFirstWelcome =true;
        });
    }

    @Override
    public void onAnnouncementUpdate(String val) {
        runOnUiThread(()->{
            Log.i(TAG, "onAnnouncementUpdate:zhao val="+val);
        });
    }

    @Override
    public void onMutedMicUpdated(String val) {
        runOnUiThread(()->{
            Log.i(TAG, "onMutedMicUpdated: ====> zhao ");
            if (!isAnchor){
                if (val !=null & val.contains(MemberUtil.getUserId())){
                    mManager.getRtcManager().muteLocalAudioStream(true);
                    isProMuted=true;
                    touristMicImg.setSelected(false);
                }else {
                    mManager.getRtcManager().muteLocalAudioStream(false);
                    isProMuted=false;
                    touristMicImg.setSelected(true);
                }
            }
        });
    }

    @Override
    public void onSeatUpdated(String userId,int position) {
        runOnUiThread(()->{
            Log.i(TAG, "onSeatUpdated: pos ="+position);
            String seatId =mChannelData.getSeatArray()[position];
            String muteVal =mChannelData.getUserMuted();
            if (Constant.isMyself(userId)){
                if (TextUtils.isEmpty(seatId)){
                    touristMicImg.setVisibility(View.GONE);
                    isTouristJoinMic =false;
                    toTouristMic.setText("上麦");
                    addMessage(new MessageListBean(MessageListBean.MSG_MEMBER_CHANGE,userId,"下了"+(position+1)+"号麦"));
                }else {
                    touristMicImg.setVisibility(View.VISIBLE);
                    if (muteVal!=null){
                        if (muteVal.contains(userId)){
                            touristMicImg.setSelected(false);
                            isProMuted=true;
                        }else {
                            touristMicImg.setSelected(true);
                            isProMuted=false;
                        }
                    }else {
                        touristMicImg.setSelected(true);
                        isProMuted=false;
                    }
                    isTouristJoinMic =true;
                    toTouristMic.setText("下麦");
                    addMessage(new MessageListBean(MessageListBean.MSG_MEMBER_CHANGE,userId,"上了"+(position+1)+"号麦"));
                }
            }
            mSeatAdapter.notifyItemChanged(position);
        });
    }

    @Override
    public void onUserStatusChanged(String userId, Boolean muted) {
        runOnUiThread(() -> {
            Log.i(TAG, "onUserStatusChanged: --->zhao  muted ="+muted+",userId ="+userId);
            if (mChannelData.isAnchor(userId) && muted !=null){
                if (muted){
                    anchorMute.setVisibility(View.VISIBLE);
                    mAnchorMic.setSelected(false);
                }else {
                    anchorMute.setVisibility(View.GONE);
                    mAnchorMic.setSelected(true);
                }
            }
            mSeatAdapter.notifyItemChanged(userId, false);
        });
    }

    @Override
    public void onUserGivingGift(String fromUserId, String toUserId, int giftId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showGift(fromUserId,toUserId,giftId);
            }
        });
    }

    @Override
    public void onChannelMessageAdded(String userId, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onChannelMessageAdded: message =" +message);
                addMessage(new MessageListBean(MessageListBean.MSG_NORMAL,userId,"："+message));
            }
        });
    }

    @Override
    public void onMemberListUpdated(String userId) {
        runOnUiThread(() -> {
            progressDialog.clearAnimation();
            setLayout(null);
            mSeatAdapter.notifyItemChanged(userId, false);
        });
    }

    @Override
    public void onAudioMixingStateChanged(int state) {
        runOnUiThread(() -> {
            Log.i(TAG, "onAudioMixingStateChanged: state ="+state);
        });
    }

    @Override
    public void onAudioVolumeIndication(String userId, int volume) {
        runOnUiThread(() -> {
            //说话的声音回调
            if (mChannelData.isAnchor(userId)){
                anchorAnim.startAnimation();
            }
            mSeatAdapter.notifyItemChanged(userId, true);
        });
    }

    @Override
    public void onMessageAdd(MessageListBean messageListBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addMessage(messageListBean);
            }
        });
    }

    @Override
    public void onNetWorkDelayChanges(int rtt) {
        runOnUiThread(()->{
            if (rtt<NET_WORK_RTT_SUPER){
                mChatSourceImg.setBackgroundResource(R.drawable.source_green);
                mChatSourceVal.setTextColor(getResources().getColor(R.color.source_green));
            }else if (rtt<NET_WORK_RTT_WELL){
                mChatSourceImg.setBackgroundResource(R.drawable.source_light_green);
                mChatSourceVal.setTextColor(getResources().getColor(R.color.source_light_green));
            }else if (rtt<NET_WORK_RTT_GOOD){
                mChatSourceImg.setBackgroundResource(R.drawable.source_yellow);
                mChatSourceVal.setTextColor(getResources().getColor(R.color.source_yellow));
            }else if (rtt<NET_WORK_RTT_BAD){
                mChatSourceImg.setBackgroundResource(R.drawable.source_orange);
                mChatSourceVal.setTextColor(getResources().getColor(R.color.source_orange));
            }else{
                mChatSourceImg.setBackgroundResource(R.drawable.source_red);
                mChatSourceVal.setTextColor(getResources().getColor(R.color.source_red));
            }
            if (rtt==0){
                mChatSourceVal.setText(10+"ms");
            }else {
                mChatSourceVal.setText(rtt+"ms");
            }

        });
    }

    public void addMessage(MessageListBean bean){
        messageAdapter.addData(bean);
        msgLayoutManager.scrollToPositionWithOffset(messageAdapter.getItemCount() - 1, Integer.MIN_VALUE);
    }

    private void closeMusic(String name, String state){
        JSONObject jsonObject =new JSONObject();
        try {
            jsonObject.put("name",name);
            jsonObject.put("state",state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_MUSIC,jsonObject.toString(),null);
    }

    private void showMemberList(){
        MemberDialog memberDialog =new MemberDialog();
        memberDialog.show(getSupportFragmentManager(),"");
    }

    private void showGift(String fromUserId, String toUserId, int giftId){
        if (rlGift.getVisibility()==View.VISIBLE){//这里应该处理 多个动画
//            giftList.add(new GiftBean(fromUserId,toUserId,giftId));
        }else {
            rlGift.setVisibility(View.VISIBLE);
            String toName =mChannelData.getName(toUserId);
            String fromName =mChannelData.getName(fromUserId);
            if (mChannelData.isAnchor(fromUserId)){
                setGiftText("主播",toUserId,toName,giftId);
            }else  if (mChannelData.isAnchor(toUserId)){
                if (fromName !=null){
                    setGiftText(fromName,toUserId,"主播",giftId);
                }else {
                    setGiftText(fromUserId,toUserId,"主播",giftId);
                }
                addGiftMessage(fromUserId,toUserId,"主播",giftId);
            }else {
                if (fromName !=null){
                    setGiftText(fromName,toUserId,toName,giftId);
                }else {
                    setGiftText(fromUserId,toUserId,toName,giftId);
                }
                addGiftMessage(fromUserId,toUserId,toUserId,giftId);
            }

            ivGift.setImageResource(giftArray[giftId]);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rlGift, "alpha", 0f, 1f);
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rlGift, "scaleX", 0.8f, 1f);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rlGift, "scaleY", 0.8f, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(2000);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    rlGift.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorSet.start();
        }
    }

    private void setGiftText(String name,String toUserId,String toName,int giftId){
        tvGiftTip.setText(name+"赠送"+(TextUtils.isEmpty(toUserId)? "麦上全体人员":toName)+"一个"+giftNameArray[giftId]);
    }
    private void addGiftMessage(String name,String toUserId,String toName,int giftId){
        if ("我".equals(name)){
            addMessage(new MessageListBean(MessageListBean.MSG_GIFT,
                    name,"赠送", (TextUtils.isEmpty(toUserId)? "麦上全体人员":toName),"一个"+giftNameArray[giftId]));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exit();
    }
    private void exit(){
        initSpData();
        mChannelData.setIsMicLock("0");
        Constant.isEffectOpen =false;
        mManager.getRtcManager().stopAudioMixing();
        mPlayMusic.clearAnimation();
        mMusicAnimation.cancel();
        mManager.leaveChannel();
    }

    private void initSpData(){
        SpUtil.putInt("musicVal",40);
        SpUtil.putInt("volumeVal",60);
        SpUtil.putInt("earVal",0);
    }
}
