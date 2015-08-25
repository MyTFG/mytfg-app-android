package de.mytfg.app.android.modules.settings;

import android.app.ActionBar;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.MainActivity;

/**
 * Switch Settings Item
 */
public class Switch extends SettingsItem {
    private String title;
    private String pref;
    private boolean state;
    private Settings.SettingsCallback onSwitch;

    private boolean isPref;

    public Switch(String title, String prefName) {
        this.title = title;
        this.pref = prefName;
        this.state = MyTFG.preferences.getBoolean(prefName, false);
        this.isPref = true;
    }

    public Switch(String title, boolean state, Settings.SettingsCallback onSwitch) {
        this.title = title;
        this.state = state;
        this.onSwitch = onSwitch;
        this.pref = "";
        this.isPref = false;
    }

    @Override
    public View createItem() {
        Context context = MainActivity.context;

        android.widget.Switch sw = new android.widget.Switch(MainActivity.context);

        sw.setTextColor(context.getResources().getColor(R.color.white));
        sw.setPadding(5, 5, 5, 5);

        LinearLayout.LayoutParams lw = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lw.setMargins(8, 20, 8, 20);
        sw.setLayoutParams(lw);

        sw.setText(title);
        sw.setChecked(state);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitch(v);
            }
        });

        return sw;
    }

    private void onSwitch(View v) {
        if (!this.isPref) {
            if (this.onSwitch != null) {
                this.onSwitch.callback();
            }
        } else {
            // Set Setting
            MyTFG.preferences.edit().putBoolean(this.pref, ((android.widget.Switch)v).isChecked()).commit();
        }
    }



}
