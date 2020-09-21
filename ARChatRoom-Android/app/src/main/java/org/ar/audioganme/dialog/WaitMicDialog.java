package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.ar.audioganme.R;
import org.ar.audioganme.adapter.WaitMicAdapter;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.model.Member;
import org.ar.audioganme.model.WaitMicBean;
import org.ar.audioganme.util.AlertUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WaitMicDialog extends Dialog implements View.OnClickListener {

    private static final String TAG =WaitMicDialog.class.getSimpleName();
    private ImageView mClose;
    private RecyclerView mWaitRecycler;
    private Button mBtnQuickMic;
    private TextView tvWaitNone;
    private Context context;
    private HashMap<String,Integer> waitMap;
    private ChatRoomManager chatRoomManager;
    private ChannelData channelData;
    private WaitMicAdapter waitMicAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ClickCallBack clickCallBack;
    private boolean isAgree;
    public interface ClickCallBack{
        void onClick(String userId,int pos,boolean isAgree);
    }

    public WaitMicDialog(@NonNull Context context, ChatRoomManager chatRoomManager,HashMap<String, Integer> waitMap,ClickCallBack clickCallBack) {
        super(context);
        this.context =context;
        this.chatRoomManager =chatRoomManager;
        this.waitMap =waitMap;
        this.clickCallBack =clickCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wait_mic);
        mClose =findViewById(R.id.wait_mic_close);
        mWaitRecycler =findViewById(R.id.wait_mic_recycler);
        mBtnQuickMic =findViewById(R.id.quick_mic);
        tvWaitNone =findViewById(R.id.wait_mic_none);
        channelData =chatRoomManager.getChannelData();
        linearLayoutManager = new LinearLayoutManager(context);
        mWaitRecycler.setLayoutManager(linearLayoutManager);
        waitMicAdapter =new WaitMicAdapter();
        mWaitRecycler.setAdapter(waitMicAdapter);

        mClose.setOnClickListener(this);
        mBtnQuickMic.setOnClickListener(this);
        setItem();
        setListener();
    }

    private void setListener() {
        waitMicAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String userId =waitMicAdapter.getItem(position).getUserId();
                String name =waitMicAdapter.getItem(position).getWaitName();
                int micPos =waitMicAdapter.getItem(position).getWaitApplyPos();
                JSONObject jsonObject =new JSONObject();
                switch (view.getId()){
                    case R.id.wait_mic_refuse:
                        isAgree =false;
                        try {
                            jsonObject.put("cmd","rejectLine");
                            jsonObject.put("reason","拒绝上麦");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        chatRoomManager.getRtmManager().sendMessageToPeer(userId,jsonObject.toString(),null);
                        break;
                    case R.id.wait_mic_agree:
                        isAgree =true;
                        Log.i(TAG, "onItemChildClick: userId ="+userId+",micPos ="+micPos);
                        chatRoomManager.toBroadcaster(userId,(micPos-1));

                        try {
                            jsonObject.put("cmd","acceptLine");
                            jsonObject.put("seat",String.valueOf(micPos));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        chatRoomManager.getRtmManager().sendMessageToPeer(userId,jsonObject.toString(),null);
                        break;
                    default:
                        break;
                }
                clickCallBack.onClick(userId,micPos,isAgree);
                deleteMicAttrVal(userId);
                waitMicAdapter.remove(position);
                if (waitMicAdapter.getItemCount() ==0){
                    tvWaitNone.setVisibility(View.VISIBLE);
                    mWaitRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    private void deleteMicAttrVal(String id){
        String val =channelData.getWaitVal();
        if (val !=null){
            try {
                JSONArray jsonArray =new JSONArray(val);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    JSONObject jsonObject =jsonArray.getJSONObject(i);
                    String userId =jsonObject.getString("userid");
                    if (id.equals(userId)){
                        jsonArray.remove(i);
                    }
                }
                Log.i(TAG, "deleteMicAttrVal: jsonArray ="+jsonArray.toString());
                chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_WAITING_LIST,jsonArray.toString(),null);
            } catch (JSONException e) {
                e.printStackTrace();
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

    private void setItem(){
        if (waitMap.size()!=0){
            tvWaitNone.setVisibility(View.GONE);
            mWaitRecycler.setVisibility(View.VISIBLE);
            for (Map.Entry<String, Integer> entry : waitMap.entrySet()) {
                Member member =channelData.getMember(entry.getKey());
                waitMicAdapter.addData(new WaitMicBean(entry.getKey(),member.getAvatarAddr(),member.getName(),entry.getValue()));
            }
        }else {
            tvWaitNone.setVisibility(View.VISIBLE);
            mWaitRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wait_mic_close:
                dismiss();
                break;
            case R.id.quick_mic:
                break;
            default:
                break;
        }
    }
}
