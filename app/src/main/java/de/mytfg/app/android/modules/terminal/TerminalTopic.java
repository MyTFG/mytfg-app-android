package de.mytfg.app.android.modules.terminal;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.Module;
import de.mytfg.app.android.modules.terminal.objects.Topic;

/**
 * Module for the TerminalTopic View.
 * Managed by the ModuleManager.
 */
public class TerminalTopic extends Module {
    private Topic loaded;
    private long id;
    private long lastUpdate;

    public interface GetTopicCallback {
        void callback(Topic topic, boolean stillLoading);
    }

    public void getTopic(GetTopicCallback callback) {
        updateTopic(id, callback);
    }

    private void sendCallback(GetTopicCallback callback, boolean stillLoading) {
        if (callback != null) {
            callback.callback(this.loaded, stillLoading);
        }
    }

    private void updateTopic(long id, GetTopicCallback userCb) {
        final GetTopicCallback callbackf = userCb;
        ApiParams params = new ApiParams();
        params.addParam("topic", "" + id);
        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                if (success) {
                    try {
                        readTopic(result);
                        sendCallback(callbackf, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API", "API call failed " + resultStr);
                }
            }
        };
        MytfgApi.call("ajax_terminal_get-topic", params, callback);
    }

    private void readTopic(JSONObject result) throws JSONException {
        this.loaded = Topic.createFromJson(result.getJSONObject("object"),
                result.getJSONObject("references"));

        this.lastUpdate = System.currentTimeMillis();
    }

    public void setId(long id) {
        Log.d("TERMINAL-TOPIC", "Id set to " + id);
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}
