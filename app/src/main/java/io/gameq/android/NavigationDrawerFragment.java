package io.gameq.android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerRelativeLayout;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    static final String TAG = "GameQ-NavD-Frag";

    private CheckBox mOnBoxNotifications;
    private CheckBox mOffBoxNotifications;
    private Button mFeedbackButton;
    private Button mChangePasswordButton;
    private Button mSubmitFeedbackButton;
    private Button mSubmitChangePasswordButton;
    private Button mTutorialButton;
    private Button mLogoutButton;
    private TextView mLblUsername;
    public Activity mMainActivity;





    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerRelativeLayout = (RelativeLayout) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        System.out.println("kommer hit iaf?");





        mOnBoxNotifications = (CheckBox) mDrawerRelativeLayout.findViewById(R.id.on_box);
        mOffBoxNotifications = (CheckBox) mDrawerRelativeLayout.findViewById(R.id.off_box);
        mFeedbackButton = (Button) mDrawerRelativeLayout.findViewById(R.id.feedback_button);
        mChangePasswordButton = (Button) mDrawerRelativeLayout.findViewById(R.id.change_password_button);
        mSubmitFeedbackButton = (Button) mDrawerRelativeLayout.findViewById(R.id.feedback_submit_button);
        mSubmitChangePasswordButton = (Button) mDrawerRelativeLayout.findViewById(R.id.change_password_submit_button);
        mTutorialButton = (Button) mDrawerRelativeLayout.findViewById(R.id.tutorial_button);
        mLogoutButton = (Button) mDrawerRelativeLayout.findViewById(R.id.logout_button);
        mLblUsername = (TextView) mDrawerRelativeLayout.findViewById(R.id.header_label);

        mFeedbackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedFeedback();
            }
        });

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedChangePassword();
            }
        });

        mSubmitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedSubmitFeedback();
            }
        });

        mSubmitChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedSubmitChangePassword();
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedLogout();
            }
        });

        mTutorialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedTutorial();
            }
        });


        mOffBoxNotifications.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedTurnOffNotifications();
            }
        });


        mOnBoxNotifications.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedTurnOnNotifications();
            }
        });

        ConnectionHandler.loadShouldReceiveNotifications();
        if (ConnectionHandler.loadShouldReceiveNotifications()) {
            mOnBoxNotifications.setChecked(false);
            mOffBoxNotifications.setChecked(true);
        } else {
            mOnBoxNotifications.setChecked(true);
            mOffBoxNotifications.setChecked(false);
        }
        mLblUsername.setText(ConnectionHandler.loadEmail());


        return mDrawerRelativeLayout;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
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

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                System.out.println("OPENED DRAWER");
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        /*if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }







    public void pressedSubmitFeedback() {
        System.out.println("pressed sub feed");
    }
    public void pressedSubmitChangePassword() {
        System.out.println("pressed sub change pass");

    }

    public void pressedFeedback() {
        System.out.println("pressed feed");

    }

    public void pressedChangePassword() {
        System.out.println("pressed change pass");

    }

    public void pressedTutorial() {
        System.out.println("pressed tutorial");
        Intent intent = new Intent(mMainActivity, Tutorial_Activity_Fragment.class);
        intent.putExtra(getString(R.string.intent_inhouse_extra), true);
        startActivity(intent);
    }

    public void pressedLogout() {

        Log.i(TAG, "pressed Logout");

        disableAll();
        ConnectionHandler.logout(new LogoutHandler());
        System.out.println("pressed logout");
    }
    public class LogoutHandler implements CallbackGeneral {
        @Override
        public void callback(final boolean success, final String error) {
            mMainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    enableAll();
                    if (success) {
                        //nothing special
                    } else {
                        Log.i(TAG, error);
                        System.out.println(error);
                    }
                    Intent intent = new Intent(mMainActivity, LoginActivity.class);
                    intent.putExtra(getString(R.string.intent_inhouse_extra), true);
                    startActivity(intent);

                }
            });

        }
    }

    public void pressedTurnOnNotifications() {
        Log.i(TAG, "pressed notifs on");
        System.out.println("pressed on notifs");
        mOffBoxNotifications.setChecked(false);
        ConnectionHandler.saveShouldReceiveNotifications(true);
    }

    public void pressedTurnOffNotifications() {
        Log.i(TAG, "pressed notifs off");
        System.out.println("pressed off notifs");
        mOnBoxNotifications.setChecked(false);
        ConnectionHandler.saveShouldReceiveNotifications(false);

    }

    private void enableAll() {
        mFeedbackButton.setEnabled(true);
        mSubmitFeedbackButton.setEnabled(true);
        mChangePasswordButton.setEnabled(true);
        mSubmitChangePasswordButton.setEnabled(true);
        mOnBoxNotifications.setEnabled(true);
        mOffBoxNotifications.setEnabled(true);
        mTutorialButton.setEnabled(true);
        mLogoutButton.setEnabled(true);
    }

    private void disableAll() {
        mFeedbackButton.setEnabled(false);
        mSubmitFeedbackButton.setEnabled(false);
        mChangePasswordButton.setEnabled(false);
        mSubmitChangePasswordButton.setEnabled(false);
        mOnBoxNotifications.setEnabled(false);
        mOffBoxNotifications.setEnabled(false);
        mTutorialButton.setEnabled(false);
        mLogoutButton.setEnabled(false);
    }



}
