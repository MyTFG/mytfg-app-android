package de.mytfg.app.android.slidemenu.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ListView;

import java.util.LinkedList;

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
        CONVERSATIONS_LIST,
        CONVERSATION,
        TERMINAL,
        TERMINAL_TOPIC
    }

    public enum Transition {
        DEFAULT,
        SLIDE,
        ZOOM,
        NONE,
        FADE;

        public int[] getAnimations() {
            int[] res = new int[4];

            switch (this) {
                case SLIDE:
                    res[0] = R.anim.slide_in_right;
                    res[1] = R.anim.slide_out_left;
                    res[2] = R.anim.slide_in_left;
                    res[3] = R.anim.slide_out_right;
                    break;
                case NONE:
                    break;
                case ZOOM:
                    break;
                case FADE:
                    res[0] = R.anim.fade_in;
                    res[1] = R.anim.fade_out;
                    res[2] = R.anim.fade_out;
                    res[3] = R.anim.fade_in;
                    break;
            }

            return res;
        }
    }

    private ListView mDrawerListView;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks;
    private View mFragmentContainerView;

    private final Context context;
    private FragmentManager fragmentManager;

    private LinkedList<NavigationCategory> categories;

    private NavigationItem currentItem;

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
        NavigationItem settings = new SettingsItem(this);
        NavigationItem conversationsList = new ConversationsListItem(this);
        NavigationItem terminal = new TerminalItem(this);

        NavigationItem conversation = new ConversationItem(this);
        NavigationItem terminaltopic = new TerminalTopicItem(this);
        NavigationItem login = new LoginItem(this);

        mainCat.addItem(start);
        mainCat.addItem(terminal);
        mainCat.addItem(conversationsList);
        mainCat.addItem(settings);

        hiddenCat.addItem(conversation);
        hiddenCat.addItem(terminaltopic);
        hiddenCat.addItem(login);

        fragmentManager = ((MainActivity)context).getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                update();
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
        navigate(item, new Bundle(), Transition.DEFAULT, false);
    }

    /**
     * Navigates to a specified NavigationItem / Fragment.
     * @param args Arguments to pass to the Fragment
     * @param item The name of the Navigation Item.
     */
    public void navigate(Navigation.ItemNames item, Bundle args) {
        navigate(item, args, Transition.DEFAULT, false);
    }

    /**
     * Navigates to a specified NavigationItem / Fragment.
     * @param args Arguments to pass to the Fragment
     * @param item The name of the Navigation Item.
     * @param transition Animation to use for this Transition.
     */
    public void navigate(Navigation.ItemNames item, Bundle args, Navigation.Transition transition) {
        navigate(item, args, transition, false);
    }

    /**
     * Navigates to a specified NavigationItem / Fragment.
     * @param item The name of the Navigation Item.
     * @param args Arguments to pass to the Fragment.
     * @param transition Animation to use for this Transition
     * @param forceBackStack Forces the navigation to add a OnBack event.
     */
    public void navigate(Navigation.ItemNames item, Bundle args, Navigation.Transition transition,
                         boolean forceBackStack) {
        LinkedList<NavigationItem> items = getItems(true);

        int position = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isItem(item)) {
                position = i;
                break;
            }
        }

        currentItem = items.get(position);

        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        if (items.get(position).isHidden || forceBackStack) {
            if (transition == Transition.DEFAULT) {
                transition = Transition.SLIDE;
            }

            ft.addToBackStack(items.get(position).getTitle());
            highlightNavigation(items.get(position).getParent());
        } else {
            if (transition == Transition.DEFAULT) {
                transition = Transition.NONE;
            }
            highlightNavigation(item);
            clearBackStack();
        }
        int[] anim = transition.getAnimations();
        ft.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);

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
     * Renews the ActionBar title, icon and the navigation Highlight
     */
    public void update() {
        updateTitle();
        updateActionBarButton();
        updateNavigationHighlight();
    }


    public void updateTitle() {
        AbstractFragment current = (AbstractFragment) ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.container);

        ((MainActivity)context).getSupportActionBar().setTitle(current.getTitle());
    }

    public void updateActionBarButton() {
        if (((MainActivity) context).getSupportFragmentManager().getBackStackEntryCount() > 0) {
            ((MainActivity) context).getSupportActionBar().setHomeAsUpIndicator(context.getResources().getDrawable(R.drawable.ic_action_back));
        } else {
            ((MainActivity) context).getSupportActionBar().setHomeAsUpIndicator(context.getResources().getDrawable(R.mipmap.ic_launcher));
        }
    }

    private void clearBackStack() {
        ((MainActivity) context).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void updateNavigationHighlight() {
        AbstractFragment current = (AbstractFragment) ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.container);
        if (current != null && current.item != null) {
            highlightNavigation(current.item.parent);
        }
    }

    public NavigationItem getCurrentItem() {
        return currentItem;
    }
}
