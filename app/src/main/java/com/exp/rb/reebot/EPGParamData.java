package com.exp.rb.reebot;

import com.exp.rb.reebot.view.EPGListViewAdapter_new;

import java.io.File;

/**
 * Created by freem on 2017-03-08.
 */

public class EPGParamData {

    public  int mode;
    public File xmlpath;
    public String xmlname;
    public EPGListViewAdapter_new adapter;
    public  String url;
    public  String chnum;
    public  String catvb;
    public  String bookmarklist;
    public  String searchstring;

    EPGParamData(File xmlpath,String xmlname,EPGListViewAdapter_new adapter)
    {
        this.xmlpath = xmlpath;
        this.xmlname = xmlname;
        this.adapter = adapter;

    }
    EPGParamData(File xmlpath,String xmlname,EPGListViewAdapter_new adapter,String url,String catvb)
    {
        this.xmlpath = xmlpath;
        this.xmlname = xmlname;
        this.adapter = adapter;
        this.url = url;
        this.catvb = catvb;
    }
    EPGParamData(int mode,File xmlpath,String xmlname,EPGListViewAdapter_new adapter,String url,String catvb,String bookmarklist)
    {
        this.bookmarklist = bookmarklist;
        this.mode = mode;
        this.xmlpath = xmlpath;
        this.xmlname = xmlname;
        this.adapter = adapter;
        this.url = url;
        this.catvb = catvb;
    }
    EPGParamData(int mode,EPGListViewAdapter_new adapter,String url,String catvb,String searchstring)
    {
        this.mode = mode;
        this.adapter = adapter;
        this.url = url;
        this.catvb = catvb;
        this.searchstring = searchstring;
    }

    public EPGParamData(File xmlpath, String xmlname, EPGListViewAdapter_new adapter, String url, String catvb, String chnum)
    {
        this.xmlpath = xmlpath;
        this.xmlname = xmlname;
        this.adapter = adapter;
        this.url = url;
        this.catvb = catvb;
        this.chnum = chnum;
    }



}
