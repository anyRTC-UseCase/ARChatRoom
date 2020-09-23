package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;
import org.ar.audioganme.model.AttributeKey;
import org.ar.audioganme.util.AlertUtil;

public class InputDialog extends Dialog {

    private EditText fileName;
    private Button btnConfirm;

    public InputDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_input);
        fileName = findViewById(R.id.ed_input);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setEnabled(false);
        btnConfirm.setClickable(false);
        fileName.setHint("请输入文件名称");
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
