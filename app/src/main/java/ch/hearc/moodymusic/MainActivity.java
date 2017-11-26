package ch.hearc.moodymusic;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ch.hearc.moodymusic.ui.SlidingTabLayout;
import ch.hearc.moodymusic.ui.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    //UI
    private Toolbar mToolBar;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mTabs;
    private CharSequence[] mTabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUi();
    }

    private void setupUi() {
        mTabTitles = new CharSequence[2];
        mTabTitles[0] = getString(R.string.detect_name);
        mTabTitles[1] = getString(R.string.player_name);

        mToolBar = (Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(mToolBar);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mTabTitles, mTabTitles.length);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);

        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorAccent);
            }
        });

        mTabs.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

}
