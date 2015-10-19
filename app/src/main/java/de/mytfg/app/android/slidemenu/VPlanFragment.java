package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStripV22;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.vplan.Vplan;
import de.mytfg.app.android.modules.vplan.objects.VplanEntry;
import de.mytfg.app.android.modules.vplan.objects.VplanInfo;
import de.mytfg.app.android.modules.vplan.objects.VplanObject;
import de.mytfg.app.android.slidemenu.items.Navigation;

public class VPlanFragment extends AbstractFragment {
    View vplanview;
    private RecyclerView vPlanList_today;
    private RecyclerView vPlanList_tomorrow;

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private PagerTabStripV22 tabs;

    private static String todayTitleDefault = "Heute";
    private static String tomorrowTitleDefault = "Morgen";

    private String todayTitle = todayTitleDefault;
    private String tomorrowTitle = tomorrowTitleDefault;

    private boolean multiClass = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vplanview = inflater.inflate(R.layout.vplan_layout, container, false);

        if (!MyTFG.isLoggedIn()) {
            MainActivity.navigation.navigate(Navigation.ItemNames.SETTINGS);
            return null;
        }

        mPager = (ViewPager) vplanview.findViewById(R.id.vplan_pager);
        mPagerAdapter = new VplanPageAdapter();
        mPager.setAdapter(mPagerAdapter);

        tabs = (PagerTabStripV22) vplanview.findViewById(R.id.vplan_pager_header);
        tabs.setTabIndicatorColor(MyTFG.color(R.color.orange_accent));

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(vplanview.getContext());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(vplanview.getContext());

        // terminalList displays terminalEntries
        vPlanList_today = (RecyclerView) vplanview.findViewById(R.id.vPlanList_today);
        // vPlanList_today.setScrollIndicators(View.SCROLL_INDICATOR_RIGHT);
        vPlanList_today.setHasFixedSize(true);
        vPlanList_today.setLayoutManager(linearLayoutManager1);

        vPlanList_tomorrow = (RecyclerView) vplanview.findViewById(R.id.vPlanList_tomorrow);
        // vPlanList_tomorrow.setScrollIndicators(View.SCROLL_INDICATOR_RIGHT);
        vPlanList_tomorrow.setHasFixedSize(true);
        vPlanList_tomorrow.setLayoutManager(linearLayoutManager2);

        refreshVPlanEntries();
        return vplanview;
    }

    /**
     * A Pager to slide the VPlan pages.
     */
    public class VplanPageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View v = null;
            if (position == 0) {
                v = ((MainActivity)MainActivity.context).findViewById(R.id.linearLayout_today);
            } else {
                v = ((MainActivity)MainActivity.context).findViewById(R.id.linearLayout_tomorrow);
            }
            return v;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return todayTitle;
            } else {
                return tomorrowTitle;
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
    }

    private void refreshVPlanEntries() {
        todayTitle = todayTitleDefault;
        tomorrowTitle = tomorrowTitleDefault;

        Vplan plan = (Vplan) MyTFG.moduleManager.getModule(Modules.VPLAN);
        plan.getPlan(true, new Vplan.GetPlanCallback() {
            @Override
            public void callback(String day, VplanInfo info, List<VplanEntry> entries) {
                List<VplanObject> objects = new LinkedList<>();
                objects.add(info);
                objects.addAll(entries);
                RVAdapter adapter = new RVAdapter(objects);

                todayTitle = day;
                mPagerAdapter.notifyDataSetChanged();

                RecyclerView today = (RecyclerView) vplanview.findViewById(R.id.vPlanList_today);
                today.setAdapter(adapter);
            }
        });

        plan.getPlan(false, new Vplan.GetPlanCallback() {
            @Override
            public void callback(String day, VplanInfo info, List<VplanEntry> entries) {
                List<VplanObject> objects = new LinkedList<>();
                objects.add(info);
                objects.addAll(entries);
                RVAdapter adapter = new RVAdapter(objects);

                tomorrowTitle = day;
                mPagerAdapter.notifyDataSetChanged();

                RecyclerView tomorrow = (RecyclerView) vplanview.findViewById(R.id.vPlanList_tomorrow);
                tomorrow.setAdapter(adapter);
            }
        });
    }


    public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<VplanObject> vplanEntries;

        RVAdapter(List<VplanObject> entries) {
            this.vplanEntries = entries;
        }

        // get position of item, 0 is vplan_info_view, > 0 are vplan_views
        @Override
        public int getItemViewType(int position) {
            return vplanEntries.get(position) instanceof VplanEntry ? 1 : 0;
        }


        @Override
        public int getItemCount() {
            return vplanEntries.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
            // if this is the vplan_info_view
            if (type == 0) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vplan_info_view_layout, viewGroup, false);
                return new VPlanInfoViewHolder(v);
            } else {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vplan_view_layout, viewGroup, false);
                return new VPlanViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            VplanObject obj = vplanEntries.get(i);
            // if this is the vplan_info_view
            if (obj instanceof VplanInfo) {
                VPlanInfoViewHolder vPlanInfoViewHolder = (VPlanInfoViewHolder) viewHolder;
                // get entries and construct String
                String string_info = "Keine Informationen gespeichert";
                for (int j = 0; j < ((VplanInfo)obj).getInfos().size(); j++) {
                    string_info = ((VplanInfo)obj).getInfos().get(j) + System.getProperty("line.separator");
                }
                vPlanInfoViewHolder.info.setText(string_info);
            } else {
                // cast to VPlanViewHolder
                VplanEntry entry = (VplanEntry) obj;
                VPlanViewHolder vPlanViewHolder = (VPlanViewHolder) viewHolder;
                // get elements from vplanEntries class
                String lesson = entry.getLessons();
                String school_class = entry.getGrade();
                String regular = entry.getPlan();
                String comment = entry.getComment();
                String substitution = entry.getSubstitution();
                // only display school class in entry cards, if user is no pupil (and has more than
                // only his own class's entries)
                if (!multiClass) {
                    vPlanViewHolder.schoolClassText.setText(school_class);
                }
                // display entries
                vPlanViewHolder.lessonText.setText(lesson);
                vPlanViewHolder.regularText.setText(regular);
                vPlanViewHolder.substitutionText.setText(substitution);
                vPlanViewHolder.substitutionText.setText(comment);
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class VPlanViewHolder extends RecyclerView.ViewHolder {
            CardView vplanView;
            TextView schoolClassText;
            TextView lessonText;
            TextView regularText;
            TextView substitutionText;
            TextView commentText;

            VPlanViewHolder(View itemView) {
                super(itemView);
                vplanView = (CardView) itemView.findViewById(R.id.vplanView);
                schoolClassText= (TextView) itemView.findViewById(R.id.school_class);
                lessonText = (TextView) itemView.findViewById(R.id.lesson_text);
                regularText = (TextView) itemView.findViewById(R.id.regular_text);
                substitutionText = (TextView) itemView.findViewById(R.id.substitution_text);
                commentText = (TextView) itemView.findViewById(R.id.comment_text);
            }
        }

        public class VPlanInfoViewHolder extends RecyclerView.ViewHolder {
            CardView vplanInfoView;
            TextView info;

            VPlanInfoViewHolder(View itemView) {
                super(itemView);
                vplanInfoView = (CardView) itemView.findViewById(R.id.vplanInfoView);
                info = (TextView) itemView.findViewById(R.id.vplan_info);
            }
        }
    }
}
