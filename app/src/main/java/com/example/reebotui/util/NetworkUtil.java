package com.example.reebotui.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by freem on 2017-06-22.
 */

public class NetworkUtil {

    private static final String TAG = "ReeBot(NetworkUtil)";

    static public String sendqury(String surl, String params, String accesstoken) {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL url = null;
        String response = "";

        Log.d(TAG, "NetworkUtil surl : " + surl);
        Log.d(TAG, "NetworkUtil Param : " + params);

        try {
            url = new URL(surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (accesstoken != null && !accesstoken.isEmpty()) {
                connection.setRequestProperty("authorization", accesstoken);
            }
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

    static public String sendQuery(String surl, String params, String accesstoken) {
        HttpURLConnection connection;
        String response = "";

        Log.d(TAG, "NetworkUtil surl : " + surl);
        Log.d(TAG, "NetworkUtil Param : " + params);

        try {
            URL url = new URL(surl);
            connection = (HttpURLConnection) url.openConnection();
            if (connection instanceof HttpsURLConnection) {
                trustAllHosts();
                connection = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        Log.d(TAG, "NetworkUtil verify s: " + s);
                        Log.d(TAG, "NetworkUtil verify sslSession: " + sslSession);
                        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                        return hv.verify("reebot.io", sslSession);
                    }
                });
            }
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (accesstoken != null && !accesstoken.isEmpty()) {
                connection.setRequestProperty("authorization", accesstoken);
            }
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(params);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            connection.connect();


            StringBuilder responseStringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (; ; ) {
                    String stringLine = bufferedReader.readLine();
                    if (stringLine == null) break;
                    responseStringBuilder.append(stringLine + '\n');
                }
                bufferedReader.close();
            }
            connection.disconnect();
            response = responseStringBuilder.toString();
            Log.d(TAG, "NetworkUtil response : " + response);
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "network error: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "network error: " + e.toString();
        }
    }

    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
