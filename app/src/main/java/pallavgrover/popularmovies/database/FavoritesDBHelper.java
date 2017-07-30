package pallavgrover.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import pallavgrover.popularmovies.database.FavoritesContract.Favorites;

public class FavoritesDBHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "favorites.db";
    private static final String LOGTAG = "FavoritesDBHelper ";

    public FavoritesDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(LOGTAG, "Helper constructed");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + Favorites.TABLE_NAME + " (" +
                Favorites._ID + " INTEGER PRIMARY KEY," +
                Favorites.COLUMN_TITLE + " TEXT NOT NULL, " +
//                Favorites.COLUMN_POSTER + " BLOB NOT NULL, " +
                Favorites.COLUMN_RATING + " TEXT NOT NULL, " +
                Favorites.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                Favorites.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                Favorites.COLUMN_API_ID + " INTEGER NOT NULL" +
                " );";
        Log.i(LOGTAG, SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        Log.i(LOGTAG, "Table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Favorites.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Favorites.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}