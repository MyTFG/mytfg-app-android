package de.mytfg.app.android.modules.terminal.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.TreeMap;

import de.mytfg.app.android.modules.general.ApiObject;
import de.mytfg.app.android.modules.general.User;

/**
 * Represents a MyTFG Terminal Review.
 */
public class Review extends ApiObject {
    private long id;
    private User author;
    private String text;
    private long created;
    private long edited;
    private boolean isSupport;
    private boolean isPrivate;
    private ReviewType reviewType;

    private long lastUpdate;

    private static Map<Long, Review> cache = new TreeMap<>();

    private Review() {

    }

    public static Review createFromJson(JSONObject json, JSONObject references)
                throws JSONException, InvalidParameterException {

        if (json == null || !json.getString("type").equals("terminalreview")) {
            throw new InvalidParameterException("JSON does not represent a Terminal Review");
        }

        Review review = new Review();

        review.id = json.getLong("id");
        review.author = User.createFromJson(
                references.getJSONObject("user").getJSONObject(json.getString("author")));
        review.isSupport = json.getBoolean("authorIsSupport");
        review.isPrivate = json.getBoolean("isPrivate");
        review.created = json.getLong("created");
        review.edited = json.getLong("edited");
        review.text = json.getString("text");
        review.reviewType = ReviewType.createFromJson(
                references.getJSONObject("terminalreviewtype").getJSONObject(json.getString("reviewType")));

        review.lastUpdate = System.currentTimeMillis();

        return review;
    }


    // GETTERS

    public long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public long getCreated() {
        return created;
    }

    public long getEdited() {
        return edited;
    }

    public boolean isSupport() {
        return isSupport;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }
}
