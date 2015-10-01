package de.mytfg.app.android.modules.terminal;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.gcm.GcmCallback;
import de.mytfg.app.android.gcm.GcmNotification;
import de.mytfg.app.android.modulemanager.Module;
import de.mytfg.app.android.modules.terminal.objects.Topic;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.items.Navigation;

/**
 * Module for the TerminalTopic List.
 * Managed by the ModuleManager.
 */
public class TerminalTopics extends Module {
    // Milliseconds to wait until next API call / Cache Call
    private static int updateInterval = 30000;
    private List<Topic> topics;

    private long lastUpdate = 0;

    public TerminalTopics() {
        topics = new LinkedList<>();
    }

    public interface GetTopicsCallback {
        void callback(List<Topic> topics, boolean stillLoading);
    }

    public void getTopics(GetTopicsCallback callback) {
        if (System.currentTimeMillis() > lastUpdate + updateInterval) {
            sendCallback(callback, true);
            this.updateTopics(callback);
        } else {
            // Data is up to date
            sendCallback(callback, false);
        }
    }

    private void sendCallback(GetTopicsCallback callback, boolean stillLoading) {
        if (callback != null) {
            callback.callback(this.topics, stillLoading);
        }
    }

    private void updateTopics(GetTopicsCallback userCb) {
        final GetTopicsCallback callbackf = userCb;
        ApiParams params = new ApiParams();
        params.addParam("all", "false");
        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                if (success) {
                    try {
                        readTopics(result);
                        sendCallback(callbackf, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API", "API call failed " + resultStr);
                }
            }
        };
        MytfgApi.call("ajax_terminal_get-topics", params, callback);

        this.lastUpdate = System.currentTimeMillis();
    }

    private void readTopics(JSONObject result) throws JSONException {
        this.topics.clear();
        JSONArray objects = result.getJSONArray("objects");
        JSONObject references = result.getJSONObject("references");
        for(int i = 0; i < objects.length(); i++) {
            Topic topic = Topic.createFromJson(objects.getJSONObject(i), references);
            topics.add(topic);
        }
    }
}
