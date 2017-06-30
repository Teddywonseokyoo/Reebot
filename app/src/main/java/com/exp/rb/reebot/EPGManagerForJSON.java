package com.exp.rb.reebot;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.exp.rb.reebot.view.EPGListViewAdapter_new;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.format;

/**
 * Created by freem on 2017-04-18.
 */

//http://reebot.io:8083/api/req_nextepg2json?catvb=epgforLG&chnum=11&reqtime=20170507234400

public class EPGManagerForJSON extends EPGManager  {

    private static final String TAG = "ReeBot(EPGManager)";
    private Context context;
    private EPGListViewAdapter_new adapter;
    EPGManagerListener epgManagerListener;
    private ProgressDialog asyncDialog;
    private String catv;




    public EPGManagerForJSON(Context _context) {
        super(_context);
        epgManagerListener = (EPGManagerListener)_context;
        context = _context;
        asyncDialog = new ProgressDialog(_context);
    }

    @Override
    protected String doInBackground(EPGParamData... params) {
        Log.d(TAG, "Start doInBackground for EPGManagerForJSON");
        //progressDialog = new ProgressDialog(context);
        //progressDialog.setMessage("채널정보 가져오는 중");
        //progressDialog.show();
        catv = params[0].catvb;
        String retmsg ="false";
        adapter =  params[0].adapter;
        String param ="";
        if(params[0].mode == 4)
        {
            param = makeParameters(params[0].mode,params[0].catvb,params[0].bookmarklist);
            if(param != null) {
                String resdata = queryEPGListJSON(params[0].url, param );
                if( resdata == null)
                {

                }
                else
                {
                    if(readProgramlInfo(params[0].mode,resdata ,adapter))
                        retmsg ="true";
                }
            }
            else {
            }
        }
        else if(params[0].mode == 5) //search program
        {
            param = makeParameters(params[0].mode,params[0].catvb,params[0].searchstring);
            if(param != null) {
                String resdata = queryEPGListJSON(params[0].url, param );
                if( resdata == null)
                {

                }
                else
                {
                    if(readProgramlInfo(params[0].mode,resdata ,adapter))
                        retmsg ="true";
                }
            }
            else {
            }

        }
        else
        {
            if( params[0].chnum != null )
            {
                param = makeParameters(params[0].catvb,params[0].chnum);
                if(param != null) {
                    String resdata = queryEPGListJSON(params[0].url, param );
                    if( resdata == null)
                    {

                    }
                    else
                    {
                        // if(readProgramlInfo(resdata ,adapter))
                        retmsg ="true";
                    }
                }
                else {
                }
            }
            else
            {
                param = makeParameters(params[0].catvb);
                if(param != null) {
                    String resdata = queryEPGListJSON(params[0].url, param );
                    if( resdata == null)
                    {

                    }
                    else
                    {
                        if(readProgramlInfo(params[0].mode,resdata ,adapter))
                            retmsg ="true";
                    }
                }
                else {
                }
            }
        }

        return retmsg;
    }

    private String makeParameters(String catvb) {
        String ret = null;
        Date date = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormatter.format(date).toString();
        //String []catv = catvb.split("_");
        //수정 필요 (통합)
        //ret = "catvb=epgfor"+catv[1]+"&reqtime="+dateFormatter.format(date).toString();
        ret = "catvb="+catvb+"&reqtime="+dateFormatter.format(date).toString();
        Log.d(TAG, "queryEPGListJSON :"+ret);
        return ret;
    }

    private String makeParameters(String catvb,String chnum) {
        String ret = null;
        Date date = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormatter.format(date).toString();
        String []catv = catvb.split("_");
        //수정 필요 (통합)
//        ret = "catvb=epgfor"+catv[1]+"&reqtime=20170524200000"+"&chnum="+chnum;
        ret = "catvb=epgfor"+catv[1]+"&reqtime="+dateFormatter.format(date).toString()+"&chnum="+chnum;
        Log.d(TAG, "queryEPGListJSON :"+ret);
        return ret;
    }
    private String makeParameters(int mode,String param1,String param2) {

        String ret = null;
        Date date = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormatter.format(date).toString();
        //String []catv = param1.split("_");
        String catv = param1;
        if(mode == 4)
        {
            //수정 필요 (통합)
            //ret = "catvb=epgfor"+catv[1]+"&reqtime=20170524200000"+"&chnum="+chnum;
            ret = "catvb="+catv+"&reqtime="+dateFormatter.format(date).toString()+"&fchnum="+param2;
            //ret = "catvb=epgfor"+catv[1]+"&reqtime="+dateFormatter.format(date).toString()+"&fchnum="+param2;
        }
        else if(mode == 5)
        {
            //ret = "catvb=epgfor"+catv[1]+"&reqtime="+dateFormatter.format(date).toString()+"&&search="+param2;
            ret = "catvb="+catv+"&reqtime="+dateFormatter.format(date).toString()+"&&search="+param2;
        }
        Log.d(TAG, "queryEPGListJSON :"+ret);

        return ret;
    }

