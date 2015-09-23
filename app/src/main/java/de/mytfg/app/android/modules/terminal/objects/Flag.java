package de.mytfg.app.android.modules.terminal.objects;

/**
 * Represents MyTFG terminal Flags
 */
public class Flag {
    private int id;
    // TODO
    private String[] names = {};

    public Flag(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        // TODO
        return "Flag " + id;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
