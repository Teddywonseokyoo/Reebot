package com.exp.rb.reebot.version;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.exp.rb.reebot.util.NetworkUtil;
import com.exp.rb.reebot.view.RBSetting;
import com.exp.rb.reebot.view.RBSettingData;

/**
 * Created by freem on 2017-06-22.
 */

public class RBVersionManager extends AsyncTask<String, Integer, String> {

    private static final String TAG = "ReeBot(RBSettingManager)";

    private String checkversion_url = "http://reebot.io:8083/api/getversion";
    private ProgressDialog asyncDialog;
    RBVersionManagerListener rbversionmanagerlistener;

    public  RBVersionManager(Context _context) {
        asyncDialog = new ProgressDialog(_context);
    }
    public void setListener(RBVersionManagerListener rbversionmanagerlistener)
    {
        this.rbversionmanagerlistener = rbversionmanagerlistener;
    }

    @Override
    protected String doInBackground(String...params ) {
        //String param = "id="+params[0].id+"&tv="+params[0].tv +"&catv="+params[0].catv;
        String res = NetworkUtil.sendqury(checkversion_url ,"","");
        Log.d(TAG,"Respose : " + res);
        return res;
    }

    @Override
    protected void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("버전 검사 중입니다..");
        // show dialog
        asyncDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        asyncDialog.dismiss();
        if(rbversionmanagerlistener != null) rbversionmanagerlistener.callbackevent(result);
    }

    public interface RBVersionManagerListener{
        public void callbackevent(String msg);
    }
}
