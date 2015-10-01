package de.mytfg.app.android.gcm;


import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import de.mytfg.app.android.MyTFG;

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
