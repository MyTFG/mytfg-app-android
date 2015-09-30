package de.mytfg.app.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.widget.Toast;

import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;
import org.json.JSONObject;

import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;

import de.mytfg.app.android.gcm.GcmManager;
import de.mytfg.app.android.modulemanager.ModuleManager;
import de.mytfg.app.android.slidemenu.MainActivity;

/**
 * ACRA collects crash information (stacktrace, device info, ...) and sends it JSON formated to
 * acralyzer.
 * A toast notification is shown to the user.
 */
@ReportsCrashes(
        // upload crash reports to acralyzer https://acra.mytfg.de/acralyzer/_design/acralyzer/index.html (HTTP PUT request)
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "https://acra.mytfg.de/acra-mytfg_app_android/_design/acra-storage/_update/report",
        // HTTP basic auth
        formUriBasicAuthLogin = "mytfg_android",
        formUriBasicAuthPassword = "xP3RvRi+elw9rAXWqMKsGUxOcD4=",

        // show toast when app crashed and report is sent
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast
)

/**
 * Main Application
 */
public class MyTFG extends Application {
    private static Context context;

    private static long login_timeout = 0;
    private static String login_username = "";
    private static int login_userId = 0;
    private static String login_token = "";

    public static SharedPreferences preferences;
    public static ModuleManager moduleManager;
    public static GcmManager gcmManager;

    public void onCreate(){
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);

        MyTFG.context = getApplicationContext();
        preferences = getSharedPreferences(getString(R.string.sharedpref_settings), Context.MODE_MULTI_PROCESS);
        refreshPrefs();

        moduleManager = new ModuleManager();
        gcmManager = new GcmManager();
    }

    public static boolean isLoggedIn() {
        return (System.currentTimeMillis() < (login_timeout * 1000));
    }

    public static String getUsername() {
        return login_username;
    }

    public static String getToken() {
        return login_token;
    }

    public static long getTokenTimeout() {
        return login_timeout * 1000;
    }

    public static int getUserId() {
        return login_userId;
    }

    public static String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void refreshPrefs() {
        login_timeout = preferences.getLong(context.getString(R.string.settings_login_timeout), 0);
        login_token = preferences.getString(context.getString(R.string.settings_login_token), "");
        login_username = preferences.getString(context.getString(R.string.settings_login_username), "");
        login_userId = preferences.getInt(context.getString(R.string.settings_login_userid), -1);
    }

    public static Context getAppContext() {
        return MyTFG.context;
    }

    public static int color(int col) {
        return context.getResources().getColor(col);
    }

    public static String string(int str) {
        return context.getResources().getString(str);
    }

    public static Drawable drawable(int draw) {
        return context.getResources().getDrawable(draw, context.getTheme());
    }

    public static float dimension(int id) {
        return context.getResources().getDimension(id);
    }

    public static void logout() {
        preferences.edit().remove(
                string(R.string.settings_login_timeout)).remove(
                string(R.string.settings_login_token)).remove(
                string(R.string.settings_login_userid)).remove(
                string(R.string.settings_login_username)).commit();

        refreshPrefs();
    }

    public static void setLoadingBarVisble(final boolean visble) {
        if (MainActivity.loadingBar != null) {
            ((MainActivity)MainActivity.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (visble) {
                        MainActivity.loadingBar.setVisibility(View.VISIBLE);
                    } else {
                        MainActivity.loadingBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public static void sendGcmToken() {
        if (isLoggedIn()) {
            String token = preferences.getString(string(R.string.settings_gcm_token), "");
            if (!token.equals("")) {
                // Send token to Server
                ApiParams params = new ApiParams();
                params.addParam("token", token);
                MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
                    @Override
                    public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                        if (!success) {
                            Toast.makeText(context, "GCM Registrierung fehlgeschlagen: " + resultStr, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                MytfgApi.call("ajax_gcm_register", params, callback);
            }
        }
    }
}
