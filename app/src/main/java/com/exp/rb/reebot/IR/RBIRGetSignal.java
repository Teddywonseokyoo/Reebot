package com.exp.rb.reebot.IR;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.exp.rb.reebot.EPGManager;
import com.exp.rb.reebot.EPGParamData;
import com.exp.rb.reebot.view.RBSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by freem on 2017-06-11.
 */

public class RBIRGetSignal  extends AsyncTask<RBIRParamData, Integer, String> {

    private static final String TAG = "ReeBot(RBIRGetSignal)";
    private Context context;
    private RBIRGetSignalListener rbIRGetSignalListener;
    private  String filename;
    private  int mode;
    private ProgressDialog asyncDialog;
    private RBSetting.SignalCallback callback;


    public  RBIRGetSignal(Context _context)
    {
        rbIRGetSignalListener = (RBIRGetSignalListener)_context;
        context = _context;
        asyncDialog = new ProgressDialog(_context);

    }



    public  RBIRGetSignal(Context _context, RBSetting.SignalCallback callback)
    {
        rbIRGetSignalListener = (RBIRGetSignalListener)_context;
        context = _context;
        asyncDialog = new ProgressDialog(_context);
        this.callback = callback;

    }

    @Override
    protected String doInBackground(RBIRParamData... params) {
        mode = params[0].mode;
        String ret ="";

        Log.d(TAG, "doInBackground : " +params[0].mode);
        if(params[0].mode == 2)//for get list
        {
            filename = makefilename(params[0].id,params[0].location,params[0].param1);
            Log.d(TAG, "makeParameters : " + params[0].mode +   params[0].param1 + params[0].param2 );
            ret = getsiganl(params[0].url,params[0].mode,makeParameters(params[0].mode, params[0].param1, params[0].param2, params[0].param3));
        }
        else  if(params[0].mode == 1)//for get list
        {
            ret = getsiganl(params[0].url,params[0].mode,makeParameters(params[0].mode  ,params[0].param1 ,"",""));
        }
        return ret;
    }

    private String makeParameters(int mode , String param1, String param2,  String param3) {
        String ret = "";
        if(mode == 1 )//for get list
        {
            ret = "dev="+param1;
        }
        else if(mode ==2 )  //for get siganl
        {
            if(param3.isEmpty())
            {
                ret = "dev="+param1+"&brend1="+param2;
            }
            else
            {
                ret = "dev="+param1+"&brend1="+param2+"&brend2="+param3;
            }
        }
        else
        {
            ret="";
        }
        //Log.d(TAG, "makeParameters : " + ret );
        return ret;
    }

    private String getsiganl(String surl,int mode ,String params)  {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL url = null;
        String response = null;

        Log.d(TAG, "getsiganl Param : "+surl+" / " + params );
        try
        {
            url = new URL(surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(params);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            int count=0;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();

            if(response != null && mode == 2 )
            {
                String extr =context.getFilesDir().getPath().toString();
                File mFolder = new File(extr + "/irsignal");
                if (!mFolder.exists()) {
                    Log.d(TAG, "Make Folder");
                    mFolder.mkdir();
                } else {
                    Log.d(TAG, "Folder");
                }
                Log.d(TAG, "filenmae : " + filename);
                File file = new File(mFolder.getAbsolutePath(),filename);
                try
                {
                    Log.d(TAG, "JSON : " + response);
                    file.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(response);
                    myOutWriter.close();
                    fOut.flush();
                    fOut.close();
                }
                catch (IOException e)
                {
                    response = "error";
                    Log.e("Exception", "File write failed: " + e.toString());
                }
            }

            Log.d(TAG,"response : " + response);
            // You can perform UI operations here
            //Toast.makeText(context, "Message from Server: \n"+ response, Toast.LENGTH_SHORT).show();
            isr.close();
            reader.close();

        }
        catch(IOException e)
        {
            // Error
            Log.d(TAG, "queryEPGListJSON :"+e);
        }
        return response;
    }

    @Override
    protected void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if(mode == 1)
            asyncDialog.setMessage("리모컨 신호 다운로드 중입니다.");
        else
            asyncDialog.setMessage("리모컨 목록을 가져오는 중입니다.");

        // show dialog
        asyncDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {

        asyncDialog.dismiss();
        //Toast.makeText(context, "Finish Make EPG List", Toast.LENGTH_SHORT).show();
        if(mode == 1){
            rbIRGetSignalListener.RBIRGetListSignaTaskResult(result);
            if(callback != null)callback.onTaskDone(result);
        }
        else
        {
            rbIRGetSignalListener.RBIRGetSignaTaskResult(result);
            if(callback != null) callback.onTaskDone(result);
        }
        //Log.d(TAG, "Finish Make EPG List");
    }


    public interface RBIRGetSignalListener{
        public void RBIRGetSignaTaskResult(String msg);
        public void RBIRGetListSignaTaskResult(String msg);
    }

    private String makefilename(String id, String location, String devicename)
    {
        String filename = id+"_"+location+"_"+devicename+".json";
        return  filename;
    }
}
