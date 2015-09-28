package de.mytfg.app.android.gcm;

import android.os.Bundle;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.items.Navigation;

/**
 * Registers all GCM Events. Add your event here.
 */
public class GcmCallbackRegistration {
    public static void registerAll() {
        registerTerminal();
    }

    private static void registerTerminal() {
        // Set callback for Terminal Pushs
        MyTFG.gcmManager.setCallback("terminal", new GcmCallback() {
            @Override
            public void callback(GcmNotification notification) {
                String[] grouper = notification.getGrouper().split("-");
                if (grouper.length == 3) {
                    int id = Integer.parseInt(grouper[2]);
                    Bundle args = new Bundle();
                    args.putLong("topic", id);
                    args.putString("title", "Laden...");
                    MainActivity.navigation.navigate(Navigation.ItemNames.TERMINAL_TOPIC, args);
                }
            }
        });
    }
}
