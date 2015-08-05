package io.gameq.android;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
    public ActionBarDrawerToggle mDrawerToggle;

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

    private EditText mTxtFeedback;
    private EditText mTxtOldPassword;
    private EditText mTxtNewPassword;
    private EditText mTxtConfirmPassword;
    private boolean mIsChangingPassword= false;
    private boolean mIsWritingFeedback = false;
    private RelativeLayout mChangePassContainer;
    private RelativeLayout mFeedbackContainer;

    private ImageButton mFacebookButton;
    private ImageButton mTwitterButton;
    private ImageButton mEmailButton;


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
        mTxtConfirmPassword = (EditText) mDrawerRelativeLayout.findViewById(R.id.change_password_confirm_pass);
        mTxtFeedback = (EditText) mDrawerRelativeLayout.findViewById(R.id.feedback_edit_text);
        mTxtNewPassword = (EditText) mDrawerRelativeLayout.findViewById(R.id.change_password_new_pass);
        mTxtOldPassword = (EditText) mDrawerRelativeLayout.findViewById(R.id.change_password_old_pass);
        mChangePassContainer = (RelativeLayout) mDrawerRelativeLayout.findViewById(R.id.change_password_container);
        mFeedbackContainer = (RelativeLayout) mDrawerRelativeLayout.findViewById(R.id.feedback_container);
        mFacebookButton = (ImageButton) mDrawerRelativeLayout.findViewById(R.id.facebook_button);
        mTwitterButton = (ImageButton) mDrawerRelativeLayout.findViewById(R.id.twitter_button);
        mEmailButton = (ImageButton) mDrawerRelativeLayout.findViewById(R.id.mail_button);




        mTxtFeedback.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mTxtOldPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mTxtNewPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mTxtConfirmPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mTxtOldPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.action_next || id == EditorInfo.IME_NULL) {
                    mTxtNewPassword.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mTxtNewPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.action_next || id == EditorInfo.IME_NULL) {
                    mTxtConfirmPassword.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mTxtConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.action_submit_pass_change || id == EditorInfo.IME_NULL) {
                    pressedSubmitChangePassword();
                    return true;
                }
                return false;
            }
        });

        mTxtFeedback.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.action_submit_feedback || id == EditorInfo.IME_NULL) {
                    pressedSubmitFeedback();
                    return true;
                }
                return false;
            }
        });


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

        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedFacebook();
            }
        });

        mTwitterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedTwitter();
            }
        });

        mEmailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedEmail();
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
        actionBar.setIcon(R.drawable.ic_drawer);
        actionBar.setHomeButtonEnabled(true);





        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                InputMethodManager imm = (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mMainActivity.getCurrentFocus().getWindowToken(), 0);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                System.out.println("OPENED DRAWER");
                super.onDrawerOpened(drawerView);
                InputMethodManager imm = (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mMainActivity.getCurrentFocus().getWindowToken(), 0);
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
        mDrawerToggle.syncState();
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });


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




    public void pressedFacebook() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/304122193079686"));
            intent.setPackage("com.facebook.katana");
            startActivity(intent);
        } catch(Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/GameQApp")));
        }
    }

    public void pressedTwitter() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=GameQApp"));
            startActivity(intent);

        }catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/GameQApp")));
        }
    }

    public void pressedEmail() {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("contact@gameq.com") + "?subject=" + Uri.encode("Contact from Android") + "&body=" + Uri.encode("Dear GameQ Team, ");
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
    }



    public void pressedSubmitFeedback() {
        System.out.println("pressed sub feed");
        if (mTxtFeedback.length() > 10) {
            disableAll();
            ConnectionHandler.submitFeedback(new FeedbackHandler(), mTxtFeedback.getText().toString());
        } else {
            mTxtFeedback.setError(getString(R.string.minimum_length));
        }
    }
    public void pressedSubmitChangePassword() {
        System.out.println("pressed sub change pass");
        if (mTxtOldPassword.length() < 6) {
            mTxtOldPassword.setError(getString(R.string.invalid_password));
        } else if (mTxtNewPassword.length() < 6) {
            mTxtNewPassword.setError(getString(R.string.invalid_password));
        } else if (!mTxtConfirmPassword.getText().toString().equals(mTxtNewPassword.getText().toString())) {
            mTxtConfirmPassword.setError(getString(R.string.password_mismatch));
        } else {
            ConnectionHandler.updatePassword(new ChangePassHandler(), ConnectionHandler.loadEmail(), mTxtNewPassword.getText().toString(), mTxtOldPassword.getText().toString());
        }
    }

    public void pressedFeedback() {
        InputMethodManager imm = (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMainActivity.getCurrentFocus().getWindowToken(), 0);
        System.out.println("pressed feed");
        if (mIsChangingPassword) {
            hideChangePassword();
            showFeedback();
            mIsWritingFeedback = true;
        } else if (mIsWritingFeedback) {
            mIsWritingFeedback = false;
            hideFeedback();
        } else {
            showFeedback();
            mIsWritingFeedback = true;
        }
        mIsChangingPassword = false;
    }

    public void pressedChangePassword() {
        InputMethodManager imm = (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMainActivity.getCurrentFocus().getWindowToken(), 0);
        System.out.println("pressed change pass");
        if (mIsChangingPassword) {
            hideChangePassword();
            mIsChangingPassword = false;
        } else if (mIsWritingFeedback) {
            hideFeedback();
            mIsChangingPassword = true;
            showChangePassword();
        } else {
            mIsChangingPassword = true;
            showChangePassword();
        }
        mIsWritingFeedback = false;

    }

    private void showChangePassword() {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 190, r.getDisplayMetrics());
        //mChangePassContainer.animate().scaleY(2f);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChangePassContainer.getLayoutParams();
        params.height = (int) px;
        mChangePassContainer.setLayoutParams(params);
        mSubmitChangePasswordButton.setVisibility(View.VISIBLE);
    }

    private void hideChangePassword() {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
        //mChangePassContainer.animate().scaleY(2f);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChangePassContainer.getLayoutParams();
        params.height = (int) px;
        mChangePassContainer.setLayoutParams(params);
        mSubmitChangePasswordButton.setVisibility(View.GONE);
    }

    private void showFeedback() {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, r.getDisplayMetrics());
        //mChangePassContainer.animate().scaleY(2f);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFeedbackContainer.getLayoutParams();
        params.height = (int) px;
        mFeedbackContainer.setLayoutParams(params);
        mSubmitFeedbackButton.setVisibility(View.VISIBLE);
    }

    private void hideFeedback() {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
        //mChangePassContainer.animate().scaleY(2f);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFeedbackContainer.getLayoutParams();
        params.height = (int) px;
        mFeedbackContainer.setLayoutParams(params);
        mSubmitFeedbackButton.setVisibility(View.GONE);
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
            Log.i(TAG, "pressed notifs onbox");
            mOffBoxNotifications.setChecked(false);
            ConnectionHandler.saveShouldReceiveNotifications(true);
            mOnBoxNotifications.setChecked(true);
    }

    public void pressedTurnOffNotifications() {
        Log.i(TAG, "pressed notifs off");
        mOnBoxNotifications.setChecked(false);
        ConnectionHandler.saveShouldReceiveNotifications(false);
        mOffBoxNotifications.setChecked(true);

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

    public class FeedbackHandler implements CallbackGeneral {
        @Override
        public void callback(final boolean success, final String error) {
            mMainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    if (success) {
                        //do nothing
                    } else {

                    }
                }
            });
        }
    }

    public class ChangePassHandler implements CallbackGeneral {
        @Override
        public void callback(final boolean success, final String error) {
            mMainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    if (success) {
                        //do nothing
                    } else {

                    }
                }
            });
        }
    }











}
