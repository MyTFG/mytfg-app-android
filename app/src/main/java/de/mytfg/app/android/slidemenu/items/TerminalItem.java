package de.mytfg.app.android.slidemenu.items;

import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.LoginFragment;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.TerminalFragment;

/**
 * Login Navigation Item loader
 */
public class TerminalItem extends NavigationItem {
    public TerminalItem(Navigation navigation) {
        super(navigation);
        this.title = MyTFG.getAppContext().getString(R.string.title_terminal);
        this.item = Navigation.ItemNames.TERMINAL;
    }


    @Override
    public Fragment load() {
        stdLoad();
        ((MainActivity)context).getSupportActionBar().setTitle(this.title);
        return new TerminalFragment();
    }
}
