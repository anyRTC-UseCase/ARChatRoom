package org.ar.audioganme.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.ar.audioganme.R;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.MessageListBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessageAdapter extends BaseQuickAdapter<MessageListBean, BaseViewHolder> {
    public MessageAdapter() {
        super(R.layout.item_message_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageListBean item) {
        TextView tvFromId = helper.getView(R.id.tv_fromId);
        TextView tvMsg = helper.getView(R.id.tv_message);
        TextView tvToId = helper.getView(R.id.tv_toId);
        TextView tvGift = helper.getView(R.id.tv_gift_name);
        switch (item.type){
            case MessageListBean.MSG_SYSYTEM:
                tvFromId.setTextColor(mContext.getResources().getColor(R.color.msg_text_system));
                tvMsg.setVisibility(View.GONE);
                tvToId.setVisibility(View.GONE);
                tvGift.setVisibility(View.GONE);
                break;
            case MessageListBean.MSG_NORMAL:
                tvFromId.setTextColor(mContext.getResources().getColor(R.color.msg_text_normal_id));
                tvMsg.setTextColor(mContext.getResources().getColor(R.color.msg_text_normal_msg));
                tvToId.setVisibility(View.GONE);
                tvGift.setVisibility(View.GONE);
                break;
            case MessageListBean.MSG_MEMBER_CHANGE:
                tvFromId.setTextColor(mContext.getResources().getColor(R.color.msg_text_normal_id));
                tvMsg.setTextColor(mContext.getResources().getColor(R.color.msg_text_system));
                tvToId.setVisibility(View.GONE);
                tvGift.setVisibility(View.GONE);
                break;
            case MessageListBean.MSG_GIFT:
                tvFromId.setTextColor(mContext.getResources().getColor(R.color.msg_text_normal_id));
                tvMsg.setTextColor(mContext.getResources().getColor(R.color.msg_text_give_gift));
                tvToId.setTextColor(mContext.getResources().getColor(R.color.msg_text_normal_msg));
                tvGift.setTextColor(mContext.getResources().getColor(R.color.msg_text_gift));
                break;
            default:
                break;
        }
        String name =ChatRoomManager.instance(mContext).getChannelData().getName(item.id);
        tvFromId.setText(name);
        tvMsg.setText(item.content);
        tvToId.setText(item.toId);
        tvGift.setText(item.gift);
    }


}
