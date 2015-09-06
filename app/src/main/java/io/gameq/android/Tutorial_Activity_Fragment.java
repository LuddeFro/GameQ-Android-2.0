package io.gameq.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.LinePageIndicator;

/**
 * Created by Ludvig on 16/07/15.
 * Copyright GameQ AB 2015
 */
public class Tutorial_Activity_Fragment extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 4;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_tutorial);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);


        //Bind the title indicator to the adapter
        LinePageIndicator indicator = (LinePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

    }

    @Override
    public void onBackPressed() {
        ConnectionHandler.saveFirstLoginDone();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.intent_inhouse_extra), true);
        startActivity(intent);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private Activity mActivity;
        public ScreenSlidePagerAdapter(FragmentManager fm, Activity act) {
            super(fm);
            mActivity = act;
        }

        @Override
        public Fragment getItem(int position) {
            Tutorial_Page_Fragment nextFrag = new Tutorial_Page_Fragment();
            nextFrag.setArguments(position, mActivity);
            return nextFrag;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}