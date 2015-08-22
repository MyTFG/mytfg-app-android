package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import de.mytfg.app.android.slidemenu.items.Navigation;

public class TerminalTopicFragment extends AbstractFragment {
    View terminalentryview;
    private String mytfg_login_user;
    private String mytfg_login_token;
    private String mytfg_login_device;
    private RecyclerView terminalTopicList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        terminalentryview = inflater.inflate(R.layout.terminal_layout, container, false);
        initialize();
        return terminalentryview;
    }

    /**
     * Loads the given topic.
     * @param args Arguments required for loading.
     */
    public boolean setArgs(Bundle args) {
        this.args = args;
        long topicId = args.getLong("topic", 0);
        if (topicId == 0) {
            MainActivity.navigation.navigate(Navigation.ItemNames.TERMINAL);
            return false;
        } else {
            if (!MyTFG.isLoggedIn()) {
                MainActivity.navigation.navigate(Navigation.ItemNames.LOGIN);
                return false;
            }
        }
        return true;
    }

    public void initialize() {
        long topicId = args.getLong("topic", 0);
        if (topicId == 0) {
            return;
        } else {
            // user, token and device id for MytfgApi calls
            mytfg_login_user = MyTFG.getUsername();
            mytfg_login_token = MyTFG.getToken();
            mytfg_login_device = MyTFG.getDeviceId();

            // terminalList displays terminalEntries
            terminalTopicList = (RecyclerView) terminalentryview.findViewById(R.id.terminalList);
            terminalTopicList.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(terminalTopicList.getContext());
            terminalTopicList.setLayoutManager(linearLayoutManager);


            refreshTerminalTopic();
        }
    }

    private void refreshTerminalTopic() {
        ApiParams params = new ApiParams();
        params.addParam("mytfg_api_login_user", mytfg_login_user);
        params.addParam("mytfg_api_login_token", mytfg_login_token);
        params.addParam("mytfg_api_login_device", mytfg_login_device);
        params.addParam("topic", "" + args.getLong("topic"));

        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                Toast toast;
                if (success) {
                    try {
                        String title = result.getString("title");
                        ((MainActivity)MainActivity.context).getSupportActionBar().setTitle(title);
                        displayTerminalReviews(result.getJSONArray("reviews"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toast = Toast.makeText(terminalentryview.getContext(), "Error parsing JSON", Toast.LENGTH_LONG);
                    }
                } else {
                    String error = "";
                    if(resultStr != null) {
                        error = resultStr;
                    }
                    toast = Toast.makeText(terminalentryview.getContext(), "Fehlgeschlagen: " + responseCode
                            + " (" + error + ")", Toast.LENGTH_LONG);
                }
                toast.show();
            }
        };
        MytfgApi.call("ajax_terminal_topic", params, callback);
    }

    private void displayTerminalReviews(JSONArray jsonTerminalReviews) throws JSONException {
        List<TerminalReview> terminalReviews = new ArrayList<>();
        for(int i = 0; i < jsonTerminalReviews.length(); i++) {
            JSONObject obj = jsonTerminalReviews.getJSONObject(i);
            if (Integer.parseInt(obj.getString("type")) == 0) {
                terminalReviews.add(new TerminalReview(
                                obj.getString("authorname"),
                                Long.parseLong(obj.getString("created")),
                                Long.parseLong(obj.getString("edited")),
                                Long.parseLong(obj.getString("id")),
                                obj.getString("text"))
                );
            }
        }
        RVAdapter adapter = new RVAdapter(terminalReviews);
        terminalTopicList.setAdapter(adapter);
    }

    class TerminalReview {
        String author;
        long created;
        long edited;
        long id;
        String text;

        TerminalReview(String author, long created,
                       long edited, long id, String text) {
            this.author = author;
            this.created = created;
            this.edited = edited;
            this.id = id;
            this.text = text;
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TerminalTopicViewHolder>{

        List<TerminalReview> terminalReviews;

        RVAdapter(List<TerminalReview> persons){
            this.terminalReviews = persons;
        }

        @Override
        public int getItemCount() {
            return terminalReviews.size();
        }

        @Override
        public TerminalTopicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.terminal_topic_view_layout, viewGroup, false);
            return new TerminalTopicViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TerminalTopicViewHolder terminalTopicViewHolder, int i) {
            String title = terminalReviews.get(i).author;
            terminalTopicViewHolder.titleText.setText(title);

            String authorDateText = MyTFG.getDate(terminalReviews.get(i).created);
            terminalTopicViewHolder.authorDateText.setText(authorDateText);

            terminalTopicViewHolder.mainText.setText(terminalReviews.get(i).text);

            terminalTopicViewHolder.titleText.setTextColor(getResources().getColor(R.color.white));
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class TerminalTopicViewHolder extends RecyclerView.ViewHolder {
            CardView terminalTopicView;
            TextView titleText;
            TextView authorDateText;
            TextView mainText;

            TerminalTopicViewHolder(View itemView) {
                super(itemView);
                terminalTopicView = (CardView)itemView.findViewById(R.id.terminalView);
                titleText = (TextView)itemView.findViewById(R.id.title_text);
                authorDateText = (TextView)itemView.findViewById(R.id.author_date_text);
                mainText = (TextView)itemView.findViewById(R.id.main_text);
            }
        }
    }
}
