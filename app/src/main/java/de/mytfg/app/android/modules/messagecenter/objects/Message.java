package de.mytfg.app.android.modules.messagecenter.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.TreeMap;

import de.mytfg.app.android.modules.general.ApiObject;
import de.mytfg.app.android.modules.general.User;

public class Message extends ApiObject {

    private long id;
    private float timestamp;
    private String text;
    private User author;

    private static Map<Long, Message> cache = new TreeMap<>();

    public static Message createFromJson(JSONObject json, JSONObject references)
            throws JSONException, InvalidParameterException {

        if (json == null || !json.getString("type").equals("message")) {
            throw new InvalidParameterException("Specified json-data does not represent message");
        } else {
            long id = json.getLong("id");
            if (cache.containsKey(id)) {
                Message msg = cache.get(id);
                msg.readFromJson(json, references);
                return msg;
            } else {
                // Create new Conversation
                Message msg = new Message();
                msg.readFromJson(json, references);
                Message.cache.put(id, msg);
                return msg;
            }
        }
    }

    private Message() {

    }

    private void readFromJson(JSONObject json, JSONObject references)
            throws JSONException, InvalidParameterException {

        if(!json.getString("type").equals("message")) {
            throw new IllegalArgumentException("Given JSON object does not represent a message!");
        }
        this.id = json.getLong("id");
        this.timestamp = (float) json.getDouble("timestamp");
        this.text = json.getString("text");
        this.author = User.createFromJson(references.getJSONObject("user").getJSONObject(json.getString("author")));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
