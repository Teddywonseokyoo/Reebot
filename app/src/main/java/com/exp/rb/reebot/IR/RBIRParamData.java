package com.exp.rb.reebot.IR;

import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * Created by freem on 2017-06-11.
 */

public class RBIRParamData {

    public int mode;
    public String url;
    public String param1;
    public String param2;
    public String param3;
    public String location;
    public String id;
    //public ArrayAdapter<String> adapter;


    public  RBIRParamData( int mode, String url,String param1)
    {
        this.mode = mode;
        this.url = url;
        this.param1 = param1;
    }
    public  RBIRParamData( int mode, String url,String id,String location,String param1)
    {
        this.mode = mode;
        this.url = url;
        this.param1 = param1;
        this.location= location;
        this.id= id;

    }

    public  RBIRParamData( int mode,String url,String id,String location,String param1,String param2)
    {

        this.mode = mode;
        this.url = url;
        this.param1 = param1;
        this.param2 = param2;
        this.location= location;

        this.id= id;
    }

    public  RBIRParamData( int mode,String url,String id,String location,String param1,String param2,String param3)
    {
        this.mode = mode;
        this.url = url;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.location= location;
        this.id= id;
    }
}
