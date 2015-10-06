package de.mytfg.app.android.modules.terminal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.Module;
import de.mytfg.app.android.modules.general.User;
import de.mytfg.app.android.modules.terminal.objects.Flag;
import de.mytfg.app.android.modules.terminal.objects.Topic;

/**
 * Assisting Module for Topic Creation.
 */
public class TerminalCreator extends Module {
    private final long flagTimeout = 3600000;
    private final long workerTimeout = 600000;
    private final long dependencyTimeout = 60000;

    private String title;
    private String text;
    private List<User> workers = new LinkedList<>();
    private List<User> possibleWorkers = new LinkedList<>();
    private boolean hasDeadline = false;
    private long deadline = System.currentTimeMillis();
    private List<Flag> flags = new LinkedList<>();
    private List<Flag> possibleFlags = new LinkedList<>();
    private List<Topic> dependencies = new LinkedList<>();
    private List<Topic> possibleDependencies = new LinkedList<>();

    private long flagsLoaded = 0;
    private long workersLoaded = 0;
    private long dependenciesLoaded = 0;

    private OnResetListener onReset = new OnResetListener() {
        @Override
        public void onReset() {

        }
    };

    private TerminalCreatorCallback onCreate = new TerminalCreatorCallback() {
        @Override
        public void callback(boolean success, long id, String error) {

        }
    };

    public interface TerminalCreatorCallback {
        void callback(boolean success, long id, String error);
    }

    public TerminalCreator() {
        reset();
    }

    public void setOnResetListener(OnResetListener listener) {
        this.onReset = listener;
    }

    public void setOnCreateListener(TerminalCreatorCallback listener) {
        this.onCreate = listener;
    }

    private boolean check() {
        return (!this.title.equals("")
                && !this.text.equals(""));
    }

