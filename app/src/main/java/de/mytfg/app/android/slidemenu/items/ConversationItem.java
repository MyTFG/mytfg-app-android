package de.mytfg.app.android.slidemenu.items;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.AbstractFragment;
import de.mytfg.app.android.slidemenu.ConversationFragment;
import de.mytfg.app.android.slidemenu.ConversationsListFragment;

public class ConversationItem extends NavigationItem {

    public ConversationItem(Navigation navigation) {
        super(navigation);
        this.title = MyTFG.getAppContext().getResources().getString(R.string.message_center_name);
        this.item = Navigation.ItemNames.CONVERSATION;
        this.parent = Navigation.ItemNames.CONVERSATIONS_LIST;
    }


    @Override
    public Fragment load() {
        if(args.getLong("conversationId") == 0) {
            throw new IllegalArgumentException("No conversation id!");
        }
        stdLoad();
        AbstractFragment frag = new ConversationFragment();
        frag.item = this;
        if (frag != null) {
            frag.args.putString("title", title);
            frag.args.putLong("conversationId", args.getLong("conversationId"));
        }
        return frag;
    }

}
