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
                            obj.getString("substitution"))
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

        VPlanEntry(String school_class, String lesson, String plan, String substitution) {
            this.school_class = school_class;
            this.lesson = lesson;
            this.plan = plan;
            this.substitution = substitution;
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TerminalViewHolder> {

        List<VPlanEntry> vplanEntries;

        RVAdapter(List<VPlanEntry> entries) {
            this.vplanEntries = entries;
        }

        @Override
        public int getItemCount() {
            return vplanEntries.size();
        }

        @Override
        public TerminalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.terminal_view_layout, viewGroup, false);
            return new TerminalViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TerminalViewHolder terminalViewHolder, int i) {
            String title = vplanEntries.get(i).school_class;
            terminalViewHolder.titleText.setText(title);

            String authorDateText = vplanEntries.get(i).lesson;
            terminalViewHolder.authorDateText.setText(authorDateText);

            String mainText = vplanEntries.get(i).plan + " - " + vplanEntries.get(i).substitution;
            terminalViewHolder.mainText.setText(mainText);

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
            TextView mainText;

            TerminalViewHolder(View itemView) {
                super(itemView);
                terminalView = (CardView) itemView.findViewById(R.id.terminalView);
                titleText = (TextView) itemView.findViewById(R.id.title_text);
                authorDateText = (TextView) itemView.findViewById(R.id.author_date_text);
                mainText = (TextView) itemView.findViewById(R.id.flags_text);
            }
        }
    }
}
