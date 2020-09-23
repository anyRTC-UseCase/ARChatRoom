package org.ar.audioganme.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ar.audioganme.R;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.model.Member;
import org.ar.audioganme.model.Seat;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.weight.SpreadView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeatGridAdapter extends RecyclerView.Adapter<SeatGridAdapter.ViewHolder> {

    private final static String TAG =SeatGridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;
    private ChannelData mChannelData;
    private Context mContext;

    public SeatGridAdapter(Context context) {
        mContext =context;
        mInflater = LayoutInflater.from(context);
        mChannelData = ChatRoomManager.instance(context).getChannelData();
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view =mInflater.inflate(R.layout.layout_chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mChannelData ==null){
            return 0;
        }
        return mChannelData.getSeatArray().length;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.size() > 0)
            holder.view_anim.startAnimation();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] names =mContext.getResources().getStringArray(R.array.tourist_name);
        holder.tv_name.setText(names[position]);
        String seatId =mChannelData.getSeatArray()[position];
        Member member =mChannelData.getMember(seatId);
        Log.i(TAG, "onBindViewHolder: position ="+position+",SeatId ="+seatId);
        if ("1".equals(mChannelData.getIsMicLock())){
            if(!TextUtils.isEmpty(seatId)){
                if (mChannelData.isUserOnline(seatId)){
                    AlertUtil.showAvatar(member.getAvatarAddr(),holder.iv_avatar);
                    holder.tv_name.setText(member.getName());
                    holder.iv_sex.setVisibility(View.VISIBLE);
                    if (member.getGender() ==0){
                        holder.iv_sex.setImageResource(R.drawable.man);
                    }else {
                        holder.iv_sex.setImageResource(R.drawable.girl);
                    }
                    holder.iv_mute.setVisibility(mChannelData.isUserMuted(seatId) ? View.VISIBLE:View.GONE);
                    holder.iv_join.setVisibility(View.GONE);

                }else {
                    holder.iv_avatar.setImageResource(R.drawable.shape_circle_bg);
                    holder.iv_mute.setVisibility(View.GONE);
                    holder.iv_sex.setVisibility(View.GONE);
                }
            }else {
                holder.iv_avatar.setImageResource(R.drawable.shape_cirlcle_lock_bg);
                holder.iv_mute.setVisibility(View.GONE);
                holder.iv_join.setVisibility(View.VISIBLE);
                holder.iv_sex.setVisibility(View.GONE);
                holder.iv_join.setImageResource(R.drawable.mic_lock);
            }
        }else {
            if(!TextUtils.isEmpty(seatId)){
                if (mChannelData.isUserOnline(seatId)){
                    AlertUtil.showAvatar(mChannelData.getMemberAvatar(seatId),holder.iv_avatar);
                    holder.tv_name.setText(member.getName());
                    holder.iv_sex.setVisibility(View.VISIBLE);
                    if (member.getGender() ==0){
                        holder.iv_sex.setImageResource(R.drawable.man);
                    }else {
                        holder.iv_sex.setImageResource(R.drawable.girl);
                    }
                    holder.iv_join.setVisibility(View.GONE);
                    holder.iv_mute.setVisibility(mChannelData.isUserMuted(seatId)?View.VISIBLE:View.GONE);
                }else {
                    holder.iv_avatar.setImageResource(R.drawable.shape_circle_bg);
                    holder.iv_join.setImageResource(R.drawable.join_mic);
                    holder.iv_join.setVisibility(View.VISIBLE);
                    holder.iv_mute.setVisibility(View.GONE);
                    holder.iv_sex.setVisibility(View.GONE);
                }
            }else {
                holder.iv_avatar.setImageResource(R.drawable.shape_circle_bg);
                holder.iv_join.setImageResource(R.drawable.join_mic);
                holder.iv_join.setVisibility(View.VISIBLE);
                holder.iv_mute.setVisibility(View.GONE);
                holder.iv_sex.setVisibility(View.GONE);
            }
        }


        holder.rl_tourist.setOnClickListener(view -> {
            if (mListener !=null){
                mListener.onItemClick(holder.view,position,seatId);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener =listener;
    }

    public void notifyItemChanged(String userId,boolean animated){
        int index =mChannelData.indexOfSeatArray(userId);
        if (index >= 0) {
            if (animated)
                notifyItemChanged(index, true);
            else
                notifyItemChanged(index);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        RelativeLayout rl_tourist;
        SpreadView view_anim;
        CircleImageView iv_avatar;
        ImageView iv_mute,iv_join,iv_sex;
        TextView tv_name;
        ViewHolder(View view) {
            super(view);
            this.view =view;
            rl_tourist =view.findViewById(R.id.rl_tourist);
            view_anim =view.findViewById(R.id.tourist_anim);
            iv_avatar =view.findViewById(R.id.tourist_head);
            iv_mute =view.findViewById(R.id.tourist_mute);
            iv_join =view.findViewById(R.id.tourist_join);
            tv_name =view.findViewById(R.id.tourist_name);
            iv_sex =view.findViewById(R.id.tourist_sex);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String userId);
    }
}
