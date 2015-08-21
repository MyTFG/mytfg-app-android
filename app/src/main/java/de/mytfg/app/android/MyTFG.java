package de.mytfg.app.android;

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "https://acra.mytfg.de/acra-mytfg_app_android/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "mytfg_android",
        formUriBasicAuthPassword = "xP3RvRi+elw9rAXWqMKsGUxOcD4=",

        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast
)

/**
 * Main Application
 */
public class MyTFG extends Application {
    private static Context context;

    public void onCreate(){
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);

        MyTFG.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyTFG.context;
    }
}
