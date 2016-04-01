package de.mytfg.app.android.modules.settings;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

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
        sw.setText(title);
        sw.setChecked(state);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitch(buttonView);
            }
        });

        this.setLayout(sw);

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
