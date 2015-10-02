package de.mytfg.app.android.modules.messagecenter.objects;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.mytfg.app.android.modules.general.ApiObject;
import de.mytfg.app.android.modules.general.User;

public class Conversation extends ApiObject {

    private long id;
    private String subject;
    private float lastMessageTimestamp;
    private User lastMessageAuthor;
    private User[] members; // TODO: groups
    private int messagecount;
    private List<Message> messages;

    private static Map<Long, Conversation> cache = new TreeMap<>();


    public static Conversation createFromJson(JSONObject json, JSONObject references)
            throws JSONException, InvalidParameterException {

        if (json == null || !json.getString("type").equals("conversation")) {
            throw new InvalidParameterException("Specified json-data does not represent conversation");
        } else {
            long id = json.getLong("id");
            if (cache.containsKey(id)) {
                Conversation conv = cache.get(id);
                conv.readFromJson(json, references);
                return conv;
            } else {
                // Create new Conversation
                Conversation conv = new Conversation();
                conv.readFromJson(json, references);
                Conversation.cache.put(id, conv);
                return conv;
            }
        }
    }

    private Conversation() {

    }

    private void readFromJson(JSONObject json, JSONObject references) throws JSONException {
        if(!json.getString("type").equals("conversation")) {
            throw new IllegalArgumentException("Given JSON object does not represent a conversation!");
        }

        this.messages.clear();

        this.id = json.getLong("id");
        this.subject = json.getString("subject");
        this.lastMessageTimestamp = (float) json.getDouble("lastMessageTime");
        this.lastMessageAuthor = User.createFromJson(references.getJSONObject("user").getJSONObject(
                json.getString("lastMessageAuthor")));

        JSONArray membersArray = json.getJSONObject("members").getJSONArray("users");
        this.members = new User[membersArray.length()];
        for(int i = 0; i < membersArray.length(); i++) {
            members[i] = User.createFromJson(references.getJSONObject("user").getJSONObject(
                    membersArray.getString(i)));
        }
        if(!json.isNull("messages") && json.getJSONArray("messages").length() != 0) {
            JSONArray messagesArray = json.getJSONArray("messages");
            this.messages = new ArrayList<>(messagesArray.length());
            for(int i = 0; i < messagesArray.length(); i++) {
                Message m = Message.createFromJson(messagesArray.getJSONObject(i), references);
                messages.add(m);
            }
            Collections.sort(messages, new Comparator<Message>() {
                @Override
                public int compare(Message lhs, Message rhs) {
                    if(lhs.getId() > rhs.getTimestamp()) {
                        return 1;
                    } else if(lhs.getId() < rhs.getTimestamp()) {
                        return -1;
                    }
                    return 0;
                }
            });
        } else {
            this.messagecount = json.getInt("messagecount");
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public float getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(float lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public User getLastMessageAuthor() {
        return lastMessageAuthor;
    }

    public void setLastMessageAuthor(User lastMessageAuthor) {
        this.lastMessageAuthor = lastMessageAuthor;
    }

    public User[] getMembers() {
        return members;
    }

    public void setMembers(User[] members) {
        this.members = members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getMessagecount() {
        return messagecount;
    }

    public void setMessagecount(int messagecount) {
        this.messagecount = messagecount;
    }
}