    public void create() {
        final TerminalCreatorCallback callback = this.onCreate;
        if (!check()) {
            callback.callback(false, -1, "Titel und Text dürfen nicht leer sein.");
            return;
        }
        ApiParams params = new ApiParams();
        JSONObject json = new JSONObject();
        try {
            json.put("title", this.title);
            json.put("text", this.text);
            if (this.hasDeadline) {
                json.put("deadline", this.deadline / 1000);
            } else {
                json.put("deadline", -1);
            }

            JSONArray flags = new JSONArray();
            for (Flag flag : this.flags) {
                flags.put(flag.getId());
            }
            json.put("flags", flags);

            JSONArray workers = new JSONArray();
            for (User worker : this.workers) {
                workers.put(worker.getId());
            }
            json.put("workers", workers);

            JSONArray dependencies = new JSONArray();
            for (Topic depen : this.dependencies) {
                dependencies.put(depen.getId());
            }
            json.put("dependencies", dependencies);

            params.addParam("data", json.toString());
            params.doLogin();


            MytfgApi.call("ajax_terminal_create-topic", params, new MytfgApi.ApiCallback() {
                @Override
                public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                    try {
                        if (success) {
                            callback.callback(true, result.getLong("topicid"), "");
                        } else {
                            callback.callback(false, -1, result.getString("error"));
                        }
                    } catch (JSONException ex) {
                        callback.callback(false, -1, "Could not parse API-Result");
                    }
                }
            });

        } catch (JSONException e) {
            callback.callback(false, -1, "Error creating request");
        }

    }

    public void reset() {
        title = "";
        text = "";
        workers.clear();
        deadline = System.currentTimeMillis();
        flags.clear();
        dependencies.clear();
        hasDeadline = false;
        onReset.onReset();
    }

    public interface FlagListCallback {
        void callback(List<Flag> list);
    }

    public interface WorkerListCallback {
        void callback(List<User> list);
    }

    public interface DependencyListCallback {
        void callback(List<Topic> list);
    }

    public interface OnResetListener {
        void onReset();
    }

    public void getFlagList(FlagListCallback callback) {
        if (System.currentTimeMillis() - this.flagTimeout > this.flagsLoaded) {
            this.loadFlags(callback);
        } else {
            callback.callback(this.possibleFlags);
        }
    }

    public void getWorkerList(WorkerListCallback callback) {
        if (System.currentTimeMillis() - this.workerTimeout > this.workersLoaded) {
            this.loadWorkers(callback);
        } else {
            callback.callback(this.possibleWorkers);
        }
    }

    public void getDependencyList(DependencyListCallback callback) {
        if (System.currentTimeMillis() - this.dependencyTimeout > this.dependenciesLoaded) {
            this.loadDependecies(callback);
        } else {
            callback.callback(this.possibleDependencies);
        }
    }

    public void addFlag(Flag flag) {
        flags.add(flag);
    }

    public void addWorker(User worker) {
        workers.add(worker);
    }

    public void addDependency(Topic depen) {
        dependencies.add(depen);
    }

    public void removeFlag(Flag flag) {
        flags.remove(flag);
    }

    public void removeWorker(User worker) {
        workers.remove(worker);
    }

    public void removeDependency(Topic depen) {
        dependencies.remove(depen);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHasDeadline(boolean hasDeadline) {
        this.hasDeadline = hasDeadline;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    // GETTERS
    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public List<User> getWorkers() {
        return workers;
    }

    public long getDeadline() {
        return deadline;
    }

    public boolean hasDeadline() {
        return hasDeadline;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }

    public boolean hasWorker(User user) {
        return workers.contains(user);
    }

    public boolean hasDependency(Topic topic) {
        return dependencies.contains(topic);
    }

    public List<Topic> getDependencies() {
        return dependencies;
    }

    private void loadDependecies(final DependencyListCallback callback) {
        MytfgApi.call("ajax_terminal_list-dependencies", new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                if (success) {
                    try {
                        setLoadedDependencies(result, callback);
                    } catch (Exception e) {
                        callback.callback(new LinkedList<Topic>());
                        e.printStackTrace();
                    }
                } else {
                    callback.callback(new LinkedList<Topic>());
                }
            }
        });
    }

    private void setLoadedDependencies(JSONObject result, DependencyListCallback cb) throws JSONException {
        possibleDependencies.clear();
        for (int i = 0; i < result.getJSONArray("objects").length(); i++) {
            possibleDependencies.add(Topic.createFromJson(result.getJSONArray("objects").getJSONObject(i),
                    result.getJSONObject("references")));
        }
        dependenciesLoaded = System.currentTimeMillis();
        cb.callback(this.possibleDependencies);
    }



    private void loadWorkers(final WorkerListCallback callback) {
        MytfgApi.call("ajax_terminal_list-workers", new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                if (success) {
                    try {
                        JSONArray fArray = result.getJSONArray("objects");
                        setLoadedWorkers(fArray, callback);
                    } catch (Exception e) {
                        callback.callback(new LinkedList<User>());
                        e.printStackTrace();
                    }
                } else {
                    callback.callback(new LinkedList<User>());
                }
            }
        });
    }

    private void setLoadedWorkers(JSONArray fArray, WorkerListCallback cb) throws JSONException {
        possibleWorkers.clear();
        for (int i = 0; i < fArray.length(); i++) {
            possibleWorkers.add(User.createFromJson(fArray.getJSONObject(i)));
        }
        workersLoaded = System.currentTimeMillis();
        cb.callback(this.possibleWorkers);
    }

    private void loadFlags(final FlagListCallback callback) {
        MytfgApi.call("ajax_terminal_list-flags", new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                if (success) {
                    try {
                        JSONArray fArray = result.getJSONArray("objects");
                        setLoadedFlags(fArray, callback);
                    } catch (Exception e) {
                        callback.callback(new LinkedList<Flag>());
                        e.printStackTrace();
                    }
                } else {
                    callback.callback(new LinkedList<Flag>());
                }
            }
        });
    }

    private void setLoadedFlags(JSONArray fArray, FlagListCallback cb) throws JSONException {
        possibleFlags.clear();
        for (int i = 0; i < fArray.length(); i++) {
            possibleFlags.add(Flag.createFromJson(fArray.getJSONObject(i)));
        }
        flagsLoaded = System.currentTimeMillis();
        cb.callback(this.possibleFlags);
    }
}
