package de.mytfg.app.android.slidemenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.items.Navigation;

public class TerminalTopicFragment extends Fragment {
    View terminalentryview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        terminalentryview = inflater.inflate(R.layout.settings_layout, container, false);
        return terminalentryview;
    }

    /**
     * Loads the given topic.
     * @param args Arguments required for loading.
     */
    public boolean initialize(Bundle args) {
        long topicId = args.getLong("topic", 0);
        if (topicId == 0) {
            MainActivity.navigation.navigate(Navigation.ItemNames.TERMINAL);
            return false;
        } else {
            return true;
        }
    }
}
