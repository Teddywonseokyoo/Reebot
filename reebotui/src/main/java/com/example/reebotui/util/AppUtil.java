package com.example.reebotui.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

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


}
