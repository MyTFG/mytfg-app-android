package de.mytfg.app.android.api;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Used to Cache API-Calls.
 */
public class ApiCache {
    // This is the default timeout to recall the API.
    private static final long defaultTimeout = 60000;

    private static  Map<ApiCacheKey, ApiResult> cache = new HashMap<>();

    /**
     * Calls the MyTFG API or returns a cached return for this call.
     * Cached result is used, if last call to this function is less seconds
     * than the defaultTimeout ago.
     * @param apiFunction The API function to call.
     * @param params Params to pass to the API.
     * @param callback A callback function to call after the API-call finishes.
     */
    public static void call(String apiFunction, ApiParams params, MytfgApi.ApiCallback callback) {
        ApiCache.call(apiFunction, params, callback, defaultTimeout);
    }

    /**
     * Calls the MyTFG API or returns a cached return for this call.
     * Cached result is used, if last call to this function is less seconds
     * than the defaultTimeout ago.
     * @param apiFunction The API function to call.
     * @param params Params to pass to the API.
     * @param callback A callback function to call after the API-call finishes.
     * @param timeout The timeout in Milliseconds for the cache.
     */
    public static void call(String apiFunction, final ApiParams params,
                            final MytfgApi.ApiCallback callback, long timeout) {
        if (params == null) {
            // Will not handle null params.
            callback.callback(false, null, -1, null);
        } else {
            params.doLogin();

            final ApiCacheKey search = new ApiCacheKey(apiFunction, params);

            if (cache.containsKey(search)) {
                ApiResult cachedResult = cache.get(search);
                if (cachedResult.isSuccessful()
                        && (cachedResult.getTimestamp() + timeout > System.currentTimeMillis()
                        || timeout == 0)) {
                    // Cache is up-to-date
                    callback.callback(cachedResult.isSuccessful(), cachedResult.getJson(),
                            cachedResult.getReturnCode(), cachedResult.getResultString());

                    if (timeout != 0) {
                        return;
                    }
                }
            }

            MytfgApi.ApiCallback cacheCallback = new MytfgApi.ApiCallback() {
                @Override
                public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                    cache.put(search, new ApiResult(resultStr, responseCode, params));
                    callback.callback(success, result, responseCode, resultStr);
                }
            };
            MytfgApi.call(apiFunction, params, cacheCallback);
        }
    }




}
