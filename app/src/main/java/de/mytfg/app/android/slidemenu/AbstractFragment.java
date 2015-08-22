package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Abstract class for used Fragments.
 * Makes Fragments have a title.
 */
public abstract class AbstractFragment extends Fragment {
    public Bundle args = new Bundle();

    public String getTitle() {
        if (args == null) {
            return "";
        } else {
            return args.getString("title", "");
        }
    }
}
