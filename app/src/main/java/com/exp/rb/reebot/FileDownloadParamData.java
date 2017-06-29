package com.exp.rb.reebot;

import java.io.File;

/**
 * Created by freem on 2017-03-09.
 */

public class FileDownloadParamData {
    public String url;
    public File filepath;
    public String filename;

    //url,filepath,filename
    FileDownloadParamData(String url,File filepath, String filename) {
        this.url = url;
        this.filepath = filepath;
        this.filename = filename;

    }
}
