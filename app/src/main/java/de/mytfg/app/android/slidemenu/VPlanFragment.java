package de.mytfg.app.android.slidemenu;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.app.android.R;

public class VPlanFragment extends AbstractFragment {
    View vplanview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vplanview = inflater.inflate(R.layout.vplan_layout, container, false);

        return vplanview;
    }
}
