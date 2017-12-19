package ch.hearc.moodymusic.ui;
/**
 * Inspired from : https://acadgild.com/blog/sliding-tab-layout-android/
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by axel.rieben on 29.10.2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence[] mTitles;
    private int mNumTabs;

    private DetectFragment mTabDetect;
    private PlayerFragment mTabPlayer;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence[] mTitles, int numTabs) {
        super(fm);

        this.mTitles = mTitles;
        this.mNumTabs = numTabs;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            mTabDetect = new DetectFragment();
            return mTabDetect;
        } else {
            mTabPlayer = new PlayerFragment();
            return mTabPlayer;
        }
    }

    // This method return the mTitles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return mNumTabs;
    }

    public PlayerFragment getPlayerFragment()
    {
        return mTabPlayer;
    }
}
