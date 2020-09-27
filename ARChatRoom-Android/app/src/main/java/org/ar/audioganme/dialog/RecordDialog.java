package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;
import org.ar.audioganme.activity.RecordActivity;
import org.ar.audioganme.manager.ChatRoomManager;

public class RecordDialog extends Dialog implements View.OnClickListener {

    private String valOne,valTwo;
    private TextView tvValOne,tvValTwo,tvCancel;
    private Context context;
    private boolean isRecordUi;
    private ChatRoomManager chatRoomManager;

    public RecordDialog(@NonNull Context context, ChatRoomManager chatRoomManager, String valOne, String valTwo, boolean isRecordUi) {
        super(context);
        this.context =context;
        this.valOne =valOne;
        this.valTwo =valTwo;
        this.isRecordUi =isRecordUi;
        this.chatRoomManager=chatRoomManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom_record);
        tvValOne =findViewById(R.id.record_text_one);
        tvValTwo =findViewById(R.id.record_text_two);
        tvCancel =findViewById(R.id.record_text_cancel);

        tvValOne.setText(valOne);
        tvValTwo.setText(valTwo);

        tvValOne.setOnClickListener(this);
        tvValTwo.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
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
            case R.id.record_text_one:
                dismiss();
                if (isRecordUi){
                    RecordDialog recordDialog=new RecordDialog(context,chatRoomManager,"高保真","有损压缩",false);
                    recordDialog.show();
                }else {
                    InputDialog inputDialog =new InputDialog(context,chatRoomManager,true);
                    inputDialog.show();
                }
                break;
            case R.id.record_text_two:
                if (isRecordUi){
                    Intent intent =new Intent();
                    intent.setClass(context, RecordActivity.class);
                    context.startActivity(intent);
                }else {
                    InputDialog inputDialog =new InputDialog(context,chatRoomManager,false);
                    inputDialog.show();
                }
                dismiss();
                break;
            case R.id.record_text_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
}
