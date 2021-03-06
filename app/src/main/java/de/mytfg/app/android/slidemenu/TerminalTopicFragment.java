package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.general.User;
import de.mytfg.app.android.modules.terminal.TerminalTopic;
import de.mytfg.app.android.modules.terminal.objects.Flag;
import de.mytfg.app.android.modules.terminal.objects.Review;
import de.mytfg.app.android.modules.terminal.objects.Topic;
import de.mytfg.app.android.slidemenu.items.Navigation;
import de.mytfg.app.android.utils.TimeUtils;
import in.uncod.android.bypass.Bypass;

public class TerminalTopicFragment extends AbstractFragment {
    View terminalentryview;
    private RecyclerView terminalTopicList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.terminal_topic_menu, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        terminalentryview = inflater.inflate(R.layout.terminal_topic_layout, container, false);
        if (!verifyArgs()) {
            return null;
        }
        initialize();
        return terminalentryview;
    }

    private boolean verifyArgs() {
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
        TerminalTopic terminalModule = (TerminalTopic) MyTFG.moduleManager.getModule(Modules.TERMINALTOPIC);
        terminalModule.setId(topicId);
        return true;
    }

    public void initialize() {
        long topicId = args.getLong("topic", 0);
        if (topicId != 0) {
            // terminalList displays terminalEntries
            terminalTopicList = (RecyclerView) terminalentryview.findViewById(R.id.terminalList);
            terminalTopicList.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(terminalTopicList.getContext());
            terminalTopicList.setLayoutManager(linearLayoutManager);

            ImageButton send = (ImageButton) terminalentryview.findViewById(R.id.terminalReplyButton);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText response = (EditText) terminalentryview.findViewById(R.id.respond_text);
                    if (response.getText().length() > 0) {
                        ApiParams params = new ApiParams();
                        params.doLogin();
                        params.addParam("topic", "" + args.getLong("topic"));
                        params.addParam("text", response.getText().toString());
                        params.addParam("isPrivate", "true");

                        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
                            @Override
                            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                                if (success) {
                                    response.setText("");
                                    refreshTerminalTopic(true);
                                } else {
                                    if (result != null) {
                                        try {
                                            Toast.makeText(MainActivity.context, "Error: " + result.getString("error"), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            Toast.makeText(MainActivity.context, "Unknown Error", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.context, "Invalid JSON: " + resultStr, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        };

                        MytfgApi.call("ajax_terminal_createreview", params, callback);
                    }
                }
            });

            refreshTerminalTopic();
        }
    }

    private void refreshTerminalTopic() {
        refreshTerminalTopic(false);
    }

    private void refreshTerminalTopic(final boolean scrollToBottom) {
        TerminalTopic terminalModule = (TerminalTopic) MyTFG.moduleManager.getModule(Modules.TERMINALTOPIC);
        terminalModule.getTopic(new TerminalTopic.GetTopicCallback() {
            @Override
            public void callback(Topic topic, boolean stillLoading) {
                showTopic(topic);
            }
        });
    }

    private void showTopic(Topic topic) {
        if(MainActivity.navigation.getCurrentItem().getItem() == this.item.getItem()) {
            ((MainActivity)MainActivity.context).getSupportActionBar().setTitle(topic.getTitle());
        }
        RVAdapter adapter = new RVAdapter(topic.getReviews());
        terminalTopicList.setAdapter(adapter);
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TerminalTopicViewHolder>{

        List<Review> terminalReviews;

        RVAdapter(List<Review> reviews){
            this.terminalReviews = reviews;
        }

        @Override
        public int getItemViewType(int position) {
            return terminalReviews.get(position).getReviewType().getId();
        }

        @Override
        public int getItemCount() {
            return terminalReviews.size();
        }

        @Override
        public TerminalTopicViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v;
            if (viewType == 0) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.terminal_topic_view_layout, viewGroup, false);
            } else {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.terminal_topic_special_view_layout, viewGroup, false);
            }
            return new TerminalTopicViewHolder(v, viewType);
        }

        @Override
        public void onBindViewHolder(TerminalTopicViewHolder terminalTopicViewHolder, int i) {

            Review review = terminalReviews.get(i);
            if (review.getReviewType().getId() == 0) {
                // Normal text Review
                String title = terminalReviews.get(i).getAuthor().toString();
                terminalTopicViewHolder.titleText.setText(title);

                String authorDateText = TimeUtils.getDateStringShort(terminalReviews.get(i).getCreated());
                terminalTopicViewHolder.authorDateText.setText(authorDateText);

                Bypass bypass = new Bypass(MyTFG.getAppContext());
                CharSequence string = bypass.markdownToSpannable(terminalReviews.get(i).getText());
                terminalTopicViewHolder.mainText.setText(string);
                terminalTopicViewHolder.mainText.setMovementMethod(LinkMovementMethod.getInstance());

                terminalTopicViewHolder.titleText.setTextColor(getResources().getColor(R.color.white));
            } else {
                terminalTopicViewHolder.actionText.setTextColor(getResources().getColor(R.color.white));
                String authorDateText = TimeUtils.getDateStringShort(terminalReviews.get(i).getCreated());
                terminalTopicViewHolder.authorDateText.setText(authorDateText);
                try {
                    JSONObject json = new JSONObject(review.getText());
                    String text;
                    String action;
                    switch (review.getReviewType().getId()) {
                        case 1:
                            // FLAG
                            action = json.getString("action");
                            Flag flag = Flag.createFromJson(json.getJSONObject("references")
                                    .getJSONObject("terminalflag").getJSONObject(json.getString("flag")));
                            text = review.getAuthor().toString() + " hat Flag „"
                                    + flag.getName()
                                    + (action.equals("removed") ? "“ entfernt." : "“ hinzugefügt.");
                            terminalTopicViewHolder.actionText.setText(text);
                            break;
                        case 3:
                            // Added / removed worker
                            action = json.getString("action");
                            text = review.getAuthor().toString() + " hat Bearbeiter ";
                            JSONArray workers = json.getJSONArray("workers");
                            for (int j = 0; j < workers.length(); j++) {
                                User worker = User.createFromJson(json.getJSONObject("references")
                                        .getJSONObject("user").getJSONObject(workers.getString(j)));
                                if (j > 0) {
                                    text += ", ";
                                }
                                text += worker.toString();
                            }
                            text += (action.equals("removed") ? " entfernt." : " hinzugefügt.");
                            terminalTopicViewHolder.actionText.setText(text);
                            break;
                        case 4:
                            // Changed title
                            text = review.getAuthor().toString() + " hat Titel von „"
                                    + json.getString("from") + "“ nach „" + json.getString("to")
                                    + "“ geändert.";
                            terminalTopicViewHolder.actionText.setText(text);
                            break;
                        default:
                            // TODO: CASE 2 (Not implemented in API)
                            terminalTopicViewHolder.actionText.setText("Unbekannter Beitrag");
                            break;

                    }
                } catch (JSONException e) {
                    terminalTopicViewHolder.actionText.setText("Fehlerhafter Beitrag");
                    e.printStackTrace();
                }

            }
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
            TextView actionText;

            TerminalTopicViewHolder(View itemView, int type) {
                super(itemView);
                if (type == 0) {
                    terminalTopicView = (CardView) itemView.findViewById(R.id.terminalTopicView);
                    titleText = (TextView) itemView.findViewById(R.id.title_text);
                    mainText = (TextView) itemView.findViewById(R.id.main_text);
                } else {
                    actionText = (TextView)itemView.findViewById(R.id.title_action);
                }
                authorDateText = (TextView)itemView.findViewById(R.id.author_date_text);
            }
        }
    }
}
