package de.mytfg.app.android.slidemenu.items;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.Map;

import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.MainActivity;

/**
 * Abstract class for Items in the Slide Menu.
 */
public abstract class NavigationItem {
    protected NavigationCategory category;
    protected String title;
    protected Context context;
    protected Navigation navigation;
    protected Navigation.ItemNames item;
    protected Map<String, String> params;

    public NavigationItem(Navigation navigation) {
        this.navigation = navigation;
        this.context = navigation.getContext();
    }

    public abstract Fragment load();
    public Fragment load(Map<String, String> params) {
        this.params = params;
        return this.load();
    }

    protected void stdLoad() {
        // ((MainActivity)context).getSupportActionBar().setDisplayShowHomeEnabled(true);
        // ((MainActivity)context).getSupportActionBar().setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
    }

    /**
     * Returns the Title of the NavigationItem.
     * @return Title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns if this Navigation Item is of the specified type.
     * This method translates the Navigation.ItemNames to NavigationItems.
     * @param item Type to check.
     * @return True iff this Navigation Item is the specified item.
     */
    public boolean isItem(Navigation.ItemNames item) {
        return item == this.item;
    }
}
