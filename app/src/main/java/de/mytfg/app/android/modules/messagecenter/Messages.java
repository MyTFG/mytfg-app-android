package de.mytfg.app.android.modules.messagecenter;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.Module;
import de.mytfg.app.android.modules.messagecenter.objects.Conversation;

public class Messages extends Module {

    private long conversationId;

    public interface GetConversationCallback {
        void callback(Conversation conversation, boolean success);
    }

    public interface SendMessageConfirmedCallback {
        void callback(boolean success);
    }

    public void getConversation(final GetConversationCallback callback, int count) {
        ApiParams params = new ApiParams();
        params.addParam("id", String.valueOf(conversationId));
        params.addParam("count", String.valueOf(count));
        MytfgApi.ApiCallback apiCallback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                boolean error = false;
                Conversation conversation = null;
                if(success) {
                    try {
                        conversation = new Conversation();
                        conversation.readFromJson(result.getJSONObject("object"));
                    } catch (JSONException e) {
                        error = true;
                        Log.e("API", "Error parsing JSON!");
                    }
                } else {
                    error = true;
                    if (!result.isNull("error")) {
                        try {
                            Log.e("API", "Remote error: " + result.getString("error"));
                        } catch (JSONException e) {
                            Log.e("API", "Remote error. Could not be read!");
                        }
                    }
                }
                callback.callback(conversation, !error);
            }
        };
        MytfgApi.call("ajax_message_get-conversation", params, apiCallback);
    }

    public void sendMessage(final SendMessageConfirmedCallback callback, String text) {
        ApiParams params = new ApiParams();
        params.addParam("conversation", String.valueOf(conversationId));
        params.addParam("message", text);
        MytfgApi.ApiCallback apiCallback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                if(!success) {
                    if (!result.isNull("error")) {
                        try {
                            Log.e("API", "Remote error: " + result.getString("error"));
                        } catch (JSONException e) {
                            Log.e("API", "Remote error. Could not be read!");
                        }
                    }
                }
                callback.callback(success);
            }
        };
        MytfgApi.call("ajax_message_new-answer", params, apiCallback);
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }
}
