package com.gaitroid;

import android.app.Application;

public class MyApplication extends Application {

    private String BaseAPIPath = "http://192.168.1.100:3000/api/";

    public String getBaseAPIPath() {
        return BaseAPIPath;
    }

//    public void setSomeVariable(String someVariable) {
//        this.someVariable = someVariable;
//    }
}
