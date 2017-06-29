package com.exp.rb.reebot;

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
 * Created by freem on 2017-04-04.
 */

public class RBEnvConnector extends AsyncTask<String, Integer,  String> {

    private static final String TAG = "ReeBot(RBEnvConnector)";
    private String resultcode;
    private String command;
    RBEnvConnectorListener rbenvConnectorListener;
    RBSetConnectorListener rbSetConnectorListener;
    RBRegCheckerListener rbRegCheckerListener;

    public RBEnvConnector (Context context , String command){
        this.command = command;
        rbenvConnectorListener = (RBEnvConnectorListener)context;
        rbSetConnectorListener = (RBSetConnectorListener)context;
        rbRegCheckerListener = (RBRegCheckerListener) context;
    }

    @Override
    protected String doInBackground(String... params) {
        return sendCommand(params[0]);
    }

    //Request to get ssid data from Reebot
    private String sendCommand(String strurl) {
        Log.e(TAG, "SendCommand : "+ strurl);
        URL url = null;
        String retdata="";
        try {
            //String gcommand = strurl+"/command?id="+sid+"&function=run&mode=ir&protocol="+protoco+"&bit=32&khz=0&data="+cmd;
            //Log.e(TAG, "Action  : "+gcommand);
            url = new URL(strurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                resultcode = Integer.toString(statusCode);
                //Log.e(TAG, "statusCode  : 200");
                InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader read = new InputStreamReader(it);
                BufferedReader buff = new BufferedReader(read);
                String chunks;
                StringBuilder dta = new StringBuilder();
                while ((chunks = buff.readLine()) != null) {
                    dta.append(chunks);
                }
                retdata = dta.toString();
                Log.e(TAG, "chunks : "+dta);

            } else {
                //Handle else
                resultcode = Integer.toString(statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException : "+e );
            resultcode = "timeout";
            return retdata;
        }
        return retdata;
    }

    public interface RBEnvConnectorListener{
        public void RBEnvConnectorTaskResult(String msg);
    }

    public interface RBSetConnectorListener{
        public void RBSetConnectorTaskResult(String msg);
    }
    public interface RBRegCheckerListener{
        public void RBRegCheckerTaskResult(String msg);
    }
    @Override
    protected void onPostExecute(String result) {
        if(command.equals("getssid")) {
            if (resultcode.equals("200")) {
                rbenvConnectorListener.RBEnvConnectorTaskResult(result);
            } else
                rbenvConnectorListener.RBEnvConnectorTaskResult("error_" + resultcode);
        }
        else if(command.equals("setwifi")){
            if (resultcode.equals("200")) {
                rbSetConnectorListener.RBSetConnectorTaskResult(result);
            } else
                rbSetConnectorListener.RBSetConnectorTaskResult("error_" + resultcode);
        }
        else if(command.equals("checkreg")){

            try {
                Thread.sleep(2000);
                if (resultcode.equals("200")) {
                    rbRegCheckerListener.RBRegCheckerTaskResult(result);
                } else
                    rbRegCheckerListener.RBRegCheckerTaskResult("error_" + resultcode);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {

        }
    }
}
