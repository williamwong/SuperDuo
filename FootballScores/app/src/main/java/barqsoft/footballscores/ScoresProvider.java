package barqsoft.footballscores;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static barqsoft.footballscores.ScoresDBContract.CONTENT_AUTHORITY;
import static barqsoft.footballscores.ScoresDBContract.ScoresTable;

public class ScoresProvider extends ContentProvider {
    private static final int SCORE_LIST = 100;
    private static final int SCORE_BY_ID = 101;

    private static ScoresDBHelper sDbHelper;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(CONTENT_AUTHORITY, ScoresTable.TABLE_PATH, SCORE_LIST);
        URI_MATCHER.addURI(CONTENT_AUTHORITY, ScoresTable.TABLE_PATH + "/#", SCORE_BY_ID);
    }

    @Override
    public boolean onCreate() {
        sDbHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case (SCORE_LIST):
                return ScoresTable.CONTENT_TYPE;
            case (SCORE_BY_ID):
                return ScoresTable.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = sDbHelper.getReadableDatabase();
        String table;
        switch (URI_MATCHER.match(uri)) {
            case (SCORE_LIST):
                table = ScoresTable.TABLE_NAME;
                break;
            case (SCORE_BY_ID):
                table = ScoresTable.TABLE_NAME;
                selection = ScoresTable.BY_MATCH_ID;
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        cursor = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = sDbHelper.getWritableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case SCORE_LIST:
                db.beginTransaction();
                int counter = 0;
                try {
                    for (ContentValues value : values) {
                        long result = db.insertWithOnConflict(ScoresTable.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (result != -1) {
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
