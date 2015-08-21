package de.mytfg.app.android.slidemenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;

public class TerminalEntryFragment extends Fragment {
    View terminalentryview;
    private Map<String, String> params;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        terminalentryview = inflater.inflate(R.layout.settings_layout, container, false);

        return terminalentryview;
    }
}
