package com.exp.rb.reebot.IR;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by freem on 2017-06-12.
 *
 *

 */

public class RBSignalForJSON  {

    private static final String TAG = "ReeBot(RBSignalForJSON)";
    private volatile static RBSignalForJSON uniqueInstance;
    final String signalpath  = "/irsignal";


    public JSONObject devjson;

    /*
    public static RBSignalForJSON getInstance(Context context, String email, String location, String devicename) {
        if(uniqueInstance == null) { // 인스턴스가 있는지 확인
            synchronized(RBSignalForJSON.class) { // 없으면 동기화 블럭으로 들어감
                if(uniqueInstance == null) { // 블럭으로 들어온 후에도 다시 한번 변수가 null인지 확인
                    uniqueInstance = new RBSignalForJSON( context,  email,  location,  devicename); // null이면 인스턴스 생성
                }
            }
        }
        return uniqueInstance;
    }
    */

    public RBSignalForJSON(Context context, String email, String location, String devicename)
    {
        try {
            String extr = context.getFilesDir().getPath().toString();
            File mFolder = new File(extr + "/irsignal");
            Log.d(TAG,"SignalForJSON File : " + makefilename(email.split("@")[0],location , devicename));
            File jfile = new File(mFolder.getAbsolutePath(),makefilename(email.split("@")[0], location, devicename) );
            devjson = loadJSONFromAsset(jfile);
        }
        catch (Exception e)
        {
            Log.d(TAG,"RBSignalForJSON : " + e);
        }
    }
    private JSONObject loadJSONFromAsset(File in) {
        JSONObject object=null;
        String temp="";
        try {
            FileInputStream fis = new FileInputStream(in);
            int size = 0;
            size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            temp = new String(buffer, "UTF-8");
            Log.d(TAG,"signal : "+ temp);
            JSONArray jArray = new JSONArray(temp);
            object = jArray.getJSONObject(0);
         } catch (IOException ex) {
            ex.printStackTrace();
            //return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private String makefilename(String id, String location, String devicename)
    {
        String filename = id+"_"+location+"_"+devicename+".json";
        return  filename;
    }


}
