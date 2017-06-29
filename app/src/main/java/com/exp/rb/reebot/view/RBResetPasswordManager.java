package com.exp.rb.reebot.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.exp.rb.reebot.InitActivity;
import com.exp.rb.reebot.util.NetworkUtil;

/**
 * Created by freem on 2017-06-27.
 */



public class RBResetPasswordManager extends AsyncTask<String, Integer, String> {

    private static final String TAG = "ReeBot(RBSettingManager)";

    private String resetpasswd_url = "http://reebot.io:8083/auth_api/pwchange";
    private ProgressDialog asyncDialog;
    private InitActivity.ChangeCallback callback;


    public  RBResetPasswordManager(Context _context, InitActivity.ChangeCallback callback, String accesstoken) {
        asyncDialog = new ProgressDialog(_context);
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String...params ) {
        String param = "email="+params[0];
        String res = NetworkUtil.sendqury(resetpasswd_url,param,"");
        Log.d(TAG,"Respose : " + res);
        return res;
    }

    @Override
    protected void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("비밀번호 재설정 메일 요청중.");
        // show dialog
        asyncDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        asyncDialog.dismiss();
        if(callback != null) callback.onTaskDone(result);
    }
}
