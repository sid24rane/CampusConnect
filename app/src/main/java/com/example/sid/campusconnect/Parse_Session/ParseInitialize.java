package com.example.sid.campusconnect.Parse_Session;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Created by Sid on 28-Aug-15.
 */
public class ParseInitialize extends Application {
    public void onCreate(){
        Parse.initialize(this, "2wV9ZiYhHiiwV9ZqLBQZSYbAUBNblAQRZ7OGew8G", "QNWCkp6iGZaJPCo6XgUxyBgat2ynE3wlYiTpIQ1u");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    String p;

    public String getp(){
        return p;
    }

    public void setp(String s){
        p=s;
    }
}
