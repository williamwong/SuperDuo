package barqsoft.footballscores;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class ScoresProvider extends ContentProvider
{
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;

    private static final String SCORES_BY_LEAGUE = ScoresDBContract.ScoresTable.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE = ScoresDBContract.ScoresTable.DATE_COL + " LIKE ?";
    private static final String SCORES_BY_ID = ScoresDBContract.ScoresTable.MATCH_ID + " = ?";

    private static ScoresDBHelper sDbHelper;
    private final UriMatcher mUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoresDBContract.ScoresTable.getBaseUri().toString();
        matcher.addURI(authority, null , MATCHES);
        matcher.addURI(authority, "league" , MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, "id" , MATCHES_WITH_ID);
        matcher.addURI(authority, "date" , MATCHES_WITH_DATE);
        return matcher;
    }

    private int matchUri(Uri uri)
    {
        String link = uri.toString();
        {
            if (link.contentEquals(ScoresDBContract.ScoresTable.getBaseUri().toString())) {
                return MATCHES;
            } else if (link.contentEquals(ScoresDBContract.ScoresTable.getScoreWithDateUri().toString())) {
                return MATCHES_WITH_DATE;
            } else if (link.contentEquals(ScoresDBContract.ScoresTable.getScoreWithIdUri().toString())) {
                return MATCHES_WITH_ID;
            } else if (link.contentEquals(ScoresDBContract.ScoresTable.getScoreWithLeagueUri().toString())) {
                return MATCHES_WITH_LEAGUE;
            }
        }
        return -1;
    }

    @Override
    public boolean onCreate()
    {
        sDbHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public String getType(@NonNull Uri uri)
    {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return ScoresDBContract.ScoresTable.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return ScoresDBContract.ScoresTable.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return ScoresDBContract.ScoresTable.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return ScoresDBContract.ScoresTable.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri );
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor cursor;
        SQLiteDatabase db = sDbHelper.getReadableDatabase();
        switch (matchUri(uri))
        {
            case MATCHES:
                cursor = db.query(ScoresDBContract.SCORES_TABLE, projection,
                        null, null, null, null, sortOrder);
                break;
            case MATCHES_WITH_DATE:
                cursor = db.query(ScoresDBContract.SCORES_TABLE, projection,
                        SCORES_BY_DATE, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_ID:
                cursor = db.query(ScoresDBContract.SCORES_TABLE, projection,
                        SCORES_BY_ID, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_LEAGUE:
                cursor = db.query(ScoresDBContract.SCORES_TABLE, projection,
                        SCORES_BY_LEAGUE, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values)
    {
        SQLiteDatabase db = sDbHelper.getWritableDatabase();
        switch (matchUri(uri))
        {
            case MATCHES:
                db.beginTransaction();
                int counter = 0;
                try
                {
                    for(ContentValues value : values)
                    {
                        long result = db.insertWithOnConflict(ScoresDBContract.SCORES_TABLE,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (result != -1)
                        {
                            counter++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return counter;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
