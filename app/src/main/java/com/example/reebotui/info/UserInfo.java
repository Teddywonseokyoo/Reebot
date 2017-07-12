package com.example.reebotui.info;

/**
 * Created by silver on 2017-07-10.
 */

public class UserInfo {
    private String _id = "";
    private String email = "";

    private String tvbrend = "";
    private String catvvendor = "";

    private String token = "";
    private String accesstoken = "";
    private String rbtoken = "";

    private String location = "";

    public UserInfo(String email, String tvbrend, String catvvendor, String token, String accesstoken, String rbtoken, String location) {
        this.email = email;
        this.tvbrend = tvbrend;
        this.catvvendor = catvvendor;
        this.token = token;
        this.accesstoken = accesstoken;
        this.rbtoken = rbtoken;
        this.location = location;
    }

    public UserInfo(String _id, String email, String tvbrend, String catvvendor, String token, String accesstoken, String rbtoken, String location) {
        this._id = _id;
        this.email = email;
        this.tvbrend = tvbrend;
        this.catvvendor = catvvendor;
        this.token = token;
        this.accesstoken = accesstoken;
        this.rbtoken = rbtoken;
        this.location = location;
    }

    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getTvbrend() {
        return tvbrend;
    }

    public String getCatvvendor() {
        return catvvendor;
    }

    public String getToken() {
        return token;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public String getRbtoken() {
        return rbtoken;
    }

    public String getLocation() {
        return location;
    }
}
