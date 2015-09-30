package de.mytfg.app.android.slidemenu.items;

import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.AbstractFragment;
import de.mytfg.app.android.slidemenu.ConversationsListFragment;

public class ConversationsListItem extends NavigationItem {

    public ConversationsListItem(Navigation navigation) {
        super(navigation);
        this.title = MyTFG.getAppContext().getResources().getString(R.string.message_center_name);
        this.item = Navigation.ItemNames.CONVERSATIONS_LIST;
        this.parent = this.item;
    }


    @Override
    public Fragment load() {
        stdLoad();
        AbstractFragment frag = new ConversationsListFragment();
        frag.item = this;
        if (frag != null) {
            frag.args.putString("title", title);
        }
        return frag;
    }

}
