package de.mytfg.app.android.slidemenu.items;

import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.AbstractFragment;
import de.mytfg.app.android.slidemenu.TerminalCreateFragment;
import de.mytfg.app.android.slidemenu.TerminalDetailFragment;

/**
 * Navigation Item for the Detail View on Terminal Topics.
 */
public class TerminalTopicCreateItem extends NavigationItem {
    public TerminalTopicCreateItem(Navigation navigation) {
        super(navigation);
        this.title = MyTFG.getAppContext().getString(R.string.title_terminal_create);
        this.item = Navigation.ItemNames.TERMINAL_CREATE;
        this.parent = Navigation.ItemNames.TERMINAL;
    }

    @Override
    public Fragment load() {
        stdLoad();
        AbstractFragment frag = new TerminalCreateFragment();
        frag.item = this;
        frag.args = this.args;
        frag.args.putString("title", title);
        return frag;
    }
}
