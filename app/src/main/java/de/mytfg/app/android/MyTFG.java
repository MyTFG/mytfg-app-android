package de.mytfg.app.android;

import android.app.Application;
import android.content.Context;

/**
 * Main Application
 */
public class MyTFG extends Application {
    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyTFG.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyTFG.context;
    }
}
