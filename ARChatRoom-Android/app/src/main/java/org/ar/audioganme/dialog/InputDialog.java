package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.model.ChannelData;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.util.SpUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputDialog extends Dialog {

    private static final String RECORD_FILE_FORMAT_WAC =".wav";
    private static final String RECORD_FILE_FORMAT_AAC =".aac";
    private EditText fileName;
    private Button btnConfirm;
    private ChatRoomManager chatRoomManager;
    private Context context;
    private ChannelData channelData;
    private boolean isHiFi;
    private List<String> recordPaths ;

    public InputDialog(@NonNull Context context,ChatRoomManager chatRoomManager,boolean isHiFi) {
        super(context);
        this.context =context;
        this.chatRoomManager =chatRoomManager;
        this.isHiFi =isHiFi;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_input);
        channelData =chatRoomManager.getChannelData();
        fileName = findViewById(R.id.ed_input);
        btnConfirm = findViewById(R.id.btn_confirm);
        recordPaths = chatRoomManager.getRecordPaths();
       /* recordPath = SpUtil.getStringSet("recordPath");
        if (recordPath == null) {
            recordPath = new HashSet<>();
        }*/

        btnConfirm.setEnabled(false);
        btnConfirm.setClickable(false);
        fileName.setHint("请输入文件名称");
        fileName.setInputType(InputType.TYPE_CLASS_TEXT);
        fileName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        fileName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        fileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()!=0){
                    btnConfirm.setEnabled(true);
                    btnConfirm.setClickable(true);
                }else {
                    btnConfirm.setEnabled(false);
                    btnConfirm.setClickable(false);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path ="";
                if (isHiFi){ //高保真
                    path= fileName.getText().toString()+RECORD_FILE_FORMAT_WAC;
                }else {
                    path= fileName.getText().toString()+RECORD_FILE_FORMAT_AAC;
                }
                AlertUtil.createFileSD(path);
                path = Environment.getExternalStorageDirectory().getPath()+"/"+path;
                if (!recordPaths.contains(path)){
                    recordPaths.add(path);
                }
                chatRoomManager.setRecordPaths(recordPaths);
                chatRoomManager.getRtcManager().startAudioRecording(path);
                chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_RECORD,"1",null);
                dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
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
}
