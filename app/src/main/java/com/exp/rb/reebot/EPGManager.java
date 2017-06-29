package com.exp.rb.reebot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.exp.rb.reebot.view.EPGListViewAdapter_new;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by freem on 2017-03-06.
 */



public class EPGManager  extends AsyncTask<EPGParamData, Integer, String> implements FIleDownloderTask.FileDownloderTaskListener {

    private static final String TAG = "ReeBot(EPGManager)";
    private Context context;
    private List<ChannelEntry> chentries = new ArrayList<>();
    private EPGListViewAdapter_new adapter;
    final Lock lock = new ReentrantLock();
    private boolean downloding;

    EPGManagerListener epgManagerListener;



    public EPGManager(Context _context)
    {
        epgManagerListener = (EPGManagerListener)_context;
        downloding = false;
        context = _context;
    }


    private int readFilefromXMLEPG(String epfxmlpath)
    {
        return 1;
    }

    //Download file
    public int getDownloadEPG(String url,File filepath,String filename)
    {
        Log.d(TAG, "Start getDownloadEPG");
        // declare the dialog as a member field of your activity
        //ProgressDialog mProgressDialog;
        // instantiate it within the onCreate method
        //mProgressDialog = new ProgressDialog(context);
        //mProgressDialog.setMessage("A message");
        //mProgressDialog.setIndeterminate(true);
        //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //mProgressDialog.setCancelable(true);
        // execute this when the downloader must be fired
        final FIleDownloderTask downloadTask = new FIleDownloderTask(context,this);
        downloadTask.execute(new FileDownloadParamData(url,filepath,filename));
        /*
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
        */
        return 1;
    }

    private static final String ns = null;

    @Override
    protected String doInBackground(EPGParamData... params) {
        Log.d(TAG, "Start doInBackground");
        chlistpaser(params[0].xmlpath,params[0].xmlname);
        adapter =  params[0].adapter;
        programlistliatpaser(params[0].xmlpath,params[0].xmlname, params[0].adapter);
        return null;
    }

    public List chlistpaser(File xmlpath, String xmlname) {
        FileInputStream fis ;
        File myXML = new File(xmlpath.getAbsolutePath(), xmlname); // give proper path
        try {
            fis = new FileInputStream(myXML);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            parser.nextTag();
            readChannelInfo(parser);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int programlistliatpaser(File xmlpath,String xmlname,EPGListViewAdapter_new adapter) {
        FileInputStream fis = null;
        File myXML = new File(xmlpath.getAbsolutePath(),xmlname); // give proper path
        try {
            fis = new FileInputStream(myXML);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            parser.nextTag();
            readProgramlInfo(parser, adapter);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private List readChannelInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
        //List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "tv");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("channel")) {
                //Log.d(TAG, "chentries size  : " + Integer.toString(chentries.size()));
                chentries.add(readChEntry(parser));
            }
            else {
                skip(parser);
            }
        }
        //Log.d(TAG, "chentries size  : " + Integer.toString(chentries.size()));
        return chentries;
    }

    private int readProgramlInfo(XmlPullParser parser,EPGListViewAdapter_new adapter) throws XmlPullParserException, IOException {
        //ADD Program
        parser.require(XmlPullParser.START_TAG, ns, "tv");
        int ch_count = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            //Log.d(TAG, "readProgramlInfo name + :" + name);
            // Starts by looking for the entry tag
            if (name.equals("programme")) {
                //readProgramEntry(parser);
                ProgramInfoEntry thisentry = readProgramEntry(parser);
                String title =  thisentry.program_title + " " + thisentry.episode_num;
                String entryInfo = thisentry.category_kr + " / "+thisentry.chnumber ;

                //Log.d(TAG, title + " / " + entryInfo);

                //thisentry.end_time;
                Date stime =stringToDate( thisentry.start_time,"yyyyMMddHHmmss");
                Date etime =stringToDate( thisentry.end_time,"yyyyMMddHHmmss");

                long curtime = new Date().getTime();
                if(stime.getTime() <= curtime && etime.getTime() >= curtime )
                {
                    ch_count++;
                    //Log.d(TAG, title + " / " + entryInfo + "/ time : "+ stime +" ~ " + etime +" |     "+stime.getTime()  +" |     "+etime.getTime());
                    adapter.addItem(getImage(context,thisentry.chlogo),ContextCompat.getDrawable(context, R.drawable.bookmark), thisentry.program_title, entryInfo ,thisentry.chnumber,thisentry.category_kr,thisentry.category_en, "start", "end","") ;
                    if(ch_count%10 == 0){
                        //Log.d(TAG, title + " / " + entryInfo + "/ time : "+ stime +" ~ " + etime +" |     "+stime.getTime()  +" |     "+etime.getTime());
                        //adapter.notifyDataSetChanged();
                    }
                }
            } else {
                skip(parser);
            }
        }
        return 1;
    }
    private Date stringToDate(String aDate, String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }



