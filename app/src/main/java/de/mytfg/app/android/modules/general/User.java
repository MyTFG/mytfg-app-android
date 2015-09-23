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

    public User(long id) {
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
}
