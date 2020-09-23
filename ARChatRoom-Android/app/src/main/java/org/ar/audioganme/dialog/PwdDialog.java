package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
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
import org.ar.audioganme.util.AlertUtil;

public class PwdDialog extends Dialog  {

    private EditText edPwd;
    private Button confirm;
    private Context context;
    private ChatRoomManager chatRoomManager;
    private boolean isPwd;
    private PwdCallBack pwdCallBack;
    public interface PwdCallBack{
        void onSucceed();
    }
    public PwdDialog(@NonNull Context context, ChatRoomManager chatRoomManager,boolean isPwd) {
        super(context);
        this.isPwd =isPwd;
        this.chatRoomManager =chatRoomManager;
        this.context =context;
    }

    public PwdDialog(@NonNull Context context, ChatRoomManager chatRoomManager,boolean isPwd,PwdCallBack pwdCallBack) {
        super(context);
        this.isPwd =isPwd;
        this.chatRoomManager =chatRoomManager;
        this.context =context;
        this.pwdCallBack =pwdCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_input);
        edPwd = findViewById(R.id.ed_input);
        confirm = findViewById(R.id.btn_confirm);
        confirm.setEnabled(false);
        confirm.setClickable(false);
        edPwd.setInputType(InputType.TYPE_CLASS_NUMBER);
        edPwd.setHint("请输入4位密码");
        edPwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        edPwd.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        edPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()==4){
                    confirm.setEnabled(true);
                    confirm.setClickable(true);
                }else {
                    confirm.setEnabled(false);
                    confirm.setClickable(false);
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPwd){
                    chatRoomManager.getRtmManager().addOrUpdateChannelAttributes(AttributeKey.KEY_IS_LOCK,edPwd.getText().toString(),null);
                    AlertUtil.showToast("密码设置成功");
                }else {
                    if (edPwd.getText().toString().equals(chatRoomManager.getChannelData().getLockVal())){
                        AlertUtil.showToast("密码正确");
                        pwdCallBack.onSucceed();
                    }else {
                        AlertUtil.showToast("密码错误，请重新输入");
                    }
                }
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
