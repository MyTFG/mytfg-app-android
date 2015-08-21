package de.mytfg.app.android.slidemenu.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.MainActivity;

/**
 * Navigation Manager class.
 */
public class Navigation {
    public enum ItemNames {
        START,
        LOGIN,
        SETTINGS,
        TERMINAL,
        TERMINAL_TOPIC
    }

    private Context context;

    private LinkedList<NavigationCategory> categories;

    /**
     * Creates a new Navigation. Only use one Instance.
     * @param context The context of MainActivity.
     */
    public Navigation(Context context) {
        this.context = context;
        categories = new LinkedList<>();

        NavigationCategory mainCat = new NavigationCategory("Hauptkategorie");
        NavigationCategory hiddenCat = new NavigationCategory("Hidden Kategorie", true);
        categories.add(mainCat);
        categories.add(hiddenCat);


        NavigationItem start = new StartItem(this);
        NavigationItem login = new LoginItem(this);
        NavigationItem settings = new SettingsItem(this);
        NavigationItem terminal = new TerminalItem(this);

        NavigationItem terminaltopic = new TerminalTopicItem(this);

        mainCat.addItem(start);
        mainCat.addItem(login);
        mainCat.addItem(terminal);
        mainCat.addItem(settings);

        hiddenCat.addItem(terminaltopic);
    }

    protected Context getContext() {
        return context;
    }

    /**
     * Creates an array of Navigation Titles for the visible Navigation.
     * @return Array of Navigation Item Titles.
     */
    public String[] toArray(){
        LinkedList<NavigationItem> items = getItems(false);
        String[] result = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            result[i] = items.get(i).getTitle();
        }

        return result;
    }

    /**
     * Loads a specified Navigation Item at the given position.
     * @param position The Position of the fragment to load in the Navigation.
     * @return The fragment.
     */
    public Fragment load(int position) {
        Fragment result = null;
        int currentPos = 0;
        int offset = 0;

        LinkedList<NavigationItem> items = getItems(false);

        if (items.size() < position) {
            return null;
        } else {
            return items.get(position).load();
        }
    }



    private Fragment load(Navigation.ItemNames item) {
        LinkedList<NavigationItem> items = getItems(true);

        int position = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isItem(item)) {
                position = i;
                break;
            }
        }

        return items.get(position).load();
    }

    /**
     * Returns a List of Navigation Items.
     * @param hidden Specifies if hidden Items (not displayed in Navigation) should be returned.
     * @return List of Items.
     */
    public LinkedList<NavigationItem> getItems(boolean hidden) {
        LinkedList<NavigationItem> items = new LinkedList<>();

        for (NavigationCategory category : categories) {
            if (hidden || !category.isHidden())
            items.addAll(category.getItems());
        }

        return items;
    }

    /**
     * Navigates to a specified NavigationItem / Fragment.
     * @param item The name of the Navigation Item.
     */
    public void navigate(Navigation.ItemNames item) {
        navigate(item, new Bundle());
    }

    /**
     * Navigates to a specified NavigationItem / Fragment.
     * @param item The name of the Navigation Item.
     * @param args parameters
     */
    public void navigate(Navigation.ItemNames item, Bundle args) {
        LinkedList<NavigationItem> items = getItems(true);

        int position = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isItem(item)) {
                position = i;
                break;
            }
        }

        if (items.get(position).isHidden) {
            FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, items.get(position).load(args))
                    .commit();
        } else {
            // Is in navigation, find navigation pos.
            items = getItems(false);
            position = 0;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).isItem(item)) {
                    position = i;
                    break;
                }
            }
            MainActivity.mNavigationDrawerFragment.selectItem(position);
        }
    }


}
