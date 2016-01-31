package barqsoft.footballscores;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ScoresDBContract {
    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class ScoresTable implements BaseColumns {
        public static final String TABLE_NAME = "ScoresTable";
        public static final String TABLE_PATH = "scores";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_PATH);

        // Table columns
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";

        public static final String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + DATE_COL + " TEXT NOT NULL,"
                + TIME_COL + " INTEGER NOT NULL,"
                + HOME_COL + " TEXT NOT NULL,"
                + AWAY_COL + " TEXT NOT NULL,"
                + LEAGUE_COL + " INTEGER NOT NULL,"
                + HOME_GOALS_COL + " TEXT NOT NULL,"
                + AWAY_GOALS_COL + " TEXT NOT NULL,"
                + MATCH_ID + " INTEGER NOT NULL,"
                + MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE (" + MATCH_ID + ") ON CONFLICT REPLACE"
                + " );";

        // MIME Types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_PATH;

        // Selections
        public static final String BY_LEAGUE = LEAGUE_COL + " = ?";
        public static final String BY_DATE = DATE_COL + " LIKE ?";
        public static final String BY_MATCH_ID = MATCH_ID + " = ?";
    }

}
