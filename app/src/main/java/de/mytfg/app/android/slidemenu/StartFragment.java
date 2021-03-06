package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.api.ApiCache;
import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.slidemenu.items.Navigation;
import de.mytfg.app.android.utils.TimeUtils;

public class StartFragment extends AbstractFragment {
    View startview;
    private RecyclerView notificationList;
    private List<Notification> notifications;
    RVAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RVAdapter(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        startview = inflater.inflate(R.layout.start_layout, container, false);

        if (!MyTFG.isLoggedIn()) {
            MainActivity.navigation.navigate(Navigation.ItemNames.SETTINGS);
            return null;
        }

        // notificationList displays notifications
        notificationList = (RecyclerView) startview.findViewById(R.id.notificationList);
        notificationList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(startview.getContext());
        notificationList.setLayoutManager(linearLayoutManager);


        refreshNotifications();
        return startview;
    }

    private void refreshNotifications() {
        ApiParams params = new ApiParams();
        params.addParam("group", "false");

        if (MyTFG.preferences.getBoolean(getResources().getString(R.string.pref_notification_group), false)) {
            params.addParam("group", "true");
        } else {
            params.addParam("group", "false");
        }

        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                Toast toast;
                if (success) {
                    try {
                        displayNotifications(result.getJSONArray("notifications"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toast = Toast.makeText(startview.getContext(), "Error parsing JSON", Toast.LENGTH_LONG);
                    }
                } else {
                    String error = "";
                    if(resultStr != null) {
                        error = resultStr;
                    }
                    toast = Toast.makeText(startview.getContext(), "Fehlgeschlagen: " + responseCode
                            + " (" + error + ")", Toast.LENGTH_LONG);
                }
                toast.show();
            }
        };
        ApiCache.call("ajax_notification_list", params, callback, 0);
    }

    private void displayNotifications(JSONArray jsonNotifications) throws JSONException {
        notifications = new ArrayList<>();
        for(int i = 0; i < jsonNotifications.length(); i++) {
            JSONObject obj = jsonNotifications.getJSONObject(i);
            String text = "";
            if(obj.has("data") && obj.getJSONObject("data").has("text")) {
                text = obj.getJSONObject("data").getString("text");
            }
            notifications.add(new Notification(
                            obj.getString("title"),
                            obj.getString("description"),
                            obj.getString("acknowledged").equals("1"),
                            Long.parseLong(obj.getString("timestamp")),
                            Long.parseLong(obj.getString("id")),
                            obj.getString("type"),
                            obj.getString("grouper"), text)
            );
        }
        adapter.notifications = notifications;
        notificationList.setAdapter(adapter);
    }

    class Notification {
        String title;
        String description;
        boolean acknowledged;
        int statusIconId;
        Long timestamp;
        Long id;
        String type;
        int typeIconId;
        String grouper;
        String text;

        Notification(String title, String description, boolean acknowledged, long timestamp,
                     long id, String type, String grouper, String text) {
            this.title = title;
            this.description = description;
            this.acknowledged = acknowledged;
            this.statusIconId = 0; // TODO
            this.timestamp = timestamp;
            this.id = id;
            this.type = type;
            this.typeIconId = 0; // TODO
            this.grouper = grouper;
            this.text = text;
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.NotificationViewHolder>{

        List<Notification> notifications;

        RVAdapter(List<Notification> persons){
            this.notifications = persons;
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_view_layout, viewGroup, false);
            return new NotificationViewHolder(v);
        }

        @Override
        public void onBindViewHolder(NotificationViewHolder notificationViewHolder, int i) {
            notificationViewHolder.typeIcon.setImageResource(notifications.get(i).typeIconId);
            notificationViewHolder.titleText.setText(notifications.get(i).title);
            notificationViewHolder.textText.setText(notifications.get(i).description);
            notificationViewHolder.statusIcon.setImageResource(notifications.get(i).statusIconId);
            notificationViewHolder.datetimeText.setText(TimeUtils.getDateStringShort(notifications.get(i).timestamp));
            if (!notifications.get(i).acknowledged) {
                notificationViewHolder.titleText.setTextColor(getResources().getColor(R.color.orange_accent));
            }

            final Notification notify = notifications.get(i);

            notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (notify.type) {
                        case "terminal":
                            String[] grouper = notify.grouper.split("-");
                            if (grouper.length == 3 && grouper[0].equals("terminal") && grouper[1].equals("topic")) {
                                long topicId = Long.parseLong(grouper[2]);
                                Bundle args = new Bundle();
                                args.putLong("topic", topicId);
                                args.putString("title", "Laden...");
                                MainActivity.navigation.navigate(Navigation.ItemNames.TERMINAL_TOPIC, args);
                            } else {
                                Toast toast = Toast.makeText(MyTFG.getAppContext(), "Unknown Grouper", Toast.LENGTH_LONG);
                                toast.show();
                            }
                            break;
                        case "message":
                            grouper = notify.grouper.split("-");
                            if (grouper.length == 2 && grouper[0].equals("conversation")) {
                                long conversationId = Long.parseLong(grouper[1]);
                                Bundle args = new Bundle();
                                args.putLong("conversationId", conversationId);
                                MainActivity.navigation.navigate(Navigation.ItemNames.CONVERSATION, args);
                            } else {
                                Toast toast = Toast.makeText(MyTFG.getAppContext(), "Unknown Grouper", Toast.LENGTH_LONG);
                                toast.show();
                            }
                            break;
                        default:
                            Toast toast = Toast.makeText(MyTFG.getAppContext(), "Not implemented yet", Toast.LENGTH_LONG);
                            toast.show();
                            break;
                    }
                }
            });
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class NotificationViewHolder extends RecyclerView.ViewHolder {
            CardView notificationView;
            ImageView typeIcon;
            TextView titleText;
            TextView textText;
            ImageView statusIcon;
            TextView datetimeText;

            NotificationViewHolder(View itemView) {
                super(itemView);
                notificationView = (CardView)itemView.findViewById(R.id.notificationView);
                typeIcon = (ImageView)itemView.findViewById(R.id.type_icon);
                titleText = (TextView)itemView.findViewById(R.id.title_text);
                textText = (TextView)itemView.findViewById(R.id.text_text);
                statusIcon = (ImageView)itemView.findViewById(R.id.status_icon);
                datetimeText = (TextView)itemView.findViewById(R.id.datetime_text);
            }
        }
    }

}
