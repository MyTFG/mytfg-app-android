package de.mytfg.app.android.slidemenu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.NavigationDrawerFragment;
import de.mytfg.app.android.R;
import de.mytfg.app.android.gcm.GcmCallbackRegistration;
import de.mytfg.app.android.gcm.RegistrationIntentService;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.terminal.TerminalCreator;
import de.mytfg.app.android.modules.terminal.TerminalTopic;
import de.mytfg.app.android.slidemenu.items.Navigation;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public static Navigation navigation;
    public static Context context;
    public static ProgressBar loadingBar;
    public static Toolbar toolbar;
    private DrawerLayout drawerLayout;
    public static NavigationView navigationView;
    private static View fragmentView;


    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static boolean isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fragmentView = findViewById(R.id.container);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        onNewIntent(getIntent());

        GcmCallbackRegistration.registerAll();

        context = this;

        navigation = new Navigation(this);

        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        mTitle = getTitle();

        // Init GCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = MyTFG.preferences;
                boolean sentToken = sharedPreferences
                        .getBoolean(MyTFG.string(R.string.settings_gcm_sent), false);
                if (!sentToken) {
                    Toast.makeText(context, "GCM Token error", Toast.LENGTH_LONG).show();
                }
            }
        };


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.i("CPS", "Intent Service started.");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Log.i("CPS", "Failed.");
        }

        navigation.setNavigationDrawerParams(
                drawerLayout,
                navigationView
        );
        navigation.navigate(0);
    }

    protected void onNewIntent(Intent intent) {
        int id = intent.getIntExtra("notificationId", -1);
        if (id >= 0) {
            MyTFG.gcmManager.clicked(id);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        /*
        // update the main content by replacing fragments
        Fragment objFragment = navigation.load(position);

        if (objFragment == null) {
            // If there was an internal redirection
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment)
                .commit();*/
    }

    public void onSectionAttached(int number) {

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        // actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_terminal_topic_details:
                TerminalTopic module = (TerminalTopic) MyTFG.moduleManager.getModule(Modules.TERMINALTOPIC);
                Bundle args = new Bundle();
                args.putLong("topic", module.getId());
                navigation.navigate(Navigation.ItemNames.TERMINAL_DETAIL, args);
                break;

            case R.id.action_terminal_topic_create_reset:
                TerminalCreator creator = (TerminalCreator) MyTFG.moduleManager.getModule(Modules.TERMINALCREATOR);
                creator.reset();
                break;

            case R.id.action_terminal_topic_create_submit:
                ((TerminalCreator) MyTFG.moduleManager.getModule(Modules.TERMINALCREATOR)).create();
                break;


            case android.R.id.home:
                if (navigation.getCurrentItem().getParent() != navigation.getCurrentItem().getItem()) {
                    this.onBackPressed();
                    return true;
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("registrationComplete"));
        isVisible = true;
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        isVisible = false;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("GCM", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static boolean isVisible() {
        return isVisible;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        public void setActionBarTitle(String title){
            getActivity().getActionBar().setTitle(title);
        }

    }

}
