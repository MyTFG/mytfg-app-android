package de.mytfg.app.android.modules.general;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.app.android.MyTFG;

/**
 * Represents a MyTFG User.
 */
public class User extends ApiObject {

    private long id;
    private String firstname;
    private String lastname;
    private String grade;
    private String username;
    private int rights;
    private boolean loaded;

    public User(long id) {
        this.loaded = false;
        this.id = id;
    }

    public User() {

    }

    public void readFromJson(JSONObject json) throws JSONException {
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
        if (!(o instanceof User)) {
            return false;
        } else {
            return this.id == ((User)o).id;
        }
    }
}
