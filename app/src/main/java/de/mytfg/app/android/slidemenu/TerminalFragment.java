package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.terminal.TerminalTopics;
import de.mytfg.app.android.modules.terminal.objects.Topic;
import de.mytfg.app.android.slidemenu.items.Navigation;

public class TerminalFragment extends AbstractFragment {
    View terminalview;
    private String mytfg_login_user;
    private String mytfg_login_token;
    private String mytfg_login_device;
    private RecyclerView terminalList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        terminalview = inflater.inflate(R.layout.terminal_layout, container, false);

        if (!MyTFG.isLoggedIn()) {
            MainActivity.navigation.navigate(Navigation.ItemNames.SETTINGS);
            return null;
        }

        // Draw Floating Button for new Topic
        FloatingActionButton fab = (FloatingActionButton) terminalview.findViewById(R.id.terminal_create_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           MainActivity.navigation.navigate(Navigation.ItemNames.START, new Bundle(), Navigation.Transition.SLIDE, true);
            }
        });

        // terminalList displays terminalEntries
        terminalList = (RecyclerView) terminalview.findViewById(R.id.terminalList);
        terminalList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(terminalview.getContext());
        terminalList.setLayoutManager(linearLayoutManager);


        // refreshTerminalEntries();
        TerminalTopics module = (TerminalTopics)MyTFG.moduleManager.getModule(Modules.TERMINALTOPICS);

        module.getTopics(new TerminalTopics.GetTopicsCallback() {
            @Override
            public void callback(List<Topic> topics) {
                RVAdapter adapter = new RVAdapter(topics);
                terminalList.setAdapter(adapter);
            }
        });

        return terminalview;
    }

    private void refreshTerminalEntries() {
        ApiParams params = new ApiParams();
        params.addParam("all", "false");
        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                Toast toast;
                if (success) {
                    try {
                        displayTerminalEntries(result.getJSONArray("topics"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toast = Toast.makeText(terminalview.getContext(), "Error parsing JSON", Toast.LENGTH_LONG);
                    }
                } else {
                    String error = "";
                    if(resultStr != null) {
                        error = resultStr;
                    }
                    toast = Toast.makeText(terminalview.getContext(), "Fehlgeschlagen: " + responseCode
                            + " (" + error + ")", Toast.LENGTH_LONG);
                }
                toast.show();
            }
        };
        MytfgApi.call("ajax_terminal_topics", params, callback);
    }

    private void displayTerminalEntries(JSONArray jsonTerminalEntries) throws JSONException {
        List<TerminalEntry> terminalEntries = new ArrayList<>();
        for(int i = 0; i < jsonTerminalEntries.length(); i++) {
            JSONObject obj = jsonTerminalEntries.getJSONObject(i);
            terminalEntries.add(new TerminalEntry(
                            obj.getString("title"),
                            obj.getString("author"),
                            obj.getJSONArray("flags"),
                            Long.parseLong(obj.getString("created")),
                            Long.parseLong(obj.getString("edited")),
                            Long.parseLong(obj.getString("id")),
                            obj.getJSONArray("workers"),
                            Long.parseLong(obj.getString("code")))
            );
        }
        //RVAdapter adapter = new RVAdapter(terminalEntries);
        //terminalList.setAdapter(adapter);
    }

    class TerminalEntry {
        String title;
        String author;
        JSONArray flags;
        long created;
        long edited;
        long id;
        JSONArray workers;
        long code;

        TerminalEntry(String title, String author, JSONArray flags, long created,
                      long edited, long id, JSONArray workers, long code) {
            this.title = title;
            this.author = author;
            this.flags = flags;
            this.created = created;
            this.edited = edited;
            this.id = id;
            this.workers = workers;
            this.code = code;
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TerminalViewHolder>{

        List<Topic> topics;

        RVAdapter(List<Topic> topics){
            this.topics = topics;
        }

        @Override
        public int getItemCount() {
            return topics.size();
        }

        @Override
        public TerminalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.terminal_view_layout, viewGroup, false);
            return new TerminalViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TerminalViewHolder terminalViewHolder, int i) {
            String title = "#" + topics.get(i).getId() + " - " + topics.get(i).getTitle();
            terminalViewHolder.titleText.setText(title);

            String authorDateText = getString(R.string.terminal_created) + " " + MyTFG.getDate(topics.get(i).getCreated())  + " " + getString(R.string.terminal_from) + " " + topics.get(i).getAuthor();
            terminalViewHolder.authorDateText.setText(authorDateText);

            if (topics.get(i).getCreated() != topics.get(i).getEdited()) {
                terminalViewHolder.editedDateText.setText(getString(R.string.terminal_edited) + ": " + MyTFG.getDate(topics.get(i).getEdited()));
            }

            final Topic topic = topics.get(i);

            terminalViewHolder.terminalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putLong("topic", topic.getId());
                    args.putString("title", topic.getTitle());
                    MainActivity.navigation.navigate(Navigation.ItemNames.TERMINAL_TOPIC, args);
                }
            });

            terminalViewHolder.flagsText.setText(topics.get(i).getFlags().toString());

            for (int j = 0; j < topics.get(i).getWorkers().size(); j++) {
                if (topics.get(i).getWorkers().get(j).getId() == MyTFG.getUserId()) {
                    terminalViewHolder.titleText.setTextColor(getResources().getColor(R.color.orange_accent));
                    return;
                }
            }
            terminalViewHolder.titleText.setTextColor(getResources().getColor(R.color.white));
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class TerminalViewHolder extends RecyclerView.ViewHolder {
            CardView terminalView;
            TextView titleText;
            TextView authorDateText;
            TextView editedDateText;
            TextView flagsText;

            TerminalViewHolder(View itemView) {
                super(itemView);
                terminalView = (CardView)itemView.findViewById(R.id.terminalView);
                titleText = (TextView)itemView.findViewById(R.id.title_text);
                authorDateText = (TextView)itemView.findViewById(R.id.author_date_text);
                editedDateText = (TextView)itemView.findViewById(R.id.edited_date_text);
                flagsText = (TextView)itemView.findViewById(R.id.flags_text);
            }
        }
    }
}
