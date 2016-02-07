package barqsoft.footballscores.ui.scores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.R;
import barqsoft.footballscores.api.UpdateScoreService;
import barqsoft.footballscores.ui.about.AboutActivity;
import barqsoft.footballscores.ui.widget.ScoresAppWidget;

public class MainActivity extends AppCompatActivity {
    /**
     * Bundle keys for saving and restoring instance state
     */
    private static final String CURRENT_PAGER_ITEM_KEY = "CURRENT_PAGER_ITEM";
    private static final String SELECTED_MATCH_ID_KEY = "SELECTED_MATCH_ID";
    private static final String PAGER_FRAGMENT_KEY = "PAGER_FRAGMENT";

    /**
     * The match ID of a selected score, used to determine whether to display further details
     * about the match in {@link ScoreListFragment}. There is only a single selected match at
     * a time, and if it is not set, no detail view will be displayed.
     */
    public static int sSelectedMatchId;

    /**
     * The currently visible item in the {@link ScoresPagerFragment}. Initialized as 2 to start on
     * the middle pager item (i.e., Today's scores, which is the 3rd out of 5 items).
     */
    public static int sCurrentPagerItem = 2;

    /**
     * Fragment which holds a ViewPager that displays scores by date
     */
    private ScoresPagerFragment mScoresPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mScoresPagerFragment = new ScoresPagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mScoresPagerFragment)
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
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_refresh:
                startService(new Intent(this, UpdateScoreService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_PAGER_ITEM_KEY, mScoresPagerFragment.mViewPager.getCurrentItem());
        outState.putInt(SELECTED_MATCH_ID_KEY, sSelectedMatchId);
        getSupportFragmentManager().putFragment(outState, PAGER_FRAGMENT_KEY, mScoresPagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        sCurrentPagerItem = savedInstanceState.getInt(CURRENT_PAGER_ITEM_KEY);
        sSelectedMatchId = savedInstanceState.getInt(SELECTED_MATCH_ID_KEY);
        mScoresPagerFragment = (ScoresPagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, PAGER_FRAGMENT_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
