package org.ar.audioganme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.ar.audioganme.R;

public class AutoTipDialog extends Dialog {

    private ImageView imgTip;
    private TextView textTip;
    private int imgRes;
    private String text;
    private Context context;


    public AutoTipDialog(@NonNull Context context,int imgRes,String text) {
        super(context);
        this.context =context;
        this.imgRes =imgRes;
        this.text =text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tip);
        imgTip =findViewById(R.id.tip_img);
        textTip =findViewById(R.id.tip_text);
        imgTip.setImageResource(imgRes);
        textTip.setText(text);

    }

    @Override
    public void show() {
        super.show();
        new Handler().postDelayed(() -> dismiss(),1000);
    }
}
