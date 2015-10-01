package de.mytfg.app.android.modules.settings;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.Module;

/**
 * The Module for the Settings Fragment.
 * Managed by ModuleManager.
 */
public class Settings extends Module {
    public interface SettingsCallback {
        void callback();
    }

    public void init(View v) {
        List<SettingsItem> items = new LinkedList<>();

        // Login / Logout
        items.add(new Auth());

        // Group Notifications
        items.add(new Switch(
                v.getContext().getResources().getString(R.string.setting_notification_group),
                v.getContext().getResources().getString(R.string.pref_notification_group)));

        // Show Terminal Archive
        items.add(new Switch(
                v.getContext().getResources().getString(R.string.setting_terminal_archive),
                v.getContext().getResources().getString(R.string.pref_terminal_archive)));


        LinearLayout border = (LinearLayout)(v.findViewById(R.id.settings_items));
        ScrollView.LayoutParams layoutParams = new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(24, 0, 24, 0);
        border.setLayoutParams(layoutParams);
        border.setPadding(10, 10, 10, 10);
        for (SettingsItem item : items) {
            border.addView(item.createItem());
        }
    }
}
