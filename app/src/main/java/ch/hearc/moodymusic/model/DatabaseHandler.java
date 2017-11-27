package ch.hearc.moodymusic.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by axel.rieben on 21.11.2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHandler";

    //Songs table
    public static final String TABLE_SONG = "song";
    public static final String SONG_ID = "id";
    public static final String SONG_PATH = "path";
    public static final String SONG_ARTIST = "artist";
    public static final String SONG_TITLE = "title";
    public static final String SONG_ALBUM = "album";
    public static final String SONG_MOOD_ID = "mood_id";
    public static final String SONG_USER_MOOD_ID = "user_mood_id";

    //Mood table
    public static final String TABLE_MOOD = "mood";
    public static final String MOOD_ID = "id";
    public static final String MOOD_NAME = "name";

    //Database
    private static final String DATABASE_NAME = "moodymusic.db";
    private static final int DATABASE_VERSION = 1;

    //SQL
    private static final String TABLE_SONG_CREATE = "CREATE TABLE " + TABLE_SONG + "("
            + SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SONG_PATH + " TEXT NOT NULL, "
            + SONG_ARTIST + " TEXT, "
            + SONG_TITLE + " TEXT, "
            + SONG_ALBUM + " TEXT, "
            + SONG_MOOD_ID + " INTEGER, "
            + SONG_USER_MOOD_ID + " INTEGER"
            + ");";

    private static final String TABLE_MOOD_CREATE = "CREATE TABLE " + TABLE_MOOD + "("
            + MOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MOOD_NAME + " TEXT NOT NULL"
            + ");";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_SONG_CREATE);
        database.execSQL(TABLE_MOOD_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading mDatabase from version " + oldVersion + " to " + newVersion);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD);
        onCreate(database);
    }

}
