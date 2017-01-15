package com.maxfilippi.myblognote.application;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.maxfilippi.myblognote.core.CoreEngine;

/**
 * Created by Max on 1/12/17.
 *
 * To improve performance:
 * Keep this class as LIGHT as possible
 * Keep this class as CLEAN as possible
 * Keep this class as CLEAR as possible
 *
 */

public class BlogNoteApplication extends Application
{
    // Static class TAG for logs
    private static String TAG = "BlogNote Application";


    @Override
    public void onCreate()
    {
        Log.d(TAG, "Create Application");
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }


    protected void initSingletons()
    {
        Log.d(TAG, "Init Singletons");

        // Initialize cache/perf library for images
        Fresco.initialize(this);

        // Initialize the first instance of RKMoteur
        CoreEngine.shared(getApplicationContext());
    }


    @Override
    public void onLowMemory()
    {
        Log.d(TAG, "Performance drop - Low Memory, time to clean up memory, data and cache");
        super.onLowMemory();
    }

}
