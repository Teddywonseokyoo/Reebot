package com.exp.rb.reebot.IR;

/**
 * Created by freem on 2017-03-09.
 */


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by freem on 2017-02-27.
 * Read Config file(XML) from Local storage
 */

public class VIRCommand extends AsyncTask<VIRDataValue, Integer,  String> {

    //private String sid ;
    private Context mContext;
    private static final String TAG = "ReeBot(VIRCommand)";
    VIRCommandListener virCommandListener;

    private String resultcode;

    public VIRCommand()
    {

    }

    public VIRCommand (VIRCommandMaker context){
        virCommandListener = (VIRCommandListener)context;
        //mContext = context;
    }

    public VIRCommand (VIRCommandMaker context, String sid){

        //this.sid = sid;
        virCommandListener = (VIRCommandListener)context;
        //mContext = context;
    }

    @Override
    protected String doInBackground(VIRDataValue... params) {

        return sendCommand(params[0].urlTo,params[0].protocol,params[0].hexdata , params[0].sid);
    }

    void readConfigFile()
    {

    }

    String sendCommand(String strurl,String protoco,String cmd,String sid) {

        Log.e(TAG, "Action  : TV ON"+strurl+"/"+cmd);
        URL url = null;
        try {
            String gcommand = strurl+"/command?id="+sid+"&function=run&mode=ir&protocol="+protoco+"&bit=32&khz=0&data="+cmd;
            Log.e(TAG, "Action  : "+gcommand);
            url = new URL(gcommand);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                resultcode = Integer.toString(statusCode);
                //Log.e(TAG, "statusCode  : 200");
                InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader read = new InputStreamReader(it);
                BufferedReader buff = new BufferedReader(read);
                StringBuilder dta = new StringBuilder();
                String chunks;
                while ((chunks = buff.readLine()) != null) {
                    dta.append(chunks);
                }
            } else {
                //Handle else
                resultcode = Integer.toString(statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultcode = "error";
        }
        return resultcode;
    }

    public interface VIRCommandListener{
        public void VIRCommandListener(String msg);
    }

    @Override
    protected void onPostExecute(String result) {
        virCommandListener.VIRCommandListener(resultcode);
    }

}
