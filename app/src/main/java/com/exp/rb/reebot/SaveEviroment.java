package com.exp.rb.reebot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by freem on 2017-04-05.
 */

public class SaveEviroment {

    private static final String TAG = "ReeBot(SaveEviroment)";
    static final String PREF_USER_EMAIL= "email";
    static final String PREF_USER_CODE= "code";
    static final String PREF_USER_TVBREND= "tvbrend";
    static final String PREF_USER_CATVBREND= "catvbrend";
    static final String PREF_USER_FAVORITE= "favorite";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String email, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_EMAIL, email);
        editor.putString(PREF_USER_CODE, userName);
        editor.commit();
    }

    public static void setUserEnv(Context ctx, String tvbrend, String catvbrend, String favorite)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_TVBREND, tvbrend);
        editor.putString(PREF_USER_CATVBREND, catvbrend);
        editor.putString(PREF_USER_FAVORITE, favorite);
        editor.commit();
    }
    public static String[] getUserLoginData(Context ctx)
    {
        String[] ret = new String[2];
        ret[0] = getSharedPreferences(ctx).getString(PREF_USER_EMAIL, "");
        ret[1] = getSharedPreferences(ctx).getString(PREF_USER_CODE, "");
        return ret;
    }
    public static String[] getUserEnvData(Context ctx)
    {
        String[] ret = new String[3];
        ret[0] = getSharedPreferences(ctx).getString(PREF_USER_TVBREND, "");
        ret[1] = getSharedPreferences(ctx).getString(PREF_USER_CATVBREND, "");
        ret[2] = getSharedPreferences(ctx).getString(PREF_USER_FAVORITE, "");
        return ret;
    }
    public static void delEnvData(Context ctx)
    {
        Log.d(TAG, "delEnvData"+ctx);
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_EMAIL);
        editor.remove(PREF_USER_CODE);
        editor.remove(PREF_USER_TVBREND);
        editor.remove(PREF_USER_CATVBREND);
        editor.remove(PREF_USER_FAVORITE);
        editor.commit();
    }
}
