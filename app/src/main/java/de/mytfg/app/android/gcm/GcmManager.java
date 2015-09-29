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
import android.os.Looper;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Hashtable;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.MainActivity;
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
        receiveCallbacks = new Hashtable<>();
    }

    public void setClickCallback(String type, GcmCallback callback) {
        clickCallbacks.put(type, callback);
    }

    public void setReceiveCallback(String type, GcmCallback callback) {
        receiveCallbacks.put(type, callback);
    }

    public void hide(GcmNotification notification) {
        Integer id = notificationIds.getKey(notification);

        if (id != null) {
            NotificationManager notificationManager = (NotificationManager) MainActivity.context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        }
    }

    public void hide(String grouper) {
        Bundle bundle = new Bundle();
        bundle.putString("grouper", grouper);
        hide(new GcmNotification(bundle));
    }

    public void notify(String from, Bundle data) {
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

        this.sendNotification(data.getString("message"), data.getString("title"), id);

        // Call on receive callback if existing
        GcmCallback toCall = receiveCallbacks.get(notification.getType());
        if (toCall != null) {
            call(toCall, notification);
        }
    }

    public void clicked(int id) {
        // Call on click callback
        GcmNotification notification = notificationIds.getValue(id);
        if (notification != null) {
            GcmCallback toCall = clickCallbacks.get(notification.getType());
            if (toCall != null) {
                call(toCall, notification);
            }
        }
    }

    private void call(final GcmCallback callback, final GcmNotification notification) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.callback(notification);
            }
        });
    }

    private void sendNotification(String message, String title, int id) {

        Intent intent = new Intent(MainActivity.context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("notificationId", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.context, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.context)
                .setLargeIcon(bm)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) MainActivity.context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notificationBuilder.build());
    }

}
