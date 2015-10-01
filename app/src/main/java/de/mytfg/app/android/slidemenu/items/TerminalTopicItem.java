package de.mytfg.app.android.slidemenu.items;

import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.AbstractFragment;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.StartFragment;
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
        this.parent = Navigation.ItemNames.TERMINAL;
    }


    @Override
    public Fragment load() {
        stdLoad();
        AbstractFragment frag = new TerminalTopicFragment();

        frag.item = this;
        if (args.containsKey("title")) {
            frag.args = args;
        } else {
            args.putString("title", title);
            frag.args = args;
        }

        return frag;
    }
}
