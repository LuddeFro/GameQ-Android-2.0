package io.gameq.android;

import android.app.Activity;
import android.content.Intent;
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
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;


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
    private final int barMax = 1000;
    private Status mLastStatus;
    private final String TAG = "MainActivity";

    private int mInterval = 3000; //milliseconds
    private Handler mHandler;


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


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mNavigationDrawerFragment.mMainActivity = this;


        mCountdownBar = (ProgressBar) findViewById(R.id.progressBar);
        Animation an = new RotateAnimation(0.0f, 270.0f, 200f, 200f);
        an.setFillAfter(true);
        mCountdownBar.startAnimation(an);
        mSpinBar = (ProgressBar) findViewById(R.id.spinBar);

        mTextViewCountdown = (TextView) findViewById(R.id.textViewCountdown);
        mTextViewGame = (TextView) findViewById(R.id.textViewGame);
        mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);

        mTextViewStatus.setText(getString(R.string.invisible_string));
        mTextViewGame.setText(getString(R.string.invisible_string));
        mTextViewCountdown.setText(getString(R.string.invisible_string));

        mSpinBar.setProgress(1);
        mCountdownBar.setProgress(1);
        mSpinBar.setAlpha(0);
        mCountdownBar.setAlpha(0);
        startRepeatingTask();

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
                                mSpinBar.setProgress(barMax / 10);
                                break;
                            case ONLINE:
                                stopReadyCountdownAt(0);
                                mSpinBar.setAlpha(0);
                                mSpinBar.setProgress(barMax / 10);
                                break;
                            case IN_LOBBY:
                                //båda av
                                stopReadyCountdownAt(0);
                                mSpinBar.setAlpha(0);
                                mSpinBar.setProgress(barMax / 10);
                                break;
                            case IN_QUEUE:
                                //blå snurra
                                //röd av
                                stopReadyCountdownAt(0);
                                mSpinBar.setAlpha(1);
                                mSpinBar.setProgress(barMax / 10);
                                break;
                            case GAME_READY:
                                //röd börja
                                //helblå
                                startCountdown(acceptBefore);
                                mSpinBar.setAlpha(1);
                                mSpinBar.setProgress(barMax);
                                break;
                            case IN_GAME:
                                //helorange
                                //helblå
                                stopReadyCountdownAt(barMax);
                                mSpinBar.setAlpha(1);
                                mSpinBar.setProgress(barMax);
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






    private void stopReadyCountdownAt(int atPromille) {
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
        }
        mSpinBar.setAlpha(0);
        mCountdownBar.setProgress(atPromille*barMax/1000);
        if (mAlphaAnimator != null) {
            mAlphaAnimator.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mTextViewCountdown.setText(getString(R.string.invisible_string));
                }
                @Override
                public void onAnimationEnd(Animation anim) {            }
                @Override
                public void onAnimationRepeat(Animation animation) {            }
            });
        }


    }

    private void startCountdown(long to) {
        long fromL = to - ConnectionHandler.serverDelay - (System.currentTimeMillis() / 1000L);
        int from = (int) fromL;
        countDown(from);
        startTimer(from);
        mCountdownBar.setAlpha(1);
        mSpinBar.setAlpha(1);

    }



    private void countDown(final int count) {
        if (count < 0) {
            mTextViewCountdown.setText("");
            return;
        }

        mAlphaAnimator = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimator.setDuration(1000);
        mAlphaAnimator.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTextViewCountdown.setText(String.valueOf(count));
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
                int barVal = barMax * ((int)leftTimeInMilliseconds/((seconds)*1000));
                mCountdownBar.setProgress(barVal);
            }
            @Override
            public void onFinish() {

            }
        }.start();

    }





}
