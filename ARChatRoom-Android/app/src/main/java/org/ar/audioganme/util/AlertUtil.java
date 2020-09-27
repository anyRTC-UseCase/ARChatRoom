package org.ar.audioganme.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.content.FileProvider;


import com.bumptech.glide.Glide;
import com.kongzue.dialog.v3.MessageDialog;

import org.ar.audioganme.App;
import org.ar.audioganme.R;

import java.io.File;
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
    public static void writeSD(Context context,String[] paths) throws IOException {
        AssetManager am = context.getAssets();
        Log.i("TAG", "musicWriteSD: music ="+paths.length);
        for (int i=0;i<paths.length;i++){
            InputStream is = am.open(paths[i]);
            // 获取SD卡根路径
            String sdPath = Environment.getExternalStorageDirectory().getPath();
            Log.i("TAG", "musicWriteSD: sdp ="+sdPath);
            FileOutputStream fos = new FileOutputStream(sdPath + "/"+paths[i]);
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

    /**
     * 把Assets目录下的音乐文件写入SD卡
     * @throws IOException
     */
    public static void createFileSD(String fileName){
        String path =Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator;
        try {
            File file =new File(path,fileName);
            file.mkdir();
            //file.createNewFile();
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setAvatar(Context context, String addr, ImageView imageView){
        Glide.with(context)
                .load(addr)
                .error(R.drawable.ic_unkown)
                .into(imageView);
    }

    public static void showAvatar(String addr, ImageView imageView){
        int resId =MemberUtil.getAvatarResId(addr);
        imageView.setImageResource(resId);
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


    /**
     * 分享文件
     *
     * @param picFile 文件路径
     */
    public static void shareRecord(Context mContext, File picFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String type="*/*";
        for (int i = 0; i < MATCH_ARRAY.length; i++) {
            //判断文件的格式
            if (picFile.getAbsolutePath().toString().contains(MATCH_ARRAY[i][0].toString())) {
                type = MATCH_ARRAY[i][1];
                break;
            }
        }
        Log.i("TAG", "shareFriend: type = "+type +",picFile ="+picFile.getAbsolutePath());
        intent.setType(type);
        Uri uri = null;
        if (picFile != null) {
            //这部分代码主要功能是判断了下文件是否存在，在android版本高过7.0（包括7.0版本）
            // 当前APP是不能直接向外部应用提供file开头的的文件路径，需要通过FileProvider转换一下。否则在7.0及以上版本手机将直接crash。
            try {
                ApplicationInfo applicationInfo = mContext.getApplicationInfo();
                int targetSDK = applicationInfo.targetSdkVersion;
                if (targetSDK >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", picFile);
                } else {
                    uri = Uri.fromFile(picFile);
                }
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mContext.startActivity(Intent.createChooser(intent, "Share"));
        }
    }

    private static final String[][] MATCH_ARRAY = {
            {".aac", "audio/aac"},
            {".wav", "audio/wav"}
    };
}
