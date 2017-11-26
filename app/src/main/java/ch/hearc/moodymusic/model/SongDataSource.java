package ch.hearc.moodymusic.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by axel.rieben on 21.11.2017.
 */

public class SongDataSource extends DataSource{
    public static final String TAG = "SongDataSource";

    private String[] mAllColumns = {DatabaseHandler.SONG_ID, DatabaseHandler.SONG_PATH, DatabaseHandler.SONG_ARTIST,
            DatabaseHandler.SONG_TITLE, DatabaseHandler.SONG_ALBUM,
            DatabaseHandler.SONG_MOOD_ID, DatabaseHandler.SONG_USER_MOOD_ID};

    public SongDataSource(Context context) {
        super(context);
    }

    public Song createSong(String path, String artist, String title, String album, Long moodId, Long userMoodId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.SONG_PATH, path);
        values.put(DatabaseHandler.SONG_ARTIST, artist);
        values.put(DatabaseHandler.SONG_TITLE, title);
        values.put(DatabaseHandler.SONG_ALBUM, album);
        values.put(DatabaseHandler.SONG_MOOD_ID, moodId);
        values.put(DatabaseHandler.SONG_USER_MOOD_ID, userMoodId);
        long insertId = mDatabase.insert(DatabaseHandler.TABLE_SONG, null, values);

        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_SONG, mAllColumns, DatabaseHandler.SONG_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Song newSong = cursorToSong(cursor);
        cursor.close();

        return newSong;
    }

    public void deleteSong(Song song) {
        long id = song.getId();
        System.out.println("Song deleted with id: " + id);
        mDatabase.delete(DatabaseHandler.TABLE_SONG, DatabaseHandler.SONG_ID + " = " + id, null);
    }

    private Song cursorToSong(Cursor cursor) {
        Song song = new Song(cursor.getLong(0), cursor.getString(2),
                cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getLong(6), cursor.getLong(7));
        return song;
    }
}