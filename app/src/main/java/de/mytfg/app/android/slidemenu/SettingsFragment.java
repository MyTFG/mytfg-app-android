package de.mytfg.app.android.slidemenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modules.settings.Settings;

public class SettingsFragment extends AbstractFragment {
    View settingsview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsview = inflater.inflate(R.layout.settings_layout, container, false);

        Settings.init(settingsview);

        return settingsview;
    }
}
