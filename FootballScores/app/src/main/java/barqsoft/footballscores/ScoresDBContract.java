package barqsoft.footballscores;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ScoresDBContract
{
    public static final String SCORES_TABLE = "ScoresTable";

    private static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    private static final String PATH = "scores";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class ScoresTable implements BaseColumns
    {
        // Table data
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";

        // Types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static Uri getBaseUri() {
            return BASE_CONTENT_URI;
        }

        public static Uri getScoreWithLeagueUri()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("league").build();
        }

        public static Uri getScoreWithIdUri()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("id").build();
        }

        public static Uri getScoreWithDateUri()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("date").build();
        }
    }

}
