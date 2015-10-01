package de.mytfg.app.android.slidemenu.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Abstract class for Items in the Slide Menu.
 */
public abstract class NavigationItem {
    // The category of this Item
    protected NavigationCategory category;
    // Title to be displayed in ActionBar
    protected String title;
    // Context of the Application
    protected Context context;
    // Navigation Object this item belongs to
    protected Navigation navigation;
    // Representative Name for this Item
    protected Navigation.ItemNames item;
    // Arguments (e.g. "title")
    public Bundle args;
    // If this item is visible in the navigation (set by navigationCategory)
    protected boolean isHidden = false;
    // Parent of this item
    // Set null if this item is own category, otherwise set to visible item
    // Will be highlighted in NavigationDrawer.
    protected Navigation.ItemNames parent;

    public NavigationItem(Navigation navigation) {
        this.args = new Bundle();
        this.navigation = navigation;
        this.context = navigation.getContext();
    }

    public Navigation.ItemNames getParent() {
        if (parent != null) {
            return parent;
        } else {
            return item;
        }
    }

    public abstract Fragment load();

    public Fragment load(Bundle args) {
        this.args = args;
        return this.load();
    }

    // Can be used to perform actions on Load of every Item
    protected void stdLoad() {

    }

    /**
     * Returns the Title of the NavigationItem.
     * @return Title.
     */
    public String getTitle() {
        return args.getString("title", this.title);
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


    public Navigation.ItemNames getItem() {
        return this.item;
    }
}
