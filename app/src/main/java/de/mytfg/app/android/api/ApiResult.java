package de.mytfg.app.android.api;

import android.util.Log;

import org.json.JSONObject;

/**
 * Wrapper for an API-Result
 */
public class ApiResult {
    private String returnString;
    private JSONObject jsonParsed;
    private int returnCode;
    private boolean success;
    private long timestamp;
    private ApiParams usedParams;

    public ApiResult(String returnString, int httpCode, ApiParams params) {
        this.usedParams = params;
        this.returnCode = httpCode;
        this.returnString = returnString;
        this.timestamp = System.currentTimeMillis();
        if (this.returnString == null) {
            this.success = false;
            this.returnString = "";
            this.jsonParsed = null;
        } else {
            try {
                this.jsonParsed = new JSONObject(this.returnString);
                this.success = true;
            } catch (Exception ex) {
                Log.e("API", "Could not parse Result");
                this.success = false;
            }
        }
    }

    public JSONObject getJson() {
        return this.jsonParsed;
    }

    public boolean isSuccessful() {
        return this.success;
    }

    public String getResultString() {
        return this.returnString;
    }

    public int getReturnCode() {
        return this.returnCode;
    }

    protected long getTimestamp() {
        return this.timestamp;
    }
}
