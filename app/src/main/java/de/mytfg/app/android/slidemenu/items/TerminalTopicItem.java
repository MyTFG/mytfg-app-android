package de.mytfg.app.android.slidemenu.items;

import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.TerminalFragment;
import de.mytfg.app.android.slidemenu.TerminalTopicFragment;

/**
 * Login Navigation Item loader
 */
public class TerminalTopicItem extends NavigationItem {
    public TerminalTopicItem(Navigation navigation) {
        super(navigation);
        this.title = MyTFG.getAppContext().getString(R.string.title_terminal);
        this.item = Navigation.ItemNames.TERMINAL_TOPIC;
    }


    @Override
    public Fragment load() {
        stdLoad();
        ((MainActivity)context).getSupportActionBar().setTitle(this.args.getString("title", this.title));
        TerminalTopicFragment frag = new TerminalTopicFragment();

        if (frag.setArgs(args)) {
            return frag;
        } else {
            return null;
        }
    }
}