    public static class ChannelEntry {
        public final String chname;
        public final String chnumber;
        public final String chid;

        private ChannelEntry(String chname, String chid, String chnumber) {
            this.chname = chname;
            this.chid = chid;
            this.chnumber = chnumber;
        }
    }

    private ChannelEntry readChEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "channel");
        String channel_title = null;
        String channel_num = null;
        String channel_id  = parser.getAttributeValue(null, "id");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("display-name")) {
                channel_title = readChTitleTitle(parser);
                //Log.d(TAG, "channel_num : " + channel_title);
            } else if (name.equals("display-number")) {
                channel_num = readChNumber(parser);
                //Log.d(TAG, "channel_num : " + channel_num);
            }  else {
                skip(parser);
            }
        }
        return new ChannelEntry(channel_title,channel_id, channel_num);
    }

    // Processes title tags in the feed.
    private String readChTitleTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "display-name");
        String channel_title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "display-name");
        return channel_title;
    }

    // Processes link tags in the feed.
    private String readChNumber(XmlPullParser parser) throws IOException, XmlPullParserException {
        //String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "display-number");
        String channel_number = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "display-number");
        return channel_number;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    public static class ProgramInfoEntry {


        public final String start_time ;
        public final String end_time;
        public final String program_title;
        public final String category_kr;
        public final String category_en;
        public final String episode_num;
        public final String rating_value;
        public final String channel_id;
        public final String chnumber;
        public final String chlogo;

        private ProgramInfoEntry(String channel_id, String start_time,String end_time,String program_title,String category_kr,String category_en,String episode_num,String rating_value,List<ChannelEntry> chentries) {
            this.start_time = start_time;
            this.end_time = end_time;
            this.program_title = program_title;
            this.category_kr = category_kr;
            this.category_en = category_en;
            this.episode_num = episode_num;
            this.rating_value = rating_value;
            this.channel_id = channel_id;
            int num = Integer.parseInt(channel_id);

            String _chnumber="";
            String _chlogo="";

            for (ChannelEntry ent : chentries) {
                if(ent.chid.equals(channel_id)){
                    _chlogo = ent.chname;
                    _chnumber = ent.chnumber;
                }
            }
            chlogo = _chlogo;
            chnumber = _chnumber;
        }
    }

    private ProgramInfoEntry readProgramEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "programme");
        String channel_id = null;
        String start_time = null;
        String end_time = null;
        String program_title = null;
        String category_kr = null;
        String category_en = null;
        String episode_num = null;
        String ratingvalue = null;
        start_time = parser.getAttributeValue(null, "start");
        //Log.d(TAG, "start time : "+start_time);
        end_time = parser.getAttributeValue(null, "stop");
        //Log.d(TAG, "end time : "+end_time);
        channel_id = parser.getAttributeValue(null, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                 program_title = readProgram_title(parser);
                 //Log.d(TAG, "title : "+program_title);
            } else if (name.equals("category")&& parser.getAttributeValue(null, "lang").equals("kr")) {
                category_kr = readCategory(parser);
                //Log.d(TAG, "category_kr : "+category_kr);
            }else if (name.equals("category")&& parser.getAttributeValue(null, "lang").equals("en")) {
                category_en = readCategory(parser);
                //Log.d(TAG, "category_en : "+category_en);
            }else if (name.equals("episode-num")) {
                episode_num = readEpisodenum(parser);
                //Log.d(TAG, "episode_num : "+episode_num);
            //}else if (name.equals("rating")) {
            //    ratingvalue = readRatingvalue(parser);
            //    Log.d(TAG, "rating : "+ratingvalue);
                //XmlPullParser parser_prenext = parser;
                //parser_prenext.next();
                //String tagname = parser_prenext.getName();
                //if (name.equals("value")) {
                //    ratingvalue = readRatingvalue(parser_prenext);
                //    Log.d(TAG, "Start readProgramEntry : "+ratingvalue);
                //}
            }
            else {
                skip(parser);
            }
        }
        return new ProgramInfoEntry(channel_id, start_time, end_time, program_title, category_kr, category_en,episode_num, ratingvalue,chentries);
        //return null;
    }

    private String readProgram_title(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String program_title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return program_title;
    }
    private String readCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "category");
        String category = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "category");
        return category;
    }
    private String readEpisodenum(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "episode-num");
        String episode_num = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "episode-num");
        return episode_num;
    }
    private String readRatingvalue(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "value");
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "value");
        return value;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    public int makeListOntime()
    {

        return 1;
    }
    public static Drawable getImage(Context context, String name) {
        if(Character.isDigit(name.charAt(0))){
            name = name.substring(1);
        }
        name = name.toLowerCase();
        Drawable dimg = null;
        if( name.matches("[a-zA-Z0-9]+")) {
            if(context.getResources().getIdentifier(name, "drawable", context.getPackageName()) != 0 ) {
                Log.d(TAG, "getImage chname :" + name);
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
            }
            else {
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier("ocn", "drawable", context.getPackageName()));
            }
        }
        else
        {
            dimg = context.getResources().getDrawable(context.getResources().getIdentifier("ocn", "drawable", context.getPackageName()));
        }
        return dimg;
    }
    public static Drawable getImage(Context context, String name,String name2) {
        //Log.d(TAG, "getImage chname :" + name2);
        Drawable dimg = null;
        name2 = name2.toLowerCase();

        try{
            if(context.getResources().getIdentifier(name2, "drawable", context.getPackageName()) != 0 ) {
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier(name2, "drawable", context.getPackageName()));
            }
            else
            {
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier("ocn", "drawable", context.getPackageName()));
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "getImage chname :" + name2);
        }

        /*
        if(Character.isDigit(name.charAt(0))){
            name = name.substring(1);
        }
        name = name.toLowerCase();
        name2 = name2.toLowerCase();
        Drawable dimg = null;
        if( name.matches("[a-zA-Z0-9]+")) {
            if(context.getResources().getIdentifier(name, "drawable", context.getPackageName()) != 0 ) {
                Log.d(TAG, "getImage chname :" + name);
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
            }
            else {
                if(context.getResources().getIdentifier(name2, "drawable", context.getPackageName()) != 0 ) {
                    //Log.d(TAG, "getImage chname :" + name2);
                    dimg = context.getResources().getDrawable(context.getResources().getIdentifier(name2, "drawable", context.getPackageName()));
                }
                else{
                    dimg = context.getResources().getDrawable(context.getResources().getIdentifier("ocn", "drawable", context.getPackageName()));
                }
            }
        }
        else
        {
            if(context.getResources().getIdentifier(name2, "drawable", context.getPackageName()) != 0 ) {
                Log.d(TAG, "getImage chname :" + name2);
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier(name2, "drawable", context.getPackageName()));
            }
            else{
                dimg = context.getResources().getDrawable(context.getResources().getIdentifier("ocn", "drawable", context.getPackageName()));
            }
        }
        */
        return dimg;
    }
    @Override
    protected void onPostExecute(String result) {
        adapter.notifyDataSetChanged();
        Toast.makeText(context, "Finish Make EPG List", Toast.LENGTH_SHORT).show();

        epgManagerListener.EPGManagerTaskResult("END|MAKEEPG");
        //Log.d(TAG, "Finish Make EPG List");
    }

    @Override
    public void FileDownloderTaskResult(String msg) {
        epgManagerListener.EPGManagerTaskResult("END|EPGDOWN");
    }

    public interface EPGManagerListener{
        public void EPGManagerTaskResult(String msg);
    }
}
