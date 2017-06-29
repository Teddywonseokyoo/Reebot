package com.exp.rb.reebot.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by freem on 2017-06-22.
 */

public class NetworkUtil {

    private static final String TAG = "ReeBot(NetworkUtil)";

    static public String  sendqury(String surl,String params ,String accesstoken)
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL url = null;
        String response = null;

        Log.d(TAG, "queryBookingListJSON Param : " + params + surl);
        try {
            url = new URL(surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if(accesstoken != null) connection.setRequestProperty("authorization", accesstoken);
            connection.setRequestMethod("POST");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(params);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            //Log.d(TAG, "response : " + response);
            // You can perform UI operations here
            //Toast.makeText(context, "Message from Server: \n"+ response, Toast.LENGTH_SHORT).show();
            isr.close();
            reader.close();

        } catch (IOException e) {
            // Error
            Log.d(TAG, "queryEPGListJSON :" + e);
        }
        return response;

    }
}
