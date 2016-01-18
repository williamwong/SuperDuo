package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PagerFragment extends Fragment {
    private static final int NUM_PAGES = 5;
    public ViewPager mViewPager;
    private final ScoreListFragment[] mScoreListFragments = new ScoreListFragment[5];

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentDate = new Date(System.currentTimeMillis() + ((i - 2) * TimeUnit.DAYS.toMillis(1)));
            mScoreListFragments[i] = new ScoreListFragment();
            mScoreListFragments[i].setFragmentDate(new SimpleDateFormat("yyyy-MM-dd").format(fragmentDate));
        }
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(MainActivity.sCurrentPagerItem);
        return v;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mScoreListFragments[i];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            long dateInMillis = System.currentTimeMillis() + ((position - 2) * TimeUnit.DAYS.toMillis(1));
            return getDayName(getActivity(), dateInMillis);
        }

        /**
         * Formats the name of the day relative to the current day. If the date is today, return
         * the localized version of "Today" instead of the actual day name. Likewise if it is
         * tomorrow or yesterday. Otherwise, the format is just the day of the week (e.g "Wednesday").
         *
         * @param context      An instance of the context, used to access string resources
         * @param dateInMillis A date represented in milliseconds
         * @return The name of the day either relative to today, or a day of the week
         */
        public String getDayName(Context context, long dateInMillis) {
            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if (julianDay == currentJulianDay + 1) {
                return context.getString(R.string.tomorrow);
            } else if (julianDay == currentJulianDay - 1) {
                return context.getString(R.string.yesterday);
            } else {
                return new SimpleDateFormat("EEEE").format(dateInMillis);
            }
        }
    }
}
