package ch.hearc.moodymusic.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by axel.rieben on 21.11.2017.
 * Class that describe, create, upgrade and delete the SQlite Database
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHandler";

    //Songs table
    public static final String TABLE_SONG = "song";
    public static final String SONG_ID = "_id";
    public static final String SONG_PATH = "path";
    public static final String SONG_ARTIST = "artist";
    public static final String SONG_TITLE = "title";
    public static final String SONG_ALBUM = "album";
    public static final String SONG_MOOD_PLAYLIST_ID = "mood_playlist_id";
    public static final String SONG_USER_MOOD_PLAYLIST_ID = "user_mood_playlist_id";

    //MoodPlaylist table
    public static final String TABLE_MOOD = "mood";
    public static final String MOOD_ID = "_id";
    public static final String MOOD_NAME = "name";

    //MoodPlaylist playlist table
    public static final String TABLE_MOOD_PLAYLIST = "mood_playlist";
    public static final String MOOD_PLAYLIST_ID = "_id";
    public static final String MOOD_PLAYLIST_NAME = "name";

    //Mapping table
    public static final String TABLE_MAPPING = "mapping";
    public static final String MAPPING_ID = "_id";
    public static final String MAPPING_MOOD_ID = "mood_id";
    public static final String MAPPING_MOOD_PLAYLIST_ID = "mood_playlist_id";

    //Database
    private static final String DATABASE_NAME = "moodymusic.db";
    private static final int DATABASE_VERSION = 2;

    //SQL create tables

    //Table Song
    private static final String TABLE_SONG_CREATE = "CREATE TABLE " + TABLE_SONG + "("
            + SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SONG_PATH + " TEXT NOT NULL, "
            + SONG_ARTIST + " TEXT, "
            + SONG_TITLE + " TEXT, "
            + SONG_ALBUM + " TEXT, "
            + SONG_MOOD_PLAYLIST_ID + " INTEGER, "
            + SONG_USER_MOOD_PLAYLIST_ID + " INTEGER"
            + ");";

    //Table Mood
    private static final String TABLE_MOOD_CREATE = "CREATE TABLE " + TABLE_MOOD + "("
            + MOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MOOD_NAME + " TEXT NOT NULL"
            + ");";

    //Table Mood Playlist
    private static final String TABLE_MOOD_PLAYLIST_CREATE = "CREATE TABLE " + TABLE_MOOD_PLAYLIST + "("
            + MOOD_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MOOD_PLAYLIST_NAME + " TEXT NOT NULL"
            + ");";

    //Table Mapping
    private static final String TABLE_MAPPING_CREATE = "CREATE TABLE " + TABLE_MAPPING + "("
            + MAPPING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MAPPING_MOOD_ID + " INTEGER, "
            + MAPPING_MOOD_PLAYLIST_ID + " INTEGER"
            + ");";

    //SQL fill tables
    private static final String TABLE_MOOD_FILL = "INSERT INTO " + TABLE_MOOD + " ("+ MOOD_NAME + ") VALUES " +
            "('neutral'), " +
            "('angry'), " +
            "('disgusted'), " +
            "('scared'), " +
            "('happy'), " +
            "('sad'), " +
            "('surprised')";

    private static final String TABLE_MOOD_PLAYLIST_FILL = "INSERT INTO " + TABLE_MOOD_PLAYLIST + " ("+ MOOD_PLAYLIST_NAME + ") VALUES " +
            "('Peaceful'), " +
            "('Romantic'), " +
            "('Sentimental'), " +
            "('Tender'), " +
            "('Easygoing'), " +
            "('Yearning'), " +
            "('Sophisticated'), " +
            "('Sensual'), " +
            "('Cool'), " +
            "('Gritty'), " +
            "('Somber'), " +
            "('Melancholy'), " +
            "('Serious'), " +
            "('Brooding'), " +
            "('Fiery'), " +
            "('Urgent'), " +
            "('Defiant'), " +
            "('Aggressive'), " +
            "('Rowdy'), " +
            "('Excited'), " +
            "('Energizing'), " +
            "('Empowering'), " +
            "('Stirring'), " +
            "('Lively'), " +
            "('Upbeat'), " +
            "('Other')";

    private static final String TABLE_MAPPING_FILL = "INSERT INTO " + TABLE_MAPPING + " ("+ MAPPING_MOOD_ID + ", " + MAPPING_MOOD_PLAYLIST_ID + ") VALUES " +
            "(1, 2), " +
            "(1, 6), " +
            "(1, 7), " +
            "(1, 9), " +
            "(1, 13), " +
            "(1, 15), " +
            "(1, 16), " +
            "(1, 22), " +
            "(1, 23), " +
            "(2, 14), " +
            "(2, 16), " +
            "(2, 17), " +
            "(2, 18), " +
            "(2, 19), " +
            "(2, 22), " +
            "(3, 7), " +
            "(3, 17), " +
            "(4, 11), " +
            "(4, 14), " +
            "(4, 23), " +
            "(5, 1), " +
            "(5, 5), " +
            "(5, 9), " +
            "(5, 15), " +
            "(5, 20), " +
            "(5, 21), " +
            "(5, 22), " +
            "(5, 24), " +
            "(5, 25), " +
            "(6, 3), " +
            "(6, 4), " +
            "(6, 6), " +
            "(6, 11), " +
            "(6, 12), " +
            "(6, 19), " +
            "(7, 8), " +
            "(7, 18), " +
            "(7, 19)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_SONG_CREATE);
        database.execSQL(TABLE_MOOD_CREATE);
        database.execSQL(TABLE_MOOD_PLAYLIST_CREATE);
        database.execSQL(TABLE_MAPPING_CREATE);
        database.execSQL(TABLE_MOOD_FILL);
        database.execSQL(TABLE_MOOD_PLAYLIST_FILL);
        database.execSQL(TABLE_MAPPING_FILL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading mDatabase from version " + oldVersion + " to " + newVersion);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD_PLAYLIST);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MAPPING);
        onCreate(database);
    }
}
