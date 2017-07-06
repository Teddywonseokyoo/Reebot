package com.example.reebotui.info;

/**
 * Created by freem on 2017-05-15.
 */

public class RBAuthData {

    //RequestBody body = new FormBody.Builder().add("email", remail).add("password",rpass).add("location","").add("tvbrend",tvbrend).add("tvvendor",catvbrend).add("pushtoken",token).build();
    private int mode;
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


    public RBAuthData(int mode, String email, String password, String location, String tvbrend, String catvvendor, String pushtoken, String devtoken, String url, String token) {

        this.mode = mode;
        this.email = email;
        this.password = password;
        this.location = location;
        this.tvbrend = tvbrend;
        this.catvvendor = catvvendor;
        this.pushtoken = pushtoken;
        this.devtoken = devtoken;
        this.url = url;
        this.token = token;
    }

    public RBAuthData(int mode, String email, String password, String location, String tvbrend, String catvvendor, String pushtoken, String devtoken, String url, String token, String atoken) {

        this.mode = mode;
        this.email = email;
        this.password = password;
        this.location = location;
        this.tvbrend = tvbrend;
        this.catvvendor = catvvendor;
        this.pushtoken = pushtoken;
        this.devtoken = devtoken;
        this.url = url;
        this.token = token;
        this.atoken = atoken;

    }

    public int getMode() {
        return mode;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public String getTvbrend() {
        return tvbrend;
    }

    public String getCatvvendor() {
        return catvvendor;
    }

    public String getPushtoken() {
        return pushtoken;
    }

    public String getToken() {
        return token;
    }

    public String getAtoken() {
        return atoken;
    }
}
