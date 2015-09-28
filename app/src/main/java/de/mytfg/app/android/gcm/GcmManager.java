package de.mytfg.app.android.gcm;

import android.os.Bundle;

import java.util.Hashtable;

import de.mytfg.app.android.utils.BiMap;

/**
 * Handles the GCM Notifications - creates IDs for existing topics and opens Fragments on press.
 */
public class GcmManager {
    private BiMap<Integer, GcmNotification> notificationIds;
    private Hashtable<String, GcmCallback> clickCallbacks;
    private Hashtable<String, GcmCallback> receiveCallbacks;

    public GcmManager () {
        notificationIds = new BiMap<>();
        clickCallbacks = new Hashtable<>();
    }

    public void setClickCallback(String type, GcmCallback callback) {
        clickCallbacks.put(type, callback);
    }

    public void setReceiveCallback(String type, GcmCallback callback) {
        receiveCallbacks.put(type, callback);
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

        // Call on receive callback if existing
        GcmCallback toCall = receiveCallbacks.get(notification.getType());
        if (toCall != null) {
            toCall.callback(notification);
        }

        return id;
    }

    public void clicked(int id) {
        // Call on click callback
        GcmNotification notification = notificationIds.getValue(id);
        if (notification != null) {
            GcmCallback toCall = clickCallbacks.get(notification.getType());
            if (toCall != null) {
                toCall.callback(notification);
            }
        }
    }



}
