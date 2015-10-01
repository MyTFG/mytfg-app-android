package de.mytfg.app.android.modules.general;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.TreeMap;

import de.mytfg.app.android.MyTFG;

/**
 * Represents a MyTFG User.
 */
public class User extends ApiObject {
    // Object vars
    private long id;
    private String firstname;
    private String lastname;
    private String grade;
    private String username;
    private int rights;
    private boolean loaded;

    private static Map<Long, User> cache = new TreeMap<>();

    private User() {

    }


    public static User createFromJson(JSONObject json)
            throws JSONException, InvalidParameterException {

        if (json == null || !json.getString("type").equals("user")) {
            throw new InvalidParameterException("Specified json-data does not represent user");
        } else {
            long id = json.getLong("id");
            if (cache.containsKey(id)) {
                User user = cache.get(id);
                user.readFromJson(json);
                return user;
            } else {
                // Create new User
                User user = new User();
                user.readFromJson(json);
                User.cache.put(id, user);
                return user;
            }
        }
    }

    private void readFromJson(JSONObject json)
            throws JSONException, InvalidParameterException {

        if(!json.getString("type").equals("user")) {
            throw new IllegalArgumentException("Given JSON object does not represent an user!");
        }
        this.id = json.getLong("id");
        this.firstname = json.getString("firstname");
        this.lastname = json.getString("lastname");
        this.grade = json.getString("grade");
        this.username = json.getString("username");
        this.rights = json.getInt("rights");
        this.loaded = true;
    }

    // GETTERS
    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getGrade() {
        return grade;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoginUser() {
        return this.id == MyTFG.getUserId();
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User && this.id == ((User) o).id;
    }

    @Override
    public String toString() {
        return getFirstname() + " " + getLastname();
    }
}
