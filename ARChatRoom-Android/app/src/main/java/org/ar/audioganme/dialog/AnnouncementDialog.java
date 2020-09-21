package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;
import org.ar.audioganme.activity.ChatActivity;
import org.ar.audioganme.model.ChannelData;

public class AnnouncementDialog extends Dialog {

    private ChatActivity activity;
    private ChannelData channelData;
    private TextView tvAnnouncement,tvConfirm;
    private ScrollView svAnnouncement;
    private LinearLayout llNoneAnn;
    public AnnouncementDialog(@NonNull ChatActivity activity, ChannelData channelData) {
        super(activity);
        this.activity =activity;
        this.channelData =channelData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_announcement);
        tvAnnouncement =findViewById(R.id.tv_show_announcement);
        tvConfirm =findViewById(R.id.ann_confirm);
        svAnnouncement =findViewById(R.id.sv_announcement);
        llNoneAnn =findViewById(R.id.ll_none_ann);

        if (TextUtils.isEmpty(channelData.getAnnouncement())){
            svAnnouncement.setVisibility(View.GONE);
            llNoneAnn.setVisibility(View.VISIBLE);
        }else {
            svAnnouncement.setVisibility(View.VISIBLE);
            llNoneAnn.setVisibility(View.GONE);
            tvAnnouncement.setText(channelData.getAnnouncement());
        }
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }
}
