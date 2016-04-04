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

    private long currentConversationId;
    private OnConversationReceived onConversationReceived;
    private HashMap<Long, Conversation> conversations = new HashMap<>();
    private HashMap<Long, Long> conversationsAge = new HashMap<>();
    private HashMap<Long, Boolean> fresh = new HashMap<>();
    private boolean autoRefresh = false;

    public interface OnConversationReceived {
        void callback(Conversation conversation, boolean success);
    }

    public interface SendMessageConfirmedCallback {
        void callback(boolean success);
    }

    public void refresh() {
        if(fresh.get(currentConversationId) != new Boolean(true)) {
            forceRefresh();
        }
    }

    private void forceRefresh() {
        ApiParams params = new ApiParams();
        params.addParam("id", String.valueOf(currentConversationId));
        params.addParam("count", String.valueOf(MESSAGE_COUNT));
        MytfgApi.ApiCallback apiCallback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                boolean error = false;
                if(success) {
                    try {
                        Conversation conversation = Conversation.createFromJson(
                                result.getJSONObject("object"), result.getJSONObject("references"));

                        conversations.put(conversation.getId(), conversation);
                    } catch (JSONException e) {
                        error = true;
                        Log.e("API", "Error parsing JSON!", e);
                    }
                } else {
                    error = true;
                    if(result == null) {
                        Log.e("API", "Result is empty!");
                    } else if (!result.isNull("error")) {
                        try {
                            Log.e("API", "Remote error: " + result.getString("error"));
                        } catch (JSONException e) {
                            Log.e("API", "Remote error. Could not be read!");
                        }
                    }
                }
                onConversationReceived.callback(getLastPulledConversation(), !error);
                if(!error) {
                    fresh.put(currentConversationId, true);
                }
            }
        };
        MytfgApi.call("ajax_message_get-conversation", params, apiCallback);
    }

    public void sendMessage(final SendMessageConfirmedCallback callback, String text) {
        ApiParams params = new ApiParams();
        params.addParam("conversation", String.valueOf(currentConversationId));
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

    public void invalidate(long conversationId) {
        if(autoRefresh && conversationId == currentConversationId) {
            forceRefresh();
        } else {
            fresh.put(conversationId, false);
        }
    }

    public long getCurrentConversationId() {
        return currentConversationId;
    }

    public void setCurrentConversationId(long currentConversationId) {
        if(currentConversationId != this.currentConversationId && conversations.size() >= CONVERSATIONS_CACHE && !conversations.containsKey(currentConversationId)) {
            Map.Entry<Long, Long> oldest = null;
            for(Map.Entry<Long, Long> entry : conversationsAge.entrySet()) {
                if (oldest == null || oldest.getValue() > entry.getValue()) {
                    oldest = entry;
                }
            }
            if (oldest != null) {
                conversationsAge.remove(oldest.getKey());
                conversations.remove(oldest.getKey());
            }
        }
        this.currentConversationId = currentConversationId;
        conversationsAge.put(currentConversationId, System.currentTimeMillis());
    }

    public OnConversationReceived getOnConversationReceived() {
        return onConversationReceived;
    }

    public void setOnConversationReceived(OnConversationReceived onConversationReceived) {
        this.onConversationReceived = onConversationReceived;
    }

    public Conversation getLastPulledConversation() {
        return conversations.get(currentConversationId);
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        if(autoRefresh) {
            refresh();
        }
    }
}
