package de.mytfg.app.android.modulemanager;

/**
 * An Enum to list all modules.
 */
public enum Modules {
    NOTIFICATIONS(0),
    TERMINAL(1),
    SETTINGS(2);

    public static int size = Modules.values().length;

    private final int id;

    Modules(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
