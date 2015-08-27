package de.mytfg.app.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

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

    public void onCreate(){
        super.onCreate();
        MyTFG.context = getApplicationContext();
        preferences = getSharedPreferences(getString(R.string.sharedpref_settings), Context.MODE_MULTI_PROCESS);
        refreshPrefs();
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

    public static String getDate(long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp * 1000);
        return DateFormat.format("dd.MM.yyyy, HH:mm", calendar).toString();
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
        return context.getResources().getDrawable(draw);
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
}
