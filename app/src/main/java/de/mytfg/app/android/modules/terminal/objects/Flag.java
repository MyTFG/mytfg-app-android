package de.mytfg.app.android.modules.terminal.objects;

/**
 * Represents MyTFG terminal Flags
 */
public class Flag {
    private int id;
    // TODO
    private String[] names = {
            "Bug",
            "Bestätigt",
            "Gelöst",
            "Kritisch",
            "Lecht",
            "Schwer",
            "Vorschlag",
            "Supportanfrage",
            "Aufgabe AG",
            "Abgewiesen",
            "In Bearbeitung",
            "Gesehen",
            "Tutorial",
            "Diskussion",
            "Verwaltungsnetz"
    };

    public Flag(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        if (id >= 0 && id < names.length) {
            return names[id];
        } else {
            return "Flag " + id;
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
