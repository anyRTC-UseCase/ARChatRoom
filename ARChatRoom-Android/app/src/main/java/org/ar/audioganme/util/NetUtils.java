package org.ar.audioganme.util;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

public class NetUtils {
    /**
     * 判断网络是否连接
     *
     * @param context context
     * @return true/false
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否wifi连接
     *
     * @param context context
     * @return true/false
     */
    public static synchronized boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int networkInfoType = networkInfo.getType();
                if (networkInfoType == ConnectivityManager.TYPE_WIFI || networkInfoType == ConnectivityManager.TYPE_ETHERNET) {
                    return networkInfo.isConnected();
                }
            }
        }
        return false;
    }


    public static int getNetworkWifiLevel(Context context) {
        if (!isWifiConnected(context)) {
            return 0;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //获得信号强度值
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int level = wifiInfo.getRssi();
            //根据获得信号的强度发送信息
            if (level <= 0 && level >= -40) {//最强
                System.out.println("level==========1===========" + level);
                return 1;
            } else if (level < -40 && level >= -60) {//较强
                System.out.println("level===========2==========" + level);
                return 2;
            } else if (level < -60 && level >= -70) {//较弱
                System.out.println("level==========3===========" + level);
                return 3;
            } else if (level < -70 && level >= -80) {//微弱
                System.out.println("level==========4===========" + level);
                return 4;
            }  else if (level < -80 && level >= -100) {//微弱
                System.out.println("level==========4===========" + level);
                return 5;
            }else {
                System.out.println("level==========5===========" + level);
                return 6;
            }
        }

        return 0;
    }
}
