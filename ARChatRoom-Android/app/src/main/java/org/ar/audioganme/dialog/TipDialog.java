package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;

public class TipDialog extends Dialog implements View.OnClickListener {

    public interface ConfirmCallBack{
        void onClick();
    }
    public interface CancelCallBack{
        void onClick();
    }
    private ConfirmCallBack confirmCallBack;
    private CancelCallBack cancelCallBack;
    private TextView tvTitle,tvCancel,tvConfirm;
    private String title,cancel,confirm;
    private Context context;

    public TipDialog(@NonNull Context context,String title,String cancel,String confirm,CancelCallBack cancelCallBack,ConfirmCallBack confirmCallBack) {
        super(context);
        this.confirm =confirm;
        this.title =title;
        this.cancel=cancel;
        this.context =context;
        this.confirmCallBack=confirmCallBack;
        this.cancelCallBack=cancelCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tip);
        tvTitle =findViewById(R.id.tip_title);
        tvCancel =findViewById(R.id.tip_cancel);
        tvConfirm =findViewById(R.id.tip_confirm);

        tvTitle.setText(title);
        tvCancel.setText(cancel);
        tvConfirm.setText(confirm);

        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tip_cancel:
                if (cancelCallBack !=null){
                    cancelCallBack.onClick();
                }
                dismiss();
                break;
            case R.id.tip_confirm:
                if (confirmCallBack !=null){
                    confirmCallBack.onClick();
                }
                dismiss();
                break;
            default:
                break;
        }
    }
}
