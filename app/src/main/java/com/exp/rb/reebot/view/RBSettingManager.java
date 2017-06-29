package com.exp.rb.reebot.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.exp.rb.reebot.util.NetworkUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by freem on 2017-06-21.
 */

public class RBSettingManager extends AsyncTask<RBSettingData, Integer, String> {

    private static final String TAG = "ReeBot(RBSettingManager)";

    private String changeservice_url = "http://reebot.io:8083/api/changeservice";
    private ProgressDialog asyncDialog;
    private RBSetting.ChangeCallback callback;
    private String accesstoken;

    public  RBSettingManager(Context _context,RBSetting.ChangeCallback callback,String accesstoken) {
        asyncDialog = new ProgressDialog(_context);
        this.callback = callback;
        this.accesstoken = accesstoken;
    }

    @Override
    protected String doInBackground(RBSettingData...params ) {
        String param = "id="+params[0].id+"&tv="+params[0].tv +"&catv="+params[0].catv;
        String res =NetworkUtil.sendqury(changeservice_url ,param,accesstoken);
        Log.d(TAG,"Respose : " + res);
        return res;
    }

    @Override
    protected void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("서비스 업데이트 중입니다.");
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
