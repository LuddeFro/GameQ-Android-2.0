package io.gameq.android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public Activity myself;
    private boolean bolGotStatus = true;
    private TextView mTextViewCountdown;
    private TextView mTextViewStatus;
    private TextView mTextViewGame;
    private ProgressBar mCountdownBar;
    private ProgressBar mSpinBar;
    private CountDownTimer mCountdownTimer;
    private AlphaAnimation mAlphaAnimator;
    private RelativeLayout mCrosshair;
    private final int barMax = 10000;
    private Status mLastStatus;
    private final String TAG = "MainActivity";

    private int mInterval = 3000; //milliseconds
    private Handler mHandler;
    private Animation mCrosshairRotationAnimation;
    private boolean isRotatingCrosshair = false;

    GoogleCloudMessaging gcm;
    protected Dialog dialog;
    protected static final String PROPERTY_APP_VERSION = "appVersion";


    private Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            if (bolGotStatus) {
                Log.i(TAG, "getStatusTick");
                bolGotStatus = false;
                ConnectionHandler.getStatus(new StatusGetter());
            }
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
    ////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        ConnectionHandler.instantiateDataModel(getApplicationContext());
        myself = this;


        Intent startIntent = getIntent();
        if (!startIntent.getBooleanExtra(getString(R.string.intent_inhouse_extra), false)) {
            if (ConnectionHandler.loadEmail() == "") {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(getString(R.string.intent_inhouse_extra), true);
                startActivity(intent);
            } else {
                ConnectionHandler.loginWithRememberedDetails(new AutoLoginHandler());
            }
        }




        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        mNavigationDrawerFragment.mMainActivity = this;
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        ProgressBar back1Bar = (ProgressBar) findViewById(R.id.progressBarBackground);
        ProgressBar back2Bar = (ProgressBar) findViewById(R.id.spinBarBackground);
        back1Bar.setProgress(back1Bar.getMax() - 1);
        back2Bar.setProgress(back2Bar.getMax() - 1);

        mCountdownBar = (ProgressBar) findViewById(R.id.progressBar);
        Animation an = AnimationUtils.loadAnimation(this, R.anim.rotate270);
        mCountdownBar.startAnimation(an);
        mSpinBar = (ProgressBar) findViewById(R.id.spinBar);

        mTextViewCountdown = (TextView) findViewById(R.id.textViewCountdown);
        mTextViewGame = (TextView) findViewById(R.id.textViewGame);
        mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);
        mCrosshair = (RelativeLayout) findViewById(R.id.crosshair_container);
        mTextViewStatus.setText(getString(R.string.invisible_string));
        mTextViewGame.setText(getString(R.string.invisible_string));
        mTextViewCountdown.setText(getString(R.string.invisible_string));

        mSpinBar.setProgress(mSpinBar.getMax() / 10);
        mCountdownBar.setProgress(1);
        mSpinBar.setAlpha(0);
        mCountdownBar.setAlpha(0);
        startRepeatingTask();

        mCrosshairRotationAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely);
        mSpinBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely_right));
        mCrosshair.startAnimation(mCrosshairRotationAnimation);
        mCrosshair.getAnimation().cancel();
        mCrosshair.getAnimation().reset();
        //isRotatingCrosshair == false
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mNavigationDrawerFragment.mDrawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRepeatingTask();
        if (!checkPlayServices()) {
            Log.i(TAG, "!checkPlayServices() returns true in onCreate() Activity Master");
        } else {
            gcm = GoogleCloudMessaging.getInstance(this);
            String regid = getRegistrationId(myself.getApplicationContext());

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }
    }


    public class StatusGetter implements CallbackGetStatus {
        @Override
        public void callback(final boolean success, final String error, final int status, final int game, final long acceptBefore) {
            myself.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    bolGotStatus = true;
                    if (success) {

                        mTextViewStatus.setText(Encoding.getStringFromGameStatus(game, status));
                        mTextViewGame.setText(Encoding.getStringFromGame(game));
                        switch (Encoding.getStatusFromInt(status)) {
                            case OFFLINE:
                                //båda av
                                stopReadyCountdownAt(0);
                                mSpinBar.setAlpha(0);
                                mCountdownBar.setAlpha(0);
                                mSpinBar.setProgress(barMax / 10);
                                if (isRotatingCrosshair) {
                                    mCrosshair.getAnimation().setRepeatCount(0);
                                    isRotatingCrosshair = !isRotatingCrosshair;
                                }
                                break;
                            case ONLINE:
                                stopReadyCountdownAt(0);
                                mSpinBar.setAlpha(0);
                                mCountdownBar.setAlpha(0);
                                mSpinBar.setProgress(barMax / 10);
                                if (!isRotatingCrosshair) {
                                    mCrosshair.startAnimation(mCrosshairRotationAnimation);
                                    isRotatingCrosshair = !isRotatingCrosshair;
                                }
                                break;
                            case IN_LOBBY:
                                //båda av
                                stopReadyCountdownAt(0);
                                mSpinBar.setAlpha(0);
                                mCountdownBar.setAlpha(0);
                                mSpinBar.setProgress(barMax / 10);
                                if (!isRotatingCrosshair) {
                                    mCrosshair.startAnimation(mCrosshairRotationAnimation);
                                    isRotatingCrosshair = !isRotatingCrosshair;
                                }
                                break;
                            case IN_QUEUE:
                                //blå snurra
                                //röd av
                                stopReadyCountdownAt(0);
                                mSpinBar.setAlpha(1);
                                mCountdownBar.setAlpha(0);
                                mSpinBar.setProgress(barMax / 10);
                                if (!isRotatingCrosshair) {
                                    mCrosshair.startAnimation(mCrosshairRotationAnimation);
                                    isRotatingCrosshair = !isRotatingCrosshair;
                                }
                                break;
                            case GAME_READY:
                                //röd börja
                                //helblå
                                if (mLastStatus == Status.GAME_READY) {

                                } else {
                                    startCountdown(acceptBefore);
                                    mSpinBar.setAlpha(1);
                                    mCountdownBar.setAlpha(1);
                                    mSpinBar.setProgress(barMax);
                                    if (!isRotatingCrosshair) {
                                        mCrosshair.startAnimation(mCrosshairRotationAnimation);
                                        isRotatingCrosshair = !isRotatingCrosshair;
                                    }
                                }
                                break;
                            case IN_GAME:
                                //helorange
                                //helblå
                                stopReadyCountdownAt(barMax);
                                mSpinBar.setAlpha(1);
                                mCountdownBar.setAlpha(1);
                                mSpinBar.setProgress(barMax);
                                if (!isRotatingCrosshair) {
                                    mCrosshair.startAnimation(mCrosshairRotationAnimation);
                                    isRotatingCrosshair = !isRotatingCrosshair;
                                }
                                break;
                        }



                        mLastStatus = Encoding.getStatusFromInt(status);

                    } else {
                        //do nothing
                    }
                }
            });
        }
    }


    public class AutoLoginHandler implements CallbackGeneral {
        @Override
        public void callback(final boolean success, final String error) {
            myself.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    if (success) {
                        //do nothing
                    } else {
                        Intent intent = new Intent(myself, LoginActivity.class);
                        intent.putExtra(getString(R.string.intent_inhouse_extra), true);
                        startActivity(intent);
                        System.out.println(error);
                    }
                }
            });
        }
    }

    public class StatusUpdateHandler implements CallbackGeneral {
        @Override
        public void callback(final boolean success, final String error) {
            myself.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    if (success) {
                        //do nothing
                    } else {
                        //do nothing
                    }
                }
            });
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    /*public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            //restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;/
        }*/

        return super.onOptionsItemSelected(item);
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
    }






    private void stopReadyCountdownAt(int ofTenThousand) {
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
        }
        mCountdownBar.setProgress(ofTenThousand);


        if (mAlphaAnimator != null) {
            mAlphaAnimator.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mTextViewCountdown.setText(getString(R.string.invisible_string));
                }

                @Override
                public void onAnimationEnd(Animation anim) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }


    }

    private void startCountdown(long to) {
        long fromL = to - ConnectionHandler.serverDelay - (System.currentTimeMillis() / 1000L);
        int from = (int) fromL;
        countDown(from);
        startTimer(from);
        mCountdownBar.setAlpha(1);
    }



    private void countDown(final int count) {
        if (count < 0) {
            mTextViewCountdown.setText("");
            return;
        }
        mTextViewCountdown.setText(String.valueOf(count));
        mAlphaAnimator = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimator.setDuration(1000);
        mAlphaAnimator.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation anim) {
                countDown(count - 1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTextViewCountdown.startAnimation(mAlphaAnimator);
    }

    private void startTimer(final int seconds) {
        mCountdownTimer = new CountDownTimer(seconds * 1000, 100) {
            // 100 means, onTick function will be called at every 100 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                int barVal = (int) ((double) barMax * (((double) (((seconds)*1000) - (int)leftTimeInMilliseconds))/((double) ((seconds)*1000))));
                if (barVal >= barMax) {
                    stopReadyCountdownAt(barMax);
                }
                mCountdownBar.setProgress(barVal);
            }
            @Override
            public void onFinish() {
                stopReadyCountdownAt(barMax);
            }
        }.start();

    }









    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    protected boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(TAG, "checkGooglePlayServicesAvailable, connectionStatusCode="
                + resultCode);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                showGooglePlayServicesAvailabilityErrorDialog(resultCode);
                //GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                //PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, myself,
                        0);
                if (dialog == null) {
                    Log.e(TAG,
                            "couldn't get GooglePlayServicesUtil.getErrorDialog");
                    Toast.makeText(myself.getApplicationContext(),
                            "incompatible version of Google Play Services",
                            Toast.LENGTH_LONG).show();
                }
                dialog.show();
            }
        });
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    protected String getRegistrationId(Context context) {
        String token = ConnectionHandler.loadToken();
        SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
        if (token == null || token.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = dataGetter.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return token;
    }


    protected static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    protected void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(myself.getApplicationContext());
                    }
                    String regid = gcm.register(getString(R.string.gcm_sender_id));
                    msg = "Device registered, registration ID=" + regid;

                    if (ConnectionHandler.loadEmail() != null && ConnectionHandler.loadEmail() != "") {
                        //logged in probably
                        ConnectionHandler.updateToken(new StatusUpdateHandler(), regid);
                    } else {
                        ConnectionHandler.saveToken(regid);
                    }

                    SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor dataSetter = dataGetter.edit();
                    dataSetter.putInt(PROPERTY_APP_VERSION, getAppVersion(myself.getApplicationContext()));
                    dataSetter.commit();

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }




}
