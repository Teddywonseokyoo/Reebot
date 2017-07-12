package com.example.reebotui.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.helper.Utility.getPackageInfo;

/**
 * Created by silver on 2017-06-13.
 */

public class AppUtil {

    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str.trim()))
            return true;
        else
            return false;
    }

    public static Drawable getImage(Context context, String name, String name2) {
        //Log.d(TAG, "getImage chname :" + name2);
        Drawable dimg = null;
        name2 = name2.toLowerCase();

        try {
            if (context.getResources().getIdentifier(name2, "drawable", context.getPackageName()) != 0) {
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier(name2, "drawable", context.getPackageName()));
            } else {
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier("ocn", "drawable", context.getPackageName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.d(TAG, "getImage chname :" + name2);
        }
        return dimg;
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }


}
