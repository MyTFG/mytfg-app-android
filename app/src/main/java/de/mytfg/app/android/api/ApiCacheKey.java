package de.mytfg.app.android.api;

import android.util.Log;

/**
 * A key for internal use in the API Cache.
 */
public class ApiCacheKey {

    private final String function;
    private final ApiParams params;

    public ApiCacheKey(String f, ApiParams p) {
        this.function = f;
        this.params = p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiCacheKey)) return false;
        ApiCacheKey key = (ApiCacheKey) o;
        if ((this.function.equals(key.function)) && (this.params.equals(key.params))) {
            Log.d("CALL", "EQUALS TRUE");
            return true;
        } else {
            Log.d("CALL", "EQUALS FALSE");
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = function.hashCode();
        result = 31 * result + params.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.function + " & " + this.params.toString();
    }
}
