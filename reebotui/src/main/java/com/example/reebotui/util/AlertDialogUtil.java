package com.example.reebotui.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

/**
 * Created by silver on 2017-06-27.
 */

public class AlertDialogUtil {

    public static void showDialog(@NonNull Context context, String msg) {
        showDialog(context, "", msg, "", "", "확인", null);
    }

    public static void showDialog(@NonNull Context context, String title, String msg) {
        showDialog(context, title, msg, "", "", "확인", null);
    }

    public static void showDialog(@NonNull Context context, String title, String msg, String posBtn, DialogInterface.OnClickListener onClickListener) {
        showDialog(context, title, msg, "", "", posBtn, onClickListener);
    }

    public static void showDialog(@NonNull Context context, String title, String msg, String negBtn, String posBtn, DialogInterface.OnClickListener onClickListener) {
        showDialog(context, title, msg, "", negBtn, posBtn, onClickListener);
    }

    public static void showDialog(@NonNull Context context, String title, String msg, String neuBtn, String negBtn, String posBtn, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alert = builder.create();
        if (title != null && !title.isEmpty()) {
            alert.setTitle(title);
        }

        alert.setMessage(msg);

        if (onClickListener == null) {
            onClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.dismiss();
                }
            };
        }

        if (posBtn != null && !posBtn.isEmpty()) {
            alert.setButton(Dialog.BUTTON_POSITIVE, posBtn, onClickListener);
        }
        if (negBtn != null && !negBtn.isEmpty()) {
            alert.setButton(Dialog.BUTTON_NEGATIVE, negBtn, onClickListener);
        }
        if (neuBtn != null && !neuBtn.isEmpty()) {
            alert.setButton(Dialog.BUTTON_NEUTRAL, neuBtn, onClickListener);
        }

        alert.setCancelable(false);
        alert.show();
    }

}
