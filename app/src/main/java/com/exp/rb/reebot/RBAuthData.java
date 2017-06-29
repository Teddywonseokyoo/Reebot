package com.exp.rb.reebot;

/**
 * Created by freem on 2017-05-15.
 */

public class RBAuthData {

    //RequestBody body = new FormBody.Builder().add("email", remail).add("password",rpass).add("location","").add("tvbrend",tvbrend).add("tvvendor",catvbrend).add("pushtoken",token).build();
    int mode;
    String email;
    String password;
    String location;
    String tvbrend;
    String catvvendor;
    String pushtoken;
    String devtoken;
    String url;
    String token;
    String atoken;


    public RBAuthData(int mode,String email,
            String password,
            String location,
            String tvbrend,
            String catvvendor,
            String pushtoken,String devtoken,String url,String token) {

        this.mode = mode;
        this.email = email;
        this.password = password;
        this.location = location;
        this.tvbrend = tvbrend;
        this.catvvendor = catvvendor;
        this.pushtoken = pushtoken;
        this.devtoken = devtoken;
        this.url = url;
        this.token = token ;
    }

    public RBAuthData(int mode,String email,
               String password,
               String location,
               String tvbrend,
               String catvvendor,
               String pushtoken,String devtoken,String url,String token, String atoken ) {

        this.mode = mode;
        this.email = email;
        this.password = password;
        this.location = location;
        this.tvbrend = tvbrend;
        this.catvvendor = catvvendor;
        this.pushtoken = pushtoken;
        this.devtoken = devtoken;
        this.url = url;
        this.token = token ;
        this.atoken = atoken;

    }

}
