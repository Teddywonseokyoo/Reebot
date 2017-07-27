package com.example.reebotui.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.reebotui.interfaceclass.ApiManagerListener;
import com.example.reebotui.util.NetworkUtil;

/**
 * Created by silver on 2017-06-27.
 */

public class ApiManager extends AsyncTask<String, String, String> {

    private static final String TAG = "ReeBot(ApiManager)";

    private ApiManagerListener apiManagerListener;
    private ProgressDialog progressDialog;
    private String dialogMsg = "";

    public ApiManager(Context context) {

        try {
            if (context != null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute() {
        try {
            if (progressDialog != null) {
                progressDialog.setMessage(dialogMsg);
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "params[0] : " + params[0]);
        Log.d(TAG, "params[1] : " + params[1]);
        Log.d(TAG, "params[2] : " + params[2]);
        String respose = NetworkUtil.sendQuery(params[0], params[1], params[2]);
        Log.d(TAG, "respose : " + respose);
        return respose;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (apiManagerListener != null) {
            apiManagerListener.onRespose(s);
        }

        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDialogMsg(String dialogMsg) {
        this.dialogMsg = dialogMsg;
    }

    public void setApiManagerListener(ApiManagerListener apiManagerListener) {
        this.apiManagerListener = apiManagerListener;
    }
}
