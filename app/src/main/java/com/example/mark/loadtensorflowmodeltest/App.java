package com.example.mark.loadtensorflowmodeltest;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by mark on 2017/10/10.
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalytics.getInstance(this);
    }
}
