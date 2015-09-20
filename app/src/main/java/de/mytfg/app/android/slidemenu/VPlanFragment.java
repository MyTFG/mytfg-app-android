package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class VPlanFragment extends AbstractFragment {
    View vplanview;
    private RecyclerView vPlanList;
    private String selected_day;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vplanview = inflater.inflate(R.layout.vplan_layout, container, false);

        if (!MyTFG.isLoggedIn()) {
            MainActivity.navigation.navigate(Navigation.ItemNames.SETTINGS);
            return null;
        }

        // terminalList displays terminalEntries
        vPlanList = (RecyclerView) vplanview.findViewById(R.id.vPlanList);
        vPlanList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(vplanview.getContext());
        vPlanList.setLayoutManager(linearLayoutManager);

        Button todayButton = (Button) vplanview.findViewById(R.id.button_today);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_day = "today";
                refreshVPlanEntries();
            }
        });

        Button tomorrowButton = (Button) vplanview.findViewById(R.id.button_tomorrow);
        tomorrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_day = "tomorrow";
                refreshVPlanEntries();
            }
        });

        // set default value for selected_day
        selected_day = "today";

        refreshVPlanEntries();
        return vplanview;
    }

    private void refreshVPlanEntries() {
        ApiParams params = new ApiParams();
        params.addParam("day", selected_day);
        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                Toast toast;
                if (success) {
                    try {
                        displayHeaderEntries(result.getJSONArray("entries").getJSONObject(0).getString("class"),
                                result.getString("status_message"));
                        displayVPlanEntries(result.getJSONArray("entries"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toast = Toast.makeText(vplanview.getContext(), "Error parsing JSON",
                                Toast.LENGTH_LONG);
                    }
                } else {
                    String error = "";
                    if (resultStr != null) {
                        error = resultStr;
                    }
                    toast = Toast.makeText(vplanview.getContext(), "Fehlgeschlagen: " + responseCode
                            + " (" + error + ")", Toast.LENGTH_LONG);
                }
                toast.show();
            }
        };
        MytfgApi.call("ajax_vplan_get", params, callback);
    }

    private void displayHeaderEntries(String school_class, String date) {
        // split string to get useful data
        String date_new[] = date.split("für");
        TextView textview_school_class = (TextView) vplanview.findViewById(R.id.school_class);
        textview_school_class.setText(school_class);
        TextView textview_date = (TextView) vplanview.findViewById(R.id.date);
        textview_date.setText(date_new[1]);
    }

    private void displayVPlanEntries(JSONArray jsonVPlanEntries) throws JSONException {
        List<VPlanEntry> vPlanEntries = new ArrayList<>();
        for (int i = 0; i < jsonVPlanEntries.length(); i++) {
            JSONObject obj = jsonVPlanEntries.getJSONObject(i);
            vPlanEntries.add(new VPlanEntry(
                            obj.getString("lesson"),
                            obj.getString("plan"),
                            obj.getString("substitution"),
                            obj.getString("comment"))
            );
        }
        RVAdapter adapter = new RVAdapter(vPlanEntries);
        vPlanList.setAdapter(adapter);
    }

    class VPlanEntry {
        String lesson;
        String plan;
        String substitution;
        String comment;

        VPlanEntry(String lesson, String plan, String comment, String substitution) {
            this.lesson = lesson;
            this.plan = plan;
            this.substitution = substitution;
            this.comment = comment;
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.VPlanViewHolder> {

        List<VPlanEntry> vplanEntries;

        RVAdapter(List<VPlanEntry> entries) {
            this.vplanEntries = entries;
        }

        @Override
        public int getItemCount() {
            return vplanEntries.size();
        }

        @Override
        public VPlanViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vplan_view_layout, viewGroup, false);
            return new VPlanViewHolder(v);
        }

        @Override
        public void onBindViewHolder(VPlanViewHolder vplanViewHolder, int i) {
            String lesson = vplanEntries.get(i).lesson;
            vplanViewHolder.lessonText.setText(lesson);

            String regular = vplanEntries.get(i).plan;
            vplanViewHolder.regularText.setText(regular);

            String substitution = vplanEntries.get(i).substitution;
            vplanViewHolder.substitutionText.setText(substitution);

            String comment = vplanEntries.get(i).comment;
            vplanViewHolder.substitutionText.setText(comment);

           // vplanViewHolder.titleText.setTextColor(getResources().getColor(R.color.white));
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class VPlanViewHolder extends RecyclerView.ViewHolder {
            CardView vplanView;
            TextView lessonText;
            TextView regularText;
            TextView substitutionText;
            TextView commentText;

            VPlanViewHolder(View itemView) {
                super(itemView);
                vplanView = (CardView) itemView.findViewById(R.id.vplanView);
                lessonText = (TextView) itemView.findViewById(R.id.lesson_text);
                regularText = (TextView) itemView.findViewById(R.id.regular_text);
                substitutionText = (TextView) itemView.findViewById(R.id.substitution_text);
                commentText = (TextView) itemView.findViewById(R.id.comment_text);
            }
        }
    }
}
