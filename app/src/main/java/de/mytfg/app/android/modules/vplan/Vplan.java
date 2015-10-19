package de.mytfg.app.android.modules.vplan;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.app.android.api.ApiCache;
import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.Module;
import de.mytfg.app.android.modules.vplan.objects.VplanEntry;
import de.mytfg.app.android.modules.vplan.objects.VplanInfo;

/**
 * Module for the VPlan.
 * Managed by the ModuleManager.
 */
public class Vplan extends Module {
    // Milliseconds to wait until next API call / Cache Call
    private static int updateInterval = 30000;

    private List<String> marquee = new LinkedList<>();
    private List<VplanEntry> entries = new LinkedList<>();
    private String dayString;

    public Vplan() {

    }

    public interface GetPlanCallback {
        void callback(String day, VplanInfo info, List<VplanEntry> entries);
    }

    public void getPlan(boolean today, GetPlanCallback callback) {
        getPlan(today, callback, false);
    }

    public void getPlan(boolean today, GetPlanCallback callback, boolean force) {
        int interval = force ? 0 : updateInterval;
        if (today) {
            updatePlan(callback, "today", interval);
        } else {
            updatePlan(callback, "tomorrow", interval);
        }

    }

    private void updatePlan(GetPlanCallback userCb, final String day, int interval) {
        final GetPlanCallback callbackf = userCb;
        ApiParams params = new ApiParams();
        params.addParam("day", day);
        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                if (success) {
                    try {
                        readPlan(result);
                        sendCallback(callbackf, dayString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API", "API call failed " + resultStr);
                }
            }
        };
        ApiCache.call("ajax_vplan_get", params, callback, interval);
    }

    private void sendCallback(GetPlanCallback callback, String day) {
        if (callback != null) {
            callback.callback(day, new VplanInfo(this.marquee), this.entries);
        }
    }

    private void readPlan(JSONObject plan) throws JSONException {
        JSONArray marquee = plan.getJSONArray("marquee");
        this.marquee.clear();
        for (int i = 0; i < marquee.length(); i++) {
            this.marquee.add(marquee.getString(i));
        }
        this.entries.clear();
        JSONArray entries = plan.getJSONArray("entries");
        for (int j = 0; j < entries.length(); j++) {
            this.entries.add(VplanEntry.createFromJson(entries.getJSONObject(j)));
        }
        dayString = plan.getString("status_message");
        dayString = dayString.replace("Unterrichtsvertretung für ", "").replace("den ", "");
    }
}
