package org.ar.audioganme.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.ar.audioganme.R;
import org.ar.audioganme.adapter.RecordAdapter;
import org.ar.audioganme.manager.ChatRoomManager;
import org.ar.audioganme.model.RecordBean;
import org.ar.audioganme.util.AlertUtil;
import org.ar.audioganme.util.SpUtil;
import org.ar.audioganme.util.StatusBarUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBack;
    private TextView mNoneData;
    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ChatRoomManager chatRoomManager;
    private List<String> recordPaths;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setDeepStatusBar(true, RecordActivity.this, Color.TRANSPARENT);
        StatusBarUtil.setStatusBarColor(RecordActivity.this, R.color.white);
        setContentView(R.layout.activity_record);
        chatRoomManager =ChatRoomManager.instance(this);
        mBack =findViewById(R.id.record_back);
        mNoneData =findViewById(R.id.none_record);
        recyclerView =findViewById(R.id.rv_record);
        recordAdapter =new RecordAdapter();
        linearLayoutManager =new LinearLayoutManager(this);
        recyclerView.setAdapter(recordAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recordPaths =chatRoomManager.getRecordPaths();
        mBack.setOnClickListener(this);
        if (recordPaths.size() == 0) {
            mNoneData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            mNoneData.setVisibility(View.GONE);
            for (String path:recordPaths) {
                File file =new File(path);
                recordAdapter.addData(new RecordBean(file.getName(),getTimeDate(file.lastModified())));
            }
        }
        setListener();
    }

    private void setListener() {
        recordAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            String path;
            File file;
            switch (view.getId()){
                case R.id.record_share:
                    path = Environment.getExternalStorageDirectory().getPath()+"/"+recordAdapter.getData().get(position).getName();
                    file =new File(path);
                    AlertUtil.shareRecord(this,file);
                    break;
                case R.id.record_delete:
                    path = Environment.getExternalStorageDirectory().getPath()+"/"+recordAdapter.getData().get(position).getName();
                    file =new File(path);
                    if (file.exists()){
                        file.delete();
                    }
                    if (recordPaths.contains(path)){
                        recordPaths.remove(path);
                    }
                    //SpUtil.putStringSet("recordPath",recordPaths);
                    recordAdapter.remove(position);
                    if (recordAdapter.getItemCount() ==0){
                        mNoneData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        });
    }

    private String getTimeDate(long time){
        Calendar calendar =Calendar.getInstance();
        SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.record_back:
                Intent intent =new Intent(RecordActivity.this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==KeyEvent.KEYCODE_BACK){
            Intent intent =new Intent(RecordActivity.this, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            overridePendingTransition(0,0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
