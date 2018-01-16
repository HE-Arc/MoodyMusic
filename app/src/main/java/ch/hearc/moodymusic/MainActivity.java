package ch.hearc.moodymusic;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ch.hearc.moodymusic.ui.PlayerFragment;
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
    private final int NUM_TABS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUi();

//        ClassificationEngine classificationEngine = new ClassificationEngine(this);
//        classificationEngine.initializeDatabaseWithSongs(0);
//        ClassificationTask classificationTask = new ClassificationTask(this);
//        classificationTask.execute();
    }

    private void setupUi() {
        mTabTitles = new CharSequence[NUM_TABS];
        mTabTitles[0] = getString(R.string.detect_name);
        mTabTitles[1] = getString(R.string.player_name);

        mToolBar = (Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(mToolBar);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mTabTitles, NUM_TABS);


        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                PlayerFragment playerFragment = mViewPagerAdapter.getPlayerFragment();

                int item = mViewPager.getCurrentItem();
                switch (item) {
                    case 0:
                        playerFragment.hideController();
                        break;
                    case 1:
                        playerFragment.showController();
                        break;
                }
            }
        });

        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorAccent);
            }
        });

        mTabs.setViewPager(mViewPager);
    }

}
