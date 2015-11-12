package de.mytfg.app.android.slidemenu.items;

import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.AbstractFragment;
import de.mytfg.app.android.slidemenu.LoginFragment;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.SettingsFragment;
import de.mytfg.app.android.slidemenu.VPlanFragment;

/**
 * Login Navigation Item loader
 */
public class VPlanItem extends NavigationItem {
    public VPlanItem(Navigation navigation) {
        super(navigation);
        this.title = MyTFG.getAppContext().getString(R.string.title_vplan);
        this.item = Navigation.ItemNames.VPLAN;
        this.parent = this.item;
    }

    @Override
    public Fragment load() {
        stdLoad();
        AbstractFragment frag = new VPlanFragment();
        frag.item = this;
        if (frag != null) {
            frag.args.putString("title", title);
        }
        return frag;
    }
}
