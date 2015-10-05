package de.mytfg.app.android.modules.terminal.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.mytfg.app.android.modules.general.ApiObject;
import de.mytfg.app.android.modules.general.User;

/**
 * Represents a MyTFG Terminal Topic.
 */
public class Topic extends ApiObject {
    private long id;
    private String title;
    private String code;
    private boolean isSupport;
    private User author;
    private long created;
    private long edited;
    private boolean hasDeadline;
    private long deadline;
    private List<Flag> flags = new LinkedList<>();
    private List<Review> reviews = new LinkedList<>();
    private List<User> workers = new LinkedList<>();
    private List<Topic> dependencies = new LinkedList<>();
    private String supportuser;

    private long lastUpdated;

    private static Map<Long, Topic> cache = new TreeMap<>();


    public static Topic createFromJson(JSONObject json, JSONObject references)
            throws JSONException, InvalidParameterException {

        if (json == null || !json.getString("type").equals("terminaltopic")) {
            throw new InvalidParameterException("Specified json-data does not represent Terminal Topic");
        } else {
            long id = json.getLong("id");
            if (cache.containsKey(id)) {
                Topic topic = cache.get(id);
                // Refresh existing topic
                topic.readFromJson(json, references);
                return topic;
            } else {
                // Create new Topic
                Topic topic = new Topic();
                topic.readFromJson(json, references);
                Topic.cache.put(id, topic);
                return topic;
            }
        }
    }

    private void readFromJson(JSONObject json, JSONObject references) throws JSONException {
        if(!json.getString("type").equals("terminaltopic")) {
            throw new IllegalArgumentException("Given JSON object does not represent Terminal Topic!");
        }

        this.flags.clear();
        this.reviews.clear();
        this.workers.clear();
        this.dependencies.clear();

        this.id = json.getLong("id");
        this.title = json.getString("title");
        this.code = json.getString("code");
        this.isSupport = json.getBoolean("isSupport");
        this.author = User.createFromJson(references.getJSONObject("user").getJSONObject(json.getString("author")));
        this.created = json.getLong("created");
        this.edited = json.getLong("edited");
        this.deadline = json.getLong("deadline");
        this.hasDeadline = this.deadline != -1;

        // FLAGS
        JSONArray flags = json.getJSONArray("flags");
        for (int i = 0; i < flags.length(); i++) {
            this.flags.add(Flag.createFromJson(
                    references.getJSONObject("terminalflag").getJSONObject(flags.getString(i))));
        }

        // REVIEWS
        JSONArray reviews = json.getJSONArray("reviews");
        for (int i = 0; i < reviews.length(); i++) {
            this.reviews.add(Review.createFromJson(
                    references.getJSONObject("terminalreview").getJSONObject(reviews.getString(i)), references));
        }

        // WORKERS
        JSONArray workers = json.getJSONArray("workers");
        for (int i = 0; i < workers.length(); i++) {
            this.workers.add(User.createFromJson(
                    references.getJSONObject("user").getJSONObject(workers.getString(i))));
        }

        // DEPENDENCIES
        JSONArray depen = json.getJSONArray("dependencies");
        for (int i = 0; i < depen.length(); i++) {
            this.dependencies.add(Topic.createFromJson(
                    references.getJSONObject("terminaltopic").getJSONObject(depen.getString(i)), references));
        }

        this.supportuser = json.getString("supportuser");
        this.lastUpdated = System.currentTimeMillis();
    }

    // GETTERS
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public boolean isSupport() {
        return isSupport;
    }

    public User getAuthor() {
        return author;
    }

    public long getCreated() {
        return created;
    }

    public long getEdited() {
        return edited;
    }

    public boolean isHasDeadline() {
        return hasDeadline;
    }

    public long getDeadline() {
        return deadline;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public List<User> getWorkers() {
        return workers;
    }

    public List<Topic> getDependencies() {
        return dependencies;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public String getSupportuser() {
        return supportuser;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        return "#" + this.getId() + " - " + this.getTitle();
    }
}
