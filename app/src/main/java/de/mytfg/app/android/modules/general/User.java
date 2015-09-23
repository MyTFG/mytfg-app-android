package de.mytfg.app.android.modules.general;

import de.mytfg.app.android.MyTFG;

/**
 * Represents a MyTFG User.
 */
public class User {
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
        this.load();
    }

    private void load() {
        // TODO: Load rest of user data from API
        this.firstname = "";
        this.lastname = "";
        this.grade = "";
        this.username = "";
        this.rights = 0;
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
