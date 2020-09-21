package org.ar.audioganme.adapter;

import android.util.Log;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.ar.audioganme.R;
import org.ar.audioganme.model.Member;
import org.ar.audioganme.model.MemberListBean;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.util.MemberUtil;

import java.util.List;

import androidx.annotation.Nullable;

public class OnlineMemberAdapter extends BaseQuickAdapter<MemberListBean, BaseViewHolder> {
    public OnlineMemberAdapter() {
        super(R.layout.online_member_item);
    }

    private  String[] avatarRes;
    private  String[] avatars;
    @Override
    protected void convert(BaseViewHolder helper, MemberListBean item) {
        helper.setText(R.id.online_name,item.getName());
        ImageView avatar = helper.getView(R.id.online_avatar);

        if (item.getGender() ==0){
            avatarRes =mContext.getResources().getStringArray(R.array.avatar_man);
            avatars =mContext.getResources().getStringArray(R.array.random_avatar_images_man);
            helper.setImageResource(R.id.online_sex,R.drawable.man);
        }else {
            avatarRes =mContext.getResources().getStringArray(R.array.avatar_woman);
            avatars =mContext.getResources().getStringArray(R.array.random_avatar_images_woman);
            helper.setImageResource(R.id.online_sex,R.drawable.girl);
        }
        AlertUtil.setAvatar(mContext,item.getAvatar(),avatar);
    }
}
