package com.example.graduateproejct;

import android.app.Application;

public class MyApplication extends Application
{
    private int mGlobalValue;
    private String score;
    @Override
    public void onCreate() {
        mGlobalValue = 0;
        score = " ";
        super.onCreate();
    }
    public void onTerminate() {
        super.onTerminate();
    }
    public int getGlobalValue()
    {
        return mGlobalValue;
    }
    public String getScore(){return score;}

    public void setmGlobalValue(int globalValue)
    {
        this.mGlobalValue = globalValue;
    }
    public void setScore(String score2){this.score = score2;}
}