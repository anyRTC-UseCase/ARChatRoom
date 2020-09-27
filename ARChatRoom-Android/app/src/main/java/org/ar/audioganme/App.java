package org.ar.audioganme;

import android.app.Application;

import com.lzf.easyfloat.EasyFloat;

import org.ar.audioganme.manager.RtcManager;
import org.ar.audioganme.manager.RtmManager;
import org.ar.audioganme.util.SpUtil;

public class App extends Application {
    public static final String RTC_APPID = "";
    public static final String RTM_APPID = "";

    public static Application instance;

    public App() {
        instance = this;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        SpUtil.init(this);
        RtcManager.getInstance(this).init();
        RtmManager.getInstance(this).init();
        EasyFloat.init(this);
    }
}
