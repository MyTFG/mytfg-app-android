package de.mytfg.app.android.slidemenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import de.mytfg.app.android.R;
import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;

public class StartFragment extends Fragment {
    View startview;
    SharedPreferences preferences;
    private String mytfg_login_user;
    private String mytfg_login_token;
    private String mytfg_login_device;
    private RecyclerView notificationList;
    private List<Notification> notifications;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        startview = inflater.inflate(R.layout.start_layout, container, false);

        preferences = startview.getContext().getSharedPreferences(getString(R.string.sharedpref_settings), Context.MODE_MULTI_PROCESS);

        // user, token and device id for MytfgApi calls
        mytfg_login_user = preferences.getString(getString(R.string.settings_login_username), "");
        mytfg_login_token = preferences.getString(getString(R.string.settings_login_token), "");
        mytfg_login_device = Settings.Secure.getString(startview.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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
        params.addParam("mytfg_api_login_user", mytfg_login_user);
        params.addParam("mytfg_api_login_token", mytfg_login_token);
        params.addParam("mytfg_api_login_device", mytfg_login_device);
        params.addParam("group", "false");
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
        MytfgApi.call("ajax_notification_list", params, callback);
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
        RVAdapter adapter = new RVAdapter(notifications);
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
            if (!notifications.get(i).acknowledged) {
                notificationViewHolder.titleText.setTextColor(getResources().getColor(R.color.orange_accent));
            }
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

            NotificationViewHolder(View itemView) {
                super(itemView);
                notificationView = (CardView)itemView.findViewById(R.id.notificationView);
                typeIcon = (ImageView)itemView.findViewById(R.id.type_icon);
                titleText = (TextView)itemView.findViewById(R.id.title_text);
                textText = (TextView)itemView.findViewById(R.id.text_text);
                statusIcon = (ImageView)itemView.findViewById(R.id.status_icon);
            }
        }
    }

}
