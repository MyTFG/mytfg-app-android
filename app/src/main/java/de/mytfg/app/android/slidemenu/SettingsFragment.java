package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.settings.Settings;

public class SettingsFragment extends AbstractFragment {
    View settingsview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsview = inflater.inflate(R.layout.settings_layout, container, false);

        Settings settings = (Settings)MyTFG.moduleManager.getModule(Modules.SETTINGS);
        settings.init(settingsview);

        return settingsview;
    }
}
