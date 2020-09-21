package org.ar.audioganme.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.kongzue.dialog.v3.MessageDialog;

import org.ar.audioganme.App;
import org.ar.audioganme.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class AlertUtil {

    public static void showToast(String format, Object... args) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(App.instance, String.format(format, args), Toast.LENGTH_SHORT).show());
    }

    public static void showTipDialog(Context context,String tips) {
        MessageDialog.show((AppCompatActivity) context, "提示", tips, "确定");
    }

    /**
     * 把Assets目录下的音乐文件写入SD卡
     * @throws IOException
     */
    public static void musicWriteSD(Context context,String[] musicPath) throws IOException {
        AssetManager am = context.getAssets();
        Log.i("TAG", "musicWriteSD: music ="+musicPath.length);
        for (int i=0;i<musicPath.length;i++){
            InputStream is = am.open(musicPath[i]);
            // 获取SD卡根路径
            String sdPath = Environment.getExternalStorageDirectory().getPath();
            Log.i("TAG", "musicWriteSD: sdp ="+sdPath);
            FileOutputStream fos = new FileOutputStream(sdPath + "/"+musicPath[i]);
            // 写入SD卡
            byte[] buff = new byte[1024];
            int count = is.read(buff);
            while (count != -1){
                fos.write(buff);
                count = is.read(buff);
            }
            fos.flush();
            is.close();
            fos.close();
        }
    }

    public static void setAvatar(Context context, String addr, ImageView imageView){
        Log.d("setAvatar", "setAvatar: addr ="+addr);
        Glide.with(context)
                .load(addr)
                .error(R.drawable.ic_unkown)
                .into(imageView);
    }

    /**
      * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
      * 
      * @param context
      * @param dpValue
      * @return
      */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * 
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
