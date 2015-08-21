package de.mytfg.app.android.slidemenu.items;

import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.LoginFragment;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.StartFragment;

/**
 * Login Navigation Item loader
 */
public class StartItem extends NavigationItem {
    public StartItem(Navigation navigation) {
        super(navigation);
        this.title = MyTFG.getAppContext().getString(R.string.title_start);
        this.item = Navigation.ItemNames.START;
    }


    @Override
    public Fragment load() {
        ((MainActivity)context).getSupportActionBar().setTitle(this.title);
        return new StartFragment();
    }
}
