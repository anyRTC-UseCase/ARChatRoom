package org.ar.audioganme.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.ar.audioganme.R;
import org.ar.audioganme.model.RecordBean;

import java.util.List;

public class RecordAdapter extends BaseQuickAdapter<RecordBean, BaseViewHolder> {

    public RecordAdapter() {
        super(R.layout.record_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecordBean item) {
        helper.setText(R.id.record_name,item.getName());
        helper.setText(R.id.record_time,item.getTime());
        helper.addOnClickListener(R.id.record_share);
        helper.addOnClickListener(R.id.record_delete);

    }
}
