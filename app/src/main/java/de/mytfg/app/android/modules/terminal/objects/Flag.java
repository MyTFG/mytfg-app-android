package de.mytfg.app.android.modules.terminal.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents MyTFG terminal Flags
 */
public class Flag {
    private int id;
    private String name;

    private static Map<Integer, Flag> cache = new TreeMap<>();

    private Flag() {

    }

    public static Flag createFromJson(JSONObject json, boolean forceReload)
            throws JSONException, InvalidParameterException {

        if (json == null || !json.getString("type").equals("terminalflag")) {
            throw new InvalidParameterException("Specified json-data does not represent a Terminal Flag");
        } else {
            int id = json.getInt("id");
            if (!forceReload && cache.containsKey(id)) {
                return cache.get(id);
            } else {
                // Create new Flag
                Flag flag = new Flag();
                flag.readFromJson(json);
                Flag.cache.put(id, flag);
                return flag;
            }
        }
    }

    public static Flag createFromJson(JSONObject json)
            throws JSONException, InvalidParameterException {

        return Flag.createFromJson(json, false);
    }

    private void readFromJson(JSONObject json) throws JSONException {
        if(!json.getString("type").equals("terminalflag")) {
            throw new IllegalArgumentException("Given JSON object does not represent a TerminalFlag!");
        }
        this.id = json.getInt("id");
        this.name = json.getString("name");
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
