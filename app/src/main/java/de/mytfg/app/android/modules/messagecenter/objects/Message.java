package de.mytfg.app.android.modules.messagecenter.objects;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.app.android.modules.general.ApiObject;
import de.mytfg.app.android.modules.general.User;

public class Message extends ApiObject {

    private long id;
    private float timestamp;
    private String text;
    private User author;

    public Message(long id, float timestamp, String text, User author) {
        this.id = id;
        this.timestamp = timestamp;
        this.text = text;
        this.author = author;
    }

    public Message() {

    }

    @Override
    public void readFromJson(JSONObject json) throws JSONException {
        if(!json.getString("type").equals("message")) {
            throw new IllegalArgumentException("Given JSON object does not represent a message!");
        }
        this.id = json.getLong("id");
        this.timestamp = (float) json.getDouble("timestamp");
        this.text = json.getString("text");
        User author = new User();
        author.readFromJson(json.getJSONObject("author"));
        this.author = author;
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
