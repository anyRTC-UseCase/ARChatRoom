package org.ar.audioganme.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.ar.audioganme.R;
import org.ar.audioganme.model.MessageListBean;
import org.ar.audioganme.model.WaitMicBean;
import org.ar.audioganme.util.AlertUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WaitMicAdapter extends BaseQuickAdapter<WaitMicBean,BaseViewHolder> {

    public WaitMicAdapter() {
        super(R.layout.wait_mic_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, WaitMicBean item) {
        CircleImageView waitAvatar = helper.getView(R.id.wait_mic_avatar);
        TextView waitName = helper.getView(R.id.wait_mic_name);
        TextView waitApply = helper.getView(R.id.wait_mic_apply);
        helper.addOnClickListener(R.id.wait_mic_agree);
        helper.addOnClickListener(R.id.wait_mic_refuse);
        AlertUtil.setAvatar(mContext,item.waitAvatar,waitAvatar);
        waitName.setText(item.waitName);
        waitApply.setText("申请"+String.valueOf(item.waitApplyPos)+"号麦");

    }
}
