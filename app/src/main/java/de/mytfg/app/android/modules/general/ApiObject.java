package de.mytfg.app.android.modules.general;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ApiObject {

    public abstract void readFromJson(JSONObject json) throws JSONException;

}
