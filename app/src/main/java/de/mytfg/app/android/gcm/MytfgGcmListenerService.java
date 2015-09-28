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

import com.google.android.gms.gcm.GcmListenerService;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.R;

/**
 * The Gcm Listener Service.
 */
public class MytfgGcmListenerService extends GcmListenerService {

    private static final String TAG = "MytfgGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message") + " " + data.getString("grouper") + " " + data.getString("type");
        String title = data.getString("title");
        int id = MyTFG.gcmManager.notify(from, data);

        Log.d(TAG, "onMessageReceived");

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        sendNotification(message, title, id);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title, int id) {
        Log.d(TAG, "sendNotification");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("notificationId", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
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
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notificationBuilder.build());
    }
}
