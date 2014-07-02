package views.core;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.knoda.knoda.R;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.NavigationAdapter;
import managers.NetworkingManager;
import managers.UserManager;
import models.ActivityItem;
import models.KnodaScreen;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.ActivitiesViewedEvent;

;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    private static final int activityRefreshInterval = 30000;
    private static final int userRefreshInterval = 30000;
    @Inject
    NetworkingManager networkingManager;
    @Inject
    UserManager userManager;
    @Inject
    Bus bus;
    NavigationAdapter adapter;
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private TextView pointsTextView;
    private TextView winLossTextView;
    private TextView winPercentTextView;
    private TextView streakTextView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private ArrayList<KnodaScreen> screens;
    private Menu mMenu;

    private Handler handler = new Handler();
    private Runnable activityRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            refreshActivity();
        }
    };
    private Runnable userRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            refreshUser();
        }
    };
    private boolean userDialogShown = false;

    public NavigationDrawerFragment() {
    }

    @Subscribe
    public void activitiesViewed(ActivitiesViewedEvent event) {
        handler.removeCallbacks(activityRefreshRunnable);
        refreshActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).inject(this);
        bus.register(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mDrawerListView = (ListView) view.findViewById(R.id.navigation_drawer_listview);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        pointsTextView = (TextView) view.findViewById(R.id.navigation_list_points_textview);
        winLossTextView = (TextView) view.findViewById(R.id.navigation_list_wl_textview);
        winPercentTextView = (TextView) view.findViewById(R.id.navigation_list_win_percent_textview);
        streakTextView = (TextView) view.findViewById(R.id.navigation_list_streak_textview);

        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Select either the default item (0) or the last selected item.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(activityRefreshRunnable);
        handler.removeCallbacks(userRefreshRunnable);
    }

    public void selectStartingItem() {
        selectItem(mCurrentSelectedPosition);
    }

    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(screens.get(position));
        }
    }

    public void resetDrawerUISelection() {
        mDrawerListView.setItemChecked(0, true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //if (((MainActivity) getActivity()).currentFragment.equals(this.getClass().getSimpleName()) && menu.findItem(R.id.action_settings) != null)
        //    menu.findItem(R.id.action_settings).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ((MainActivity) getActivity()).setActionBarTitle("KNODA");
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public boolean isDrawerToggleEnabled() {
        return mDrawerToggle.isDrawerIndicatorEnabled();
    }

    public void setDrawerToggleEnabled(boolean enabled) {
        mDrawerToggle.setDrawerIndicatorEnabled(enabled);
    }

    public void setDrawerLockerMode(int lockerMode) {
        mDrawerLayout.setDrawerLockMode(lockerMode);
    }

    public void setScreens(ArrayList<KnodaScreen> screens) {
        this.screens = screens;
        adapter = new NavigationAdapter(getActivity(), screens);
        mDrawerListView.setAdapter(adapter);
        if (userManager.isLoggedIn()) {
            refreshActivity();
            adapter.setUser(userManager.getUser());
            refreshUser();
        }
    }

    public void refreshActivity() {
        if (getActivity() == null || ((MainActivity) getActivity()).connectivityManager == null)
            return;
        NetworkInfo activeNetwork = ((MainActivity) getActivity()).connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                networkingManager.getUnseenActivityItems(new NetworkListCallback<ActivityItem>() {
                    @Override
                    public void completionHandler(ArrayList<ActivityItem> object, ServerError error) {
                        if (error != null || object == null)
                            adapter.setAlertsCount(0);
                        else
                            adapter.setAlertsCount(object.size());
                        handler.postDelayed(activityRefreshRunnable, activityRefreshInterval);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void refreshUser() {
        if (getActivity() == null || ((MainActivity) getActivity()).connectivityManager == null)
            return;
        NetworkInfo activeNetwork = ((MainActivity) getActivity()).connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            adapter.setUser(userManager.getUser());
            refreshStats(userManager.getUser());
            userManager.refreshUser(new NetworkCallback<User>() {
                @Override
                public void completionHandler(User object, ServerError error) {
                    handler.postDelayed(userRefreshRunnable, userRefreshInterval);
                }
            });
        } else {
            if (!userDialogShown) {
                userDialogShown = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Network Connectivity")
                        .setMessage("You have lost internet connectivity. Retry connecting?")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userDialogShown = false;
                                handler.postDelayed(userRefreshRunnable, userRefreshInterval);
                                handler.postDelayed(activityRefreshRunnable, activityRefreshInterval);
                            }
                        })
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userDialogShown = false;
                                refreshActivity();
                                refreshUser();
                            }
                        })
                        .create().show();
            }

        }

    }

    private void refreshStats(User user) {
        if (user == null)
            return;

        pointsTextView.setText(user.points.toString());
        winLossTextView.setText(user.won.toString() + "-" + user.lost.toString());
        setStreak(user.streak);
        winPercentTextView.setText(user.winningPercentage.toString() + "%");
    }

    public void setStreak(String streak) {
        if (streak == null || streak == "") {
            streakTextView.setText("W0");
        } else {
            streakTextView.setText(streak.toString());
        }
    }

    public static interface NavigationDrawerCallbacks {

        void onNavigationDrawerItemSelected(KnodaScreen screen);
    }
}
