package de.mytfg.app.android.slidemenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.security.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mytfg.app.android.R;

public class SettingsFragment extends Fragment {
    View settingsview;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    TableLayout mainTable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsview = inflater.inflate(R.layout.settings_layout, container, false);
        preferences = settingsview.getContext().getSharedPreferences(getString(R.string.sharedpref_settings), Context.MODE_MULTI_PROCESS);
        prefEditor = preferences.edit();

        // Display Settings
        mainTable = (TableLayout) settingsview.findViewById(R.id.settings_table);
        // Status, Username, Token, Timeout, DeviceID, UserID
        LinkedHashMap<String, String> rows = new LinkedHashMap<>();

        Long currentTimestamp = System.currentTimeMillis();
        long tokenTimeout = preferences.getLong(getString(R.string.settings_login_timeout), 0) * 1000;
        Date timeout = new Date(tokenTimeout);
        Date now = new Date(System.currentTimeMillis());

        if (currentTimestamp < tokenTimeout) {
            rows.put("Login Status", "Authentifiziert");
        } else {
            rows.put("Login Status", "Nicht authentifiziert / Abgelaufen");
        }
        rows.put("Nutzername", preferences.getString(getString(R.string.settings_login_username), ""));
        rows.put("Token", preferences.getString(getString(R.string.settings_login_token), ""));
        rows.put("TokenTimeout", timeout.toString());
        rows.put("Systemzeit", now.toString());
        rows.put("Geräte ID", Settings.Secure.getString(settingsview.getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        rows.put("Nutzer ID", "" + preferences.getInt(getString(R.string.settings_login_userid), 0));

        for (Map.Entry<String, String> entry : rows.entrySet()) {
            TableRow tr = new TableRow(settingsview.getContext());
            TableRow tr2 = new TableRow(settingsview.getContext());
            tr.setPadding(5, 5, 5, 5);
            tr2.setPadding(70, 5, 5, 5);
            TextView title = new TextView(settingsview.getContext());
            TextView value = new TextView(settingsview.getContext());
            title.setText(entry.getKey());
            value.setText(entry.getValue());

            tr.addView(title);
            tr2.addView(value);

            mainTable.addView(tr);
            mainTable.addView(tr2);
        }
        return settingsview;
    }
}
