package de.mytfg.app.android.modules.messagecenter.objects;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public Conversation(long id, String subject, float lastMessageTimestamp, User lastMessageAuthor, User[] users, List<Message> messages) {
        this.id = id;
        this.subject = subject;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageAuthor = lastMessageAuthor;
        this.members = users;
        this.messages = messages;
        this.messagecount = messages.size();
    }

    public Conversation() {

    }

    @Override
    public void readFromJson(JSONObject json) throws JSONException {
        if(!json.getString("type").equals("conversation")) {
            throw new IllegalArgumentException("Given JSON object does not represent a conversation!");
        }
        this.id = json.getLong("id");
        this.subject = json.getString("subject");
        this.lastMessageTimestamp = (float) json.getDouble("lastMessageTime");
        this.lastMessageAuthor = new User();
        lastMessageAuthor.readFromJson(json.getJSONObject("lastMessageAuthor"));
        JSONArray membersArray = json.getJSONArray("members");
        this.members = new User[membersArray.length()];
        for(int i = 0; i < membersArray.length(); i++) {
            members[i] = new User();
            members[i].readFromJson(membersArray.getJSONObject(i));
        }
        if(!json.isNull("messages") && json.getJSONArray("messages").length() != 0) {
            JSONArray messagesArray = json.getJSONArray("messages");
            this.messages = new ArrayList<Message>(messagesArray.length());
            for(int i = 0; i < messagesArray.length(); i++) {
                Message m = new Message();
                m.readFromJson(messagesArray.getJSONObject(i));
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
