package de.mytfg.app.android.modules.messagecenter;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.Module;
import de.mytfg.app.android.modules.messagecenter.objects.Conversation;

public class Messages extends Module {

    private final static int MESSAGE_COUNT = 500;
    private final static int CONVERSATIONS_CACHE = 10;

    private long conversationId;
    private OnConversationReceived onConversationReceived;
    private HashMap<Long, Conversation> conversations = new HashMap<>();
    private HashMap<Long, Long> conversationsAge = new HashMap<>();

    public interface OnConversationReceived {
        void callback(Conversation conversation, boolean success);
    }

    public interface SendMessageConfirmedCallback {
        void callback(boolean success);
    }

    public void refresh() {
        ApiParams params = new ApiParams();
        params.addParam("id", String.valueOf(conversationId));
        params.addParam("count", String.valueOf(MESSAGE_COUNT));
        MytfgApi.ApiCallback apiCallback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                boolean error = false;
                if(success) {
                    try {
                        Conversation conversation = new Conversation();
                        conversation.readFromJson(result.getJSONObject("object"));
                        conversations.put(conversation.getId(), conversation);
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
                onConversationReceived.callback(getLastPulledConversation(), !error);
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
        if(conversationId != this.conversationId && conversations.size() >= CONVERSATIONS_CACHE && !conversations.containsKey(conversationId)) {
            Map.Entry<Long, Long> oldest = null;
            for(Map.Entry<Long, Long> entry : conversationsAge.entrySet()) {
                if (oldest == null || oldest.getValue() > entry.getValue()) {
                    oldest = entry;
                }
            }
            conversationsAge.remove(oldest.getKey());
            conversations.remove(oldest.getKey());
        }
        this.conversationId = conversationId;
        conversationsAge.put(conversationId, System.currentTimeMillis());
    }

    public OnConversationReceived getOnConversationReceived() {
        return onConversationReceived;
    }

    public void setOnConversationReceived(OnConversationReceived onConversationReceived) {
        this.onConversationReceived = onConversationReceived;
    }

    public Conversation getLastPulledConversation() {
        return conversations.get(conversationId);
    }
}
