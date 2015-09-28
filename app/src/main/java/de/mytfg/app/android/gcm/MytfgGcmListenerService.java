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
        MyTFG.gcmManager.notify(from, data);
    }
}
