package de.mytfg.app.android.modules.messagecenter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.Module;
import de.mytfg.app.android.modules.messagecenter.objects.Conversation;

public class Conversations extends Module {

    public interface GetConversationsCallback {
        void callback(List<Conversation> conversations, boolean success);
    }

    public void getConversations(final GetConversationsCallback callback) {
        ApiParams params = new ApiParams();
        MytfgApi.ApiCallback apiCallback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                boolean error = false;
                ArrayList<Conversation> conversations = new ArrayList<>();
                if (success) {
                    try {
                        JSONArray conversationsArray = result.getJSONArray("conversations");
                        for(int i = 0; i < conversationsArray.length(); i++) {
                            Conversation conversation = new Conversation();
                            conversation.readFromJson(conversationsArray.getJSONObject(i));
                            conversations.add(conversation);
                        }
                        Collections.sort(conversations, new Comparator<Conversation>() {
                            @Override
                            public int compare(Conversation lhs, Conversation rhs) {
                                if(lhs.getLastMessageTimestamp() < rhs.getLastMessageTimestamp()) {
                                    return 1;
                                } else if(lhs.getLastMessageTimestamp() > rhs.getLastMessageTimestamp()) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("API", "JSON parsing failed", e);
                        error = true;
                    }
                } else {
                    Log.e("API", "API call failed " + resultStr);
                    error = true;
                }
                callback.callback(conversations, !error);
            }
        };
        MytfgApi.call("ajax_message_list-conversations", params, apiCallback);
    }

}
