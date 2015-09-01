package de.mytfg.app.android.slidemenu.items;

import android.content.Context;
import android.support.v4.app.Fragment;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.AbstractFragment;
import de.mytfg.app.android.slidemenu.LoginFragment;
import de.mytfg.app.android.slidemenu.MainActivity;

/**
 * Login Navigation Item loader
 */
public class LoginItem extends NavigationItem {
    public LoginItem(Navigation navigation) {
        super(navigation);
        this.title = MyTFG.getAppContext().getString(R.string.title_login);
        this.item = Navigation.ItemNames.LOGIN;
        this.parent = Navigation.ItemNames.SETTINGS;
    }


    @Override
    public Fragment load() {
        stdLoad();
        AbstractFragment frag = new LoginFragment();
        frag.item = this;
        if (frag != null) {
            frag.args.putString("title", title);
        }
        return frag;
    }
}
