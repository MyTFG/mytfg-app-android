package de.mytfg.app.android.modules.terminal.objects;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.app.android.modules.general.User;

/**
 * Represents a MyTFG Terminal Topic.
 */
public class Topic {
    private long id;
    private String title;
    private String author;
    private long created;
    private long edited;
    private List<Flag> flags;
    private List<User> workers;
    private long code;

    public Topic(long id, String title, String author, long created, long edited, long code) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.created = created;
        this.edited = edited;
        this.flags = new LinkedList<>();
        this.workers = new LinkedList<>();
        this.code = code;
    }

    public void addFlag(int id) {
        flags.add(new Flag(id));
    }

    public void addFlag(Flag flag) {
        if (flag != null) {
            flags.add(flag);
        }
    }

    public void addWorker(long id) {
        workers.add(new User(id));
    }


    // GETTERS
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getCreated() {
        return created;
    }

    public long getEdited() {
        return edited;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public List<User> getWorkers() {
        return workers;
    }

    public long getCode() {
        return code;
    }
}
