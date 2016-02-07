package barqsoft.footballscores.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class ScoresDBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 2;

    public ScoresDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(ScoresDBContract.ScoresTable.CREATE_SCORES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + ScoresDBContract.ScoresTable.TABLE_NAME);
    }
}
