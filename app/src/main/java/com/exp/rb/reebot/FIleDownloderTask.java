package com.exp.rb.reebot;

import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by freem on 2017-03-07.
 */



public class FIleDownloderTask extends AsyncTask<FileDownloadParamData, Integer, String> {

    private static final String TAG = "ReeBot(DownloderTask)";
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    FileDownloderTaskListener fileDownloderTaskListener;

    public FIleDownloderTask(Context context, EPGManager epgmanager) {
        fileDownloderTaskListener = (FileDownloderTaskListener)epgmanager;
        this.context = context;
    }

    @Override
    protected String doInBackground(FileDownloadParamData... params) {
            Log.d(TAG, "Start getDownloadEPG doInBackground");
            //url,filepath,filename)
            //mFolder.getAbsolutePath(), s
            File file = new File(params[0].filepath.getAbsolutePath(),params[0].filename);
            Date lastModifiedTime = new Date(file.lastModified());
            long diff = (new Date().getTime() - lastModifiedTime.getTime()) / 60 / 60 / 24;
            if (file.exists() && diff <= 24) {
             //   long length = file.length();
                //length = length/1024;
                //System.out.println("File Path : " + file.getPath() + ", File size : " + length +" Byte");
                //Toast.makeText(context, "Finish FIleDownloderTask", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Do not action for downloding epg file");
             //   Log.d(TAG, "File Path : " + file.getPath() + ", File size : " + length +" Byte");
            }
            else
            {
                //Toast.makeText(context, "FIleDownloderTask", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Start downloding epg file");
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                URL url = new URL(params[0].url);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }
                    if (connection != null)
                        connection.disconnect();
                }
            }
        return null;
    }

    public interface FileDownloderTaskListener{
        public void FileDownloderTaskResult(String msg);
    }
    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "Finish FIleDownloderTask", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Finish FIleDownloderTask");
        fileDownloderTaskListener.FileDownloderTaskResult(result);
    }
}
