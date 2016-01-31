package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String CURRENT_PAGER_ITEM_KEY = "CURRENT_PAGER_ITEM";
    private static final String SELECTED_MATCH_ID_KEY = "SELECTED_MATCH_ID";
    private static final String PAGER_FRAGMENT_KEY = "PAGER_FRAGMENT";

    public static int sSelectedMatchId;
    public static int sCurrentPagerItem = 2;    // Start on middle pager item

    private PagerFragment mPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mPagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mPagerFragment)
                    .commit();
        }

        // If started with an intent that specified a match, start activity with that match selected.
        double matchId = getIntent().getDoubleExtra(ScoresAppWidget.EXTRA_MATCH_ID, 0);
        if (matchId != 0) {
            sSelectedMatchId = (int) matchId;
        }

        startService(new Intent(this, UpdateScoreService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_PAGER_ITEM_KEY, mPagerFragment.mViewPager.getCurrentItem());
        outState.putInt(SELECTED_MATCH_ID_KEY, sSelectedMatchId);
        getSupportFragmentManager().putFragment(outState, PAGER_FRAGMENT_KEY, mPagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        sCurrentPagerItem = savedInstanceState.getInt(CURRENT_PAGER_ITEM_KEY);
        sSelectedMatchId = savedInstanceState.getInt(SELECTED_MATCH_ID_KEY);
        mPagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, PAGER_FRAGMENT_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