    private String queryEPGListJSON(String surl,String params)  {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL url = null;
        String response = null;

        Log.d(TAG, "queryEPGListJSON Param : " + params );
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
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            Log.d(TAG,"response : " + response);
            isr.close();
            reader.close();

        }
        catch(IOException e)
        {

            Log.d(TAG, "queryEPGListJSON :"+e);
        }
        return response;
    }

    private boolean readProgramlInfo(int mode,String epgjson ,EPGListViewAdapter_new adapter) {
        boolean ret = false;
        Log.d(TAG, "Response JSON :"+epgjson);
        try {
            //JSONObject jObject = new JSONObject(epgjson);
            JSONArray jArray = new JSONArray(epgjson);
            if(jArray.length() == 0) {

            }
            else {
                for (int i=0; i < jArray.length(); i++)
                {

                    String chnum = "";
                    String chname = "";
                    String chlogoname = "";
                    String maintitle = "";
                    String subtitle = "";
                    String category = "";
                    String starttime = "";
                    String endtime = "";
                    String episode = "";
                    String title = "";
                    Log.d(TAG,"mode : " + mode);
                    if(mode == 5 || mode == 4 )
                    {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        JSONObject channel = oneObject.getJSONObject("channel");
                        JSONObject program = channel.getJSONObject("program");
                        JSONObject isplist = channel.getJSONObject("isplist");
                        JSONObject isp = isplist.getJSONObject(catv);
                        chnum = Integer.toString(isp.getInt("chnum"));
                        chname = channel.getString("name");
                        chlogoname = channel.getString("imglogoname");
                        maintitle = program.getString("maintitle");
                        subtitle = program.getString("subtitle");
                        category = program.getString("category");
                        starttime = program.getString("starttime");
                        endtime = program.getString("endtime");
                        episode = program.getString("episode");
                        title = maintitle;
                    }
                    else
                    {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        //JSONObject channel = oneObject.getJSONObject("channel");
                        //JSONObject program = channel.getJSONObject("program");
                        //JSONObject isplist = channel.getJSONObject("isplist");
                        //JSONObject isp = isplist.getJSONObject(catv);
                        chnum = oneObject.getString("chnum");
                        //String chnum = Integer.toString(isp.getInt("chnum"));
                        chname = oneObject.getString("chname");
                        chlogoname = oneObject.getString("imglogoname");
                        maintitle = oneObject.getString("maintitle");
                        subtitle = oneObject.getString("subtitle");
                        category = oneObject.getString("category");
                        starttime = oneObject.getString("starttime");
                        endtime = oneObject.getString("endtime");
                        episode = oneObject.getString("episode");
                        title = maintitle;

                    }

                    if(!(subtitle.isEmpty()))
                    {
                        title = maintitle +" ["+subtitle+"]";
                    }
                    if (episode != null && episode.trim().length() > 0) {
                        title += " (" + episode + "화)";
                    }
                    if(chlogoname.isEmpty()) {
                        chlogoname="null";
                    }

                    String entryInfo = category + " / "+ chnum ;
                    String  chlongo = chname.toString();
                    chlongo = chlongo.replace(" ", "");
                    Log.d(TAG, "maintitle : ("+ chnum +")" +  maintitle+" / IMG : "+ chname + "/ IMG2 : "+ chlogoname);
                    adapter.addItem(getImage(context,chlongo,chlogoname), ContextCompat.getDrawable(context, R.drawable.unbookmark), title, entryInfo ,chnum,category,category, starttime, endtime,chname) ;
                    //Log.d(TAG, "maintitle : ("+ chnum +")" +  maintitle+" / IMG : "+ chname + "/ IMG2 : "+ chlogoname);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("편성표 가져오기 중입니다.");
        // show dialog
        asyncDialog.show();
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(String result) {
        asyncDialog.dismiss();
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onPostExecuter EPGManagerForJSON");
        //Toast.makeText(context, "Finish Make EPG List", Toast.LENGTH_SHORT).show();
        epgManagerListener.EPGManagerTaskResult("END|MAKEEPG");
    }
}
