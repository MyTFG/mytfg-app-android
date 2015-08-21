package de.mytfg.app.android;

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formKey = "", // will not be used
        mailTo = "helpdesk@mytfg.de",
        mode = ReportingInteractionMode.TOAST
        //resToastText = // text: "MyTFG crashed, please send bug report"
)

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
