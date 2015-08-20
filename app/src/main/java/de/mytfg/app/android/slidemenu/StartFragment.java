package de.mytfg.app.android.slidemenu;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mytfg.app.android.R;

public class StartFragment extends Fragment {
    View loginview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loginview = inflater.inflate(R.layout.start_layout, container, false);
        return loginview;
    }
}
