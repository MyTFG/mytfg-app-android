package de.mytfg.app.android.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Hashtable;

import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.utils.BiMap;

/**
 * Handles the GCM Notifications - creates IDs for existing topics and opens Fragments on press.
 */
public class GcmManager {
    private BiMap<Integer, GcmNotification> notificationIds;
    private Hashtable<String, GcmCallback> callBacks;

    public GcmManager () {
        notificationIds = new BiMap<>();
        callBacks = new Hashtable<>();
    }

    public void setCallback(String type, GcmCallback callback) {
        callBacks.put(type, callback);
    }

    public int notify(String from, Bundle data) {
        GcmNotification notification = new GcmNotification(data);

        int id;
        if (notificationIds.getKey(notification) == null) {
            // Create new ID.
            id = notificationIds.getSize();
            notificationIds.add(id, notification);
        } else {
            id = notificationIds.getKey(notification);
            notificationIds.add(id, notification);
        }

        return id;
    }

    public void clicked(int id) {
        GcmNotification notification = notificationIds.getValue(id);
        if (notification != null) {
            GcmCallback toCall = callBacks.get(notification.getType());
            if (toCall != null) {
                toCall.callback(notification);
            }
        }
    }



}
