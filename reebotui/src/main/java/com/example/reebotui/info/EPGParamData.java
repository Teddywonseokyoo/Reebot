package com.example.reebotui.info;

import com.example.reebotui.epglist.EPGListViewAdapter;

import java.io.File;

/**
 * Created by freem on 2017-03-08.
 */

public class EPGParamData {

    public int mode;
    public File xmlpath;
    public String xmlname;
    public EPGListViewAdapter adapter;
    public String url;
    public String chnum;
    public String catvb;
    public String bookmarklist;
    public String searchstring;

    public EPGParamData(int mode, String catvb) {
        this.mode = mode;
        this.catvb = catvb.replaceAll("[0-9]", "");
    }

//    public EPGParamData(int mode, String catvb, String chnum) {
//        this.mode = mode;
//        this.catvb = catvb;
//        this.chnum = chnum;
//    }
//
//    public EPGParamData(int mode, String catvb, String chnum, String searchstring) {
//        this.mode = mode;
//        this.catvb = catvb;
//        this.chnum = chnum;
//        this.searchstring = searchstring;
//    }

    public void setChnum(String chnum) {
        this.chnum = chnum;
    }

    public void setCatvb(String catvb) {
        this.catvb = catvb;
    }

    public void setBookmarklist(String bookmarklist) {
        this.bookmarklist = bookmarklist;
    }

    public void setSearchstring(String searchstring) {
        this.searchstring = searchstring;
    }


//    public EPGParamData(File xmlpath, String xmlname, EPGListViewAdapter adapter) {
//        this.xmlpath = xmlpath;
//        this.xmlname = xmlname;
//        this.adapter = adapter;
//
//    }
//
//    public EPGParamData(File xmlpath, String xmlname, EPGListViewAdapter adapter, String url, String catvb) {
//        this.xmlpath = xmlpath;
//        this.xmlname = xmlname;
//        this.adapter = adapter;
//        this.url = url;
//        this.catvb = catvb;
//    }
//
//    public EPGParamData(int mode, File xmlpath, String xmlname, EPGListViewAdapter adapter, String url, String catvb, String bookmarklist) {
//        this.bookmarklist = bookmarklist;
//        this.mode = mode;
//        this.xmlpath = xmlpath;
//        this.xmlname = xmlname;
//        this.adapter = adapter;
//        this.url = url;
//        this.catvb = catvb;
//    }
//
//    public EPGParamData(int mode, EPGListViewAdapter adapter, String url, String catvb, String searchstring) {
//        this.mode = mode;
//        this.adapter = adapter;
//        this.url = url;
//        this.catvb = catvb;
//        this.searchstring = searchstring;
//    }
//
//    public EPGParamData(File xmlpath, String xmlname, EPGListViewAdapter adapter, String url, String catvb, String chnum) {
//        this.xmlpath = xmlpath;
//        this.xmlname = xmlname;
//        this.adapter = adapter;
//        this.url = url;
//        this.catvb = catvb;
//        this.chnum = chnum;
//    }


}
