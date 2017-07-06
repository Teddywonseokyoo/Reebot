package com.example.reebotui;

import android.app.Activity;
import android.app.Application;

import com.example.reebotui.kakao.KaKaoSDKAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by silver on 2017-06-13.
 */

public class GlobalApplication extends Application {

    private static volatile GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KakaoSDK.init(new KaKaoSDKAdapter());

    }

    public static GlobalApplication getGlobalApplicationContext() {
        return instance;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

}


