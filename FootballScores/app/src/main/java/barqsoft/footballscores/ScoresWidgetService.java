package barqsoft.footballscores;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.ScoresDBContract.ScoresTable;

public class ScoresWidgetService extends RemoteViewsService {
    private static final String TAG = "ScoresWidgetService";

    public ScoresWidgetService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScoresRemoteViewsFactory(this, intent);
    }

    class ScoresRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private int mAppWidgetId;
        private Cursor mCursor;

        public ScoresRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) {
                mCursor.close();
            }
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
            String[] args = {date};
            mCursor = mContext.getContentResolver().query(ScoresTable.CONTENT_URI, null, ScoresTable.BY_DATE, args, null);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            Log.d(TAG, "getViewAt: " + position);

            // Retrieve column indices from cursor
            final int matchIdColumnIndex = mCursor.getColumnIndex(ScoresTable.MATCH_ID);
            final int homeColumnIndex = mCursor.getColumnIndex(ScoresTable.HOME_COL);
            final int awayColumnIndex = mCursor.getColumnIndex(ScoresTable.AWAY_COL);
            final int dateColumnIndex = mCursor.getColumnIndex(ScoresTable.DATE_COL);
            final int homeGoalsColumnIndex = mCursor.getColumnIndex(ScoresTable.HOME_GOALS_COL);
            final int awayGoalsColumnIndex = mCursor.getColumnIndex(ScoresTable.AWAY_GOALS_COL);

            // Get data for this position from content provider
            double matchId;
            String homeName = "";
            String awayName = "";
            String date = "";
            String score = "";
            int homeCrest = 0;
            int awayCrest = 0;

            if (mCursor.moveToPosition(position)) {
                matchId = mCursor.getDouble(matchIdColumnIndex);
                homeName = mCursor.getString(homeColumnIndex);
                awayName = mCursor.getString(awayColumnIndex);
                date = mCursor.getString(dateColumnIndex);
                score = Util.getScores(mCursor.getInt(homeGoalsColumnIndex), mCursor.getInt(awayGoalsColumnIndex));
                homeCrest = Util.getTeamCrestByTeamName(mCursor.getString(homeColumnIndex));
                awayCrest = Util.getTeamCrestByTeamName(mCursor.getString(awayColumnIndex));
            }

            // Create view and populate with data
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_score);

            rv.setTextViewText(R.id.home_name, homeName);
            rv.setTextViewText(R.id.away_name, awayName);
            rv.setTextViewText(R.id.data_text_view, date);
            rv.setTextViewText(R.id.score_text_view, score);
            rv.setImageViewResource(R.id.home_crest, homeCrest);
            rv.setImageViewResource(R.id.away_crest, awayCrest);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
