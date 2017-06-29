package com.exp.rb.reebot.IR;

/**
 * Created by freem on 2017-03-09.
 */

public class VIRDataValue {

    String urlTo;
    String protocol;
    String hexdata;
    String sid;
    public VIRDataValue(String _urlTo,String _protocol,String _hexdata, String _sid)
    {
        urlTo=_urlTo;
        protocol = _protocol;
        hexdata = _hexdata;
        sid=_sid;
    }
}
