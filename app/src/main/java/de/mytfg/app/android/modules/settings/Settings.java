package de.mytfg.app.android.modules.settings;

import android.view.View;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.MainActivity;

/**
 * Created by lennart on 25-Aug-15.
 */
public class Settings {
    public interface SettingsCallback {
        void callback();
    }

    public static void init(View v) {
        List<SettingsItem> items = new LinkedList<>();

        // Login / Logout
        if (MyTFG.isLoggedIn()) {
            // TODO: Logout mask
        } else {
            // TODO: Login Mask / Link
        }

        // Group Notifications
        items.add(new Switch(
                v.getContext().getResources().getString(R.string.setting_notification_group),
                v.getContext().getResources().getString(R.string.pref_notification_group)));

        // Show Terminal Archive
        items.add(new Switch(
                v.getContext().getResources().getString(R.string.setting_terminal_archive),
                v.getContext().getResources().getString(R.string.pref_terminal_archive)));


        LinearLayout border = (LinearLayout)(v.findViewById(R.id.settings_items));
        for (SettingsItem item : items) {
            border.addView(item.createItem());
        }
    }
}
