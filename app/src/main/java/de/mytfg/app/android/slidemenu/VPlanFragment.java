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

        refreshVPlanEntries();
        return vplanview;
    }

    private void refreshVPlanEntries() {
        ApiParams params = new ApiParams();
        params.addParam("day", "today");
        MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
            @Override
            public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                Toast toast;
                if (success) {
                    try {
                        displayVPlanEntries(result.getJSONArray("entries"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toast = Toast.makeText(vplanview.getContext(), "Error parsing JSON", Toast.LENGTH_LONG);
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

    private void displayVPlanEntries(JSONArray jsonVPlanEntries) throws JSONException {
        List<VPlanEntry> vPlanEntries = new ArrayList<>();
        for (int i = 0; i < jsonVPlanEntries.length(); i++) {
            JSONObject obj = jsonVPlanEntries.getJSONObject(i);
            vPlanEntries.add(new VPlanEntry(
                            obj.getString("class"),
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
        String school_class;
        String lesson;
        String plan;
        String substitution;
        String comment;

        VPlanEntry(String school_class, String lesson, String plan, String comment, String substitution) {
            this.school_class = school_class;
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
