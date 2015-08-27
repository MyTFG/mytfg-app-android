package de.mytfg.app.android.slidemenu.items;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Groups Navigation Items.
 */
public class NavigationCategory {
    private String title;
    private LinkedList<NavigationItem> items;
    private boolean hidden;

    protected NavigationCategory(String title, boolean hidden) {
        items = new LinkedList<>();
        this.title = title;
        this.hidden = hidden;
    }

    protected NavigationCategory(String title) {
        this(title, false);
    }

    protected void addItem(NavigationItem item) {
        item.isHidden = hidden;
        items.add(item);
    }

    protected boolean isHidden() {
        return hidden;
    }

    protected LinkedList<NavigationItem> getItems() {
        return (LinkedList<NavigationItem>)items.clone();
    }
}
