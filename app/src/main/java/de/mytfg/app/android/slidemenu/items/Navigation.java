package de.mytfg.app.android.slidemenu.items;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import de.mytfg.app.android.NavigationDrawerFragment;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.AbstractFragment;
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

    private ListView mDrawerListView;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks;
    private View mFragmentContainerView;

    private final Context context;
    private FragmentManager fragmentManager;

    private LinkedList<NavigationCategory> categories;

    /**
     * Creates a new Navigation. Only use one Instance.
     * @param mainContext The context of MainActivity.
     */
    public Navigation(Context mainContext) {
        this.context = mainContext;
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

        fragmentManager = ((MainActivity)context).getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fr = fragmentManager.findFragmentById(R.id.container);
                if (fr != null && fr instanceof AbstractFragment) {
                    ((MainActivity)context).getSupportActionBar().setTitle(((AbstractFragment) fr).getTitle());
                }
            }
        });
    }

    public void setNavigationDrawerParams(ListView mDrawerListView, DrawerLayout mDrawerLayout,
                                          NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks,
                                          View mFragmentContainerView) {
        this.mDrawerListView = mDrawerListView;
        this.mDrawerLayout = mDrawerLayout;
        this.mCallbacks = mCallbacks;
        this.mFragmentContainerView = mFragmentContainerView;

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

        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        if (items.get(position).isHidden) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            ft.addToBackStack(items.get(position).getTitle());
            highlightNavigation(items.get(position).getParent());
        } else {
            highlightNavigation(item);
        }
        ((MainActivity)context).getSupportActionBar().setTitle(items.get(position).getTitle());
        ft.replace(R.id.container, items.get(position).load(args));
        ft.commit();
    }

    /**
     * Allows the NavigationDrawer to navigate to a specified Fragment
     * @param position The position in the NavigationDrawer
     * @return The current position for the NavigationDrawer
     */
    public int navigate(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }

        LinkedList<NavigationItem> items = getItems(false);
        navigate(items.get(position).getItem());
        return position;
    }

    public void highlightNavigation(Navigation.ItemNames item) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(getItemPos(item), true);
        }
    }

    public int getItemPos(Navigation.ItemNames item) {
        int i = 0;
        for (NavigationItem found : getItems(false)) {
            if (found.isItem(item))
                return i;
            i++;
        }
        return -1;
    }

    /**
     * Renews the ActionBar title
     */
    public void updateTitle() {
        AbstractFragment current = (AbstractFragment) ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.container);

        ((MainActivity)context).getSupportActionBar().setTitle(current.getTitle());
    }


}
