package com.exp.rb.reebot;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.exp.rb.reebot.view.RBSetting;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by freem on 2017-05-15.
 */

public class RBAuthManager extends AsyncTask<RBAuthData, Integer, String> {

    private static final String TAG = "ReeBot(RBAuthManager)";
    RBAuthManagerListener rbAuthManagerListener;
    private int mode = 0;
    private ProgressDialog asyncDialog;

    private RBSetting.AuthCallback callback;

    public  RBAuthManager(Context _context) {
        rbAuthManagerListener = (RBAuthManagerListener) _context;
        asyncDialog = new ProgressDialog(_context);
    }

    public  RBAuthManager(Context _context,RBSetting.AuthCallback callback) {
        rbAuthManagerListener = (RBAuthManagerListener) _context;
        asyncDialog = new ProgressDialog(_context);
        this.callback = callback;
    }



    @Override
    protected String doInBackground(RBAuthData... params) {
        Log.d(TAG, "Start doInBackground12 : " +  params[0].mode);
        mode = params[0].mode;
        String ret = null;
        if(mode == 2) //일반 회원 가입
        {
            ret=regMember(makeparam(params[0].email,params[0].password,params[0].location,params[0].tvbrend,params[0].catvvendor,params[0].pushtoken),params[0].url,params[0].token);
        }
        else if(mode == 3) {
            ret=regMember(makeparam(params[0].email,params[0].password,params[0].location,params[0].tvbrend,params[0].catvvendor,params[0].pushtoken, params[0].devtoken) ,params[0].url,params[0].token);
        }
        else if(mode == 4) {
            ret=regMember("",params[0].url,params[0].token);
        }
        else if(mode == 5) {
            String param = "email="+params[0].email;
            ret=regMember(param,params[0].url,params[0].token);
        }
        else if(mode == 6) //카카오 회원 가입
        {
            ret=regMember(makeparam(params[0].email,"",params[0].location,params[0].tvbrend,params[0].catvvendor,params[0].pushtoken),params[0].url,params[0].atoken);
        }
        else
        {
            ret=regMember(makeparam(params[0].email,params[0].password),params[0].url,params[0].token);
        }
        if(ret == null)
            return "error";
        else
            return ret;
    }

    private String makeparam(String email, String password, String location, String tvbrend, String catvvendor, String pushtoken)
    {
        if(mode == 6)
        {
            return  "email="+email+"&password="+password+"&location="+location+"&tvbrend="+tvbrend+"&catvvendor="+catvvendor+"&pushtoken="+pushtoken+"&authtype=kakao";
        }
        else
        {
            return  "email="+email+"&password="+password+"&location="+location+"&tvbrend="+tvbrend+"&catvvendor="+catvvendor+"&pushtoken="+pushtoken;
        }

    }

    private String makeparam(String email, String password)
    {
        return "email="+email+"&password="+password;
    }
    private String makeparam(String email, String password, String location, String tvbrend, String catvvendor, String pushtoken,String devtoken)
    {
        return  "email="+email+"&password="+password+"&location="+location+"&tvbrend="+tvbrend+"&catvvendor="+catvvendor+"&pushtoken="+pushtoken+"&devtoken="+devtoken;
    }
    private String regMember(String params ,String surl,String acesstoken)  {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL url = null;
        String response = null;
        Log.d(TAG, "regMember Param : "+surl+"?"+ params );
        try
        {
            url = new URL(surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if(acesstoken != null) connection.setRequestProperty("authorization", acesstoken);
            connection.setRequestMethod("POST");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(params);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            Log.d(TAG, "response :"+response);
            // You can perform UI operations here
            //Toast.makeText(context, "Message from Server: \n"+ response, Toast.LENGTH_SHORT).show();
            isr.close();
            reader.close();
        }
        catch(IOException e)
        {
            response ="error";
            // Error
            Log.d(TAG, "regMember :"+e);
        }

        return response;
    }

    public interface RBAuthManagerListener{
        public void RBAuthSinginTaskResult(String msg);
        public void RBAuthLoginTaskResult(String msg);
        public void RBAuthCheckdevTaskResult(String msg);
        public void RBAuthChecktokenTaskResult(String msg);
    }

    @Override
    protected void onPostExecute(String result) {
        asyncDialog.dismiss();
        if(mode == 2) {
            rbAuthManagerListener.RBAuthSinginTaskResult(result);
        }
        else if(mode == 3){
            rbAuthManagerListener.RBAuthCheckdevTaskResult(result);
        }
        else if(mode == 4)
        {
            rbAuthManagerListener.RBAuthChecktokenTaskResult(result);
            if(callback != null) callback.onTaskDone(result);
        }
        else if(mode == 5)
        {
            rbAuthManagerListener.RBAuthChecktokenTaskResult(result);
        }
        else if(mode == 6) {
            rbAuthManagerListener.RBAuthSinginTaskResult(result);
        }
        else
        {
            Log.d(TAG, "RBAuthLoginTaskResult : "+result);
            rbAuthManagerListener.RBAuthLoginTaskResult(result);
        }
    }

    @Override
    protected void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if(mode == 3)
            asyncDialog.setMessage("장치 연결 중입니다.");
        if(mode == 3)
            asyncDialog.setMessage("회원 가입 중입니다..");
        else
            asyncDialog.setMessage("인증 요청 중입니다.");
        // show dialog
        asyncDialog.show();
        super.onPreExecute();
    }
}
