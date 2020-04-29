package com.example.graduateproejct;

import android.app.Application;

public class MyApplication extends Application
{
    private int mGlobalValue;

    @Override
    public void onCreate() {
        mGlobalValue = 0;
        super.onCreate();
    }
    public void onTerminate() {
        super.onTerminate();
    }
    public int getGlobalValue()
    {
        return mGlobalValue;
    }

    public void setmGlobalValue(int globalValue)
    {
        this.mGlobalValue = globalValue;
    }
}