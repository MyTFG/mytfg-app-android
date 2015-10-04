package de.mytfg.app.android.modules.terminal.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.TreeMap;

import de.mytfg.app.android.modules.general.ApiObject;

/**
 * Represents the Type of a MyTFG Terminal Review.
 */
public class ReviewType extends ApiObject {
    private int id;
    private String name;

    private static Map<Integer, ReviewType> cache = new TreeMap<>();

    private ReviewType() {

    }

    public static ReviewType createFromJson(JSONObject json)
            throws JSONException, InvalidParameterException {

        if (json == null || !json.getString("type").equals("terminalreviewtype")) {
            throw new InvalidParameterException("JSON does not represent a Terminal Review Type");
        }

        if (cache.containsKey(json.getInt("id"))) {
            return cache.get(json.getInt("id"));
        } else {
            ReviewType reviewType = new ReviewType();
            reviewType.readFromJson(json);
            cache.put(json.getInt("id"), reviewType);
            return reviewType;
        }
    }

    private void readFromJson(JSONObject json) throws JSONException, InvalidParameterException {
        if (json == null || !json.getString("type").equals("terminalreviewtype")) {
            throw new InvalidParameterException("JSON does not represent a Terminal Review Type");
        }

        this.id = json.getInt("id");
        this.name = json.getString("name");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
